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
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.kafka.handler.DeliveryOrderItemStatusEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.productinventory.dto.v1.PhysicalProduct;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang.RandomStringUtils;
import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.like;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions.PRODUCT_MANAGEMENT_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class DeliveryStatusEventConsumer {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;

    @Autowired
    private KafkaTemplate<String, DeliveryOrderItemStatusEvent> kafkaTemplate;

    @Autowired
    private DeliveryOrderItemStatusEventHandler deliveryOrderItemStatusEventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private DeliveryOrderItemStatusPayloadEvent deliveryOrderItemStatusPayloadEvent;

    @Before
    public void beforeEach() {
        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
        wireMockServer.resetAll();
    }

    @And("the node with id {string} has the following related products:")
    public void theNodeWithIdHasRelatedProductsWithTheFollowingData(String nodeId, List<OrchestrationPlanNodeRecord> productsList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeById(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));
        orchestrationPlan.getOrchestrationPlanNodes().remove(orchestrationPlanNode);
        List<RelatedProduct> relatedProducts = productsList.stream().map(product -> RelatedProduct.builder()
                .id(product.productId())
                .type(RelatedProductType.fromValue(product.productType()))
                .relationshipType(RelatedProductRelationType.fromValue(product.relationshipType()))
                .productOrderItemId(product.orderItemId())
                .productSpecification(ProductSpecification.builder().id(product.specificationId()).build())
                .realisingService(List.of(RealisingService.builder().id(product.realizingServiceId()).href(product.realizingServiceHref()).build()))
                .build()).toList();

        List<RelatedProductOrderItem> relatedProductOrderItems = productsList.stream().map(product -> RelatedProductOrderItem.builder()
                .id(product.orderItemId())
                .action(product.orderItemAction())
                .build()
        ).toList();

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
        orchestrationPlanNode.setRelatedProductOrderItem(relatedProductOrderItems);
        orchestrationPlanNode.setRelatedProduct(relatedProducts);
        orchestrationPlan.getOrchestrationPlanNodes().add(orchestrationPlanNode);
        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the node with id {string} has the following error messages:")
    public void theNodeWithIdHasErrorMessagesWithTheFollowingData(String nodeId, List<ErrorMessageRecord> errorMessageRecordList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeById(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));
        orchestrationPlan.getOrchestrationPlanNodes().remove(orchestrationPlanNode);

        List<OrchestrationNodeErrorMessage> errorMessageList = errorMessageRecordList.stream().map(errorMessage -> OrchestrationNodeErrorMessage.builder()
                .message(errorMessage.message())
                .reason(errorMessage.reason())
                .code(errorMessage.code())
                .timeStamp(errorMessage.timestamp())
                .build()
        ).toList();

        orchestrationPlanNode.setErrorMessage(errorMessageList);

        orchestrationPlan.getOrchestrationPlanNodes().add(orchestrationPlanNode);
        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @When("the system consumes delivery order item status event from topic {string} for orchestration plan node with id {string} and state {string}")
    public void theSystemConsumesDeliveryOrderItemStatusEventForOrchestrationPlanNodeWithIdAndState(String topic, String nodeId, String state) {
        DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent = DeliveryOrderItemStatusEvent.builder()
                .event(DeliveryOrderItemStatusPayloadEvent.builder()
                        .orderItemRef(OrderItemRef.builder()
                                .realizingResourceRef(RealizingResourceRef.builder().id("realizingServiceId").href("realizingServiceHref").build())
                                .orchestrationNodeId(nodeId)
                                .deliveryStatusMapping(DeliveryStatusMapping.builder().nodeStatus(DeliveryStatusMapping.NodeStatusEnum.fromValue(state)).build())
                                .build())
                        .build())
                .eventType(DeliveryOrderItemStatusEvent.class.getSimpleName()).build();

        kafkaTemplate.send(topic, deliveryOrderItemStatusEvent);
    }

    @When("the system consumes delivery status event from topic {string} for orchestration plan node with id {string} and state {string} and service order state {string}")
    public void theSystemConsumesDeliveryStatusEventFromTopicForOrchestrationPlanNodeWithIdAndStateAndServiceOrderState(String topic, String nodeId, String nodeState, String serviceState) {
        DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent = DeliveryOrderItemStatusEvent.builder()
                .event(DeliveryOrderItemStatusPayloadEvent.builder()
                        .orderItemRef(OrderItemRef.builder()
                                .orchestrationNodeId(nodeId)
                                .deliveryStatusMapping(DeliveryStatusMapping.builder()
                                        .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.fromValue(nodeState))
                                        .build())
                                .realizingResourceRef(RealizingResourceRef.builder()
                                        .id(UUID.randomUUID().toString())
                                        .href(RandomStringUtils.randomAlphabetic(10))
                                        .build())
                                .build())
                        .build())
                .eventType(DeliveryOrderItemStatusEvent.class.getSimpleName()).build();

        deliveryOrderItemStatusEventHandler.handleEvent(deliveryOrderItemStatusEvent, new HashMap<>());
    }

    @And("an installed product with id {string} exists in the CPIB, associated with order id {string} and order item id {string}, having state {string} and operational state {string}, and the following characteristics:")
    public void theCPIBHasInstalledProductWihIdAssociatedWithOrderIdAndOrderItemIdInStateAndOperationalStateAndProductCharacteristicsWithTheFollowingData(String productId, String orderId, String orderItemId, String state, String operationalState, List<ProductCharacteristicRecord> productCharacteristicsList) throws JsonProcessingException {
        List characteristics = productCharacteristicsList.stream().map(characteristic -> StringCharacteristic.builder().value(characteristic.value()).name(characteristic.name()).valueType(characteristic.valueType()).build()).toList();
        Product product = PhysicalProduct.builder().id(productId).status(ProductStatusType.fromValue(state)).operationalStatus(ProductOperationalStatusType.fromValue(operationalState)).atType("PhysicalProduct").productCharacteristic(characteristics).build();
        wireMockServer.stubFor(get(urlPathEqualTo(PRODUCT_MANAGEMENT_URL)).withQueryParam("productOrderItem.productOrderId", equalTo(orderId)).withQueryParam("productOrderItem.orderItemId", equalTo(orderItemId)).willReturn(ok().withBody("[%s]".formatted(objectMapper.writeValueAsString(product))).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        wireMockServer.stubFor(patch(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}"))).withPathParam("id", equalTo(productId)).willReturn(like(ResponseDefinitionBuilder.jsonResponse(product))));
    }

    @When("the system consumes delivery status event from topic {string} for orchestration plan node with id {string} and state {string} and service order state {string} and the following service characteristics:")
    public void theSystemConsumesDeliveryStatusEventFromTopicForOrchestrationPlanNodeWithIdAndStateAndServiceOrderStateAndServiceCharacteristicsWithTheFollowingData(String topic, String nodeId, String nodeState, String serviceState, List<ServiceCharacteristicRecord> serviceCharacteristicsList) {
        List<Characteristic> serviceCharacteristics = serviceCharacteristicsList.stream()
                .<Characteristic>map(serviceCharacteristic -> com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic.builder().name(serviceCharacteristic.name()).value(serviceCharacteristic.value()).valueType(serviceCharacteristic.valueType()).build()).toList();

        DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent = DeliveryOrderItemStatusEvent.builder()
                .event(DeliveryOrderItemStatusPayloadEvent.builder()
                        .orderItemRef(OrderItemRef.builder()
                                .orchestrationNodeId(nodeId)
                                .deliveryStatusMapping(DeliveryStatusMapping.builder()
                                        .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.fromValue(nodeState))
                                        .build())
                                .realizingResourceRef(RealizingResourceRef.builder()
                                        .id(UUID.randomUUID().toString())
                                        .href(RandomStringUtils.randomAlphabetic(10))
                                        .build())
                                .orderItemCharacteristics(serviceCharacteristics)
                                .build())
                        .build())
                .eventType(DeliveryOrderItemStatusEvent.class.getSimpleName()).build();

        deliveryOrderItemStatusEventHandler.handleEvent(deliveryOrderItemStatusEvent, new HashMap<>());
    }

    @And("the product with id {string} has the following characteristics:")
    public void theProductWithIdHasCharacteristicsWithTheFollowingData(String productId, List<ProductCharacteristicRecord> productCharacteristicsList) {
        Set<Characteristic> productCharacteristics = productCharacteristicsList.stream().map(productCharacteristic -> StringCharacteristic.builder().name(productCharacteristic.name()).value(productCharacteristic.value()).valueType(productCharacteristic.valueType()).build()).collect(Collectors.toSet());

        Query query = new Query();
        query.addCriteria(Criteria.where("orchestrationPlanNodes").elemMatch(Criteria.where("relatedProduct").elemMatch(Criteria.where("id").is(productId))));
        OrchestrationPlan plan = mongoTemplate.findOne(query, OrchestrationPlan.class);
        assertThat(plan).isNotNull();
        OrchestrationPlanNode node = plan.getOrchestrationPlanNodes().stream().filter(orchestrationPlanNode -> orchestrationPlanNode.getRelatedProduct().stream().anyMatch(relatedProduct -> productId.equals(relatedProduct.getId()))).findFirst().orElseThrow();
        RelatedProduct product = node.getRelatedProduct().stream().filter(relatedProduct -> productId.equals(relatedProduct.getId())).findAny().orElseThrow();
        product.setProductCharacteristic(productCharacteristics);

        mongoTemplate.save(plan);
    }

    @Then("the system installed product with id {string} state will be {string} and operational state will be {string} and has the following characteristics:")
    public void theSystemInstalledProductWithIdStateWillBeAndOperationalStateWillBeAndProductCharacteristicsHasTheFollowingData(String productId, String state, String operationalState, List<ProductCharacteristicRecord> productCharacteristicsList) {
        List productCharacteristics = productCharacteristicsList.stream().map(productCharacteristic -> StringCharacteristic.builder().name(productCharacteristic.name()).value(productCharacteristic.value()).valueType(productCharacteristic.valueType()).build()).toList();
        Product product = PhysicalProduct.builder().id(productId).atType("PhysicalProduct").status(ProductStatusType.fromValue(state)).operationalStatus(ProductOperationalStatusType.fromValue(operationalState)).productOrderItem(List.of()).productCharacteristic(productCharacteristics).build();
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            wireMockServer.verify(1, patchRequestedFor(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}"))).withPathParam("id", equalTo(productId)).withRequestBody(equalToJson(objectMapper.writeValueAsString(product), true, true)));
        });
    }

    @And("the CPIB has installed product wih id {string} associated with order id {string} and order item id {string} and invalid product type")
    public void theCPIBHasInstalledProductWihIdAssociatedWithOrderIdAndOrderItemIdAndInvalidProductType(String productId, String orderId, String orderItemId) {
        Product product = Product.builder().id(productId).status(ProductStatusType.ACTIVE).operationalStatus(ProductOperationalStatusType.ACTIVE).atType("invalid").build();
        wireMockServer.stubFor(get(urlPathEqualTo(PRODUCT_MANAGEMENT_URL)).withQueryParam("productOrderItem.productOrderId", equalTo(orderId)).withQueryParam("productOrderItem.orderItemId", equalTo(orderItemId)).willReturn(like(ResponseDefinitionBuilder.jsonResponse(List.of(product)))));

        wireMockServer.stubFor(post("/processManagement/v1/processFlow").willReturn(ok().withBody("{}").withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @Then("Fallout incident will be created with error code {string}")
    public void falloutIncidentWillBeCreatedWithErrorCode(String errorCode) {
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            wireMockServer.verify(1, postRequestedFor(urlEqualTo("/processManagement/v1/processFlow")).withRequestBody(containing(errorCode)));
        });
    }

    @When("the system consumes delivery status event from topic {string} for orchestration plan node with id {string} and state {string} with the following realizing services:")
    public void theSystemConsumesDeliveryStatusEventFromTopicForOrchestrationPlanNodeWithIdAndStateWithTheFollowingData(String topicName, String nodeId, String state, List<OrchestrationPlanNodeRecord> realisingServiceList) {
        DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent = DeliveryOrderItemStatusEvent.builder()
                .event(DeliveryOrderItemStatusPayloadEvent.builder()
                        .orderItemRef(OrderItemRef.builder()
                                .orchestrationNodeId(nodeId)
                                .deliveryStatusMapping(DeliveryStatusMapping.builder().nodeStatus(DeliveryStatusMapping.NodeStatusEnum.fromValue(state)).build())
                                .realizingResourceRef(RealizingResourceRef.builder()
                                        .id(realisingServiceList.get(0).realizingServiceId())
                                        .href(realisingServiceList.get(0).realizingServiceHref())
                                        .build())
                                .build())
                        .build())
                .eventType(DeliveryOrderItemStatusEvent.class.getSimpleName())
                .build();

        try {
            deliveryOrderItemStatusEventHandler.handleEvent(deliveryOrderItemStatusEvent, new HashMap<>());
        } catch (Exception ignored) {
            // This try block always throws because the service order call is not stubbed.
            // We don't care about the response; we only need to verify the call was made.
            // We'll keep this try-catch to allow later assertion that the call occurred.
        }
    }

    @And("node {string} is updated with the following realizing service data:")
    public void nodeIsUpdatedWithTheFollowingRealizingServiceData(String nodeId, List<OrchestrationPlanNodeRecord> nodeDetails) {

        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(planNode -> planNode.getId().equals(nodeId)).findFirst().get();

        nodeDetails.forEach(nodeDetailsItem -> {
                    assertThat(orchestrationPlanNode.getRelatedProduct().get(0).getRealisingService().get(0).getId()).isEqualTo(nodeDetailsItem.realizingServiceId());
                    assertThat(orchestrationPlanNode.getRelatedProduct().get(0).getRealisingService().get(0).getHref()).isEqualTo(nodeDetailsItem.realizingServiceHref());
                    assertThat(orchestrationPlanNode.getRelatedServiceOrder().getId()).isEqualTo(nodeDetailsItem.relatedServiceOrderId());
                    assertThat(orchestrationPlanNode.getRelatedServiceOrder().getOrderItemId()).isEqualTo(nodeDetailsItem.relatedServiceOrderItemId());
                }
        );
    }

    @And("the node with id {string} should have the following error messages:")
    public void theNodeWithIdShouldHaveErrorMessages(String nodeId, List<ErrorMessageRecord> errorMessageList) {

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {

            OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
            OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeById(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));

            assertEquals(errorMessageList.size(), orchestrationPlanNode.getErrorMessage().size());

            IntStream.range(0, errorMessageList.size()).forEach(i -> {
                assertEquals(errorMessageList.get(i).code(), orchestrationPlanNode.getErrorMessage().get(i).getCode());
                assertEquals(errorMessageList.get(i).message(), orchestrationPlanNode.getErrorMessage().get(i).getMessage());
                assertEquals(errorMessageList.get(i).reason(), orchestrationPlanNode.getErrorMessage().get(i).getReason());
                if (errorMessageList.get(i).timestamp().equals(Instant.parse("2000-01-01T00:00:00Z"))) {
                    assertTrue(Instant.now().toEpochMilli() > orchestrationPlanNode.getErrorMessage().get(i).getTimeStamp().toEpochMilli());
                } else {
                    assertEquals(errorMessageList.get(i).timestamp(), orchestrationPlanNode.getErrorMessage().get(i).getTimeStamp());
                }
            });
        });
    }

    @And("the delivery order item state change event for orchestration plan node with id {string} and state {string} has the following factory details:")
    public void theDeliveryOrderItemStateChangeEventForOrchestrationPlanNodeWithIdAndStateHasTheFollowingFactoryDetails(String nodeId, String status, List<DeliveryOrderItemRecord> deliveryOrderItemRecords) {
        deliveryOrderItemStatusPayloadEvent = DeliveryOrderItemStatusPayloadEvent.builder()
                .factoryOrderId(deliveryOrderItemRecords.get(0).factoryOrderId())
                .orderItemRef(OrderItemRef.builder().factoryOrderItemId(deliveryOrderItemRecords.get(0).factoryOrderItemId())
                        .orchestrationNodeId(nodeId)
                        .deliveryStatusMapping(DeliveryStatusMapping.builder()
                                .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.fromValue(status))
                                .build())
                        .build())
                .build();
    }

    @When("the system consumes delivery status event from topic {string}")
    public void theSystemConsumesDeliveryStatusEventFromTopic(String topicName) {

        DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent = DeliveryOrderItemStatusEvent.builder()
                .event(deliveryOrderItemStatusPayloadEvent).build();
        try {
            deliveryOrderItemStatusEventHandler.handleEvent(deliveryOrderItemStatusEvent, new HashMap<>());
        } catch (Exception ignored) {
            // This try block always throws because the service order call is not stubbed.
            // We don't care about the response; we only need to verify the call was made.
            // We'll keep this try-catch to allow later assertion that the call occurred.
        }
    }

    @Then("node {string} is updated with the following related supply chain order data:")
    public void nodeIsUpdatedWithTheFollowingRelatedSupplyChainOrderData(String
                                                                                 nodeId, List<OrchestrationPlanNodeRecord> orchestrationPlanNodeRecords) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(planNode -> planNode.getId().equals(nodeId)).findFirst().get();
        assertThat(orchestrationPlanNode.getRelatedSupplyChainOrder().getId()).isEqualTo(orchestrationPlanNodeRecords.get(0).relatedSupplyChainOrderId());
        assertThat(orchestrationPlanNode.getRelatedSupplyChainOrder().getOrderItemId()).isEqualTo(orchestrationPlanNodeRecords.get(0).relatedSupplyChainOrderItemId());
    }


    @And("the delivery order item state change event for orchestration plan node with id {string} and state {string} has the following  details:")
    public void theDeliveryOrderItemStateChangeEventForOrchestrationPlanNodeWithIdAndStateHasTheFollowingDetails(String nodeId, String status, List<DeliveryOrderItemRecord> deliveryOrderItemRecords) {
        deliveryOrderItemStatusPayloadEvent = DeliveryOrderItemStatusPayloadEvent.builder()
                .factoryOrderId(deliveryOrderItemRecords.get(0).factoryOrderId())
                .orderItemRef(OrderItemRef.builder()
                        .realizingResourceRef(RealizingResourceRef.builder()
                                .id(deliveryOrderItemRecords.get(0).realizingServiceId())
                                .href(deliveryOrderItemRecords.get(0).realizingServiceHref())
                                .build())
                        .factoryOrderItemId(deliveryOrderItemRecords.get(0).factoryOrderItemId())
                        .orchestrationNodeId(nodeId)
                        .deliveryStatusMapping(DeliveryStatusMapping.builder()
                                .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.fromValue(status))
                                .build())
                        .build())
                .build();
    }
}
