// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.creator.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class OrchestrationPlanNodeStateChangeEventEntityCreator implements EventEntityCreator<OrchestrationPlanNode, CDCEvent> {

    private final ObjectMapper objectMapper;

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    @Override
    public EventEntity create(OrchestrationPlanNode orchestrationPlanNode, Map<String, String> headers) throws JsonProcessingException {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(orchestrationPlanNode.getId()).orElseThrow(
                () -> new CoodNonRecoverableAndNonRetryableException(
                        new OrchestrationPlanNodeNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, orchestrationPlanNode.getId()))
        );

        return EventEntity.create(orchestrationPlan.getId(),
                getCDCEvent().getType(), getCDCEvent().getTopicName(),
                objectMapper.writeValueAsString(createOrchestrationPlanNodeEvent(orchestrationPlanNode, getCDCEvent())),
                headers);
    }

    @Override
    public CDCEvent getCDCEvent() {
        return ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT;
    }

    private OrchestrationPlanNodeStateChangeEvent createOrchestrationPlanNodeEvent(OrchestrationPlanNode orchestrationPlanNode, CDCEvent eventType) {
        OrchestrationPlanNodeStateChangePayloadEvent orchestrationPlanNodeStateChangePayloadEvent = OrchestrationPlanNodeStateChangePayloadEvent
                .builder()
                .orchestrationPlanNode(orchestrationPlanNode)
                .build();

        return OrchestrationPlanNodeStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(eventType.getType())
                .event(orchestrationPlanNodeStateChangePayloadEvent)
                .build();
    }
}
