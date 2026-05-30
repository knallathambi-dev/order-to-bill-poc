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
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.kafka.handler.DeliveryOrderItemStatusEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class RepoMockCommonStepDefinitions {

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;

    private OrchestrationPlan orchestrationPlan;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private DeliveryOrderItemStatusEventHandler deliveryOrderItemStatusEventHandler;

    @And("the system has a mock orchestration plan with id {string}, state {string}, order id {string} and nodes:")
    public void theSystemHasOrchestrationPlanWithIdStateOrderIdAndNodes(String id, String state, String orderId, List<OrchestrationPlanNodeRecord> nodesList) {
        orchestrationPlan = OrchestrationPlan.builder()
                .id(id)
                .state(State.fromValue(state))
                .relatedProductOrder(RelatedProductOrder.builder().id(orderId).build())
                .build();

        Set<OrchestrationPlanNode> nodesSet = nodesList.stream().map(node -> OrchestrationPlanNode.builder()
                .id(node.id())
                .relatedProductOrder(Objects.nonNull(node.orderId()) ? RelatedProductOrder.builder().id(node.orderId()).build() : null)
                .state(OrchestrationPlanNodeState.fromValue(node.state()))
                .previousState(Objects.nonNull(node.previousState()) ? OrchestrationPlanNodeState.fromValue(node.previousState()) : null)
                .relatedProduct(List.of(RelatedProduct.builder()
                        .id("id")
                        .type(Objects.nonNull(node.relationType()) ? RelatedProductType.fromValue(node.relationType()) : null)
                        .relationshipType(RelatedProductRelationType.DELIVERS)
                        .productOrderItemId("pOId")
                        .realisingService(
                                List.of(RealisingService.builder()
                                        .id(node.realizingServiceId())
                                        .href(node.realizingServiceHref())
                                        .build()))
                        .build()))
                .relatedProductOrderItem(List.of(RelatedProductOrderItem.builder()
                        .id("id")
                        .action("")
                        .build()))
                .build()).collect(Collectors.toSet());
        orchestrationPlan.setOrchestrationPlanNodes(nodesSet);
    }


    @And("the system mocked node with {string} has related products:")
    public void theSystemMockedNodeWithHasRelatedProducts(String nodeId, List<OrchestrationPlanNodeRecord> productsList) {
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeById(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));
        List<RelatedProduct> relatedProducts = productsList.stream().map(product -> RelatedProduct.builder()
                .id(product.productId())
                .type(RelatedProductType.fromValue(product.productType()))
                .relationshipType(RelatedProductRelationType.fromValue(product.relationshipType()))
                .productOrderItemId(product.orderItemId())
                .realisingService(List.of(RealisingService.builder().id(product.realizingServiceId()).href(product.realizingServiceHref()).build()))
                .build()).toList();
        orchestrationPlanNode.setRelatedProduct(relatedProducts);

        List<RelatedProductOrderItem> relatedProductOrderItems = productsList.stream().map(product -> RelatedProductOrderItem.builder()
                .id(product.orderItemId())
                .action(product.orderItemAction())
                .build()
        ).toList();
        orchestrationPlanNode.setRelatedProductOrderItem(relatedProductOrderItems);

        List<RelatedOrchestrationPlanNode> relatedOrchestrationPlanNodes = productsList.stream()
                .filter(product -> RelatedProductRelationType.RELIES_ON.getValue().equals(product.relationshipType()))
                .flatMap(product ->
                        orchestrationPlan.getOrchestrationPlanNodes().stream()
                                .filter(node -> node.getActualRelatedProductOptional().isPresent() && node.getActualRelatedProductOptional().get().getProductOrderItemId().equals(product.orderItemId()))
                                .map(node -> RelatedOrchestrationPlanNode.builder()
                                        .relatedNodeId(node.getId())
                                        .relationshipType(RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)
                                        .build())
                )
                .toList();
        orchestrationPlanNode.setRelatedOrchestrationPlanNode(relatedOrchestrationPlanNodes);
        Mockito.when(orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId)).thenReturn(Optional.of(orchestrationPlan));
        Mockito.when(orchestrationPlanRepository.findOrchestrationPlanNodeById(nodeId)).thenReturn(Optional.of(orchestrationPlanNode));
    }

    @And("the system repository will fail on save")
    public void theSystemRepositoryWillFailOnSave() {
        Mockito.when(orchestrationPlanRepository.save(Mockito.any())).thenThrow(new RuntimeException("Could not save orchestration plan"));
    }


    @When("the system consumes mocked orchestration plan node state change event on topic {string} with node id {string} and state {string}")
    public void theTopicReceivesEventWithNodeWithStatus(String topicName, String nodeId, String status) {
        OrchestrationPlanNode node = orchestrationPlan.getOrchestrationPlanNodeById(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));
        node.setState(OrchestrationPlanNodeState.fromValue(status));

        OrchestrationPlanNodeStateChangeEvent planNodeStateChangeEvent = OrchestrationPlanNodeStateChangeEvent.builder()
                .event(OrchestrationPlanNodeStateChangePayloadEvent.builder().orchestrationPlanNode(node).build())
                .eventType(OrchestrationPlanNodeStateChangeEvent.class.getSimpleName())
                .eventId("id")
                .eventTime(Instant.MAX)
                .build();

        kafkaTemplate.send(topicName, planNodeStateChangeEvent);
    }

    @When("the system consumes delivery status event from topic {string} for mocked orchestration plan node with id {string} and state {string}")
    public void theSystemConsumesDeliveryStatusEventForOrchestrationPlanNodeWithIdAndState(String topic, String nodeId, String state) {
        DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent = DeliveryOrderItemStatusEvent.builder()
                .event(DeliveryOrderItemStatusPayloadEvent.builder()
                        .orderItemRef(OrderItemRef.builder()
                                .orchestrationNodeId(nodeId)
                                .deliveryStatusMapping(DeliveryStatusMapping
                                        .builder()
                                        .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.fromValue(state))
                                        .build())
                                .build())
                        .build())
                .eventType(DeliveryOrderItemStatusEvent.class.getSimpleName()).build();

        try {
            deliveryOrderItemStatusEventHandler.handleEvent(deliveryOrderItemStatusEvent, new HashMap<>());
        } catch (Exception ignored) {
            System.out.println(ignored);
            // This try block always throws because the service order call is not stubbed.
            // We don't care about the response; we only need to verify the call was made.
            // We'll keep this try-catch to allow later assertion that the call occurred.
        }
    }

}
