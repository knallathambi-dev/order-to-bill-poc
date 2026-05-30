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
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class DeliveryOrderEventCreator implements EventEntityCreator<DeliveryOrderPayloadEvent, CDCEvent> {

    private final ObjectMapper objectMapper;

    @Override
    public EventEntity create(DeliveryOrderPayloadEvent deliveryOrderEvent, Map<String, String> headers) throws JsonProcessingException {
        return EventEntity.create(UUID.randomUUID().toString(),
                getCDCEvent().getType(), getCDCEvent().getTopicName(),
                objectMapper.writeValueAsString(getDeliveryStartEvent(deliveryOrderEvent, getCDCEvent())),
                headers);
    }

    private DeliveryOrderEvent getDeliveryStartEvent(DeliveryOrderPayloadEvent deliveryOrderPayloadEvent, CDCEvent cdcEvent) {
        return DeliveryOrderEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(cdcEvent.getType())
                .event(deliveryOrderPayloadEvent)
                .build();
    }

    @Override
    public CDCEvent getCDCEvent() {
        return CDCEvent.DELIVERY_ORDER_EVENT;
    }
}

