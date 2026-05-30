// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.mocks.producer.impl;


import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.mocks.producer.ServiceOrderStateChangeEventProducer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class ServiceOrderStateChangeEventProducerImpl implements ServiceOrderStateChangeEventProducer {

    private final KafkaTemplate<String, ServiceOrderEvent> kafkaTemplate;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ServiceOrderStateChangeEventProducerImpl(KafkaTemplate<String, ServiceOrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static ServiceOrderEvent getServiceOrderStateEvent(ServiceOrder serviceOrder) {
        ServiceOrderPayloadEvent serviceOrderStateChangePayloadEvent = ServiceOrderPayloadEvent
                .builder()
                .serviceOrder(serviceOrder)
                .build();

        return ServiceOrderEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(EventType.SERVICE_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(serviceOrderStateChangePayloadEvent)
                .build();
    }

    @Override
    public void publishEvent(ServiceOrder serviceOrder) {
        log.info("trigger the service order event");
        ServiceOrderEvent serviceOrderEvent = getServiceOrderStateEvent(serviceOrder);
        String partitionKey = serviceOrder.getId();

        Message<ServiceOrderEvent> message = MessageBuilder.withPayload(serviceOrderEvent)
                .setHeader(MessageHeaders.CONTENT_TYPE, "application/json") // Set the content type header
                .build();

        kafkaTemplate.send("disco.service-order-management.serviceOrderStateChange-event", partitionKey, message.getPayload());
        log.info("event service is published");
    }
}
