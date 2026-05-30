// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.creator.impl.DeliveryOrderItemStatusPayloadEventCreator;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DeliveryOrderItemStatusPayloadEventCreator.class)
class DeliveryOrderItemStatusPayloadEventCreatorTest {

    @MockBean
    ObjectMapper objectMapper;

    @Autowired
    DeliveryOrderItemStatusPayloadEventCreator eventCreator;

    @Test
    void givenValidDeliveryOrderItemStatusPayloadEvent_whenCreate_thenEventEntityIsCreatedSuccessfully() throws JsonProcessingException {
        // Given
        String nodeId = UUID.randomUUID().toString();
        DeliveryOrderItemStatusPayloadEvent payloadEvent = DeliveryOrderItemStatusPayloadEvent.builder()
                .orderItemRef(OrderItemRef.builder()
                        .orchestrationNodeId(nodeId)
                        .build())
                .build();


        // When
        EventEntity result = eventCreator.create(payloadEvent, Map.of());

        // Then
        assertNotNull(result);
        assertEquals(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT.getTopicName(), result.getType());

        ArgumentCaptor<DeliveryOrderItemStatusEvent> captor = ArgumentCaptor.forClass(DeliveryOrderItemStatusEvent.class);
        verify(objectMapper).writeValueAsString(captor.capture());

        DeliveryOrderItemStatusEvent capturedEvent = captor.getValue();
        assertNotNull(capturedEvent.getEventId());
        assertNotNull(capturedEvent.getEventTime());
        assertEquals(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT.getType(), capturedEvent.getEventType());
        assertEquals(payloadEvent, capturedEvent.getEvent());
    }


    @Test
    void whenGetCDCEvent_thenReturnsCorrectCDCEvent() {
        // Given / When
        CDCEvent result = eventCreator.getCDCEvent();

        // Then
        assertEquals(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT, result);
    }
}
