// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.creator.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.generated.fallout.FalloutIncidentStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.EventEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class FalloutStateChangeEventEntityCreator implements EventEntityCreator<FalloutIncident> {

    private final ObjectMapper objectMapper;

    @Override
    public EventEntity create(FalloutIncident falloutIncident) throws JsonProcessingException {
        return EventEntity.create(falloutIncident.getId(), getCDCEvent(), objectMapper.writeValueAsString(getFalloutIncidentEvent(falloutIncident, getCDCEvent())));
    }

    @Override
    public CDCEvent getCDCEvent() {
        return CDCEvent.FALLOUT_STATE_CHANGE_EVENT;
    }

    private FalloutIncidentStateChangeEvent getFalloutIncidentEvent(FalloutIncident falloutIncident, CDCEvent eventType) {
        return FalloutIncidentStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(eventType.getType().getValue())
                .event(falloutIncident)
                .build();
    }
}
