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
class ProductOrderUpdateGuardTest {
    @InjectMocks
    private ProductOrderUpdateGuard productOrderUpdateGuard;

    @DisplayName("given re-executed product order update guard " +
            "when check if product order was updated " +
            "then return true")
    @Test
    void testProductOrderUpdateGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PRODUCT_ORDER_UPDATE_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = productOrderUpdateGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the product order not updated " +
            "when check if product order was updated " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testProductOrderNotUpdated() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);

        //when
        Mono<Boolean> result = productOrderUpdateGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_UPDATE_GUARD);

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

    @DisplayName("given the product order was updated " +
            "when check if product order was updated " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testProductOrderWasUpdated() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.TRUE);

        //when
        Mono<Boolean> result = productOrderUpdateGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_UPDATE_GUARD);

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

    private DefaultStateContext<String, String> mockStatContext(boolean isProductOrderUpdated) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, isProductOrderUpdated);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}