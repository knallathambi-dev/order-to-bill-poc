// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.*;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductConfigurationService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.exception.DiscoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.ObjectState;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.DefaultExternalTransition;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcquisitionUseCaseCheckerGuardTest {

    public static final String DEFAULT_CONFIGURATION_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);

    @InjectMocks
    private AcquisitionUseCaseCheckerGuard acquisitionUseCaseCheckerGuard;

    @Mock
    private ProductConfigurationService productConfigurationService;

    @DisplayName("Given re-executed acquisition use case guard, " +
            "when check acquisition use case was provided, " +
            "then return true")
    @Test
    void shouldReturnTrueWhenReExecuted() {
        // Given
        StateContext<String, String> context = mockStatContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = acquisitionUseCaseCheckerGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @ParameterizedTest
    @MethodSource("provideUseCases")
    @DisplayName("Given product configuration with specific action, " +
            "when check acquisition use case was provided, " +
            "then return expected outcome")
    void shouldReturnExpectedOutcomeBasedOnConfiguration(String action, boolean expectedOutcome) {
        // Given
        StateContext<String, String> context = mockStatContext();
        QueryProductConfiguration productConfiguration = createProductConfiguration(action);
        when(productConfigurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);

        // When
        Mono<Boolean> result = acquisitionUseCaseCheckerGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedOutcome)
                .expectComplete()
                .verify();
    }

    @DisplayName("Given null product configuration in get configuration, " +
            "when check acquisition use case was provided, " +
            "then the guard result in context is false and description is set")
    @Test
    void shouldSetGuardResultToFalseWhenProductConfigIsNull() {
        // Given
        StateContext<String, String> context = mockStatContext();
        QueryProductConfigurationItem requestedItem = QueryProductConfigurationItem.builder()
                .productConfiguration(null)
                .build();
        QueryProductConfiguration productConfiguration = QueryProductConfiguration.builder()
                .requestedProductConfigurationItems(Collections.singletonList(requestedItem))
                .computedProductConfigurationItems(Collections.singletonList(QueryProductConfigurationItem.builder()
                        .id(DEFAULT_CONFIGURATION_ID)
                        .build()))
                .build();

        when(productConfigurationService.getProductConfigurationById(any())).thenReturn(productConfiguration);

        // When
        acquisitionUseCaseCheckerGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);

        // Then
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.VALID_CONFIGURATION_IDENTIFIER_REQUIRED, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given null productOfferingId in get configuration, " +
            "when check acquisition use case was provided, " +
            "then guard result is false and description is set")
    @Test
    void shouldSetGuardResultToFalseWhenProductOfferingIdIsNull() {
        // Given
        StateContext<String, String> context = mockStatContext();

        ConfigurationAction configurationAction = ConfigurationAction.builder()
                .action(OrderCaptureConstants.ADD).isSelected(Boolean.TRUE)
                .build();
        ProductConfiguration productConfiguration = ProductConfiguration.builder()
                .configurationActions(Collections.singletonList(configurationAction))
                .productOffering(null)
                .build();
        QueryProductConfigurationItem requestedItem = QueryProductConfigurationItem.builder()
                .productConfiguration(productConfiguration)
                .build();
        QueryProductConfiguration queryProductConfiguration = QueryProductConfiguration.builder()
                .requestedProductConfigurationItems(Collections.singletonList(requestedItem))
                .computedProductConfigurationItems(Collections.singletonList(QueryProductConfigurationItem.builder()
                        .id(DEFAULT_CONFIGURATION_ID)
                        .build()))
                .build();

        when(productConfigurationService.getProductConfigurationById(any())).thenReturn(queryProductConfiguration);

        // When
        acquisitionUseCaseCheckerGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);

        // Then
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.VALID_CONFIGURATION_IDENTIFIER_REQUIRED, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given exception in get configuration, " +
            "when check acquisition use case was provided, " +
            "then guard result is false and description is set")
    @Test
    void shouldSetGuardResultToFalseOnGetConfigException() {
        // Given
        DiscoException exception = new DiscoException(DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE);
        StateContext<String, String> context = mockStatContext();
        doThrow(exception).when(productConfigurationService).getProductConfigurationById(DEFAULT_CONFIGURATION_ID);

        // When
        acquisitionUseCaseCheckerGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);

        // Then
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given exception in get configuration, " +
            "when check acquisition use case was provided, " +
            "then guard result is false and description is set")
    @Test
    void shouldSetGuardResultToFalseOnGetConfigError() {
        // Given
        DiscoException exception = new DiscoException(DescriptionConstants.ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS);
        StateContext<String, String> context = mockStatContext();
        doThrow(exception).when(productConfigurationService).getProductConfigurationById(any());

        // When
        acquisitionUseCaseCheckerGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.ACQUISITION_USE_CASE_CHECKER_GUARD);

        // Then
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.VALID_CONFIGURATION_IDENTIFIER_REQUIRED, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    private QueryProductConfiguration createProductConfiguration(String action) {
        ProductOfferingRef productOffering = ProductOfferingRef.builder().id(DEFAULT_PRODUCT_OFFERING_ID).build();
        ConfigurationAction configurationAction = ConfigurationAction.builder()
                .action(action).isSelected(Boolean.TRUE)
                .build();
        ProductConfiguration productConfiguration = ProductConfiguration.builder()
                .configurationActions(Collections.singletonList(configurationAction))
                .productOffering(productOffering)
                .build();

        QueryProductConfigurationItem requestedItem = QueryProductConfigurationItem.builder()
                .productConfiguration(productConfiguration)
                .build();

        return QueryProductConfiguration.builder()
                .requestedProductConfigurationItems(Collections.singletonList(requestedItem))
                .computedProductConfigurationItems(Collections.singletonList(QueryProductConfigurationItem.builder()
                        .id(DEFAULT_CONFIGURATION_ID)
                        .build()))
                .build();
    }

    private static Stream<Arguments> provideUseCases() {
        return Stream.of(
                Arguments.of("modify", false),
                Arguments.of("terminate", false),
                Arguments.of("add", true)
        );
    }

    private DefaultStateContext<String, String> mockStatContext() {
        ObjectState<String, String> source = new ObjectState<>("CANCEL");
        ObjectState<String, String> target = new ObjectState<>("END");
        Collection<State<String, String>> states = new ArrayList<>();
        states.add(source);
        states.add(target);
        DefaultExternalTransition<String, String> transition = new DefaultExternalTransition<>(source, target, null, "cancelProcess", null, null, null);
        List<Transition<String, String>> transitions = new ArrayList<>();
        transitions.add(transition);
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.PRODUCT_OFFERING_ID, DEFAULT_PRODUCT_OFFERING_ID);
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(states, transitions, source);
        extendedState.getVariables().put(OrderCaptureConstants.CONFIGURATION_ID, DEFAULT_CONFIGURATION_ID);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}