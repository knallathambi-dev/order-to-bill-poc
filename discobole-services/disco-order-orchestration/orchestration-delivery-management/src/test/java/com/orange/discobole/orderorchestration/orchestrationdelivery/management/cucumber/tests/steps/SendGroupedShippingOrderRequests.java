// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.AddressCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ProductOrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShipmentRefOrValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEventPayload;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.DeliveryOrderItemStatusRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.DeliveryOrderRefRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.OrderItemCharacteristicRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.OrderItemRefRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.OrchestrationPlanNodeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.RelatedPartyRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.DeliveryOrderEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.impl.ShippingOrderStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ValidityCharacteristic;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.awaitility.Awaitility;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SendGroupedShippingOrderRequests {

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ShippingOrderStateChangeEventHandler shippingOrderStateChangeEventHandler;

    @Autowired
    private DeliveryOrderEventHandler deliveryOrderEventHandler;

    private String deliveryOrderId;
    private String productOrderId;
    private String deliveryFactoryType;
    private final Map<String, OrderItemRef> orderItemRefs = new LinkedHashMap<>();
    private List<RelatedParty> relatedParties = new ArrayList<>();
    private ShippingOrderStateChangeEventPayload shippingOrderStateChangeEventPayload;

    @Before
    public void init() {
        deliveryOrderRepository.deleteAll();
        orderItemRefs.clear();
        relatedParties = new ArrayList<>();
        shippingOrderStateChangeEventPayload = null;
        if (Mockito.mockingDetails(eventPublisher).isMock()) {
            Mockito.reset(eventPublisher);
        }
    }

    // === Steps for SendGroupedShippingOrderRequest.feature ===

    @Given("a delivery order with id {string} and product order id {string} and the {string} delivery factory type")
    public void aDeliveryOrderWithIdAndProductOrderId(String aDeliveryOrderId, String aProductOrderId, String deliveryFactoryType) {
        this.deliveryOrderId = aDeliveryOrderId;
        this.productOrderId = aProductOrderId;
        this.deliveryFactoryType = deliveryFactoryType;
        this.orderItemRefs.clear();
        this.relatedParties = new ArrayList<>();
    }

    @And("the delivery order has the following order items:")
    public void theDeliveryOrderHasTheFollowingOrderItems(List<OrderItemRefRecord> records) {
        records.forEach(r -> orderItemRefs.put(
                r.orderItemId(),
                OrderItemRef.builder()
                        .productOrderItemId(r.orderItemId())
                        .action(r.action())
                        .quantity(Integer.parseInt(r.quantity()))
                        .productSpecificationRef(ProductSpecificationRef.builder()
                                .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder()
                                        .id(r.serviceSpecificationId())
                                        .build()))
                                .build())
                        .build()));
    }

    @And("order item {string} has the following characteristics:")
    public void orderItemHasTheFollowingCharacteristics(String orderItemId, List<OrderItemCharacteristicRecord> records) {
        OrderItemRef ref = orderItemRefs.get(orderItemId);
        ref.setOrderItemCharacteristics(buildDtoCharacteristics(records));
    }

    @And("the delivery order has the following related parties:")
    public void theDeliveryOrderHasTheFollowingRelatedParties(List<RelatedPartyRecord> relatedPartyRecords) {
        relatedParties = relatedPartyRecords.stream()
                .map(r -> RelatedParty.builder().id(r.id()).name(r.name()).build())
                .toList();
    }

    @When("delivery management consumes the delivery order event")
    public void deliveryManagementConsumesTheDeliveryOrderEvent() {
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id(deliveryOrderId)
                .productOrderId(productOrderId)
                .relatedParty(relatedParties)
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.fromValue(deliveryFactoryType))
                        .build())
                .orderItemRef(new ArrayList<>(orderItemRefs.values()))
                .build();

        DeliveryOrderEvent event = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        deliveryOrderEventHandler.handleEvent(event);
    }

    @And("the delivery order {string} will have factory order id {string}")
    public void theDeliveryOrderWillHaveFactoryOrderId(String aDeliveryOrderId, String factoryOrderId) {
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            Optional<DeliveryOrder> saved = deliveryOrderRepository.findById(aDeliveryOrderId);
            assertThat(saved).isPresent();
            assertThat(saved.get().getFactoryOrderId()).isEqualTo(factoryOrderId);
        });
    }

    @And("order item {string} in delivery order {string} will have factory order item id {string}")
    public void orderItemInDeliveryOrderWillHaveFactoryOrderItemId(String productOrderItemId, String aDeliveryOrderId, String factoryOrderItemId) {
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            Optional<DeliveryOrder> saved = deliveryOrderRepository.findById(aDeliveryOrderId);
            assertThat(saved).isPresent();
            Optional<OrderItemRef> ref = saved.get().getOrderItemRef().stream()
                    .filter(r -> productOrderItemId.equals(r.getProductOrderItemId()))
                    .findFirst();
            assertThat(ref).isPresent();
            assertThat(ref.get().getFactoryOrderItemId()).isEqualTo(factoryOrderItemId);
        });
    }

    // === WireMock request verification steps ===

    @Then("delivery management will send a shipping order request with the following order items:")
    public void deliveryManagementWillSendAShippingOrderRequestWithTheFollowingOrderItems(List<OrchestrationPlanNodeRecord> orderItems) {
        List<ShippingOrderItem> shippingOrderItems = orderItems.stream().map(orderItem -> {
            DateCharacteristic productCharacteristic = DateCharacteristic.builder()
                    .name("requested delivery date")
                    .value(OffsetDateTime.parse(orderItem.requestedDeliveryDate()))
                    .build();
            return ShippingOrderItem.builder()
                    .action(ShippingOrderItem.ShippingOrderItemActionType.fromValue(orderItem.action()))
                    .productOrderItem(ProductOrderItemRef.builder()
                            .id(orderItem.orderItemId())
                            .productOrderId(orderItem.orderId())
                            .build())
                    .quantity(orderItem.quantity())
                    .shipment(ShipmentRefOrValue.builder()
                            .requestedDeliveryDate(productCharacteristic.getValue().toInstant())
                            .build())
                    .build();
        }).toList();

        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            wireMockServer.verify(1, postRequestedFor(urlEqualTo("/shippingOrder/v1/shippingOrder"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(Map.of(
                            "shippingOrderItem", shippingOrderItems)), true, true)));
        });
    }

    @And("the shipping order request will have the following related parties:")
    public void theShippingOrderRequestWillHaveTheFollowingRelatedParties(List<RelatedPartyRecord> relatedPartyRecords) {
        List<RelatedParty> parties = relatedPartyRecords.stream()
                .map(r -> RelatedParty.builder().id(r.id()).name(r.name()).build())
                .toList();
        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            wireMockServer.verify(1, postRequestedFor(urlEqualTo("/shippingOrder/v1/shippingOrder"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(Map.of(
                            "relatedParty", parties)), true, true)));
        });
    }

    @And("the shipping order request will have the following characteristics:")
    public void theShippingOrderRequestWillHaveTheFollowingCharacteristics(List<OrderItemCharacteristicRecord> records) {
        // Uses servicecharacteristic model types matching the serialized ShippingOrderCreate request body
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic> characteristicList =
                records.stream().map(item -> {
                    com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic stringCharacteristic =
                            StringCharacteristic.builder()
                                    .atType(StringCharacteristic.class.getSimpleName())
                                    .name(item.name())
                                    .value(item.value())
                                    .build();
                    if (item.type() != null) {
                        return switch (item.type().toLowerCase()) {
                            case "validitycharacteristic" ->
                                    (com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic)
                                            ValidityCharacteristic.builder()
                                                    .name(item.name())
                                                    .atType(ValidityCharacteristic.class.getSimpleName())
                                                    .value(com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ValidityValue.builder()
                                                            .value(item.value() != null ? Integer.valueOf(item.value()) : null)
                                                            .unitOfMeasure(item.unitOfMeasure())
                                                            .build())
                                                    .build();
                            case "addresscharacteristic" ->
                                    com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.AddressCharacteristic.builder()
                                            .atType(com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.AddressCharacteristic.class.getSimpleName())
                                            .name(item.name())
                                            .addressId(item.addressId())
                                            .country(item.country())
                                            .city(item.city())
                                            .streetName(item.streetName())
                                            .postcode(item.postCode())
                                            .build();
                            case "datecharacteristic" ->
                                    com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.DateCharacteristic.builder()
                                            .atType(com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.DateCharacteristic.class.getSimpleName())
                                            .name(item.name())
                                            .value(OffsetDateTime.parse(item.value()))
                                            .build();
                            default -> stringCharacteristic;
                        };
                    } else {
                        return stringCharacteristic;
                    }
                }).toList();

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            wireMockServer.verify(1, postRequestedFor(urlEqualTo("/shippingOrder/v1/shippingOrder"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(Map.of(
                            "shippingOrderCharacteristic", characteristicList)), true, true)));
        });
    }

    // === Steps for EnhanceShippingDeliveryStatusUpdate.feature ===

    @And("the system has a delivery order with factory order id {string} for plan {string} with the following order item refs:")
    public void theSystemHasADeliveryOrderWithFactoryOrderId(String factoryOrderId, String planId, List<DeliveryOrderRefRecord> refs) {
        List<OrderItemRef> orderItemRefList = refs.stream()
                .map(r -> OrderItemRef.builder()
                        .productOrderItemId(r.productOrderItemId())
                        .orchestrationNodeId(r.orchestrationNodeId())
                        .productSpecificationRef(ProductSpecificationRef.builder()
                                .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder()
                                        .id(r.serviceSpecificationId())
                                        .build()))
                                .build())
                        .action(r.action())
                        .build())
                .toList();
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id(UUID.randomUUID().toString())
                .factoryOrderId(factoryOrderId)
                .orderItemRef(new ArrayList<>(orderItemRefList))
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .build())
                .build();
        deliveryOrderRepository.save(deliveryOrder);
    }

    @Then("delivery management will publish delivery order item status events with the following data:")
    public void deliveryManagementWillPublishDeliveryOrderItemStatusEvents(List<DeliveryOrderItemStatusRecord> expected) {
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            ArgumentCaptor<List<DeliveryOrderItemStatusPayloadEvent>> captor = ArgumentCaptor.forClass(List.class);
            Mockito.verify(eventPublisher, times(1)).publishEvents(
                    eq(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT),
                    captor.capture());

            List<DeliveryOrderItemStatusPayloadEvent> events = captor.getValue();
            Map<String, DeliveryOrderItemStatusPayloadEvent> eventMap = events.stream()
                    .collect(Collectors.toMap(
                            e -> e.getOrderItemRef().getOrchestrationNodeId(),
                            e -> e));

            assertThat(events).hasSameSizeAs(expected);
            expected.forEach(row -> {
                DeliveryOrderItemStatusPayloadEvent event = eventMap.get(row.orchestrationNodeId());
                assertThat(event).as("Event for orchestration node %s", row.orchestrationNodeId()).isNotNull();
                assertThat(event.getOrderItemRef().getDeliveryStatusMapping().getDeliveryStatus().value())
                        .isEqualToIgnoringCase(row.deliveryStatus());
                assertThat(event.getOrderItemRef().getDeliveryStatusMapping().getNodeStatus().value())
                        .isEqualTo(row.nodeStatus());
            });
        });
    }

    @And("the node with id {string} has the related supply chain order with id {string} and order item id {string}")
    public void theNodeWithIdHasRelatedSupplyChainOrder(String nodeId, String shippingOrderId, String orderItemId) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findByFactoryOrderId(shippingOrderId)
                .orElse(DeliveryOrder.builder()
                        .id(UUID.randomUUID().toString())
                        .factoryOrderId(shippingOrderId)
                        .orderItemRef(new ArrayList<>())
                        .build());
        if (deliveryOrder.getOrderItemRef() == null) {
            deliveryOrder.setOrderItemRef(new ArrayList<>());
        }
        deliveryOrder.getOrderItemRef().add(OrderItemRef.builder()
                .orchestrationNodeId(nodeId)
                .productOrderItemId(orderItemId)
                .build());
        deliveryOrderRepository.save(deliveryOrder);
    }

    @And("The shipping order state change event for order with id {string} has the following shipping order items")
    public void theShippingOrderStateChangeEventHasTheFollowingShippingOrderItems(String shippingOrderId, List<OrchestrationPlanNodeRecord> orderItems) {
        List<ShippingOrderItem> shippingOrderItems = orderItems.stream().map(orderItem -> {
            DateCharacteristic productCharacteristic = DateCharacteristic.builder()
                    .name("requested delivery date")
                    .value(OffsetDateTime.parse(orderItem.requestedDeliveryDate()))
                    .build();
            return ShippingOrderItem.builder()
                    .id(orderItem.orderItemId())
                    .action(ShippingOrderItem.ShippingOrderItemActionType.fromValue(orderItem.action()))
                    .productOrderItem(ProductOrderItemRef.builder()
                            .id(orderItem.orderItemId())
                            .productOrderId(orderItem.orderId())
                            .build())
                    .quantity(orderItem.quantity())
                    .shipment(ShipmentRefOrValue.builder()
                            .requestedDeliveryDate(productCharacteristic.getValue().toInstant())
                            .build())
                    .status(orderItem.state())
                    .build();
        }).toList();

        ShippingOrder shippingOrder = ShippingOrder.builder()
                .id(shippingOrderId)
                .shippingOrderItem(shippingOrderItems)
                .build();
        shippingOrderStateChangeEventPayload = ShippingOrderStateChangeEventPayload.builder()
                .shippingOrder(shippingOrder)
                .build();
    }

    @When("the system consumes shipping order state change event from the topic {string}")
    public void theSystemConsumesShippingOrderStateChangeEventFromTheTopic(String topicName) {
        ShippingOrderStateChangeEvent event = ShippingOrderStateChangeEvent.builder()
                .event(shippingOrderStateChangeEventPayload)
                .build();
        shippingOrderStateChangeEventHandler.handleEvent(event);
    }

    @Then("Delivery management will send events to topic {string} for orchestration plan nodes with the following data")
    public void deliveryManagementWillSendEventsToTopicForOrchestrationPlanNodesWithTheFollowingData(String topicName, List<OrchestrationPlanNodeRecord> nodes) {
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            ArgumentCaptor<List<DeliveryOrderItemStatusPayloadEvent>> captor = ArgumentCaptor.forClass(List.class);
            Mockito.verify(eventPublisher, times(1)).publishEvents(
                    eq(CDCEvent.fromTopicName(topicName)),
                    captor.capture());

            List<DeliveryOrderItemStatusPayloadEvent> events = captor.getValue();
            Map<String, DeliveryOrderItemStatusPayloadEvent> eventMap = events.stream()
                    .collect(Collectors.toMap(
                            e -> e.getOrderItemRef().getOrchestrationNodeId(),
                            e -> e));

            assertThat(events).hasSameSizeAs(nodes);
            nodes.forEach(node -> {
                DeliveryOrderItemStatusPayloadEvent event = eventMap.get(node.id());
                assertThat(event).as("Event for orchestration node %s", node.id()).isNotNull();
                assertThat(event.getOrderItemRef().getDeliveryStatusMapping().getNodeStatus().value())
                        .isEqualTo(node.state());
            });
        });
    }

    @And("the system will not fire any event for the node with id {string} and its state still be {string}")
    public void theSystemWillNotFireAnyEventForTheNodeWithIdAndItsStateStillBe(String nodeId, String state) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            ArgumentCaptor<List<DeliveryOrderItemStatusPayloadEvent>> captor = ArgumentCaptor.forClass(List.class);
            verify(eventPublisher, atLeastOnce()).publishEvents(any(), captor.capture());
            List<String> allPublishedNodeIds = captor.getAllValues().stream()
                    .flatMap(Collection::stream)
                    .map(e -> e.getOrderItemRef().getOrchestrationNodeId())
                    .toList();
            assertThat(allPublishedNodeIds).isNotEmpty().doesNotContain(nodeId);
        });
    }

    // === Helper methods ===

    public static List<Characteristic> buildDtoCharacteristics(List<OrderItemCharacteristicRecord> records) {
        return records.stream().map(item -> {
            Characteristic stringCharacteristic =
                    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic.builder()
                            .name(item.name())
                            .value(item.value())
                            .build();
            if (item.type() != null) {
                return switch (item.type().toLowerCase()) {
                    case "validitycharacteristic" -> (Characteristic)
                            com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityCharacteristic.builder()
                                    .name(item.name())
                                    .value(ValidityValue.builder()
                                            .value(item.value() != null ? Integer.valueOf(item.value()) : null)
                                            .unitOfMeasure(item.unitOfMeasure())
                                            .validTo(item.validTo() != null ? OffsetDateTime.parse(item.validTo()) : null)
                                            .validFrom(item.validFrom() != null ? OffsetDateTime.parse(item.validFrom()) : null)
                                            .build())
                                    .build();
                    case "addresscharacteristic" -> AddressCharacteristic.builder()
                            .name(item.name())
                            .addressId(item.addressId())
                            .country(item.country())
                            .city(item.city())
                            .streetName(item.streetName())
                            .postcode(item.postCode())
                            .build();
                    case "datecharacteristic" -> DateCharacteristic.builder()
                            .name(item.name())
                            .value(OffsetDateTime.parse(item.value()))
                            .build();
                    default -> stringCharacteristic;
                };
            }
            return stringCharacteristic;
        }).toList();
    }

    @And("order item {string} in delivery order with factory order id {string} will have {string} delivery status and {string} node status")
    public void orderItemInDeliveryOrderWithIdWillHaveDeliveryStatusAndNodeStatus(String orderItemId, String factoryOrderId, String deliveryStatus, String nodeStatus) {
        Optional<DeliveryOrder> deliveryOrder = deliveryOrderRepository.findByFactoryOrderId(factoryOrderId);
        assertThat(deliveryOrder).isPresent();

        Optional<OrderItemRef> dbOrderItemRef = deliveryOrder.get().getOrderItemRef().stream()
                .filter(orderItemRef -> orderItemRef.getProductOrderItemId().equals(orderItemId)).findFirst();
        assertThat(dbOrderItemRef).isPresent();

        assertThat(dbOrderItemRef.get().getDeliveryStatusMapping().getDeliveryStatus()).isEqualTo(DeliveryStatusMapping.DeliveryStatusEnum.fromValue(deliveryStatus));
        assertThat(dbOrderItemRef.get().getDeliveryStatusMapping().getNodeStatus()).isEqualTo(DeliveryStatusMapping.NodeStatusEnum.fromValue(nodeStatus));
    }
}