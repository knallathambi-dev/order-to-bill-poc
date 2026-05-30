// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.role.EngagedParty;
import com.orange.discobole.ordermanagement.commons.dto.role.PartyRole;
import com.orange.discobole.ordermanagement.commons.dto.role.PartyRoleSpecification;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.service.impl.PartyRoleManagementServiceImpl;
import com.orange.discobole.ordermanagement.ordercapture.service.impl.ProductOrderServiceImpl;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelatedPartyRefOrPartyRoleRef;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.exception.DiscoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerPartyRoleCreationActionTest {

    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PARTY_ROLE_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String ENGAGED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PARTY_ROLE_SPEC_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PARTY_REF_TYPE = "PartyRef";
    private static final String RELATED_PARTY_REF_TYPE = "RelatedPartyRefOrPartyRoleRef";

    @Mock
    private PartyRoleManagementServiceImpl partyRoleService;

    @Mock
    private ProductOrderServiceImpl productOrderService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private CustomerPartyRoleCreationAction customerPartyRoleAction;

    @Test
    @DisplayName("Given the action is re-executed, " +
            "when it runs, " +
            "then it should set the creation flag to false")
    void shouldSetFlagToFalseOnReExecution() {
        // Given
        StateContext<String, String> context = createMockStateContext();
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, TRUE);

        // When
        Mono<Void> result = customerPartyRoleAction.apply(context);

        // Then
        boolean isCreated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_PARTY_ROLE_CREATED, FALSE);
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertFalse(isCreated);
    }

    @Test
    @DisplayName("Given an exception occurs during createPartyRole, " +
            "when the action runs, " +
            "then it should set the flag to false and update description")
    void shouldHandleRuntimeExceptionDuringCreatePartyRole() {
        // Given
        mockPartyManagementEnabled();
        StateContext<String, String> context = createMockStateContext();

        doThrow(RuntimeException.class).when(partyRoleService).createPartyRole(any());

        // When
        Mono<Void> result = customerPartyRoleAction.apply(context);

        // Then
        boolean isCreated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_PARTY_ROLE_CREATED, FALSE);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertFalse(isCreated);
        Assertions.assertEquals(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE, description, "Description should be updated with error message");
    }

    @Test
    @DisplayName("Given a DiscoException occurs during createPartyRole, " +
            "when the action runs, " +
            "then it should set the flag to false and update description")
    void shouldHandleDiscoExceptionDuringCreatePartyRole() {
        // Given
        mockPartyManagementEnabled();
        DiscoException discoEx = new DiscoException(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE);
        StateContext<String, String> context = createMockStateContextEndProcess();

        doThrow(discoEx).when(partyRoleService).createPartyRole(any());

        // When
        Mono<Void> result = customerPartyRoleAction.apply(context);

        // Then
        boolean isCreated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_PARTY_ROLE_CREATED, FALSE);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertFalse(isCreated);
        Assertions.assertEquals(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE, description, "Description should reflect error");
    }

    @Test
    @DisplayName("Given an exception during updateProductOrderRelatedParties, " +
            "when the action runs, " +
            "then it should set the flag to false")
    void shouldSetFlagToFalseOnUpdateProductOrderRelatedPartiesException() {
        // Given
        mockPartyManagementEnabled();
        StateContext<String, String> context = createMockStateContext();

        doReturn(createSamplePartyRole()).when(partyRoleService).createPartyRole(any());
        doThrow(RuntimeException.class).when(productOrderService).updateProductOrderRelatedParties(any());

        // When
        Mono<Void> result = customerPartyRoleAction.apply(context);

        // Then
        boolean isCreated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_PARTY_ROLE_CREATED, FALSE);
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertFalse(isCreated);
    }

    @Test
    @DisplayName("Given successful creation, " +
            "when the action runs, " +
            "then it should set the creation flag to true")
    void shouldSetFlagToTrueOnSuccessfulCreation() {
        // Given
        mockPartyManagementEnabled();
        StateContext<String, String> context = createMockStateContext();

        doReturn(createSamplePartyRole()).when(partyRoleService).createPartyRole(any());
        doNothing().when(productOrderService).updateProductOrderRelatedParties(any());

        // When
        Mono<Void> result = customerPartyRoleAction.apply(context);

        // Then
        boolean isCreated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_PARTY_ROLE_CREATED, FALSE);
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(isCreated);
    }

    @Test
    @DisplayName("Given party management check is disabled, " +
            "when the action runs, " +
            "then no creation occurs and related-party is copied")
    void shouldCopyRelatedPartyAndSetFlagWhenCheckDisabled() {
        // Given
        DefaultStateContext<String, String> context = createMockStateContext();

        RelatedParty existingRelatedParty = new RelatedParty()
                .id("existingId")
                .name("existingName")
                .role(OrderCaptureConstants.CUSTOMER)
                .referredType("Individual");
        context.getExtendedState().getVariables().put(OrderCaptureConstants.RELATED_PARTY, existingRelatedParty);

        SettingsEntity settings = mock(SettingsEntity.class);
        when(settings.isCheckPartyManagementEnabled()).thenReturn(false);
        when(settingsService.getSettings()).thenReturn(settings);

        // When
        Mono<Void> result = customerPartyRoleAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        verify(partyRoleService, never()).createPartyRole(any());
        verify(productOrderService, times(1)).updateProductOrderRelatedParties(any());

        boolean isCreated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_PARTY_ROLE_CREATED, FALSE);
        List<RelatedParty> taskParties = StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.TASK_RELATED_PARTY, RelatedParty.class);
        Assertions.assertTrue(isCreated);
        Assertions.assertEquals(1, taskParties.size());
        Assertions.assertEquals(PARTY_ROLE_ID, taskParties.get(0).getId());
    }

    private void mockPartyManagementEnabled() {
        SettingsEntity settings = mock(SettingsEntity.class);
        when(settings.isCheckPartyManagementEnabled()).thenReturn(true);
        when(settingsService.getSettings()).thenReturn(settings);
    }

    private ProductOrder createSampleProductOrder() {
        PartyRef partyRef = PartyRef.builder()
                .id(PARTY_ROLE_ID)
                .atType(PARTY_REF_TYPE)
                .build();

        RelatedPartyRefOrPartyRoleRef relatedPartyRef = RelatedPartyRefOrPartyRoleRef.builder()
                .role(OrderCaptureConstants.PROSPECT)
                .partyOrPartyRole(partyRef)
                .atType(RELATED_PARTY_REF_TYPE)
                .build();

        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.DRAFT)
                .relatedParty(Collections.singletonList(relatedPartyRef))
                .build();
    }

    private PartyRole createSamplePartyRole() {
        return PartyRole.builder()
                .engagedParty(EngagedParty.builder().id(ENGAGED_PARTY_ID).build())
                .partyRoleSpecification(PartyRoleSpecification.builder()
                        .id(PARTY_ROLE_SPEC_ID)
                        .name(OrderCaptureConstants.CUSTOMER)
                        .build())
                .build();
    }

    private DefaultStateContext<String, String> createMockStateContext() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createSampleProductOrder());

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null, extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null, null, null);
    }

    private DefaultStateContext<String, String> createMockStateContextEndProcess() {
        ObjectState<String, String> sourceState = new ObjectState<>("CANCEL");
        ObjectState<String, String> targetState = new ObjectState<>("END");
        DefaultExternalTransition<String, String> transition = new DefaultExternalTransition<>(sourceState, targetState, null, "cancelProcess", null, null, null);
        List<Transition<String, String>> transitions = new ArrayList<>();
        transitions.add(transition);
        Collection<State<String, String>> states = List.of(sourceState, targetState);
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createSampleProductOrder());

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(states, transitions, null, null, null, extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null, null, null);
    }
}