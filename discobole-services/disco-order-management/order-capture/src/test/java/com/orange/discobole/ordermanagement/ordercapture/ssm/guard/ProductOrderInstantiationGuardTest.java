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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.ObjectState;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.InitialTransition;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductOrderInstantiationGuardTest {
    public static final String START_STATE = "start";
    public static final String EVENT = "event";

    @InjectMocks
    private ProductOrderInstantiationGuard productOrderInstantiationGuard;

    @DisplayName("given re-executed product order instantiation guard " +
            "when check if product order was instantiated " +
            "then return true")
    @Test
    void testProductOrderInstantiationGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PRODUCT_ORDER_INSTANTIATION_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = productOrderInstantiationGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the product order not instantiated " +
            "when check if product order was instantiated " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testProductOrderNotInstantiated() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);

        //when
        Mono<Boolean> result = productOrderInstantiationGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_INSTANTIATION_GUARD);

        //then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the product order was instantiated " +
            "when check if product order was instantiated " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testProductOrderWasInstantiated() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.TRUE);

        //when
        Mono<Boolean> result = productOrderInstantiationGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_INSTANTIATION_GUARD);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    private DefaultStateContext<String, String> mockStatContext(boolean isProductOrderInstantiated) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.IS_PRODUCT_ORDER_INSTANTIATED, isProductOrderInstantiated);

        State<String, String> state = new ObjectState<>(START_STATE);
        Transition<String, String> transition = new InitialTransition<>(state);
        Message<String> event = MessageBuilder.withPayload(EVENT).build();
        MessageHeaders header = new MessageHeaders(new HashMap<>());
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(Collections.emptyList(), Collections.emptyList(), state, transition, event,
                extendedState, UUID.randomUUID());

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, event, header, extendedState, transition, stateMachine, state,
                state, null);
    }
}