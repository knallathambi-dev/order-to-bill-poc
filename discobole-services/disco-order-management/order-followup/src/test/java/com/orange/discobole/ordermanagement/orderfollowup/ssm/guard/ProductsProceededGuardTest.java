// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.ssm.guard;

import com.orange.discobole.ordermanagement.orderfollowup.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderfollowup.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.PRODUCT_ORDER_STATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ProductsProceededGuardTest {

    @Mock
    private ProductOrderEventService productOrderEventService;

    @InjectMocks
    private ProductsProceededGuard productsProceededGuard;

    @Test
    @DisplayName("Given the guard is re-executed, " +
            "when checking if all products are proceeded, " +
            "then the guard returns true")
    void shouldReturnTrueWhenGuardIsReExecutedAndAllProductsAreProceeded() {
        // Given
        StateContext<String, String> context = mockStateContext(ProductOrderStateType.PARTIAL);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = productsProceededGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Given the product order state is INPROGRESS, " +
            "when checking if all products are proceeded, " +
            "then the guard returns false and does not delete the product order event")
    void shouldReturnFalseWhenOrderStateIsInProgress() {
        // Given
        StateContext<String, String> context = mockStateContext(ProductOrderStateType.INPROGRESS);

        // When
        Mono<Boolean> result = productsProceededGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);

        // Then
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        Mockito.verify(productOrderEventService, Mockito.times(0)).deleteByProductOrderId(any());
    }

    @Test
    @DisplayName("Given an error occurs when deleting the product order event, " +
            "when checking if all products are proceeded, " +
            "then the guard returns false")
    void shouldReturnFalseWhenDeletingProductOrderEventThrowsError() {
        // Given
        StateContext<String, String> context = mockStateContext(ProductOrderStateType.PARTIAL);
        doThrow(NullPointerException.class).when(productOrderEventService).deleteByProductOrderId(any());

        // When
        Mono<Boolean> result = productsProceededGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);

        // Then
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Given the product order state is PARTIAL, " +
            "when checking if all products are proceeded, " +
            "then the guard returns true and deletes the product order event")
    void shouldReturnTrueWhenOrderStateIsPartialAndAllProductsAreProceeded() {
        // Given
        StateContext<String, String> context = mockStateContext(ProductOrderStateType.PARTIAL);

        // When
        Mono<Boolean> result = productsProceededGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
        Mockito.verify(productOrderEventService, Mockito.times(1)).deleteByProductOrderId(any());
    }

    private DefaultStateContext<String, String> mockStateContext(ProductOrderStateType orderState) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(PRODUCT_ORDER_STATE, orderState);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null, extendedState, null);

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