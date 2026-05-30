// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.DeliveryOrderEventHandler;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryOrderEventConsumerTest {

    @Mock
    private DeliveryOrderEventHandler handler;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DeliveryOrderEventConsumer consumer;

    private DeliveryOrderEvent deliveryOrderEvent;
    private Message<DeliveryOrderEvent> message;

    @BeforeEach
    void setUp() {
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .factoryOrderId("test-factory-order-id")
                .build();

        deliveryOrderEvent = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        message = MessageBuilder.withPayload(deliveryOrderEvent).build();
    }

    @Test
    void givenValidDeliveryOrderEvent_whenListen_thenHandlerCalled() {
        // When
        consumer.listen(message);

        // Then
        verify(handler, times(1)).handleEvent(deliveryOrderEvent);
    }

    @Test
    void givenDltWithStructuredCoodError_whenDeadLetterTopic_thenDeserializesAndHandles() throws JsonProcessingException {
        // Given
        String topicName = "delivery-order-topic";
        long offset = 123L;
        String traceParent = "00-trace-id-parent-id-01";
        
        CoodError expectedCoodError = new CoodError(
                "SERVICE_CREATION_FAILED",
                "Failed to create service order",
                "Service catalog unavailable",
                Instant.parse("2024-08-12T10:15:30Z")
        );

        Map<String, String> headers = new HashMap<>();
        String serializedError = "{\"code\":\"SERVICE_CREATION_FAILED\",\"message\":\"Failed to create service order\"}";
        headers.put(Headers.CONSUMER_ERROR, serializedError);

        when(objectMapper.readValue(serializedError, CoodError.class)).thenReturn(expectedCoodError);

        // When
        consumer.deadLetterTopic(
                message,
                "default-reason",
                "default-message",
                "default-code",
                traceParent,
                topicName,
                offset,
                headers
        );

        // Then
        ArgumentCaptor<CoodError> errorCaptor = ArgumentCaptor.forClass(CoodError.class);
        verify(handler, times(1)).handleDeadLetter(
                eq(deliveryOrderEvent),
                errorCaptor.capture(),
                eq(traceParent),
                eq(topicName)
        );

        CoodError capturedError = errorCaptor.getValue();
        assertEquals(expectedCoodError, capturedError);
        verify(objectMapper, times(1)).readValue(serializedError, CoodError.class);
    }

    @Test
    void givenDltWithoutStructuredCoodError_whenDeadLetterTopic_thenCreatesCoodErrorFromHeaders() throws JsonProcessingException {
        // Given
        String topicName = "delivery-order-topic";
        long offset = 456L;
        String traceParent = "00-trace-id-parent-id-02";
        String exceptionReason = "Service validation failed";
        String exceptionMessage = "Invalid service specification";
        String exceptionCode = "VALIDATION_ERROR";

        Map<String, String> headers = new HashMap<>();
        // No CONSUMER_ERROR header

        // When
        consumer.deadLetterTopic(
                message,
                exceptionReason,
                exceptionMessage,
                exceptionCode,
                traceParent,
                topicName,
                offset,
                headers
        );

        // Then
        ArgumentCaptor<CoodError> errorCaptor = ArgumentCaptor.forClass(CoodError.class);
        verify(handler, times(1)).handleDeadLetter(
                eq(deliveryOrderEvent),
                errorCaptor.capture(),
                eq(traceParent),
                eq(topicName)
        );

        CoodError capturedError = errorCaptor.getValue();
        assertNotNull(capturedError);
        assertEquals(exceptionCode, capturedError.code());
        assertEquals(exceptionMessage, capturedError.message());
        assertEquals(exceptionReason, capturedError.reason());
        assertNotNull(capturedError.timestamp());
        
        verify(objectMapper, never()).readValue(anyString(), eq(CoodError.class));
    }

    @Test
    void givenDltWithNullHeaders_whenDeadLetterTopic_thenCreatesCoodErrorFromDefaultHeaders() throws JsonProcessingException {
        // Given
        String topicName = "delivery-order-topic";
        long offset = 789L;
        String traceParent = "00-trace-id-parent-id-03";
        String exceptionReason = "Network timeout";
        String exceptionMessage = "Connection timed out";
        String exceptionCode = "TIMEOUT_ERROR";

        // When
        consumer.deadLetterTopic(
                message,
                exceptionReason,
                exceptionMessage,
                exceptionCode,
                traceParent,
                topicName,
                offset,
                null // null headers
        );

        // Then
        ArgumentCaptor<CoodError> errorCaptor = ArgumentCaptor.forClass(CoodError.class);
        verify(handler, times(1)).handleDeadLetter(
                eq(deliveryOrderEvent),
                errorCaptor.capture(),
                eq(traceParent),
                eq(topicName)
        );

        CoodError capturedError = errorCaptor.getValue();
        assertNotNull(capturedError);
        assertEquals(exceptionCode, capturedError.code());
        assertEquals(exceptionMessage, capturedError.message());
        assertEquals(exceptionReason, capturedError.reason());
        
        verify(objectMapper, never()).readValue(anyString(), eq(CoodError.class));
    }

    @Test
    void givenDltWithJsonProcessingException_whenDeadLetterTopic_thenThrowsException() throws JsonProcessingException {
        // Given
        String topicName = "delivery-order-topic";
        long offset = 222L;
        String traceParent = "00-trace-id-parent-id-05";
        
        Map<String, String> headers = new HashMap<>();
        String malformedJson = "{invalid-json}";
        headers.put(Headers.CONSUMER_ERROR, malformedJson);

        when(objectMapper.readValue(malformedJson, CoodError.class))
                .thenThrow(new JsonProcessingException("Malformed JSON") {});

        // When & Then
        assertThrows(JsonProcessingException.class, () -> 
                consumer.deadLetterTopic(
                        message,
                        "default-reason",
                        "default-message",
                        "default-code",
                        traceParent,
                        topicName,
                        offset,
                        headers
                )
        );

        verify(objectMapper, times(1)).readValue(malformedJson, CoodError.class);
        verify(handler, never()).handleDeadLetter(any(), any(), anyString(), anyString());
    }
}
