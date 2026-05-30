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

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.IS_PRODUCT_UPDATED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@ExtendWith(MockitoExtension.class)
class ProductUpdateGuardTest {

    @InjectMocks
    private ProductUpdateGuard productUpdateGuard;

    @Test
    @DisplayName("Given the product update guard is re-executed, " +
            "when checking if the product was updated, " +
            "then the guard returns true")
    void shouldReturnTrueWhenGuardIsReExecutedAndProductWasUpdated() {
        // Given
        StateContext<String, String> context = mockStateContext(TRUE);
        StateMachineUtil.setGuardContext(context, TRUE, GuardNameConstants.PRODUCT_UPDATE_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, TRUE);

        // When
        Mono<Boolean> result = productUpdateGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Given the product is not updated, " +
            "when checking if the product was updated, " +
            "then the guard returns false")
    void shouldReturnFalseWhenProductIsNotUpdated() {
        // Given
        StateContext<String, String> context = mockStateContext(FALSE);

        // When
        Mono<Boolean> result = productUpdateGuard.apply(context);
        Mono<Boolean> guardResultContext =
                StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_UPDATE_GUARD);

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
    @DisplayName("Given the product is updated, " +
            "when checking if the product was updated, " +
            "then the guard returns true")
    void shouldReturnTrueWhenProductIsUpdated() {
        // Given
        StateContext<String, String> context = mockStateContext(TRUE);

        // When
        Mono<Boolean> result = productUpdateGuard.apply(context);
        Mono<Boolean> guardResultContext =
                StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_UPDATE_GUARD);

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

    private DefaultStateContext<String, String> mockStateContext(Boolean isProductUpdated) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(IS_PRODUCT_UPDATED, isProductUpdated);

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