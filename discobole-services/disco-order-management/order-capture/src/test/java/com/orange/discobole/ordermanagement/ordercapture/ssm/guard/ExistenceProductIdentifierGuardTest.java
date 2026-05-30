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
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.dto.generated.RelatedEntity;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
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
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class ExistenceProductIdentifierGuardTest {
    public static final String DEFAULT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PRODUCT_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_PARTY_ROLE = "customer";
    public static final String DEFAULT_CHANNEL_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PRODUCT_OFFERING_NAME = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PRODUCT_NAME = RandomStringUtils.randomAlphabetic(5);

    @InjectMocks
    private ExistenceProductIdentifierGuard existenceProductIdentifierGuard;

    @DisplayName("given re-executed product identifier guard " +
            "when check if product identifier exists " +
            "then return true")
    @Test
    void testProductOfferingGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.EXISTENCE_PRODUCT_IDENTIFIER_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = existenceProductIdentifierGuard.apply(context);
        String productOfferingId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.PRODUCT_OFFERING_ID);
        String relatedPartyId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.RELATED_PARTY_ID);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        Assertions.assertNull(productOfferingId);
        Assertions.assertNull(relatedPartyId);
    }

    @DisplayName("given missing product identifier " +
            "when check if product identifier exists " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void testMissingProductOffering() {
        //given
        StateContext<String, String> context = mockStatContext();
        context.getExtendedState().getVariables().put(OrderCaptureConstants.PROCESS_RELATED_ENTITY, Collections.emptyList());

        //when
        Mono<Boolean> result = existenceProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.EXISTENCE_PRODUCT_IDENTIFIER_GUARD);

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

    @DisplayName("given product identifier exist " +
            "when check if product identifier exists " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testProductOfferingExist() {
        //given
        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = existenceProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.EXISTENCE_PRODUCT_IDENTIFIER_GUARD);
        String productOfferingId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.OPTIONAL_PRODUCT_OFFERING_ID);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DEFAULT_PRODUCT_OFFERING_ID, productOfferingId);
        Assertions.assertEquals(DEFAULT_PRODUCT_ID, StateMachineUtil.getStringValue(context, OrderCaptureConstants.CONTRACT_PRODUCT_ID));
        Assertions.assertEquals(setProcessRelatedParty().get(0), Objects.requireNonNull(StateMachineUtil.getObjectValue(context, OrderCaptureConstants.RELATED_PARTY, RelatedParty.class)));
    }

    private List<RelatedEntity> setProcessRelatedEntity() {
        RelatedEntity productOfferingRelatedEntity = new RelatedEntity()
                .id(DEFAULT_PRODUCT_OFFERING_ID)
                .name(DEFAULT_PRODUCT_OFFERING_NAME)
                .referredType(OrderCaptureConstants.PRODUCT_OFFERING_TYPE);
        RelatedEntity productRelatedEntity = new RelatedEntity()
                .id(DEFAULT_PRODUCT_ID)
                .name(DEFAULT_PRODUCT_NAME)
                .referredType(OrderCaptureConstants.PRODUCT_TYPE);
        return List.of(productOfferingRelatedEntity, productRelatedEntity);
    }

    private List<RelatedParty> setProcessRelatedParty() {
        return List.of(new RelatedParty()
                .id(DEFAULT_RELATED_PARTY_ID)
                .role(DEFAULT_RELATED_PARTY_ROLE));
    }

    private List<ChannelRef> setProcessChannel() {
        return List.of(new ChannelRef()
                .id(DEFAULT_CHANNEL_ID));
    }

    private DefaultStateContext<String, String> mockStatContext() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.PROCESS_RELATED_ENTITY, setProcessRelatedEntity());
        extendedState.getVariables().put(OrderCaptureConstants.PROCESS_RELATED_PARTY, setProcessRelatedParty());
        extendedState.getVariables().put(OrderCaptureConstants.PROCESS_CHANNEL, setProcessChannel());

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}