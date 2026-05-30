// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.ProductOrderItemRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.ProductSpecificationRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationRelationship;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
public class DependencyManagementForMigratedProductOrders {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EventPublisher eventPublisher;

    @Given("The product catalog has the following installed products:")
    public void theProductCatalogHasInstalledProductsWithTheFollowingData(List<ProductSpecificationRecord> productSpecificationList) throws JsonProcessingException {
        doNothing().when(eventPublisher).publishEvent(any(), any());

        List<ProductSpecification> productSpecifications = productSpecificationList.stream().map(catalogProduct ->
                ProductSpecification.builder()
                        .id(catalogProduct.specificationId())
                        .name(catalogProduct.name())
                        .serviceSpecification(List.of())
                        .relatedResource(List.of())
                        .productSpecificationRelationship(List.of(ProductSpecificationRelationship.builder()
                                .id(Objects.nonNull(catalogProduct.relatedSpecificationID()) ? catalogProduct.relatedSpecificationID() : null)
                                .relationshipType(Objects.nonNull(catalogProduct.relationType()) ? ProductSpecificationRelationshipType.fromValue(catalogProduct.relationType()) : ProductSpecificationRelationshipType.RELIES_ON).build()))
                        .build()
        ).toList();

        wireMockServer.stubFor(get(urlPathEqualTo("/productCatalogManagement/v1/productSpecification"))
                .willReturn(ok().withBody(objectMapper.writeValueAsString(productSpecifications)).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

    }

    @And("The system is going to have node with order id {string} and the following related nodes relations")
    public void theSystemIsGoingToHaveNodeWithOrderIdAndTheFollowingRelatedNodesRelations(String productOrderId, List<ProductOrderItemRecord> relationTable) {

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(new Query(), OrchestrationPlan.class);
            OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(planNode -> planNode.getRelatedProductOrderItem().stream().anyMatch(relatedProductOrderItem -> relatedProductOrderItem.getId().equals(productOrderId))).findFirst().get();

            relationTable.stream().map(relatedNode -> {
                        OrchestrationPlanNode orchestrationPlanNodeTT = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(planNode -> planNode.getRelatedProductOrderItem().stream().anyMatch(relatedProductOrderItem -> relatedProductOrderItem.getId().equals(relatedNode.orderItemId()))).findFirst().get();
                        assertThat(orchestrationPlanNode.getRelatedOrchestrationPlanNode().stream().filter(relatedOrchestrationPlanNode -> relatedOrchestrationPlanNode.getRelationshipType().equals(RelatedOrchestrationPlanNodeRelationshipType.fromValue(relatedNode.relationType()))).findFirst().get().getRelatedNodeId()).isEqualTo(orchestrationPlanNodeTT.getId());
                        return null;
                    }
            );
        });
    }

    @And("the system will fire orchestration plan state change event to topic {string} with order id {string} and state {string}:")
    public void theSystemWillFireOrchestrationPlanStateChangeEventToTopicWithOrderIdAndState(String topicName, String orderId, String state) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            ArgumentCaptor<OrchestrationPlan> planCaptor = ArgumentCaptor.forClass(OrchestrationPlan.class);
            Mockito.verify(eventPublisher, times(2)).publishEvent(eq(CDCEvent.fromTopicName(topicName)), planCaptor.capture());
            List<OrchestrationPlan> plans = planCaptor.getAllValues();
            plans.forEach(plan -> assertThat(plan.getRelatedProductOrder().getId()).isEqualTo(orderId));
            assertThat(plans.stream().map(OrchestrationPlan::getState).collect(Collectors.toSet())).contains(State.fromValue(state));
        });
    }
}