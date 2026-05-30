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
class PartyRolesCheckingGuardTest {
    @InjectMocks
    private PartyRolesCheckingGuard partyRolesCheckingGuard;

    @DisplayName("given re-executed party roles checking guard " +
            "when check if party roles were checked " +
            "then return true")
    @Test
    void testPartyRolesCheckingGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PARTY_ROLES_CHECKING_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = partyRolesCheckingGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given unchecked party roles " +
            "when check if party roles were checked " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testUncheckedPartyRoles() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.FALSE);

        //when
        Mono<Boolean> result = partyRolesCheckingGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_ROLES_CHECKING_GUARD);

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

    @DisplayName("given checked party roles " +
            "when check if party roles were checked " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testCheckedPartyRoles() {
        //given
        StateContext<String, String> context = mockStatContext(Boolean.TRUE);

        //when
        Mono<Boolean> result = partyRolesCheckingGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_ROLES_CHECKING_GUARD);

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

    private DefaultStateContext<String, String> mockStatContext(boolean isPartyRolesChecked) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, isPartyRolesChecked);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}