// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.producer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributeValueChangeEvent;
import com.orange.discobole.ordermanagement.orderinventory.IntegrationTest;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.impl.ProductOrderAttributeValueChangeEventProducerImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import java.util.List;

import static com.orange.discobole.ordermanagement.commons.enumeration.EventType.PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@Import(TestChannelBinderConfiguration.class)
class ProductOrderAttributeValueChangeEventProducerImplTest {
    private static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(8);
    private static final String DEFAULT_PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(8);
    private static final String TOPIC = "disco.order-management.productOrderAttributeValueChange-event";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductOrderAttributeValueChangeEventProducerImpl productOrderAttributeValueChangeEventProducer;
    @Autowired
    private OutputDestination outputDestination;

    @DisplayName("Given a null event type, " +
            "when attempting to publish a product order attribute value change event, " +
            "then an IllegalArgumentException should be thrown")
    @Test
    void shouldThrowExceptionWhenEventTypeIsNull() {
        // Given
        ProductOrder productOrder = createDefaultProductOrder();

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () ->
                productOrderAttributeValueChangeEventProducer.publishEvent(productOrder, null));
    }

    @DisplayName("Given a null product order, " +
            "when attempting to publish a product order attribute value change event, " +
            "then an IllegalArgumentException should be thrown")
    @Test
    void shouldThrowExceptionWhenProductOrderIsNull() {
        // Given
        // When
        // Then
        assertThrows(IllegalArgumentException.class, () ->
                productOrderAttributeValueChangeEventProducer.publishEvent(null, PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT));
    }

    @DisplayName("Given a product order with a null ID, " +
            "when attempting to publish a product order attribute value change event, " +
            "then an IllegalArgumentException should be thrown")
    @Test
    void shouldThrowExceptionWhenProductOrderIdIsNull() {
        // Given
        ProductOrder productOrder = createDefaultProductOrder();
        productOrder.setId(null);

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () ->
                productOrderAttributeValueChangeEventProducer.publishEvent(productOrder, PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT));
    }

    @DisplayName("Given a product order with no items, " +
            "when attempting to publish a product order attribute value change event, " +
            "then an IllegalArgumentException should be thrown")
    @Test
    void shouldThrowExceptionWhenProductOrderItemsAreNull() {
        // Given
        ProductOrder productOrder = createDefaultProductOrder();
        productOrder.setProductOrderItem(null);

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () ->
                productOrderAttributeValueChangeEventProducer.publishEvent(productOrder, PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT));
    }

    @DisplayName("Given a valid product order, " +
            "when publishing a product order attribute value change event, " +
            "then the event should be published successfully")
    @Test
    void shouldPublishEventSuccessfully() throws JsonProcessingException {
        // Given
        ProductOrder productOrder = createDefaultProductOrder();

        // When
        productOrderAttributeValueChangeEventProducer.publishEvent(productOrder, PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT);

        // Then
        ProductOrderAttributeValueChangeEvent publishedEvent = fetchLatestPublishedEvent();

        assertNotNull(publishedEvent.getEventId(), "Event ID should not be null");
        assertNotNull(publishedEvent.getEventTime(), "Event time should not be null");
        assertEquals(DEFAULT_PRODUCT_ORDER_ID, publishedEvent.getEvent().getProductOrder().getId(), "Product order ID should match");
        assertEquals(PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT.getValue(), publishedEvent.getEventType(), "Event type should match");
    }

    private ProductOrder createDefaultProductOrder() {
        ProductOrderItem defaultOrderItem = ProductOrderItem.builder()
                .id(DEFAULT_PRODUCT_ORDER_ITEM_ID)
                .build();

        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .productOrderItem(List.of(defaultOrderItem))
                .build();
    }

    private ProductOrderAttributeValueChangeEvent fetchLatestPublishedEvent() throws JsonProcessingException {
        Message<byte[]> received = outputDestination.receive(1000, TOPIC);

        assert received != null;
        return objectMapper.readValue(new String(received.getPayload()), ProductOrderAttributeValueChangeEvent.class);
    }
}