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
import com.orange.discobole.processflow.dto.generated.RelatedParty;
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
class PartyIdentificationOnPostGuardTest {
    public static final String EMPLOYEE = "Employee";
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);

    @InjectMocks
    private PartyIdentificationOnPostGuard partyIdentificationOnPostGuard;

    @DisplayName("given re-executed party identification guard " +
            "when check if party was identified " +
            "then return true")
    @Test
    void testPartyIdentificationGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext(OrderCaptureConstants.CUSTOMER);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PARTY_IDENTIFICATION_ON_POST_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = partyIdentificationOnPostGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given party not identified " +
            "when check if party was identified " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testPartyNotIdentified() {
        //given
        StateContext<String, String> context = mockStatContext(EMPLOYEE);

        //when
        Mono<Boolean> result = partyIdentificationOnPostGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_POST_GUARD);

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

    @DisplayName("given empty party role " +
            "when check if party was identified " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testEmptyPartyRole() {
        //given
        StateContext<String, String> context = mockStatContext("");

        //when
        Mono<Boolean> result = partyIdentificationOnPostGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_POST_GUARD);

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

    @DisplayName("given customer party role " +
            "when check if party was identified " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testCustomerPartyRole() {
        //given
        StateContext<String, String> context = mockStatContext(OrderCaptureConstants.CUSTOMER);

        //when
        Mono<Boolean> result = partyIdentificationOnPostGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_POST_GUARD);

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

    @DisplayName("given prospect party role " +
            "when check if party was identified " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testProspectPartyRole() {
        //given
        StateContext<String, String> context = mockStatContext(OrderCaptureConstants.PROSPECT);

        //when
        Mono<Boolean> result = partyIdentificationOnPostGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_POST_GUARD);

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

    private DefaultStateContext<String, String> mockStatContext(String partyRole) {
        ExtendedState extendedState = new DefaultExtendedState();

        RelatedParty relatedParty = new RelatedParty()
                .id(DEFAULT_RELATED_PARTY_ID)
                .role(partyRole);

        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}