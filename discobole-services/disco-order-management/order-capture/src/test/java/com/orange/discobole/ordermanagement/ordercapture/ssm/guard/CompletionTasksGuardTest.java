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
class CompletionTasksGuardTest {

    @InjectMocks
    private CompletionTasksGuard completionTasksGuard;

    @DisplayName("Given re-executed completion tasks guard, " +
            "when check if completion tasks was set, " +
            "then return true")
    @Test
    void shouldReturnTrueWhenReExecuted() {
        // Given
        StateContext<String, String> context = mockStatContext(false);
        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.COMPLETION_TASKS_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, true);

        // When
        Mono<Boolean> result = completionTasksGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("Given the completion tasks not set, " +
            "when check if completion tasks was set, " +
            "then the result of the guard in context is set to false and return false")
    @Test
    void shouldReturnFalseWhenCompletionTasksNotSet() {
        // Given
        StateContext<String, String> context = mockStatContext(false);

        // When
        Mono<Boolean> result = completionTasksGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.COMPLETION_TASKS_GUARD);

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

    @DisplayName("Given the completion tasks was set, " +
            "when check if completion tasks was set, " +
            "then the result of the guard in context is set to true and return true")
    @Test
    void shouldReturnTrueWhenCompletionTasksWasSet() {
        // Given
        StateContext<String, String> context = mockStatContext(true);

        // When
        Mono<Boolean> result = completionTasksGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.COMPLETION_TASKS_GUARD);

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

    private DefaultStateContext<String, String> mockStatContext(boolean isCompletionTasksSet) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.ARE_COMPLETION_TASKS_SET, isCompletionTasksSet);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}