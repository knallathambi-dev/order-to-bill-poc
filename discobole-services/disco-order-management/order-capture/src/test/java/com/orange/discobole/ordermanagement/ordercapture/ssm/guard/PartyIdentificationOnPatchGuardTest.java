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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PartyIdentificationOnPatchGuardTest {

    public static final String RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String EMPLOYEE = "Employee";

    @InjectMocks
    private PartyIdentificationOnPatchGuard partyIdentificationOnPatchGuard;

    @DisplayName("given re-executed party identification guard " +
            "when check if party was identified " +
            "then return true")
    @Test
    void testPartyIdentificationGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext(RELATED_PARTY_ID, OrderCaptureConstants.CUSTOMER);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PARTY_IDENTIFICATION_ON_PATCH_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = partyIdentificationOnPatchGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given blank party id " +
            "when check if party was identified " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testBlankPartyId() {
        //given
        StateContext<String, String> context = mockStatContext("", OrderCaptureConstants.CUSTOMER);

        //when
        Mono<Boolean> result = partyIdentificationOnPatchGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_PATCH_GUARD);

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
        StateContext<String, String> context = mockStatContext(RELATED_PARTY_ID, "");

        //when
        Mono<Boolean> result = partyIdentificationOnPatchGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_PATCH_GUARD);

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

    @DisplayName("given party not identified " +
            "when check if party was identified " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testPartyNotIdentified() {
        //given
        StateContext<String, String> context = mockStatContext(RELATED_PARTY_ID, EMPLOYEE);

        //when
        Mono<Boolean> result = partyIdentificationOnPatchGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_PATCH_GUARD);

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

    @DisplayName("given identified party " +
            "when check if party was identified " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testPartyIdentified() {
        //given
        StateContext<String, String> context = mockStatContext(RELATED_PARTY_ID, OrderCaptureConstants.CUSTOMER);

        //when
        Mono<Boolean> result = partyIdentificationOnPatchGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_PATCH_GUARD);

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

    private StateContext<String, String> mockStatContext(String partyId, String partyRole) {
        ExtendedState extendedState = new DefaultExtendedState();
        List<RelatedParty> relatedPartyList = List.of(new RelatedParty().id(partyId).role(partyRole));
        extendedState.getVariables().put(OrderCaptureConstants.TASK_RELATED_PARTY, relatedPartyList);
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}