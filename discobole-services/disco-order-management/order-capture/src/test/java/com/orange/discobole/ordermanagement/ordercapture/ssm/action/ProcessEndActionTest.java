// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.ObjectState;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.DefaultExternalTransition;
import org.springframework.statemachine.transition.Transition;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProcessEndActionTest {

    @InjectMocks
    private ProcessEndAction processEndAction;

    @Test
    @DisplayName("Given re-executed end process action, " +
            "when apply is invoked, " +
            "then the state machine transitions are not cleared")
    void shouldNotClearTransitionsOnReExecution() {
        // Given
        StateContext<String, String> context = createMockStateContext();
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Void> result = processEndAction.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(CollectionUtils.isEmpty(context.getStateMachine().getTransitions()));
    }

    @Test
    @DisplayName("Given end process is invoked, " +
            "when apply is called, " +
            "then the state machine transitions are cleared")
    void shouldClearTransitionsOnEndProcess() {
        // Given
        StateContext<String, String> context = createMockStateContext();

        // When
        Mono<Void> result = processEndAction.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertTrue(CollectionUtils.isEmpty(context.getStateMachine().getTransitions()));
    }

    private DefaultStateContext<String, String> createMockStateContext() {
        ObjectState<String, String> sourceState = new ObjectState<>("CANCEL");
        ObjectState<String, String> targetState = new ObjectState<>("END");

        DefaultExternalTransition<String, String> transition = new DefaultExternalTransition<>(
                sourceState, targetState, null, "cancelProcess", null, null, null);

        List<Transition<String, String>> transitions = new ArrayList<>();
        transitions.add(transition);

        Collection<State<String, String>> states = List.of(sourceState, targetState);
        ExtendedState extendedState = new DefaultExtendedState();

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(states, transitions, sourceState);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null,
                extendedState, null, stateMachine, null, null, null);
    }
}