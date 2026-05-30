// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.OrchestrationPlanNodeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.ProductOrderItemRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@Slf4j
public class OrderItemWithActionNoChange {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EventPublisher eventPublisher;

    @And("The system orchestration plan with order id {string} will have exactly the following orchestration plan nodes")
    public void theSystemOrchestrationPlanWithOrderIdWillHaveExactlyTheFollowingOrchestrationPlanNodes(String orderId, List<OrchestrationPlanNodeRecord> nodesList) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(orderId));
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(orchestrationPlan).isNotNull();
            Set<OrchestrationPlanNode> orchestrationPlanNodes = orchestrationPlan.getOrchestrationPlanNodes();
            assertThat(orchestrationPlanNodes).hasSameSizeAs(nodesList);
            nodesList.forEach(nodeRecord -> {
                Optional<OrchestrationPlanNode> orchestrationPlanNodeOptional = orchestrationPlanNodes.stream().filter(node -> nodeRecord.orderItemId().equals(node.getActualOrderItemId())).findFirst();
                assertThat(orchestrationPlanNodeOptional).isPresent();
                assertThat(orchestrationPlanNodeOptional.get().getState()).isEqualTo(OrchestrationPlanNodeState.fromValue(nodeRecord.state()));
                assertThat(orchestrationPlanNodeOptional.get().getActualRelatedOrderItem().getAction()).isEqualTo(nodeRecord.action());
                if (Objects.nonNull(nodeRecord.estimatedLeadTimeDelivery())) {
                    int secondsInHour = 3600;
                    assertThat(orchestrationPlanNodeOptional.get().getOrchestrationNodeSchedule().getEstimatedOrderItemDeliveryLeadTime()).isEqualTo(nodeRecord.estimatedLeadTimeDelivery() * secondsInHour);
                }
                if (Objects.nonNull(nodeRecord.orderItemStartDate())) {
                    assertThat(orchestrationPlanNodeOptional.get().getOrchestrationNodeSchedule().getOrderItemStartDate()).isEqualTo(nodeRecord.orderItemStartDate());
                }
            });
        });
    }

    @And("the system will fire orchestration plan state change event to topic {string} with order id {string} and state {string}")
    public void theSystemWillFireOrchestrationPlanStateChangeEventToTopicWithOrderIdAndState(String topicName, String orderId, String state) {
        ArgumentCaptor<OrchestrationPlan> planCaptor = ArgumentCaptor.forClass(OrchestrationPlan.class);
        Mockito.verify(eventPublisher, times(2)).publishEvent(eq(CDCEvent.fromTopicName(topicName)), planCaptor.capture());
        List<OrchestrationPlan> plans = planCaptor.getAllValues();
        plans.forEach(plan -> assertThat(plan.getRelatedProductOrder().getId()).isEqualTo(orderId));
        assertThat(plans.stream().map(OrchestrationPlan::getState).collect(Collectors.toSet())).contains(State.fromValue(state));
    }

    @And("the system will fire orchestration plan state change events to topic {string} with order id {string} and state {string}")
    public void theSystemWillFireOrchestrationPlanStateChangeEventsToTopicWithOrderIdAndState(String topicName, String orderId, String state) {
        await().atMost(Duration.ofSeconds(70)).untilAsserted(() -> {
            ArgumentCaptor<List<OrchestrationPlan>> planCaptor = ArgumentCaptor.forClass(List.class);
            Mockito.verify(eventPublisher, times(1)).publishEvents(eq(CDCEvent.fromTopicName(topicName)), planCaptor.capture());
            List<OrchestrationPlan> plans = planCaptor.getValue();
            plans.forEach(plan -> assertThat(plan.getRelatedProductOrder().getId()).isEqualTo(orderId));
            assertThat(plans.stream().map(OrchestrationPlan::getState).collect(Collectors.toSet())).contains(State.fromValue(state));
        });
    }

    @And("The system is going to have node with order id {string} and the following related products")
    public void theSystemIsGoingToHaveNodeWithOrderIdAndTheFollowingRelatedProducts(String orderItemId, List<ProductOrderItemRecord> relatedProducts) {
        Query query = new Query();
        OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
        assertThat(orchestrationPlan).isNotNull();

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(planNode -> planNode.getRelatedProductOrderItem().stream()
                        .anyMatch(relatedProductOrderItem -> relatedProductOrderItem.getId().equals(orderItemId)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No matching OrchestrationPlanNode found"));

        List<RelatedProduct> relatedProductList = orchestrationPlanNode.getRelatedProduct();
        assertThat(relatedProductList).hasSameSizeAs(relatedProducts);

        relatedProducts.forEach(relatedProductData -> {
            String relationType = relatedProductData.relationType();
            boolean matchFound = relatedProductList.stream()
                    .anyMatch(relatedProduct -> relationType.equals(relatedProduct.getRelationshipType().toString()));
            assertThat(matchFound).isTrue();
        });
    }

    @And("The system is going to have node with node id {string} and the following related products")
    public void theSystemIsGoingToHaveNodeWithNodeIdAndTheFollowingRelatedProducts(String nodeId, List<OrchestrationPlanNodeRecord> relatedProducts) {
        Query query = new Query();
        OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
        assertThat(orchestrationPlan).isNotNull();

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(planNode -> nodeId.equals(planNode.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No matching OrchestrationPlanNode found"));

        List<RelatedProduct> relatedProductList = orchestrationPlanNode.getRelatedProduct();
        assertThat(relatedProductList.size()).isNotZero();

        relatedProducts.forEach(relatedProductData -> {
            String relationType = relatedProductData.relationType();
            String expectedProductId = relatedProductData.productId();
            RelatedProduct expectedRelatedProduct = relatedProductList.stream()
                    .filter(relatedProduct -> expectedProductId.equals(relatedProduct.getId()))
                    .findFirst()
                    .orElse(null);

            assertThat(expectedRelatedProduct).isNotNull();
            assertThat(expectedRelatedProduct.getId()).isEqualTo(expectedProductId);
            assertThat(expectedRelatedProduct.getRelationshipType()).hasToString(relationType);
        });
    }
}
