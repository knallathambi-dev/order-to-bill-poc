// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutTechnicalException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.EventRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class EventPublisher {

    private final EventRepository eventRepository;

    private final List<EventEntityCreator> eventEntityCreators;

    public <T> void publishEvent(CDCEvent eventType, T payload) {
        try {
            eventRepository.save(getEventEntityCreator(eventType).create(payload));
        } catch (JsonProcessingException e) {
            throw new FalloutTechnicalException("Could not save event in outbox collection");
        }
    }

    private EventEntityCreator getEventEntityCreator(CDCEvent cdcEvent) {
        for (EventEntityCreator creator : eventEntityCreators) {
            if (cdcEvent.equals(creator.getCDCEvent())) {
                return creator;
            }
        }

        throw new FalloutTechnicalException("Could not find event creator for event " + cdcEvent);
    }
}
