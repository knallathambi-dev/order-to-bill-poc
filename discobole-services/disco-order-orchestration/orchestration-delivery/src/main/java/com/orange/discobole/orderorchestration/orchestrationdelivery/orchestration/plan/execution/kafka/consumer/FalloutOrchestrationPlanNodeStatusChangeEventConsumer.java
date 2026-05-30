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
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.FalloutOrchestrationPlanNodeStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl.FalloutCompletedOrchestrationPlanNodeStateChangeEventHandlerImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl.FalloutFailedOrchestrationPlanNodeStateChangeEventHandlerImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl.FalloutHeldOrchestrationPlanNodeStateChangeEventHandlerImpl;
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

import java.util.EnumMap;
import java.util.Map;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_TOPIC;

@Slf4j
@Component
public class FalloutOrchestrationPlanNodeStatusChangeEventConsumer {

    private final Map<OrchestrationPlanNodeState, FalloutOrchestrationPlanNodeStateChangeEventHandler> nodeStateRourterMap = new EnumMap<>(OrchestrationPlanNodeState.class);


    public FalloutOrchestrationPlanNodeStatusChangeEventConsumer(FalloutCompletedOrchestrationPlanNodeStateChangeEventHandlerImpl falloutCompletedHandler,
                                                                 FalloutHeldOrchestrationPlanNodeStateChangeEventHandlerImpl falloutHeldHandler,
                                                                 FalloutFailedOrchestrationPlanNodeStateChangeEventHandlerImpl falloutFailedHandler) {
        nodeStateRourterMap.put(OrchestrationPlanNodeState.COMPLETED, falloutCompletedHandler);
        nodeStateRourterMap.put(OrchestrationPlanNodeState.HELD, falloutHeldHandler);
        nodeStateRourterMap.put(OrchestrationPlanNodeState.FAILED, falloutFailedHandler);
    }

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
            groupId = "${app.kafka.fallout-node-group-id}"
    )
    public void listen(Message<OrchestrationPlanNodeStateChangeEvent> message, @Header(value = "headers", required = false) Map<String, String> headers) {
        OrchestrationPlanNode node = message.getPayload().getEvent().getOrchestrationPlanNode();
        FalloutOrchestrationPlanNodeStateChangeEventHandler handler = nodeStateRourterMap.get(node.getState());
        if (handler != null) {
            handler.handleEvent(node, headers);
        } else {
            log.info("Kafka fallout node handler not available, received message: {}", message);
        }
    }

    @DltHandler
    public void deadLetterTopic(Message<OrchestrationPlanNodeStateChangeEvent> message,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume FalloutOrchestrationPlanNodeStateChangeEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume FalloutOrchestrationPlanNodeStateChangeEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume FalloutOrchestrationPlanNodeStateChangeEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("FalloutOrchestrationPlanNodeStatusChangeEventConsumer | deadLetterTopic | Dead letter topic {} received: {} , with offset {} ", topicName, message, offset);
    }

}