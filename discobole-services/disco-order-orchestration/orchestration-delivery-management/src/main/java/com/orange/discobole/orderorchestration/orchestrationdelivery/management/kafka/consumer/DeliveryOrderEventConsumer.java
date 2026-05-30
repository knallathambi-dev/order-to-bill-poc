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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.DeliveryOrderEventHandler;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import lombok.RequiredArgsConstructor;
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

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.DELIVERY_ORDER_TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryOrderEventConsumer {
    private final DeliveryOrderEventHandler handler;
    private final ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "${app.kafka.retry.attempts}",
            backoff = @Backoff(delayExpression = "${app.kafka.retry.delay}"), // Initial delay of 60 seconds, doubling with each attempt
            include = {CoodException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = DELIVERY_ORDER_TOPIC, groupId = "input-group-1")
    public void listen(Message<DeliveryOrderEvent> event) {
        log.debug("consume DeliveryOrderEvent for event: {}", event.getPayload());
        handler.handleEvent(event.getPayload());
    }

    @DltHandler
    public void deadLetterTopic(Message<DeliveryOrderEvent> message,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume DeliveryOrderEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume DeliveryOrderEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume DeliveryOrderEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(KafkaHeaders.OFFSET) long offset,
                                @Header(value = "headers", required = false) Map<String, String> headers) throws JsonProcessingException {
        log.info("Dead letter topic {} received: {} , with offset {} ", topicName, message, offset);
        CoodError coodError = Objects.nonNull(headers) && headers.containsKey(Headers.CONSUMER_ERROR)
                ? objectMapper.readValue(headers.get(Headers.CONSUMER_ERROR), CoodError.class)
                : new CoodError(exceptionCode, exceptionMessage, exceptionReason, Instant.now());
        handler.handleDeadLetter(message.getPayload(), coodError, traceParent, topicName);
    }
}
