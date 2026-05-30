// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.creator.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ServiceOrderStateChangeEventCreator implements EventEntityCreator<ServiceOrder, CDCEvent> {

    private final ObjectMapper objectMapper;

    @Override
    public EventEntity create(ServiceOrder serviceOrder, Map<String, String> headers) throws JsonProcessingException {
        return EventEntity.create(serviceOrder.getId(),
                getCDCEvent().getType(), getCDCEvent().getTopicName(),
                objectMapper.writeValueAsString(getServiceOrderEvent(serviceOrder, getCDCEvent())),
                headers);
    }

    private ServiceOrderEvent getServiceOrderEvent(ServiceOrder serviceOrder, CDCEvent cdcEvent) {
        return ServiceOrderEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(cdcEvent.getType())
                .event(ServiceOrderPayloadEvent
                        .builder()
                        .serviceOrder(serviceOrder)
                        .build())
                .build();
    }

    @Override
    public CDCEvent getCDCEvent() {
        return CDCEvent.SERVICE_ORDER_STATE_CHANGE_EVENT;
    }
}
