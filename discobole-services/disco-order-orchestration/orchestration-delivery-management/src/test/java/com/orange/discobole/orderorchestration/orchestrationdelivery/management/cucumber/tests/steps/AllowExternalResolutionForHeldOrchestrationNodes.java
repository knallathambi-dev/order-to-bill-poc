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

import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ProductOrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEventPayload;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.AbstractIntegrationUtil;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class AllowExternalResolutionForHeldOrchestrationNodes {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @When("The system consumes shippingOrderStateChange event from topic {string} for shipping order id {string} and order item id {string} with status update {string}")
    public void consumingShippingOrderStateChangeEventFromTopicForShippingOrderIdWithStatusUpdate(String topicName,
                                                                                                  String shippingOrderId, String orderItemId, String shippingOrderStatus) {
        ShippingOrderStateChangeEvent event = ShippingOrderStateChangeEvent.builder()
                .eventType(EventType.SHIPPING_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(ShippingOrderStateChangeEventPayload.builder()
                        .shippingOrder(ShippingOrder.builder()
                                .id(shippingOrderId)
                                .shippingOrderItem(List.of(ShippingOrderItem.builder()
                                        .id(orderItemId)
                                        .status(shippingOrderStatus)
                                        .productOrderItem(ProductOrderItemRef.builder()
                                                .id(orderItemId)
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();

        kafkaTemplate.send(topicName, event);
    }

  @Then("Delivery order item status event will be published to topic {string} for orchestration node id {string} with delivery status {string} and node status {string}")
    public void deliveryOrderItemStatusEventWillBePublishedToTopic(
            String topicName, String nodeId, String deliveryStatus, String nodeStatus) {
        KafkaConsumer<String, DeliveryOrderItemStatusEvent> consumer = AbstractIntegrationUtil.createKafkaConsumer(
                topicName,
                UUID.randomUUID().toString(),
                DeliveryOrderItemStatusEvent.class,
                "earliest");
        await()
                .atMost(15, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    ConsumerRecords<String, DeliveryOrderItemStatusEvent> orderItemStatusEventConsumerRecords =
                            consumer.poll(Duration.ofMillis(500));

                    assertThat(orderItemStatusEventConsumerRecords)
                            .anySatisfy(orderItemStatusEventConsumerRecord -> {
                                DeliveryOrderItemStatusEvent event = orderItemStatusEventConsumerRecord.value();
                                assertThat(event.getEvent().getOrderItemRef().getOrchestrationNodeId()).isEqualTo(nodeId);
                                assertThat(event.getEvent().getOrderItemRef().getDeliveryStatusMapping().getDeliveryStatus().value())
                                        .isEqualTo(deliveryStatus);
                                assertThat(event.getEvent().getOrderItemRef().getDeliveryStatusMapping().getNodeStatus().value())
                                        .isEqualTo(nodeStatus);
                            });
                });
    }

    @When("The system consumes serviceOrderStateChange event from topic {string} for service order id {string} and order item id {string} with status update {string}")
    public void consumingServiceOrderStateChangeEventFromTopicForServiceOrderIdAndOrderItemIdWithStatusUpdate(
            String topicName, String serviceOrderId, String orderItemId, String status) {
        ServiceOrderEvent serviceOrderEvent = ServiceOrderEvent.builder()
                .eventType(EventType.SERVICE_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(ServiceOrderPayloadEvent
                .builder()
                .serviceOrder(ServiceOrder.builder()
                        .id(serviceOrderId)
                        .serviceOrderItem(List.of(ServiceOrderItem.builder()
                                .id(orderItemId)
                                .state(ServiceOrderItem.State.fromValue(status))
                                .service(Service.builder()
                                        .id("id")
                                        .href("href")
                                        .serviceCharacteristic(List.of())
                                        .build())
                                .build()))
                        .build())
                .build()).build();

        kafkaTemplate.send(topicName, serviceOrderEvent);
    }
}
