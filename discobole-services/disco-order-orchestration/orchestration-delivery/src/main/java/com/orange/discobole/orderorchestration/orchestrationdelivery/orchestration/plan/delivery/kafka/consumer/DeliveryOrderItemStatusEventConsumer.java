// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.kafka.consumer;

import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.kafka.handler.DeliveryOrderItemStatusEventHandler;
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

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.DELIVERY_ORDER_ITEM_STATUS_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryOrderItemStatusEventConsumer {

    private final DeliveryOrderItemStatusEventHandler handler;

    @RetryableTopic(
            attempts = "${app.kafka.retry.attempts}",
            backoff = @Backoff(delayExpression = "${app.kafka.retry.delay}"), // Initial delay of 60 seconds, doubling with each attempt
            include = {CoodException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = DELIVERY_ORDER_ITEM_STATUS_TOPIC, groupId = "${app.kafka.default-group-id}")
    public void listen(Message<DeliveryOrderItemStatusEvent> event, @Header(value = "headers", required = false) Map<String, String> headers) {
        handler.handleEvent(event.getPayload(), Objects.nonNull(headers) ? headers : Map.of());
    }

    @DltHandler
    public void deadLetterTopic(Message<DeliveryOrderItemStatusEvent> message,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume DeliveryOrderItemStatusEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume DeliveryOrderItemStatusEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume DeliveryOrderItemStatusEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(KafkaHeaders.OFFSET) long offset,
                                @Header(value = "headers", required = false) Map<String, String> headers) {
        log.info("Dead letter topic {} received: {} , with offset {} ", topicName, message, offset);
        DeliveryOrderItemStatusPayloadEvent payloadEvent = message.getPayload().getEvent();
        FalloutCharacteristicWrapper characteristicWrapper = FalloutCharacteristicWrapper.builder()
                .coodError(new CoodError(exceptionCode, exceptionMessage, exceptionReason, Instant.now()))
                .topicName(Objects.nonNull(headers) && Objects.nonNull(headers.get(Headers.SOURCE_TOPIC_NAME)) && !headers.get(Headers.SOURCE_TOPIC_NAME).isEmpty() ? headers.get(Headers.SOURCE_TOPIC_NAME) : topicName)
                .traceParent(traceParent)
                .handlerClass(DeliveryOrderItemStatusEventHandler.class.getSimpleName())
                .eventPayload(Objects.nonNull(payloadEvent.getSourcePayload()) ? payloadEvent.getSourcePayload() : message.getPayload())
                .build();
        handler.handleDeadLetter(message.getPayload(), characteristicWrapper, Objects.nonNull(headers) ? headers : Map.of());
    }
}
