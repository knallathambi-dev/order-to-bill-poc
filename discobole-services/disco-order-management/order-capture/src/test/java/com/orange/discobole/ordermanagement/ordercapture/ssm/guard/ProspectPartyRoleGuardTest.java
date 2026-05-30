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
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelatedPartyRefOrPartyRoleRef;
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

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ProspectPartyRoleGuardTest {

    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PARTY_REF_TYPE = "PartyRef";

    @InjectMocks
    private ProspectPartyRoleGuard prospectPartyRoleGuard;

    @DisplayName("given re-executed prospect party role guard " +
            "when check if party role prospect is provided " +
            "then return true")
    @Test
    void testProspectPartyRoleGuardExecution() {
        //given
        StateContext<String, String> context = mockStateContext(OrderCaptureConstants.PROSPECT);
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.PROSPECT_PARTY_ROLE_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = prospectPartyRoleGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given not prospect party role " +
            "when check if party role prospect is provided " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testPartyRoleNotCustomer() {
        //given
        StateContext<String, String> context = mockStateContext(OrderCaptureConstants.CUSTOMER);
        context.getExtendedState().getVariables().put(OrderCaptureConstants.RELATED_PARTY_ROLE, OrderCaptureConstants.CUSTOMER);

        //when
        Mono<Boolean> result = prospectPartyRoleGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PROSPECT_PARTY_ROLE_GUARD);

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

    @DisplayName("given prospect party role " +
            "when check if party role prospect is provided " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testProductsWereUpdated() {
        //given
        StateContext<String, String> context = mockStateContext(OrderCaptureConstants.PROSPECT);

        //when
        Mono<Boolean> result = prospectPartyRoleGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.PROSPECT_PARTY_ROLE_GUARD);

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

    private DefaultStateContext<String, String> mockStateContext(String partyRole) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createProductOrder(partyRole));

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }

    private ProductOrder createProductOrder(String partyRole) {
        PartyRef partyRef = PartyRef.builder()
                .id(DEFAULT_RELATED_PARTY_ID)
                .atType(PARTY_REF_TYPE)
                .build();

        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .relatedParty(Collections.singletonList(RelatedPartyRefOrPartyRoleRef.builder()
                        .role(partyRole)
                        .partyOrPartyRole(partyRef)
                        .build()))
                .build();
    }
}