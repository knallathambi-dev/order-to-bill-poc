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
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.OrchestrationPlanEventHandler;
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

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.ORCHESTRATION_PLAN_STATE_CHANGE_TOPIC;

@Slf4j
@Component
public class OrchestrationPlanStateChangeEventConsumer {
    private final OrchestrationPlanEventHandler handler;

    public OrchestrationPlanStateChangeEventConsumer(OrchestrationPlanEventHandler handler) {
        this.handler = handler;
    }

    @RetryableTopic(
            attempts = "${app.kafka.retry.attempts}",
            backoff = @Backoff(delayExpression = "${app.kafka.retry.delay}"), // Initial delay of 60 seconds, doubling with each attempt
            include = {CoodException.class, MongoException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(
            topics = ORCHESTRATION_PLAN_STATE_CHANGE_TOPIC,
            groupId = "${app.kafka.plan-group-id}"
    )
    public void listen(Message<OrchestrationPlanStateChangeEvent> message) {
        handler.handleEvent(message.getPayload());
    }

    @DltHandler
    public void deadLetterTopic(Message<OrchestrationPlanStateChangeEvent> message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume OrchestrationPlanStateChangeEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume OrchestrationPlanStateChangeEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume OrchestrationPlanStateChangeEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.error("OrchestrationPlanStateChangeEventConsumer | deadLetterTopic | Dead letter topic {} received: {} , with offset {} ", topicName, message, offset);

        {
            // Check the current state of the orchestration plan.
            // If the plan is already in the HELD state,
            // it means an issue has previously been detected and is pending resolution.
            // In this case, we do not need to process this message further right now.
            // The plan will remain in the HELD state until the underlying issue is resolved.
            // Once resolved, the system can retry processing this plan in a future attempt.
            OrchestrationPlan orchestrationPlan = message.getPayload().getEvent().getOrchestrationPlan();

            if (State.HELD.equals(orchestrationPlan.getState())) {
                // Skip further processing for plans already in HELD state.
                log.debug("Dead letter topic plan already held, plan id: {}", orchestrationPlan.getId());
                return;
            }
        }

        FalloutCharacteristicWrapper falloutCharacteristicWrapper = FalloutCharacteristicWrapper.builder()
                .coodError(new CoodError(exceptionCode, exceptionMessage, exceptionReason, Instant.now()))
                .traceParent(traceParent)
                .topicName(topicName)
                .handlerClass(OrchestrationPlanEventHandler.class.getSimpleName())
                .eventPayload(message.getPayload())
                .build();
        handler.handleDeadLetter(message.getPayload(), falloutCharacteristicWrapper);

    }

}