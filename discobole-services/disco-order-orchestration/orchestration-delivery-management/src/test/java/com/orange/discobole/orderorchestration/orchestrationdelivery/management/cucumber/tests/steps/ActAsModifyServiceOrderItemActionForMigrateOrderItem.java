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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.ProductSpecificationRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.ServiceSpecificationRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ProductOrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShipmentRefOrValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.DeliveryOrderEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.impl.ServiceOrderEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
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
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.common.ContentTypes.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

public class ActAsModifyServiceOrderItemActionForMigrateOrderItem {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryOrderEventHandler deliveryStartEventHandler;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private ServiceOrderEventHandler serviceOrderEventHandler;

    @Autowired
    private EventPublisher eventPublisher;


    private DeliveryOrderPayloadEvent deliveryOrderPayloadEvent;

    private DeliveryOrder deliveryOrder;

    private ServiceOrderPayloadEvent serviceOrderPayloadEvent;

    @Before
    public void init() {
        deliveryOrderRepository.deleteAll();
    }

    @When("the system consumes delivery order event on topic {string} with factory order id {string} and state {string}")
    public void theSystemConsumesDeliveryOrderEventOnTopicWithNodeIdAndState(String topicName, String factoryOrderId,
                                                                             String state) {

        DeliveryOrderEvent deliveryStartEvent = DeliveryOrderEvent.builder()
                .event(deliveryOrderPayloadEvent)
                .eventId("iii")
                .eventTime(Instant.MAX)
                .eventType(EventType.DELIVERY_ORDER_EVENT.getValue())
                .build();

        try {
            deliveryStartEventHandler.handleEvent(deliveryStartEvent);
        } catch (Exception ignored) {
            // This try block always throws because the service order call is not stubbed.
            // We don't care about the response; we only need to verify the call was made.
            // We'll keep this try-catch to allow later assertion that the call occurred.
        }
    }

    @Then("the system will create a service order request with the following service orders:")
    public void theSystemShouldCreateTheFollowingServiceOrders(List<ServiceOrderRecord> serviceOrderRecordList) {
        List<ServiceOrder> serviceOrderRequest = serviceOrderRecordList.stream()
                .map(serviceOrder -> ServiceOrder.builder().serviceOrderItem(List.of(ServiceOrderItem
                        .builder()
                        .action(ServiceOrderItem.Action
                                .fromValue(serviceOrder.serviceOrderItemAction()))
                        .service(Service.builder()
                                .serviceSpecification(
                                        com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification
                                                .builder()
                                                .id(serviceOrder.serviceSpecificationId())
                                                .build())
                                .build())
                        .build())).build())
                .toList();

        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            wireMockServer.verify(1, postRequestedFor(urlEqualTo("/serviceOrdering/v1/serviceOrder"))
                    .withRequestBody(equalToJson(
                            objectMapper.writeValueAsString(serviceOrderRequest.get(0)),
                            true, true)));
        });
    }

    private com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification createServiceSpecification() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.CharacteristicSpecification serviceCharacteristic =
                com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.CharacteristicSpecification.builder()
                        .id("serviceSpec1")
                        .name("volume")
                        .build();
        Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.CharacteristicSpecification> characteristicSpecificationSet = new HashSet<>();
        characteristicSpecificationSet.add(serviceCharacteristic);
        return com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification
                .builder()
                .id("serviceSpec1")
                .specCharacteristic(characteristicSpecificationSet)
                .build();
    }

    @And("shipping order response returns shipping order {string} with the following order items:")
    public void shippingOrderResponseShouldHaveShippingOrderWithTheFollowingDetails(String shippingOrderId, List<OrchestrationPlanNodeRecord> orderItems) throws JsonProcessingException {
        List<ShippingOrderItem> shippingOrderItems = orderItems.stream().map(orderItem -> {
            DateCharacteristic productCharacteristic = DateCharacteristic.builder().name("requested delivery date").value(OffsetDateTime.parse(orderItem.requestedDeliveryDate())).build();
            return ShippingOrderItem.builder()
                    .id(orderItem.id())
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

        ShippingOrder shippingOrder = ShippingOrder.builder().id(shippingOrderId).shippingOrderItem(shippingOrderItems).build();
        wireMockServer.stubFor(post("/shippingOrder/v1/shippingOrder")
                .willReturn(ok().withBody(objectMapper.writeValueAsString(shippingOrder))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }


    @And("service order response returns service order {string} with the following order items:")
    public void serviceOrderResponseShouldHaveServiceOrderWithTheFollowingDetails(String serviceOrderId, List<OrchestrationPlanNodeRecord> orderItems) throws JsonProcessingException {
        List<ServiceOrderItem> serviceOrderItems = orderItems.stream().map(orderItem -> ServiceOrderItem.builder()
                .id(orderItem.id())
                .action(ServiceOrderItem.Action.fromValue(orderItem.action()))
                .quantity(Integer.valueOf(orderItem.quantity()))
                .build()).toList();

        ServiceOrder serviceOrder = ServiceOrder.builder().id(serviceOrderId).serviceOrderItem(serviceOrderItems).build();
        wireMockServer.stubFor(post("/serviceOrdering/v1/serviceOrder")
                .willReturn(ok().withBody(objectMapper.writeValueAsString(serviceOrder))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @Given("The delivery order event for order with id {string} and the delivery factory type is {string} has the following order item refs:")
    public void theDeliveryOrderEventForOrderWithIdHasTheFollowingOrderItemRefs(String id, String deliveryFactoryType, List<DeliveryOrderRefRecord> refs) {
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
                        .orderItemCharacteristics(List.of(StringCharacteristic.builder()
                                .name("volume")
                                .value("1")
                                .build()))
                        .build())
                .toList();

        deliveryOrder = new DeliveryOrder();

        deliveryOrder.setId(id);
        deliveryOrder.setOrderItemRef(new ArrayList<>(orderItemRefList));
        deliveryOrder.setDeliveryFactoryRef(DeliveryFactoryRef.builder()
                .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.fromValue(deliveryFactoryType))
                .build());
    }

    @And("the delivery order request has the following data:")
    public void theDeliveryOrderHasTheFollowingData(List<DeliveryOrderRefRecord> deliveryOrderRefRecords) throws JsonProcessingException {
        DeliveryOrderRefRecord deliveryOrderData = deliveryOrderRefRecords.get(0);

        deliveryOrder.setProductOrderId(deliveryOrderData.productOrderId());
        deliveryOrder.setRequestedDeliveryDate(Instant.parse(deliveryOrderData.requestedDeliveryDate()));
        deliveryOrder.setStartDate(Instant.parse(deliveryOrderData.startDate()));
        deliveryOrder.setOrchestrationPlanId(deliveryOrderData.orchestrationPlanId());


        deliveryOrderPayloadEvent = DeliveryOrderPayloadEvent.builder()
                .deliveryOrder(deliveryOrder)
                .build();

        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification serviceSpecification = createServiceSpecification();
        wireMockServer.stubFor(get("/serviceCatalogManagement/v1/serviceSpecification?id=serviceSpec1")
                .willReturn(ok().withBody(objectMapper.writeValueAsString(List.of(serviceSpecification)))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @And("the service delivery order has the following related parties:")
    public void theServiceDeliveryOrderHasTheFollowingRelatedParties(List<RelatedPartyRecord> relatedPartyRecords) {
        List<RelatedParty> relatedParties = relatedPartyRecords.stream()
                .map(relatedPartyRecord -> RelatedParty.builder()
                        .id(relatedPartyRecord.id())
                        .name(relatedPartyRecord.name())
                        .build())
                .toList();

        deliveryOrder.setRelatedParty(relatedParties);
    }

    @And("order item ref with product order item Id {string} has the following characteristics:")
    public void orderItemRefWithProductOrderItemIdHasTheFollowingCharacteristics(String productOrderItemId, List<OrderItemCharacteristicRecord> orderItemCharacteristicRecords) {
        List<Characteristic> characteristicList = orderItemCharacteristicRecords.stream().map(item -> {
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

        OrderItemRef orderItemRef = deliveryOrder.getOrderItemRef().stream()
                .filter(orderItemRef1 -> Objects.equals(orderItemRef1.getProductOrderItemId(), productOrderItemId))
                .findFirst()
                .orElse(null);

        if (orderItemRef != null) {
            int index = deliveryOrder.getOrderItemRef().indexOf(orderItemRef);

            orderItemRef.setOrderItemCharacteristics(characteristicList);
            deliveryOrder.getOrderItemRef().set(index, orderItemRef);
        }
    }

    @And("the service order request will have the following related parties:")
    public void theServiceOrderRequestWillHaveTheFollowingRelatedParties(List<RelatedPartyRecord> relatedPartyRecords) {
        List<RelatedParty> parties = relatedPartyRecords.stream()
                .map(r -> RelatedParty.builder().id(r.id()).name(r.name()).build())
                .toList();
        Awaitility.await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            wireMockServer.verify(1, postRequestedFor(urlEqualTo("/serviceOrdering/v1/serviceOrder"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(Map.of(
                            "relatedParty", parties)), true, true)));
        });
    }

    @And("the service order request will have the following characteristics:")
    public void theServiceOrderRequestWillHaveTheFollowingCharacteristics(List<OrderItemCharacteristicRecord> records) {
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic> characteristicList =
                records.stream().map(item -> {
                    com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic stringCharacteristic =
                            com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic.builder()
                                    .atType(com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic.class.getSimpleName())
                                    .name(item.name())
                                    .value(item.value())
                                    .build();
                    if (item.type() != null) {
                        return switch (item.type().toLowerCase()) {
                            case "validitycharacteristic" ->
                                    (com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic)
                                            com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ValidityCharacteristic.builder()
                                                    .name(item.name())
                                                    .atType(com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.ValidityCharacteristic.class.getSimpleName())
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

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            ServiceOrder expectedServiceOrder = ServiceOrder.builder()
                    .serviceOrderItem(
                            List.of(ServiceOrderItem.builder()
                                    .service(Service.builder().serviceCharacteristic(characteristicList).build())
                                    .build()))
                    .build();
            // Verify the request was made
            wireMockServer.verify(1, postRequestedFor(urlEqualTo("/serviceOrdering/v1/serviceOrder"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(expectedServiceOrder), true, true)));

        });

    }

    @And("The service order state change event for order with id {string} has the following order items")
    public void theServiceOrderStateChangeEventForOrderWithIdHasTheFollowingOrderItems(String factoryOrderId, List<OrderItemRefRecord> orderItemRefRecords) {
        String serviceOrderItemId = orderItemRefRecords.get(0).orderItemId();
        ServiceOrder serviceOrder = ServiceOrder.builder()
                .id(factoryOrderId)
                .serviceOrderItem(
                        List.of(
                                ServiceOrderItem.builder()
                                        .id(serviceOrderItemId)
                                        .state(ServiceOrderItem.State.fromValue(orderItemRefRecords.get(0).state()))
                                        .serviceRelationship(List.of())
                                        .service(Service.builder().id(UUID.randomUUID().toString()).build()).build()
                        ))
                .build();
        serviceOrderPayloadEvent = ServiceOrderPayloadEvent.builder().serviceOrder(serviceOrder)
                .build();
    }

    @When("the system consumes service order state change event from the topic {string}")
    public void theSystemConsumesServiceOrderStateChangeEventFromTheTopic(String topicName) {
        ServiceOrderEvent serviceOrderEvent = ServiceOrderEvent.builder().event(serviceOrderPayloadEvent).build();
        serviceOrderEventHandler.handleEvent(serviceOrderEvent);
    }

    @Then("delivery management will publish delivery order item status event with the following data:")
    public void deliveryManagementWillPublishDeliveryOrderItemStatusEventWithTheFollowingData(List<DeliveryOrderItemStatusRecord> expected) {
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            ArgumentCaptor<DeliveryOrderItemStatusPayloadEvent> captor = ArgumentCaptor.forClass(DeliveryOrderItemStatusPayloadEvent.class);
            Mockito.verify(eventPublisher, times(1)).publishEvent(
                    eq(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT),
                    captor.capture(),eq (Map.of("source-topic-name"  ,CDCEvent.SERVICE_ORDER_STATE_CHANGE_EVENT.getTopicName())));

            DeliveryOrderItemStatusPayloadEvent event = captor.getValue();

            expected.forEach(row -> {
                assertThat(event).as("Event for orchestration node %s", row.orchestrationNodeId()).isNotNull();
                assertThat(event.getOrderItemRef().getDeliveryStatusMapping().getDeliveryStatus().value())
                        .isEqualToIgnoringCase(row.deliveryStatus());
                assertThat(event.getOrderItemRef().getDeliveryStatusMapping().getNodeStatus().value())
                        .isEqualTo(row.nodeStatus());
            });
        });
    }
}
