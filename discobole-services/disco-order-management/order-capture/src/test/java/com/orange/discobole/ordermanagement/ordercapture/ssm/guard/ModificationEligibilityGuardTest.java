// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingQualificationService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOfferingService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificationEligibilityGuardTest {

    public static final String DEFAULT_OPTIONAL_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PRODUCT_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String START_STATE = "start";
    public static final String EVENT = "event";
    @Mock
    private ProductOfferingService productOfferingService;
    @Mock
    private ProductOfferingQualificationService qualificationService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private ModificationEligibilityGuard modificationEligibilityGuard;


    @DisplayName("given re-executed eligibility guard " +
            "when check optional product Offering qualification " +
            "then return true")
    @Test
    void testEligibilityContractModificationGuardReExecution() {
        //given
        StateContext<String, String> context = mockStatContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.MODIFICATION_ELIGIBILITY_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        //when
        Mono<Boolean> result = modificationEligibilityGuard.apply(context);

        //then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("given exception on validate optional product offering service " +
            "when check optional product offering qualification " +
            "then the result of the guard in context is set to false " +
            "and the description is set to INTERNAL_SERVER_ERROR" +
            "and return false")
    @Test
    void testExceptionProductOfferingService() {
        //given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenThrow(RuntimeException.class);
        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = modificationEligibilityGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.MODIFICATION_ELIGIBILITY_GUARD);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        //then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, description);
    }

    @DisplayName("given CheckCommercialEligibility is disabled " +
            "when check product offering qualification " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testCheckCommercialEligibilityIsDisabled() {
        //given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(ResponseResult.builder()
                .result(Boolean.TRUE)
                .build());

        SettingsEntity settingsEntity = createSettings();
        settingsEntity.setCheckCommercialEligibilityEnabled(false);
        when(settingsService.getSettings()).thenReturn(settingsEntity);

        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = modificationEligibilityGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.MODIFICATION_ELIGIBILITY_GUARD);

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

    @DisplayName("given exception on product offering qualification service " +
            "when check product offering qualification " +
            "then the result of the guard in context is set to false " +
            "and the description is set to INTERNAL_SERVER_ERROR" +
            "and return false")
    @Test
    void testExceptionProductOfferingQualificationService() {
        //given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(ResponseResult.builder()
                .result(Boolean.TRUE)
                .description(DescriptionConstants.LOGICAL_SELECTED_OFFER)
                .build());

        SettingsEntity settingsEntity = createSettings();
        when(settingsService.getSettings()).thenReturn(settingsEntity);

        when(qualificationService.isProductOfferingQualified(any())).thenThrow(RuntimeException.class);
        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = modificationEligibilityGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.MODIFICATION_ELIGIBILITY_GUARD);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        //then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, description);
    }

    @DisplayName("given invalid optional product offering and qualified " +
            "when check optional product Offering Qualification " +
            "then the result of the guard in context is set to false " +
            "and the description field is set to SELECTED_OFFER_NOT_VALID " +
            "and return false")
    @Test
    void testNotValidProductOffering() {
        //given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(ResponseResult.builder()
                .result(Boolean.FALSE)
                .description(DescriptionConstants.SELECTED_OFFER_NOT_VALID)
                .build());
        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = modificationEligibilityGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.MODIFICATION_ELIGIBILITY_GUARD);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        //then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.SELECTED_OFFER_NOT_VALID, description);
    }

    @DisplayName("given valid product offering and not qualified " +
            "when check product offering qualification " +
            "then the result of the guard in context is set to false " +
            "and the description field is set to SELECTED_OFFER_NOT_ELIGIBLE " +
            "and return false")
    @Test
    void testNotQualifiedProductOffering() {
        //given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(ResponseResult.builder()
                .result(Boolean.TRUE)
                .description(DescriptionConstants.LOGICAL_SELECTED_OFFER)
                .build());

        SettingsEntity settingsEntity = createSettings();
        when(settingsService.getSettings()).thenReturn(settingsEntity);

        when(qualificationService.isProductOfferingQualified(any())).thenReturn(ResponseResult.builder()
                .result(Boolean.FALSE)
                .description(DescriptionConstants.SELECTED_OFFER_UNQUALIFIED)
                .build());
        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = modificationEligibilityGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.MODIFICATION_ELIGIBILITY_GUARD);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        //then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.SELECTED_OFFER_UNQUALIFIED, description);

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
    }

    @DisplayName("given valid optional product offering and qualified " +
            "when check optional product offering qualification " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @Test
    void testValidAndQualifiedProductOffering() {
        //given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(ResponseResult.builder()
                .result(Boolean.TRUE)
                .description(DescriptionConstants.LOGICAL_SELECTED_OFFER)
                .build());
        SettingsEntity settingsEntity = createSettings();
        when(settingsService.getSettings()).thenReturn(settingsEntity);

        when(qualificationService.isProductOfferingQualified(any())).thenReturn(ResponseResult.builder()
                .result(Boolean.TRUE)
                .description(DescriptionConstants.SELECTED_OFFER_UNQUALIFIED)
                .build());
        StateContext<String, String> context = mockStatContext();

        //when
        Mono<Boolean> result = modificationEligibilityGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.MODIFICATION_ELIGIBILITY_GUARD);

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

    private SettingsEntity createSettings() {
        return SettingsEntity
                .builder()
                .reservePhysicalResourceEnabled(true)
                .reserveLogicalResourceEnabled(true)
                .checkCommercialEligibilityEnabled(true)
                .checkPaymentRefEnabled(true)
                .build();
    }

    private DefaultStateContext<String, String> mockStatContext() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.OPTIONAL_PRODUCT_OFFERING_ID, DEFAULT_OPTIONAL_PRODUCT_OFFERING_ID);

        RelatedParty relatedParty = new RelatedParty()
                .id(DEFAULT_RELATED_PARTY_ID)
                .role(OrderCaptureConstants.CUSTOMER);

        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);
        extendedState.getVariables().put(OrderCaptureConstants.CONTRACT_PRODUCT_ID, DEFAULT_PRODUCT_ID);
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