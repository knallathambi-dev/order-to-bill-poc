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
class AcquisitionEligibilityGuardTest {

    public static final String DEFAULT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RELATED_PARTY_ROLE = RandomStringUtils.randomAlphabetic(5);
    public static final String START_STATE = "start";
    public static final String EVENT = "event";

    @Mock
    private ProductOfferingService productOfferingService;
    @Mock
    private ProductOfferingQualificationService qualificationService;
    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private AcquisitionEligibilityGuard acquisitionEligibilityGuard;

    @Test
    @DisplayName("Given eligibility guard is re-executed, " +
            "when applying the guard, " +
            "then it should return true")
    void shouldReturnTrueWhenEligibilityGuardReExecuted() {
        // Given
        StateContext<String, String> context = mockStatContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.ACQUISITION_ELIGIBILITY_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = acquisitionEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given an exception occurs in product offering service, " +
            "when validating product offering, " +
            "then it should return false and set description to INTERNAL_SERVER_ERROR")
    void shouldReturnFalseWhenExceptionInProductOfferingService() {
        // Given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenThrow(RuntimeException.class);
        StateContext<String, String> context = mockStatContext();

        // When
        Mono<Boolean> result = acquisitionEligibilityGuard.apply(context);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, description);
    }

    @Test
    @DisplayName("Given catalog service is unreachable, " +
            "when validating product offering, " +
            "then it should return false and set description to CATALOG_SERVICE_UNREACHABLE")
    void shouldReturnFalseWhenCatalogServiceIsUnreachable() {
        // Given
        ResponseResult responseResult = ResponseResult.builder()
                .result(false)
                .description(DescriptionConstants.CATALOG_SERVICE_UNREACHABLE)
                .build();
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(responseResult);
        StateContext<String, String> context = mockStatContext();

        // When
        Mono<Boolean> result = acquisitionEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
        Assertions.assertEquals(DescriptionConstants.CATALOG_SERVICE_UNREACHABLE, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given commercial eligibility check is disabled, " +
            "when validating product offering, " +
            "then it should return true")
    void shouldReturnTrueWhenEligibilityCheckIsDisabled() {
        // Given
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(ResponseResult.builder()
                .result(true)
                .build());

        SettingsEntity settings = createSettings();
        settings.setCheckCommercialEligibilityEnabled(false);
        when(settingsService.getSettings()).thenReturn(settings);
        StateContext<String, String> context = mockStatContext();

        // When
        Mono<Boolean> result = acquisitionEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given qualification service returns false, " +
            "when validating product offering, " +
            "then it should return false with QUALIFICATION_SERVICE_UNREACHABLE")
    void shouldReturnFalseWhenQualificationFails() {
        // Given
        ResponseResult responseResult = ResponseResult.builder()
                .result(true)
                .description(DescriptionConstants.CONTRACT_SELECTED_OFFER)
                .build();
        when(productOfferingService.isProductOfferingExist(anyString(), any())).thenReturn(responseResult);
        when(settingsService.getSettings()).thenReturn(createSettings());
        when(qualificationService.isProductOfferingQualified(any())).thenReturn(
                ResponseResult.builder().result(false).description(DescriptionConstants.QUALIFICATION_SERVICE_UNREACHABLE).build()
        );

        StateContext<String, String> context = mockStatContext();

        // When
        Mono<Boolean> result = acquisitionEligibilityGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    private SettingsEntity createSettings() {
        return SettingsEntity.builder()
                .reservePhysicalResourceEnabled(true)
                .checkCommercialEligibilityEnabled(true)
                .checkPaymentRefEnabled(true)
                .build();
    }

    private StateContext<String, String> mockStatContext() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.PRODUCT_OFFERING_ID, DEFAULT_PRODUCT_OFFERING_ID);

        RelatedParty relatedParty = new RelatedParty()
                .id(DEFAULT_RELATED_PARTY_ID)
                .role(DEFAULT_RELATED_PARTY_ROLE);
        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);

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