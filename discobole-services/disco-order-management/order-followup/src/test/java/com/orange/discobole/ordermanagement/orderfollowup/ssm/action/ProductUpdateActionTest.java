// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.ssm.action;

import com.orange.discobole.ordermanagement.orderfollowup.enums.OfupStateType;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderfollowup.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.exception.DiscoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage.ERROR_UPDATING_PRODUCTS_IN_INVENTORY;
import static com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage.PRODUCTS_UPDATE_FAILED_IN_INVENTORY;
import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.*;
import static java.lang.Boolean.FALSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ProductUpdateActionTest {
    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PRODUCT_ORDER_ITEM_EVENT_ID = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderEventService productOrderEventService;

    @Mock
    private ProductInventoryService productInventoryService;

    @InjectMocks
    private ProductUpdateAction productUpdateAction;

    @Test
    @DisplayName("Given the product update action is re-executed, " +
            "when we apply the action, " +
            "then isProductUpdated is set to false")
    void shouldSetIsProductUpdatedToFalseWhenActionIsReExecuted() {
        // Given
        StateContext<String, String> context = mockStateContext();
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Void> result = productUpdateAction.apply(context);

        // Then
        boolean isProductUpdated = StateMachineUtil.getBooleanValue(context, IS_PRODUCT_UPDATED, FALSE);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(isProductUpdated);
    }

    @Test
    @DisplayName("Given an unchecked exception in updateProductsHierarchy, " +
            "when we apply the product update action, " +
            "then isProductUpdated is set to false")
    void shouldSetIsProductUpdatedToFalseWhenUpdateProductsHierarchyThrowsRuntimeException() {
        // Given
        doThrow(RuntimeException.class).when(productInventoryService).updateProductsHierarchy(anyString(), anyString(), any());
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Void> result = productUpdateAction.apply(context);

        // Then
        boolean isProductUpdated = StateMachineUtil.getBooleanValue(context, IS_PRODUCT_UPDATED, FALSE);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(isProductUpdated);
    }

    @Test
    @DisplayName("Given a DiscoException in updateProductsHierarchy with 'ERROR_UPDATING_PRODUCTS_IN_INVENTORY', " +
            "when we apply the product update action, " +
            "then isProductUpdated is set to false")
    void shouldSetIsProductUpdatedToFalseWhenDiscoExceptionOccursWithErrorUpdatingProducts() {
        // Given
        DiscoException exception = new DiscoException(ERROR_UPDATING_PRODUCTS_IN_INVENTORY);
        doThrow(exception).when(productInventoryService).updateProductsHierarchy(anyString(), anyString(), any());
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Void> result = productUpdateAction.apply(context);

        // Then
        boolean isProductUpdated = StateMachineUtil.getBooleanValue(context, IS_PRODUCT_UPDATED, FALSE);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(isProductUpdated);
    }

    @Test
    @DisplayName("Given the inventory is unavailable with 'PRODUCTS_UPDATE_FAILED_IN_INVENTORY', " +
            "when we apply the product update action, " +
            "then isProductUpdated is set to false")
    void shouldSetIsProductUpdatedToFalseWhenProductsUpdateFailedInInventory() {
        // Given
        DiscoException exception = new DiscoException(PRODUCTS_UPDATE_FAILED_IN_INVENTORY);
        doThrow(exception).when(productInventoryService).updateProductsHierarchy(anyString(), anyString(), any());
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Void> result = productUpdateAction.apply(context);

        // Then
        boolean isProductUpdated = StateMachineUtil.getBooleanValue(context, IS_PRODUCT_UPDATED, FALSE);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(isProductUpdated);
    }

    @Test
    @DisplayName("Given a valid product update, " +
            "when we apply the product update action, " +
            "then isProductUpdated is set to true")
    void shouldSetIsProductUpdatedToTrueWhenProductIsSuccessfullyUpdated() {
        // Given
        doNothing().when(productInventoryService).updateProductsHierarchy(anyString(), anyString(), any());
        doNothing().when(productOrderEventService)
                .updateProductItemOfupState(anyString(), anyString(), any(OfupStateType.class));

        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Void> result = productUpdateAction.apply(context);

        // Then
        boolean isProductUpdated = StateMachineUtil.getBooleanValue(context, IS_PRODUCT_UPDATED, FALSE);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertTrue(isProductUpdated);
    }

    private DefaultStateContext<String, String> mockStateContext() {
        ExtendedState extendedState = new DefaultExtendedState();

        extendedState.getVariables().put(PRODUCT_ORDER_ID, DEFAULT_PRODUCT_ORDER_ID);
        extendedState.getVariables().put(PRODUCT_ORDER_ITEM_ID, DEFAULT_PRODUCT_ORDER_ITEM_ID);
        extendedState.getVariables().put(PRODUCT_ORDER_ITEM_EVENT_ID, DEFAULT_PRODUCT_ORDER_ITEM_EVENT_ID);
        extendedState.getVariables().put(PRODUCT_ORDER_STATE, ProductOrderStateType.COMPLETED);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(
                null, null, null, null, null,
                extendedState, null
        );

        return new DefaultStateContext<>(
                StateContext.Stage.TRANSITION,
                null,
                null,
                extendedState,
                null,
                stateMachine,
                null,
                null,
                null
        );
    }
}