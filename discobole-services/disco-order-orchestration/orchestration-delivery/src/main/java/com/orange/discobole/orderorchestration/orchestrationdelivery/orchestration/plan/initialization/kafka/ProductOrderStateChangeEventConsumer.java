// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.kafka.handler.ProductOrderStateChangeEventHandler;
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

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.PRODUCT_ORDER_STATE_CHANGE_TMR_TOPIC;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.PRODUCT_ORDER_STATE_CHANGE_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductOrderStateChangeEventConsumer {

    private final ProductOrderStateChangeEventHandler handler;

    @RetryableTopic(
            attempts = "${app.kafka.retry.attempts}",
            backoff = @Backoff(delayExpression = "${app.kafka.retry.delay}"), // Initial delay of 60 seconds, doubling with each attempt
            include = {CoodException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(
            topics = {
                    PRODUCT_ORDER_STATE_CHANGE_TOPIC,
                    PRODUCT_ORDER_STATE_CHANGE_TMR_TOPIC
            },
            groupId = "${app.kafka.default-group-id}"
    )
    public void listen(Message<ProductOrderStateChangeEvent> message) {
        handler.handleEvent(message.getPayload());
    }

    @DltHandler
    public void deadLetterTopic(Message<ProductOrderStateChangeEvent> message,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume ProductOrderStateChangeEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume ProductOrderStateChangeEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume ProductOrderStateChangeEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("ProductOrderStateChangeEventConsumer | deadLetterTopic | Dead letter topic {} received: {} , with offset {} ", topicName, message, offset);
        FalloutCharacteristicWrapper falloutCharacteristicWrapper = FalloutCharacteristicWrapper.builder()
                .coodError(new CoodError(exceptionCode, exceptionMessage, exceptionReason, Instant.now()))
                .topicName(topicName)
                .traceParent(traceParent)
                .handlerClass(ProductOrderStateChangeEventHandler.class.getSimpleName())
                .eventPayload(message.getPayload())
                .build();
        handler.handleDeadLetter(message.getPayload(), falloutCharacteristicWrapper);
    }
}