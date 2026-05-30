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
import com.orange.discobole.ordermanagement.orderfollowup.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.PRODUCT_ORDER_STATE;

@ExtendWith(MockitoExtension.class)
class ProductOrderStateGuardTest {

    @InjectMocks
    private ProductOrderStateGuard productOrderStateGuard;

    @Test
    @DisplayName("Given re-executed product order state tasks guard, " +
            "when checking if product order state is partial, " +
            "then guard returns true")
    void shouldReturnTrueWhenGuardIsReExecutedAndProductOrderStateIsPartial() {
        // Given
        StateContext<String, String> context = mockStateContext(ProductOrderStateType.PARTIAL);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PRODUCT_ORDER_STATE_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = productOrderStateGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Given the product order state is not partial, " +
            "when checking if product order state is partial, " +
            "then guard returns false")
    void shouldReturnFalseWhenProductOrderStateIsNotPartial() {
        // Given
        StateContext<String, String> context = mockStateContext(ProductOrderStateType.COMPLETED);

        // When
        Mono<Boolean> result = productOrderStateGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_STATE_GUARD);

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
    @DisplayName("Given the product order state is partial, " +
            "when checking if product order state is partial, " +
            "then guard returns true")
    void shouldReturnTrueWhenProductOrderStateIsPartial() {
        // Given
        StateContext<String, String> context = mockStateContext(ProductOrderStateType.PARTIAL);

        // When
        Mono<Boolean> result = productOrderStateGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_STATE_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    private DefaultStateContext<String, String> mockStateContext(ProductOrderStateType orderState) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(PRODUCT_ORDER_STATE, orderState);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(
                null, null, null, null, null, extendedState, null
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