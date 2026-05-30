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
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEventPayload;
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
public class ShippingOrderStateChangeEventCreator implements EventEntityCreator<ShippingOrder, CDCEvent> {

    private final ObjectMapper objectMapper;

    @Override
    public EventEntity create(ShippingOrder shippingOrder, Map<String, String> headers) throws JsonProcessingException {
        return EventEntity.create(shippingOrder.getId(),
                getCDCEvent().getType(), getCDCEvent().getTopicName(),
                objectMapper.writeValueAsString(getShippingOrderEvent(shippingOrder, getCDCEvent())),
                headers);
    }

    private ShippingOrderStateChangeEvent getShippingOrderEvent(ShippingOrder shippingOrder, CDCEvent cdcEvent) {
        return ShippingOrderStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(cdcEvent.getType())
                .event(ShippingOrderStateChangeEventPayload
                        .builder()
                        .shippingOrder(shippingOrder)
                        .build())
                .build();
    }

    @Override
    public CDCEvent getCDCEvent() {
        return CDCEvent.SHIPPING_ORDER_STATE_CHANGE_EVENT;
    }
}
