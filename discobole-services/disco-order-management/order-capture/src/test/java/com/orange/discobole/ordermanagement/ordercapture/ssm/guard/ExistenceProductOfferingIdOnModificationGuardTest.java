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
class ExistenceProductOfferingIdOnModificationGuardTest {
    public static final String DEFAULT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);

    @InjectMocks
    private ExistenceProductOfferingIdOnModificationGuard existenceProductOfferingIdOnModificationGuard;

    @DisplayName("given re-executed isExistProductOfferingIdOnModificationGuard guard " +
            "when check if the product offering id is provided on modification guard " +
            "then return true")
    @Test
    void testProductOrderUpdateGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_ID_ON_MODIFICATION_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = existenceProductOfferingIdOnModificationGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given the product offering id is not provided " +
            "when check if the product offering id is provided on modification guard " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testProductOrderNotUpdated() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);

        //when
        Mono<Boolean> result = existenceProductOfferingIdOnModificationGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_ID_ON_MODIFICATION_GUARD);

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

    @DisplayName("given the product offering id is  provided " +
            "when check if the product offering id is provided on modification guard " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testProductWasCreated() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.TRUE);

        //when
        Mono<Boolean> result = existenceProductOfferingIdOnModificationGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_ID_ON_MODIFICATION_GUARD);

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

    private DefaultStateContext<String, String> mockStatContext(boolean isProductOfferingIdProvided) {
        ExtendedState extendedState = new DefaultExtendedState();
        if (isProductOfferingIdProvided) {
            extendedState.getVariables().put(OrderCaptureConstants.OPTIONAL_PRODUCT_OFFERING_ID, DEFAULT_PRODUCT_OFFERING_ID);
        }

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}