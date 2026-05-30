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

import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.OrchestrationPlanNodeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.ServiceCharacteristicRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RealisingService;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

public class SendingRealizingServiceBeforeFiringDeliveryStatusEvent {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Then("the system should publish a Delivery Status event to topic {string} for the orchestration plan node with ID {string}, updating its state to {string} with the following realizing service details:")
    public void theSystemShouldPublishADeliveryStatusEventToTopicForTheOrchestrationPlanNodeWithIDUpdatingItsStateToWithTheFollowingRealizingServiceDetails(
            String topicName, String nodeId, String state,
            List<OrchestrationPlanNodeRecord> realizingServiceDataList) {

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Map<String, Object> headers = new HashMap<>();
            headers.put(Headers.SOURCE_TOPIC_NAME, KafkaTopic.SERVICE_ORDER_STATE_CHANGE_TOPIC);
            ArgumentCaptor<DeliveryOrderItemStatusPayloadEvent> deliveryStatusEventArgumentCaptor = ArgumentCaptor
                    .forClass(DeliveryOrderItemStatusPayloadEvent.class);
            Mockito.verify(eventPublisher, times(1)).publishEvent(eq(CDCEvent.fromTopicName(topicName)),
                    deliveryStatusEventArgumentCaptor.capture(), eq(headers));

            OrderItemRef orderItemRef = deliveryStatusEventArgumentCaptor.getValue()
                    .getOrderItemRef();
            assertThat(orderItemRef.getOrchestrationNodeId()).isEqualTo(nodeId);
            assertThat(orderItemRef.getDeliveryStatusMapping().getNodeStatus())
                    .isEqualTo(DeliveryStatusMapping.NodeStatusEnum.fromValue(state));

            List<RealisingService> realisingServices = realizingServiceDataList.stream()
                    .map(realizingService -> RealisingService.builder()
                            .id(realizingService.realizingServiceId())
                            .href(realizingService.realizingServiceHref())
                            .build())
                    .toList();

            assertThat(orderItemRef.getRealizingResourceRef()
                    .getId()).isEqualTo(realisingServices.get(0).getId());
            assertThat(orderItemRef.getRealizingResourceRef()
                    .getHref()).isEqualTo(realisingServices.get(0).getHref());

        });
    }

    @When("the system should consume a serviceOrderStateChange event from topic {string} for service order ID {string} and order item ID {string} with status update {string} and the following service characteristics:")
    public void theSystemShouldConsumeServiceOrderStateChangeEventFromTopicForServiceOrderIdAndOrderItemIdWithStatusUpdateAndHasTheFollowingServiceCharacteristics(
            String topicName, String serviceOrderId, String orderItemId, String status,
            List<ServiceCharacteristicRecord> serviceOrderList) {
        List<Characteristic> serviceCharacteristics = serviceOrderList.stream()
                .<Characteristic>map(serviceCharacteristic -> StringCharacteristic.builder()
                        .name(serviceCharacteristic.name())
                        .value(serviceCharacteristic.value())
                        .valueType(serviceCharacteristic.valueType())
                        .build())
                .toList();

        List<Service> services = serviceOrderList.stream().map(serviceOrder -> Service.builder()
                .id(Objects.nonNull(serviceOrder.serviceId()) ? serviceOrder.serviceId() : null)
                .href(Objects.nonNull(serviceOrder.serviceHref()) ? serviceOrder.serviceHref() : null)
                .serviceCharacteristic(serviceCharacteristics)
                .build()).toList();

        ServiceOrderEvent serviceOrderEvent = ServiceOrderEvent.builder()
                .event(ServiceOrderPayloadEvent.builder()
                .serviceOrder(ServiceOrder.builder()
                        .id(serviceOrderId)
                        .serviceOrderItem(List.of(ServiceOrderItem.builder()
                                .id(orderItemId)
                                .state(ServiceOrderItem.State.fromValue(status))
                                .service(services.get(0))
                                .build()))
                        .build())
                .build())
                .eventType(EventType.SERVICE_ORDER_STATE_CHANGE_EVENT.getValue())

                .build();

        kafkaTemplate.send(topicName, serviceOrderEvent);
    }
}
