// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.kafka.consumer;

import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributePayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributeValueChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
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
class ProductOrderAttributeValueChangeEventConsumerTest {
    public static final Instant EVENT_TIME = Instant.now();
    public static final String EVENT_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_SPECIFICATION_NAME = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderEventService productOrderEventService;
    @InjectMocks
    private ProductOrderAttributeValueChangeEventConsumer productOrderAttributeValueChangeEventConsumer;

    @Test
    @DisplayName("Given null event type, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenEventTypeIsNull() {
        // Given
        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(null)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderAttributeValueChangeEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given null event, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenEventIsNull() {
        // Given
        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(null)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderAttributeValueChangeEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given null product order, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenProductOrderIsNull() {
        // Given
        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder()
                .productOrder(null)
                .build();

        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderAttributeValueChangeEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given null product order ID, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenProductOrderIdIsNull() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder().build();
        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderAttributeValueChangeEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given product order item with state null, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotServiceWhenProductOrderItemStateNull() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.INPROGRESS)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(ProductSpecificationRef.builder()
                                        .id(PRODUCT_SPECIFICATION_ID)
                                        .name(PRODUCT_SPECIFICATION_NAME)
                                        .atType("ProductSpecificationRef")
                                        .build())
                                .build())
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given product order item with state InProgress, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotServiceWhenProductOrderItemStateInProgress() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.INPROGRESS)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.INPROGRESS)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(ProductSpecificationRef.builder()
                                        .id(PRODUCT_SPECIFICATION_ID)
                                        .name(PRODUCT_SPECIFICATION_NAME)
                                        .atType("ProductSpecificationRef")
                                        .build())
                                .build())
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given valid product order item, " +
            "when event is consumed, " +
            "then service method should be called once")
    void shouldCallServiceWhenValidProductOrderItem() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.COMPLETED)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(ProductSpecificationRef.builder()
                                        .id(PRODUCT_SPECIFICATION_ID)
                                        .name(PRODUCT_SPECIFICATION_NAME)
                                        .atType("ProductSpecificationRef")
                                        .build())
                                .build())
                        .isInstallable(Boolean.TRUE)
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(1)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given product order item with null product, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenProductIsNull() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.COMPLETED)
                        .product(null)
                        .isInstallable(Boolean.TRUE)
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given product order item with null product specification, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenProductSpecificationIsNull() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.COMPLETED)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(null)
                                .build())
                        .isInstallable(Boolean.TRUE)
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given product order item with null state, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenItemStateIsNull() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(null)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(ProductSpecificationRef.builder()
                                        .id(PRODUCT_SPECIFICATION_ID)
                                        .name(PRODUCT_SPECIFICATION_NAME)
                                        .atType("ProductSpecificationRef")
                                        .build())
                                .build())
                        .isInstallable(Boolean.TRUE)
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given product order item with isInstallable false, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenIsInstallableIsFalse() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.COMPLETED)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(ProductSpecificationRef.builder()
                                        .id(PRODUCT_SPECIFICATION_ID)
                                        .name(PRODUCT_SPECIFICATION_NAME)
                                        .atType("ProductSpecificationRef")
                                        .build())
                                .build())
                        .isInstallable(Boolean.FALSE)
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given product order item with isInstallable null, " +
            "when event is consumed, " +
            "then service method should not be called")
    void shouldNotCallServiceWhenIsInstallableIsNull() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.COMPLETED)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(ProductSpecificationRef.builder()
                                        .id(PRODUCT_SPECIFICATION_ID)
                                        .name(PRODUCT_SPECIFICATION_NAME)
                                        .atType("ProductSpecificationRef")
                                        .build())
                                .build())
                        .isInstallable(null)
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(0)).addProductOrderItem(any());
    }

    @Test
    @DisplayName("Given valid event and service throws unexpected exception, " +
            "when event is consumed, " +
            "then exception is caught and handled gracefully")
    void shouldCatchUnexpectedExceptionWhenServiceThrowsRuntimeException() {
        // Given
        ProductOrder productOrderDTO = ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.COMPLETED)
                .productOrderItem(List.of(ProductOrderItem.builder()
                        .id(PRODUCT_ORDER_ITEM_ID)
                        .state(ProductOrderItemStateType.COMPLETED)
                        .product(Product.builder()
                                .id(PRODUCT_ID)
                                .productSpecification(ProductSpecificationRef.builder()
                                        .id(PRODUCT_SPECIFICATION_ID)
                                        .name(PRODUCT_SPECIFICATION_NAME)
                                        .atType("ProductSpecificationRef")
                                        .build())
                                .build())
                        .isInstallable(Boolean.TRUE)
                        .build()))
                .build();

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder().productOrder(productOrderDTO).build();

        ProductOrderAttributeValueChangeEvent productOrderItemEvent = ProductOrderAttributeValueChangeEvent.builder()
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .eventType(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .event(productOrderAttributePayloadEvent)
                .build();
        Message<ProductOrderAttributeValueChangeEvent> message = MessageBuilder.withPayload(productOrderItemEvent).build();

        Mockito.doThrow(new RuntimeException("Unexpected database error"))
                .when(productOrderEventService)
                .addProductOrderItem(any());

        // When
        productOrderAttributeValueChangeEventConsumer.accept(message);

        // Then
        Mockito.verify(productOrderEventService, Mockito.times(1)).addProductOrderItem(any());
    }
}