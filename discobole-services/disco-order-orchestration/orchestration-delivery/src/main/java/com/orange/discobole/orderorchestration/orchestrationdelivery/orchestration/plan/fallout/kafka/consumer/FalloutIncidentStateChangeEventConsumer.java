// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.fallout.kafka.consumer;

 import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.generated.fallout.FalloutIncidentStateChangeEvent;
 import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.FalloutIncidentResolutionStateInvalidException;
 import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
 import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
 import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.fallout.kafka.handler.ResolvedFalloutIncidentStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.fallout.kafka.handler.facade.UnresolvedFalloutIncidentStateChangeFacadeService;
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

 import java.util.Objects;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.FALLOUT_INCIDENT_STATE_CHANGE_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class FalloutIncidentStateChangeEventConsumer {

    private final ResolvedFalloutIncidentStateChangeEventHandler resolvedFalloutIncidentStateChangeEventHandler;

    private final UnresolvedFalloutIncidentStateChangeFacadeService unresolvedFalloutIncidentStateChangeFacadeService;

    @RetryableTopic(
            attempts = "${app.kafka.retry.attempts}",
            backoff = @Backoff(delayExpression = "${app.kafka.retry.delay}"), // Initial delay of 60 seconds, doubling with each attempt
            include = {CoodException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = FALLOUT_INCIDENT_STATE_CHANGE_TOPIC, groupId = "${app.kafka.default-group-id}")
    public void listen(Message<FalloutIncidentStateChangeEvent> event) {
        FalloutIncident falloutIncident = event.getPayload().getEvent();
        if (Objects.isNull(falloutIncident.getResolution()) || !State.COMPLETED.equals(falloutIncident.getState())) {
            return;
        }
        switch (falloutIncident.getResolution().getStatus()) {
            case RESOLVED -> resolvedFalloutIncidentStateChangeEventHandler.handle(event.getPayload());
            case UNRESOLVED ->
                    unresolvedFalloutIncidentStateChangeFacadeService.processEvent(falloutIncident, event.getPayload().getEventId());
            case SKIPPED -> {
                log.info("FalloutIncidentStateChangeEventConsumer | fallout incident with id : {} | skipped", falloutIncident.getId());
            }
            default ->
                    throw CoodRecoverableAndNonRetryableException.of(new FalloutIncidentResolutionStateInvalidException(ExceptionCode.INVALID_FALLOUT_INCIDENT_RESOLUTION_STATE, event.getPayload().getEvent().getId()));
        }
    }

    @DltHandler
    public void deadLetterTopic(Message<FalloutIncidentStateChangeEvent> message,
                                @Header(value = "exception-reason", defaultValue = "Failed to consume FalloutIncidentStateChangeEvent") String exceptionReason,
                                @Header(value = "exception-message", defaultValue = "Failed to consume FalloutIncidentStateChangeEvent") String exceptionMessage,
                                @Header(value = "exception-code", defaultValue = "Failed to consume FalloutIncidentStateChangeEvent") String exceptionCode,
                                @Header(value = "traceparent", defaultValue = "No traceparent provided") String traceParent,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("FalloutIncidentStateChangeEventConsumer | deadLetterTopic | Dead letter topic: {} | received: {} | offset: {} errorMessage: {} |   errorReason: {} | errorCode: {}",
                topicName,
                message,
                offset,
                exceptionMessage,
                exceptionReason,
                exceptionCode);
    }
}
