// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.kafka.consumer;

import com.orange.discobole.ordermanagement.event.om.Command;
import com.orange.discobole.ordermanagement.event.om.ProductOrderCommand;
import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadCommand;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOrderCommandConsumerTest {
    private static final String PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);
    @Mock
    private ProductOrderService productOrderService;

    @InjectMocks
    private ProductOrderCommandConsumer productOrderCommandConsumer;
    private ProductOrder productOrder;
    private ProductOrderPayloadCommand productOrderPayloadCommand;
    private ProductOrderCommand productOrderCommand;
    private Message<ProductOrderCommand> message;

    @BeforeEach
    void setUp() {
        productOrder = ProductOrder.builder().id(PRODUCT_ID).build();
        productOrderPayloadCommand = ProductOrderPayloadCommand.builder().productOrder(productOrder).build();
    }

    @Test
    @DisplayName("Given a null event type, " +
            "when accepting the message, " +
            "then no service methods are called")
    void shouldNotCallServiceMethodsWhenEventTypeIsNull() {
        // Given
        productOrderCommand = ProductOrderCommand.builder().eventType(null).build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verifyNoInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a null event, " +
            "when accepting the message, " +
            "then no service methods are called")
    void shouldNotCallServiceMethodsWhenEventIsNull() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.PRODUCT_ORDER_STATE_CHANGE_COMMAND)
                .event(null)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verifyNoInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a null product order, " +
            "when accepting the message, " +
            "then no service methods are called")
    void shouldNotCallServiceMethodsWhenProductOrderIsNull() {
        // Given
        productOrderPayloadCommand = ProductOrderPayloadCommand.builder().productOrder(null).build();
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.PRODUCT_ORDER_STATE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verifyNoInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a null product order ID, " +
            "when accepting the message, " +
            "then no service methods are called")
    void shouldNotCallServiceMethodsWhenProductOrderIdIsNull() {
        // Given
        productOrder = ProductOrder.builder().build();
        productOrderPayloadCommand = ProductOrderPayloadCommand.builder().productOrder(productOrder).build();
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.PRODUCT_ORDER_STATE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verifyNoInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid product order state change command, " +
            "when accepting the message, " +
            "then updateState is called")
    void shouldUpdateStateWhenProductOrderStateChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.PRODUCT_ORDER_STATE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateState(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid realizing resource value change command, " +
            "when accepting the message, " +
            "then updateProductOrderRealizingResource is called")
    void shouldUpdateProductOrderRealizingResourceWhenRealizingResourceValueChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.REALIZING_RESOURCE_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateProductOrderRealizingResource(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid order items and order total price value change command, " +
            "when accepting the message, " +
            "then updateOrderItemsAndOrderTotalPrice is called")
    void shouldUpdateOrderItemsAndOrderTotalPriceWhenOrderItemsAndOrderTotalPriceValueChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.ORDER_ITEMS_AND_ORDER_TOTAL_PRICE_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateOrderItemsAndOrderTotalPrice(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid payment value change command, " +
            "when accepting the message, " +
            "then updateProductOrderPayment is called")
    void shouldUpdateProductOrderPaymentWhenPaymentValueChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.PAYMENT_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateProductOrderPayment(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid related parties value change command, " +
            "when accepting the message, " +
            "then updateProductOrderRelatedParties is called")
    void shouldUpdateProductOrderRelatedPartiesWhenRelatedPartiesValueChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.RELATED_PARTIES_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateProductOrderRelatedParties(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid billing account value change command, " +
            "when accepting the message, " +
            "then updateProductOrderBillingAccount is called")
    void shouldUpdateProductOrderBillingAccountWhenBillingAccountValueChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.BILLING_ACCOUNT_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateProductOrderBillingAccount(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid product value change command, " +
            "when accepting the message, " +
            "then updateProductOrderProduct is called")
    void shouldUpdateProductOrderProductRefWhenProductRefValueChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.PRODUCT_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateProductOrderProduct(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a valid appointment value change command, " +
            "when accepting the message, " +
            "then updateProductOrderAppointment is called")
    void shouldUpdateProductOrderAppointmentWhenAppointmentValueChangeCommandIsReceived() {
        // Given

        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.APPOINTMENT_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();
        // When

        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateProductOrderAppointment(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

    @Test
    @DisplayName("Given a requested completion date value change command, " +
            "when accepting the message, " +
            "then updateRequestedCompletionDate is called")
    void shouldUpdateRequestedCompletionDateWhenRequestedCompletionDateValueChangeCommandIsReceived() {
        // Given
        productOrderCommand = ProductOrderCommand.builder()
                .eventType(Command.EventType.REQUESTED_COMPLETION_DATE_VALUE_CHANGE_COMMAND)
                .event(productOrderPayloadCommand)
                .build();
        message = MessageBuilder.withPayload(productOrderCommand).build();

        // When
        productOrderCommandConsumer.accept(message);

        // Then
        verify(productOrderService, times(1)).updateRequestedCompletionDate(productOrder);
        verifyNoMoreInteractions(productOrderService);
    }

}