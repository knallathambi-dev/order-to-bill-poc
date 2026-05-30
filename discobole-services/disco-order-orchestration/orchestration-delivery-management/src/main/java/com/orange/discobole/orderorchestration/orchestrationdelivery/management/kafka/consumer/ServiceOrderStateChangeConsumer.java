// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.kafka.consumer;


import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.impl.ServiceOrderEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.SERVICE_ORDER_STATE_CHANGE_TOPIC;

@Slf4j
@Component
public class ServiceOrderStateChangeConsumer {
    private final ServiceOrderEventHandler handler;

    public ServiceOrderStateChangeConsumer(ServiceOrderEventHandler handler) {
        this.handler = handler;
    }

    @RetryableTopic(
            attempts = "${app.kafka.retry.attempts}",
            backoff = @Backoff(delayExpression = "${app.kafka.retry.delay}"), // Initial delay of 60 seconds, doubling with each attempt
            include = {CoodException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = SERVICE_ORDER_STATE_CHANGE_TOPIC, groupId = "${app.kafka.group-id}")
    public void listen(Message<ServiceOrderEvent> message) {
        log.info("Consume ServiceOrderEvent for service order id: {}", message.getPayload().getEvent().getServiceOrder().getId());
        log.debug("Consume ServiceOrderEvent for event: {}", message.getPayload());
        handler.handleEvent(message.getPayload());
    }

    @DltHandler
    public void deadLetterTopic(Message<ServiceOrderEvent> event,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume ServiceOrderEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume ServiceOrderEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume ServiceOrderEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("ServiceOrderStateChangeConsumer | deadLetterTopic | Dead letter topic {} received: {} , with offset: {}, traceparent: {} ", topicName, event, offset, traceParent);
        CoodError coodError = CoodError.builder()
                .message(exceptionMessage)
                .reason(exceptionReason)
                .code(exceptionCode)
                .build();
        handler.handleDltEvent(event.getPayload(), coodError, topicName);
    }
}
