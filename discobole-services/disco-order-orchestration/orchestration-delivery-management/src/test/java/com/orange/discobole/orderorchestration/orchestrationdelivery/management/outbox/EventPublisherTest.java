// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.creator.EventEntityCreator;
import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventEntityCreator<Object, CDCEvent> mockEventEntityCreator;

    @InjectMocks
    private EventPublisher eventPublisher;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        eventPublisher = new EventPublisher(eventRepository, List.of(mockEventEntityCreator), objectMapper);
    }

    @Test
    void publishEvent_shouldSaveEvent_whenValidEventTypeAndPayloadProvided() {
        // Given a valid event type and payload
        String payload = "testPayload";
        when(mockEventEntityCreator.getCDCEvent()).thenReturn(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT);

        // When publishEvent is called
        eventPublisher.publishEvent(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT, payload);

        // Then the event is saved in the repository
        verify(eventRepository, times(1)).save(any());
    }

    @Test
    void publishEvent_shouldThrowDiscoTechnicalException_whenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        // Given a mock EventEntityCreator that throws JsonProcessingException
        String payload = "testPayload";
        when(mockEventEntityCreator.getCDCEvent()).thenReturn(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT);
        when(mockEventEntityCreator.create(any(), any())).thenThrow(JsonProcessingException.class);

        // When & Then - Expect a DiscoTechnicalException when publishEvent is called
        CoodTechnicalException exception = assertThrows(
                CoodTechnicalException.class,
                () -> eventPublisher.publishEvent(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT, payload)
        );

        assertEquals(exception.getCode(), ExceptionCode.COOD_TECHNICAL_EXCEPTION.getCode());
    }


    @Test
    void getEventEntityCreator_shouldThrowDiscoTechnicalException_whenNoMatchingCreatorFound() {
        // Given a CDCEvent that does not match any EventEntityCreator in the list
        CDCEvent nonExistentEvent = null; // Assume OTHER_EVENT is not in the mock

        DeliveryOrderItemStatusPayloadEvent deliveryStatusPayloadEvent = DeliveryOrderItemStatusPayloadEvent.builder().build();

        // When & Then - Expect a DiscoTechnicalException when getEventEntityCreator is called
        CoodTechnicalException exception = assertThrows(
                CoodTechnicalException.class,
                () -> eventPublisher.publishEvent(nonExistentEvent, deliveryStatusPayloadEvent));

        assertTrue(exception.getMessage().contains("Could not find event creator for event " + nonExistentEvent));
    }
}
