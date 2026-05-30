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
import com.orange.discobole.orderorchestration.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class OrchestrationPlanStateChangeEventEntityCreator implements EventEntityCreator<OrchestrationPlan, CDCEvent> {

    private final ObjectMapper objectMapper;

    @Override
    public EventEntity create(OrchestrationPlan orchestrationPlan, Map<String, String> headers) throws JsonProcessingException {
        return EventEntity.create(orchestrationPlan.getId(),
                getCDCEvent().getType(), getCDCEvent().getTopicName(),
                objectMapper.writeValueAsString(getOrchestrationPlanEvent(orchestrationPlan, getCDCEvent())),
                headers);
    }

    @Override
    public CDCEvent getCDCEvent() {
        return ORCHESTRATION_PLAN_STATE_CHANGE_EVENT;
    }

    private OrchestrationPlanStateChangeEvent getOrchestrationPlanEvent(OrchestrationPlan orchestrationPlan, CDCEvent eventType) {
        OrchestrationPlanStateChangePayloadEvent orchestrationPlanStateChangePayloadEvent = OrchestrationPlanStateChangePayloadEvent
                .builder()
                .orchestrationPlan(orchestrationPlan)
                .build();

        return OrchestrationPlanStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(eventType.getType())
                .event(orchestrationPlanStateChangePayloadEvent)
                .build();
    }
}
