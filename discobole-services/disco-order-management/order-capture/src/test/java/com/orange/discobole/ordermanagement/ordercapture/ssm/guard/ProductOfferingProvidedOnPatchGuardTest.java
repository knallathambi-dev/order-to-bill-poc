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
import com.orange.discobole.ordermanagement.ordercapture.enums.ReferredType;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import org.apache.commons.lang3.RandomStringUtils;
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
class ProductOfferingProvidedOnPatchGuardTest {

    public static final String INVALID_PRODUCT_OFFERING = RandomStringUtils.randomAlphabetic(5);
    @InjectMocks
    private ProductOfferingProvidedOnPatchGuard productOfferingProvidedOnPatchGuard;

    @DisplayName("given re-executed ProductOfferingProvided guard " +
            "when check if product offering was provided " +
            "then return true")
    @Test
    void testProductOfferingProvidedGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PRODUCT_OFFERING_PROVIDED_ON_PATCH_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = productOfferingProvidedOnPatchGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the product offering not provided " +
            "when check if product offering was provided " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testProductOfferingNotProvided() {
        //given
        StateContext<String, String> context = mockStatContext();
        context.getExtendedState().getVariables().put(OrderCaptureConstants.REFERRED_TYPE, ReferredType.PRODUCT);

        //when
        Mono<Boolean> result = productOfferingProvidedOnPatchGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_OFFERING_PROVIDED_ON_PATCH_GUARD);

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

    @DisplayName("given invalid referred type " +
            "when check if product offering was provided " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testInvalidReferredType() {
        //given
        StateContext<String, String> context = mockStatContext();
        context.getExtendedState().getVariables().put(OrderCaptureConstants.REFERRED_TYPE, INVALID_PRODUCT_OFFERING);

        //when
        Mono<Boolean> result = productOfferingProvidedOnPatchGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_OFFERING_PROVIDED_ON_PATCH_GUARD);

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

    @DisplayName("given the product offering was provided  " +
            "when check if product offering was provided " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testProductsWereUpdated() {
        //given
        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = productOfferingProvidedOnPatchGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_OFFERING_PROVIDED_ON_PATCH_GUARD);

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

    private DefaultStateContext<String, String> mockStatContext() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.REFERRED_TYPE, ReferredType.PRODUCT_OFFERING);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}