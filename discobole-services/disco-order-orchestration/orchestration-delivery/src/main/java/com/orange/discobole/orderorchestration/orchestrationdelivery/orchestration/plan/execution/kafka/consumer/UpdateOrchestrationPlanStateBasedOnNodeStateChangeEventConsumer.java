// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.consumer;

import com.mongodb.MongoException;
import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler;
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

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventConsumer {

    private final UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler handler;

    @RetryableTopic(
            attempts = "${app.kafka.retry.attempts}",
            backoff = @Backoff(delayExpression = "${app.kafka.retry.delay}"), // Initial delay of 60 seconds, doubling with each attempt
            include = {CoodException.class, MongoException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(
            topics = {
                    ORCHESTRATION_PLAN_NODE_STATE_CHANGE_TOPIC
            },
            groupId = "${app.kafka.plan-group-id}",
            autoStartup = "${app.kafka.consumer.autoStartup:true}"
    )
    public void listen(Message<OrchestrationPlanNodeStateChangeEvent> message) {
        handler.handleEvent(message.getPayload());
    }

    @DltHandler
    public void deadLetterTopic(Message<OrchestrationPlanNodeStateChangeEvent> message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume OrchestrationPlanNodeStateChangeEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume OrchestrationPlanNodeStateChangeEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume OrchestrationPlanNodeStateChangeEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Dead letter topic {} received: {} , with offset {} ", topicName, message, offset);
    }
}

