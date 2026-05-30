// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.validator.ProductOrderStateChangeEventValidator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class ProductOrderStateChangeEventHandlerImplTest {

    public static final String INVALID_PRODUCT_ORDER_EVENT_JSON_FILE = "/invalidProductOrderEvent.json";
    public static final String VALID_PRODUCT_ORDER_EVENT_JSON_FILE = "/validProductOrderEvent.json";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("given an invalid event" + "when check event " + "then throw Exception")
    @Test
    void testCheckProductOrderEvent_InvalidEvent() {
        // given
        ProductOrderStateChangeEvent event = JsonUtil.readObjectFromResource(INVALID_PRODUCT_ORDER_EVENT_JSON_FILE, new TypeReference<>() {
        });
        //when
        //then
        Assertions.assertThrows(CoodNonRecoverableAndNonRetryableException.class, () -> ProductOrderStateChangeEventValidator.checkProductOrderEvent(event));
    }

    @DisplayName("given a valid event, when checking product order event, then log valid event")
    @Test
    void testCheckProductOrderEvent_validEvent() throws ProductOrderValidationException {

        //given
        ProductOrderStateChangeEvent event = JsonUtil.readObjectFromResource(VALID_PRODUCT_ORDER_EVENT_JSON_FILE, new TypeReference<>() {
        });

        //when
        ProductOrderStateChangeEventValidator.checkProductOrderEvent(event);

        //then
        Assertions.assertDoesNotThrow(() -> ProductOrderStateChangeEventValidator.checkProductOrderEvent(event));
    }

    @DisplayName("given an valid event" + "when check product order state " + "then throw Exception")
    @Test
    void testIsProductOrderStatus_accepted() {
        //given

        ProductOrderStateChangeEvent validEvent = JsonUtil.readObjectFromResource(VALID_PRODUCT_ORDER_EVENT_JSON_FILE, new TypeReference<>() {
        });

        //when
        boolean result = ProductOrderStateChangeEventValidator.isProductOrderStatusAccepted(validEvent);

        //then
        Assertions.assertTrue(result, "Product order status should be accepted");
    }

    @DisplayName("given an invalid event" + "when check product order state " + "then throw Exception")

    @Test
    void testIsProductOrderStatus_notAccepted() {
        //given

        ProductOrderStateChangeEvent invalidEvent = JsonUtil.readObjectFromResource(INVALID_PRODUCT_ORDER_EVENT_JSON_FILE, new TypeReference<>() {
        });

        //when
        boolean result = ProductOrderStateChangeEventValidator.isProductOrderStatusAccepted(invalidEvent);

        //then
        Assertions.assertFalse(result, "Product order status should be accepted");
    }
}

