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
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
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
public class DeliveryOrderItemStatusPayloadEventCreator implements EventEntityCreator<DeliveryOrderItemStatusPayloadEvent, CDCEvent> {

    private final ObjectMapper objectMapper;

    @Override
    public EventEntity create(DeliveryOrderItemStatusPayloadEvent event, Map<String, String> headers) throws JsonProcessingException {
        return EventEntity.create(
                event.getOrderItemRef().getOrchestrationNodeId(),
                getCDCEvent().getType(),
                getCDCEvent().getTopicName(),
                objectMapper.writeValueAsString(getDeliveryOrderItemStatusEvent(event, getCDCEvent())),
                headers);
    }

    private DeliveryOrderItemStatusEvent getDeliveryOrderItemStatusEvent(DeliveryOrderItemStatusPayloadEvent payload, CDCEvent cdcEvent) {
        return DeliveryOrderItemStatusEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(cdcEvent.getType())
                .event(payload)
                .build();
    }

    @Override
    public CDCEvent getCDCEvent() {
        return CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT;
    }
}
