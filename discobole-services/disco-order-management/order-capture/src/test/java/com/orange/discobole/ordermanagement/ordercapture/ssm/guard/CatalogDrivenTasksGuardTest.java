// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
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

@ExtendWith(MockitoExtension.class)
class CatalogDrivenTasksGuardTest {

    @InjectMocks
    private CatalogDrivenTasksGuard catalogDrivenTasksGuard;

    @DisplayName("Given re-executed catalog driven tasks guard, " +
            "when check if catalog driven tasks were set, " +
            "then return true")
    @Test
    void shouldReturnTrueWhenCatalogDrivenTasksAreReExecuted() {
        // Given
        StateContext<String, String> context = mockStatContext(false);
        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.CATALOG_DRIVEN_TASKS_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, true);

        // When
        Mono<Boolean> result = catalogDrivenTasksGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("Given catalog driven tasks not set, " +
            "when check if catalog driven tasks were set, " +
            "then the guard result in context is false and return false")
    @Test
    void shouldReturnFalseWhenCatalogDrivenTasksNotSet() {
        // Given
        StateContext<String, String> context = mockStatContext(false);

        // When
        Mono<Boolean> result = catalogDrivenTasksGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.CATALOG_DRIVEN_TASKS_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
    }

    @DisplayName("Given catalog driven tasks set, " +
            "when check if catalog driven tasks were set, " +
            "then the guard result in context is true and return true")
    @Test
    void shouldReturnTrueWhenCatalogDrivenTasksAreSet() {
        // Given
        StateContext<String, String> context = mockStatContext(true);

        // When
        Mono<Boolean> result = catalogDrivenTasksGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.CATALOG_DRIVEN_TASKS_GUARD);

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

    private DefaultStateContext<String, String> mockStatContext(boolean areCatalogDrivenTasksSet) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.ARE_CATALOG_DRIVEN_TASKS_SET, areCatalogDrivenTasksSet);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}