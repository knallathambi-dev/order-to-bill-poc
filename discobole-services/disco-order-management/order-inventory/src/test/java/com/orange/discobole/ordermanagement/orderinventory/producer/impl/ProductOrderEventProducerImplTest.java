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
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderinventory.IntegrationTest;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.impl.ProductOrderEventProducerImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import static com.orange.discobole.ordermanagement.commons.enumeration.EventType.PRODUCT_ORDER_STATE_CHANGE_EVENT;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@Import(TestChannelBinderConfiguration.class)
class ProductOrderEventProducerImplTest {

    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String PRODUCT_ORDER_EVENT_TOPIC = "disco.order-management.productOrderStateChange-event";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductOrderEventProducerImpl productOrderEventProducer;

    @Autowired
    private OutputDestination outputDestination;

    @Test
    @DisplayName("Given a null event type, " +
            "when publishing a product order event, " +
            "then IllegalArgumentException is thrown")
    void shouldThrowIllegalArgumentExceptionWhenEventTypeIsNull() {
        // Given
        ProductOrder productOrder = createProductOrder();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> productOrderEventProducer.publishEvent(productOrder, null));
    }

    @Test
    @DisplayName("Given a null product order, " +
            "when publishing a product order event, " +
            "then IllegalArgumentException is thrown")
    void shouldThrowIllegalArgumentExceptionWhenProductOrderIsNull() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class, () -> productOrderEventProducer.publishEvent(null, PRODUCT_ORDER_STATE_CHANGE_EVENT));
    }

    @Test
    @DisplayName("Given a product order with null ID, " +
            "when publishing a product order event, " +
            "then IllegalArgumentException is thrown")
    void shouldThrowIllegalArgumentExceptionWhenProductOrderIdIsNull() {
        // Given
        ProductOrder productOrder = createProductOrder();
        productOrder.setId(null);

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> productOrderEventProducer.publishEvent(productOrder, PRODUCT_ORDER_STATE_CHANGE_EVENT)
        );
    }

    @Test
    @DisplayName("Given a valid product order, " +
            "when publishing a product order event, " +
            "then the event is published successfully")
    void shouldPublishProductOrderStateChangeEventSuccessfully() throws JsonProcessingException {
        // Given
        ProductOrder productOrder = createProductOrder();

        // When
        productOrderEventProducer.publishEvent(productOrder, PRODUCT_ORDER_STATE_CHANGE_EVENT);

        // Then
        ProductOrderStateChangeEvent productOrderEvent = getLatestPublishedEvent();

        assertNotNull(productOrderEvent.getEventId());
        assertNotNull(productOrderEvent.getEventTime());
        assertEquals(DEFAULT_PRODUCT_ORDER_ID, productOrderEvent.getEvent().getProductOrder().getId());
        assertEquals(PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue(), productOrderEvent.getEventType());
    }

    private ProductOrder createProductOrder() {
        return ProductOrder.builder().id(DEFAULT_PRODUCT_ORDER_ID).build();
    }

    private ProductOrderStateChangeEvent getLatestPublishedEvent() throws JsonProcessingException {
        Message<byte[]> received = outputDestination.receive(1000, PRODUCT_ORDER_EVENT_TOPIC);

        assert received != null;
        return objectMapper.readValue(new String(received.getPayload()), ProductOrderStateChangeEvent.class);
    }
}