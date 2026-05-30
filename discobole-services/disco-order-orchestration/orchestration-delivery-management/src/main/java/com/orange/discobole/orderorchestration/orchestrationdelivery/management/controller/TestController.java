// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.controller;


import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEventPayload;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.scheduler.ServiceOrderStateChangeServiceMockScheduler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.scheduler.ShippingOrderStateChangeServiceMockScheduler;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/cood/test")
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TestController {

    private final ServiceOrderStateChangeServiceMockScheduler serviceOrderStateChangeServiceMockScheduler;

    private final ShippingOrderStateChangeServiceMockScheduler shippingOrderStateChangeServiceMockScheduler;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/enable-service-order-state-change-job")
    public String enableServiceOrderStateChangeServiceMock() {
        serviceOrderStateChangeServiceMockScheduler.setEnabled(true);
        return "Job state enabled.";
    }

    @PostMapping("/disable-service-order-state-change-job")
    public String disableServiceOrderStateChangeServiceMock() {
        serviceOrderStateChangeServiceMockScheduler.setEnabled(false);
        return "Job state disabled.";
    }

    // this endpoint is used in the QA regression suite used by bruno.
    @PostMapping(value = "/publishEvent/serviceOrder")
    public String publishServiceOrder(@RequestBody ServiceOrder serviceOrder) {
        log.info("trigger the service order event");
        ServiceOrderEvent serviceOrderEvent = getProductOrderEvent(serviceOrder);
        String partitionKey = serviceOrder.getId();

        Message<ServiceOrderEvent> message = MessageBuilder.withPayload(serviceOrderEvent)
                .setHeader(MessageHeaders.CONTENT_TYPE, "application/json") // Set the content type header
                .setHeader("partitionKey", partitionKey)
                .build();

        kafkaTemplate.send(KafkaTopic.SERVICE_ORDER_STATE_CHANGE_TOPIC, partitionKey, message.getPayload());
        log.info("event service is published");

        return "success";
    }

    // this endpoint is used in the QA regression suite used by bruno.
    @PostMapping(value = "/publishEvent/shippingOrder")
    public String publishShippingOrder(@RequestBody ShippingOrder shippingOrder) {
        log.info("trigger the shipping order event");

        ShippingOrderStateChangeEvent event = getShippingOrderEvent(shippingOrder);
        kafkaTemplate.send(KafkaTopic.SHIPPING_ORDER_STATE_CHANGE_TOPIC, event);

        log.info("shipping order event is published");

        return "success";
    }

    private static ShippingOrderStateChangeEvent getShippingOrderEvent(ShippingOrder serviceOrder) {
        ShippingOrderStateChangeEventPayload serviceOrderPayloadEvent = ShippingOrderStateChangeEventPayload
                .builder()
                .shippingOrder(serviceOrder)
                .build();

        return ShippingOrderStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(EventType.SHIPPING_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(serviceOrderPayloadEvent)
                .build();
    }

    private static ServiceOrderEvent getProductOrderEvent(ServiceOrder serviceOrder) {
        ServiceOrderPayloadEvent serviceOrderPayloadEvent = ServiceOrderPayloadEvent
                .builder()
                .serviceOrder(serviceOrder)
                .build();

        return ServiceOrderEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(EventType.SERVICE_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(serviceOrderPayloadEvent)
                .build();
    }
    @PostMapping("/toggle-shipping-order-state-change-job")
    public String toggleShippingOrderStateChangeServiceMock() {
        shippingOrderStateChangeServiceMockScheduler.setEnabled(!shippingOrderStateChangeServiceMockScheduler.isEnabled());
        return "Job %s".formatted(shippingOrderStateChangeServiceMockScheduler.isEnabled() ? "Enabled" : "Disabled");
    }
}
