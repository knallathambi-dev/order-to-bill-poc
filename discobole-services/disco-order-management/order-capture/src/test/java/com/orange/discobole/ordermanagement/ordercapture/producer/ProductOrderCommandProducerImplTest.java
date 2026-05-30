// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.event.om.ProductOrderCommand;
import com.orange.discobole.ordermanagement.ordercapture.IntegrationTest;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import static com.orange.discobole.ordermanagement.event.om.Command.EventType.PRODUCT_ORDER_STATE_CHANGE_COMMAND;
import static org.junit.jupiter.api.Assertions.assertThrows;


@IntegrationTest
@Import(TestChannelBinderConfiguration.class)
class ProductOrderCommandProducerImplTest {

    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String PRODUCT_ORDER_COMMAND_TOPIC = "disco.order-management.productOrderChange-command";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductOrderCommandProducer productOrderCommandProducer;

    @Autowired
    private OutputDestination outputDestination;

    @DisplayName("Given null event type, " +
            "when publish product order event, " +
            "then throw IllegalArgumentException")
    @Test
    void shouldThrowExceptionWhenPublishingWithNullEventType() {
        // Given
        ProductOrder productOrderDTO = ProductOrder
                .builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> productOrderCommandProducer.publishCommand(productOrderDTO, null));
    }

    @DisplayName("Given null product order, " +
            "when publish product order event, " +
            "then throw IllegalArgumentException")
    @Test
    void shouldThrowExceptionWhenPublishingWithNullProductOrder() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class, () -> productOrderCommandProducer.publishCommand(null, PRODUCT_ORDER_STATE_CHANGE_COMMAND));
    }

    @DisplayName("Given null product order id, " +
            "when publish product order event, " +
            "then throw IllegalArgumentException")
    @Test
    void shouldThrowExceptionWhenPublishingWithNullProductOrderId() {
        // Given
        ProductOrder productOrderDTO = ProductOrder
                .builder()
                .id(null)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> productOrderCommandProducer.publishCommand(productOrderDTO, PRODUCT_ORDER_STATE_CHANGE_COMMAND));
    }

    @DisplayName("Given valid product order event, " +
            "when publish product order event,  " +
            "then event is publish successfully")
    @Test
    void shouldPublishProductOrderEventSuccessfully() throws JsonProcessingException {
        // Given
        ProductOrder productOrderDTO = ProductOrder
                .builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .build();

        // When
        productOrderCommandProducer.publishCommand(productOrderDTO, PRODUCT_ORDER_STATE_CHANGE_COMMAND);

        // Then
        ProductOrderCommand productOrderCommand = getLatestPublishedEvent();

        Assertions.assertNotNull(productOrderCommand.getEventId());
        Assertions.assertNotNull(productOrderCommand.getEventTime());
        Assertions.assertEquals(DEFAULT_PRODUCT_ORDER_ID, productOrderCommand.getEvent().getProductOrder().getId());
        Assertions.assertEquals(PRODUCT_ORDER_STATE_CHANGE_COMMAND, productOrderCommand.getEventType());
    }

    private ProductOrderCommand getLatestPublishedEvent() throws JsonProcessingException {
        Message<byte[]> received = outputDestination.receive(1000, PRODUCT_ORDER_COMMAND_TOPIC);

        return objectMapper.readValue(new String(received.getPayload()), ProductOrderCommand.class);
    }
}