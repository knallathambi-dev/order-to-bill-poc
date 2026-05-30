// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.kafka.consumer;

import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Instant;
import java.util.List;

import static com.orange.discobole.ordermanagement.commons.enumeration.EventType.PRODUCT_ORDER_STATE_CHANGE_EVENT;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class ProductOrderEventConsumerTest {
    public static final Instant EVENT_TIME = Instant.now();
    public static final String EVENT_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderEventService productOrderEventService;

    @InjectMocks
    private ProductOrderEventConsumer productOrderEventConsumer;

    @Test
    @DisplayName("Given a null event type, " +
            "when accept is called, " +
            "then no event is created or deleted")
    void shouldNotCreateOrDeleteEventWhenEventTypeIsNull() {
        // Given
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(null)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given a null event payload, " +
            "when accept is called, " +
            "then no event is created or deleted")
    void shouldNotCreateOrDeleteEventWhenEventPayloadIsNull() {
        // Given
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(null)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given a null productOrder, " +
            "when accept is called, " +
            "then no event is created or deleted")
    void shouldNotCreateOrDeleteEventWhenProductOrderIsNull() {
        // Given
        ProductOrderPayloadEvent productOrderPayloadEvent = ProductOrderPayloadEvent.builder()
                .productOrder(null)
                .build();
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderPayloadEvent)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given a null productOrder ID, " +
            "when accept is called, " +
            "then no event is created or deleted")
    void shouldNotCreateOrDeleteEventWhenProductOrderIdIsNull() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder().id(null).build();
        ProductOrderPayloadEvent productOrderPayloadEvent = ProductOrderPayloadEvent.builder()
                .productOrder(productOrderDTO)
                .build();
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderPayloadEvent)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given an accepted productOrder, " +
            "when accept is called, " +
            "then a product order state change event is created")
    void shouldCreateEventWhenProductOrderIsAccepted() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.ACCEPTED)
                .productOrderItem(List.of(ProductOrderItem.builder().id(PRODUCT_ORDER_ITEM_ID).build()))
                .build();

        ProductOrderPayloadEvent productOrderPayloadEvent = ProductOrderPayloadEvent.builder()
                .productOrder(productOrderDTO)
                .build();
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderPayloadEvent)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(1))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given a cancelled productOrder, " +
            "when accept is called, " +
            "then product order state is deleted")
    void shouldDeleteProductOrderStateWhenProductOrderIsCancelled() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.CANCELLED)
                .productOrderItem(List.of(ProductOrderItem.builder().id(PRODUCT_ORDER_ITEM_ID).build()))
                .build();

        ProductOrderPayloadEvent productOrderPayloadEvent = ProductOrderPayloadEvent.builder()
                .productOrder(productOrderDTO)
                .build();
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderPayloadEvent)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(1))
                .deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given a product order with non-actionable state (COMPLETED), " +
            "when accept is called, " +
            "then no event is created or deleted")
    void shouldNotCreateOrDeleteEventWhenProductOrderStateIsNotActionable() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder().id(PRODUCT_ORDER_ITEM_ID).build()))
                .build();

        ProductOrderPayloadEvent productOrderPayloadEvent = ProductOrderPayloadEvent.builder()
                .productOrder(productOrderDTO)
                .build();
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderPayloadEvent)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given an accepted product order and service throws unexpected exception, " +
            "when accept is called, " +
            "then exception is caught and no event is created")
    void shouldCatchUnexpectedExceptionWhenServiceThrowsRuntimeException() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.ACCEPTED)
                .productOrderItem(List.of(ProductOrderItem.builder().id(PRODUCT_ORDER_ITEM_ID).build()))
                .build();

        ProductOrderPayloadEvent productOrderPayloadEvent = ProductOrderPayloadEvent.builder()
                .productOrder(productOrderDTO)
                .build();
        ProductOrderStateChangeEvent productOrderEvent = ProductOrderStateChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderPayloadEvent)
                .build();
        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderEvent).build();

        Mockito.doThrow(new RuntimeException("Unexpected database error"))
                .when(productOrderEventService)
                .createProductOrderItemStateChangedEvent(any(), any());

        // When
        productOrderEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(1))
                .createProductOrderItemStateChangedEvent(any(), any());
        Mockito.verify(productOrderEventService, Mockito.times(0))
                .deleteByProductOrderId(any());
    }
}