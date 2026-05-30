// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.OrderValidityService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.exception.DiscoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType.ACKNOWLEDGED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUpdateActionTest {

    public static final String PRODUCT_ORDER_TYPE = "ProductOrder";
    private static final String HREF = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final Instant CREATION_DATE = Instant.now();
    private static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderService productOrderService;

    @InjectMocks
    private OrderUpdateAction orderUpdateAction;
    @Mock
    private OrderValidityService validityCharacteristicHelper;

    @Test
    @DisplayName("Given re-executed order update action, " +
            "when apply is called, " +
            "then product order is not updated")
    void shouldNotUpdateProductOrderWhenReExecuted() {
        // Given
        StateContext<String, String> context = createStateContext(null, true);

        // When
        Mono<Void> result = orderUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, false));
    }

    @Test
    @DisplayName("Given a DiscoException with a message during product order update, " +
            "when apply is called, " +
            "then isProductOrderUpdated is set to false and description context is set")
    void shouldSetIsProductOrderUpdatedToFalseAndDescriptionContextOnDiscoException() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = createStateContext(productOrder, false);
        String errorMessage = "Unable to update product order";

        doNothing().when(validityCharacteristicHelper).adjustOrderRequestedCompletionDate(productOrder, RELATED_PARTY_ID);
        doNothing().when(validityCharacteristicHelper).handleInvalidValidityCharacteristics(productOrder, RELATED_PARTY_ID, false);
        doThrow(new DiscoException(errorMessage)).when(productOrderService).updateProductOrderInventoryState(any(), eq(ACKNOWLEDGED));

        // When
        Mono<Void> result = orderUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, false));
        Assertions.assertEquals(errorMessage, context.getExtendedState().getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given a RuntimeException during product order update, " +
            "when apply is called, " +
            "then isProductOrderUpdated is set to false")
    void shouldHandleGenericExceptionAndSetIsProductOrderUpdatedToFalseOnRuntimeException() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = createStateContext(productOrder, false);

        doThrow(new RuntimeException("Unexpected error")).when(productOrderService).updateProductOrderInventoryState(any(), eq(ACKNOWLEDGED));
        doNothing().when(validityCharacteristicHelper).adjustOrderRequestedCompletionDate(productOrder, RELATED_PARTY_ID);
        doNothing().when(validityCharacteristicHelper).handleInvalidValidityCharacteristics(productOrder, RELATED_PARTY_ID, false);

        // When
        Mono<Void> result = orderUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, false));
    }

    @Test
    @DisplayName("Given a valid product order, " +
            "when apply is called, " +
            "then product order is updated successfully")
    void shouldUpdateProductOrderInValidAcquisitionCase() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = createStateContext(productOrder, false);

        doNothing().when(validityCharacteristicHelper).adjustOrderRequestedCompletionDate(productOrder, RELATED_PARTY_ID);
        doNothing().when(validityCharacteristicHelper).handleInvalidValidityCharacteristics(productOrder, RELATED_PARTY_ID, false);

        // When
        Mono<Void> result = orderUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verify(productOrderService, times(1)).updateProductOrderInventoryState(productOrder, ACKNOWLEDGED);
        assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, false));
    }

    private ProductOrder createProductOrder() {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID)
                .action(ItemActionType.ADD)
                .product(ProductRef.builder()
                        .id(PRODUCT_ID)
                        .atType("ProductRef")
                        .build())
                .state(ProductOrderItemStateType.DRAFT)
                .build();

        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .href(HREF)
                .creationDate(CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(Collections.singletonList(productOrderItem))
                .atType(PRODUCT_ORDER_TYPE)
                .build();
    }

    private DefaultStateContext<String, String> createStateContext(ProductOrder productOrder, boolean isReExecution) {
        DefaultExtendedState extendedState = new DefaultExtendedState();

        com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = new com.orange.discobole.processflow.dto.generated.RelatedParty();
        relatedParty.setId(RELATED_PARTY_ID);
        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);

        if (productOrder != null) {
            extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        }

        if (isReExecution) {
            extendedState.getVariables().put(StateMachineUtil.ON_METHOD, true);
        }

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, null, null, null, null);
    }
}