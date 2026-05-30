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
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
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
class PartyRolesCheckActionTest {

    public static final String EMPLOYEE = "employee";
    public static final String ENGAGED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PARTY_NAME = RandomStringUtils.randomAlphabetic(10);

    @InjectMocks
    private PartyRolesCheckAction partyRolesCheckAction;

    @Mock
    private PartyRoleManagementServiceImpl partyRoleManagementService;

    @Mock
    private SettingsService settingsService;

    @Test
    @DisplayName("Given party management check is disabled, " +
            "when apply is invoked, " +
            "then service is not called and flags are false/true/true")
    void shouldNotCallServiceAndSetFlagsWhenPartyManagementDisabled() {
        // Given
        setPartyManagementFlag(false);
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(partyRoleManagementService, never()).getPartyRoles(any(), any());
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE));
    }

    @Test
    @DisplayName("Given re-executed party role check, " +
            "when check party roles, " +
            "then all flags are set to false")
    void shouldResetFlagsOnReExecution() {
        // Given
        StateContext<String, String> context = mockStateContext();
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, TRUE);

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE));
    }

    @Test
    @DisplayName("Given an exception in getPartyRoles, " +
            "when check party roles, " +
            "then arePartyRolesChecked is false and description indicates error")
    void shouldHandleDiscoExceptionInGetPartyRoles() {
        // Given
        setPartyManagementFlag(true);
        DiscoException exception = new DiscoException("Error retrieving party role");
        doThrow(exception).when(partyRoleManagementService).getPartyRoles(any(), any());
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean arePartyRolesChecked = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(arePartyRolesChecked);
        Assertions.assertEquals(DescriptionConstants.VALID_PARTY_IDENTIFIER_REQUIRED, description);
    }

    @Test
    @DisplayName("Given the party management service is unreachable, " +
            "when check party roles, " +
            "then arePartyRolesChecked is false and description indicates unreachable service")
    void shouldHandleUnreachableServiceException() {
        // Given
        setPartyManagementFlag(true);
        DiscoException exception = new DiscoException(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE);
        doThrow(exception).when(partyRoleManagementService).getPartyRoles(any(), any());
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean arePartyRolesChecked = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(arePartyRolesChecked);
        Assertions.assertEquals(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE, description);
    }

    @Test
    @DisplayName("Given an internal server error occurs in getPartyRoles, " +
            "when check party roles, " +
            "then arePartyRolesChecked is false and description indicates internal error")
    void shouldHandleRuntimeExceptionInGetPartyRoles() {
        // Given
        setPartyManagementFlag(true);
        StateContext<String, String> context = mockStateContext();
        doThrow(RuntimeException.class).when(partyRoleManagementService).getPartyRoles(any(), any());

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean arePartyRolesChecked = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(arePartyRolesChecked);
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, description);
    }

    @Test
    @DisplayName("Given a prospect party role, " +
            "when check party roles, " +
            "then isCustomerOrProspectPartyRole is true and flags are set accordingly")
    void shouldSetFlagsForProspectPartyRole() {
        // Given
        setPartyManagementFlag(true);
        StateContext<String, String> context = mockStateContext();
        doReturn(createPartyRoles(OrderCaptureConstants.PROSPECT)).when(partyRoleManagementService).getPartyRoles(any(), any());

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE));
    }

    @Test
    @DisplayName("Given a customer party role, " +
            "when check party roles, " +
            "then isCustomerOrProspectPartyRole is true and flags are set accordingly")
    void shouldSetFlagsForCustomerPartyRole() {
        // Given
        setPartyManagementFlag(true);
        StateContext<String, String> context = mockStateContext();
        doReturn(createPartyRoles(OrderCaptureConstants.CUSTOMER)).when(partyRoleManagementService).getPartyRoles(any(), any());

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE));
    }

    @Test
    @DisplayName("Given a role other than customer or prospect, " +
            "when check party roles, " +
            "then flags indicate non-customer, non-prospect role")
    void shouldSetFlagsForOtherRoles() {
        // Given
        setPartyManagementFlag(true);
        StateContext<String, String> context = mockStateContext();
        doReturn(createPartyRoles(EMPLOYEE)).when(partyRoleManagementService).getPartyRoles(any(), any());

        // When
        Mono<Void> result = partyRolesCheckAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE, FALSE));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PARTY_ROLES_CHECKED, FALSE));
    }

    private void setPartyManagementFlag(boolean enabled) {
        SettingsEntity settings = mock(SettingsEntity.class);
        when(settings.isCheckPartyManagementEnabled()).thenReturn(enabled);
        when(settingsService.getSettings()).thenReturn(settings);
    }

    private List<PartyRole> createPartyRoles(String roleName) {
        return Collections.singletonList(PartyRole.builder()
                .engagedParty(EngagedParty.builder().id(ENGAGED_PARTY_ID).build())
                .name(PARTY_NAME)
                .partyRoleSpecification(PartyRoleSpecification.builder().name(roleName).build())
                .build());
    }

    private DefaultStateContext<String, String> mockStateContext() {
        ObjectState<String, String> sourceState = new ObjectState<>("CANCEL");
        ObjectState<String, String> targetState = new ObjectState<>("END");

        DefaultExternalTransition<String, String> transition = new DefaultExternalTransition<>(sourceState, targetState, null, "cancelProcess", null, null, null);
        List<Transition<String, String>> transitions = new ArrayList<>();
        transitions.add(transition);
        Collection<State<String, String>> states = new ArrayList<>();
        states.add(sourceState);
        states.add(targetState);
        ExtendedState extendedState = new DefaultExtendedState();
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(states, transitions, sourceState);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null, null, null);
    }
}