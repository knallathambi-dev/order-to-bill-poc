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
import reactor.test.StepVerifier;

import java.util.Collection;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProspectPartyRoleCreationActionTest {

    private static final String DEFAULT_PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_PARTY_NAME = RandomStringUtils.randomAlphabetic(10);

    @InjectMocks
    private ProspectPartyRoleCreationAction prospectPartyRoleAction;

    @Mock
    private PartyRoleManagementServiceImpl partyRoleService;

    @Mock
    private SettingsService settingsService;

    @Test
    @DisplayName("Given re-execution flag is true, " +
            "when apply is invoked, " +
            "then do not create prospect party role and do not set creation flag")
    void shouldSkipCreationIfAlreadyReExecuted() {
        // Given
        StateContext<String, String> context = createMockStateContext();
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        StepVerifier.create(prospectPartyRoleAction.apply(context)).verifyComplete();

        // Then
        Assertions.assertFalse(
                StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PROSPECT_PARTY_ROLE_CREATED, FALSE));
    }

    @Test
    @DisplayName("Given an exception during createPartyRole, " +
            "when apply is invoked, " +
            "then do not create prospect party role and set internal server error description")
    void shouldHandleExceptionDuringPartyRoleCreation() {
        // Given
        setPartyManagementFeatureFlag(true);
        when(partyRoleService.createPartyRole(any())).thenThrow(RuntimeException.class);
        // When
        StateContext<String, String> context = createMockStateContext();
        StepVerifier.create(prospectPartyRoleAction.apply(context)).verifyComplete();

        // Then
        Assertions.assertFalse(
                StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PROSPECT_PARTY_ROLE_CREATED, FALSE));
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR,
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given valid party role data and party management enabled, " +
            "when apply is invoked, " +
            "then create party role and set creation flag to true")
    void shouldCreatePartyRoleWhenValidDataAndEnabled() {
        // Given
        setPartyManagementFeatureFlag(true);
        when(partyRoleService.createPartyRole(any())).thenReturn(createSamplePartyRole());

        // When
        StateContext<String, String> context = createMockStateContext();
        StepVerifier.create(prospectPartyRoleAction.apply(context)).verifyComplete();

        // Then
        Assertions.assertTrue(
                StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PROSPECT_PARTY_ROLE_CREATED, FALSE));
    }

    @Test
    @DisplayName("Given party management is disabled, " +
            "when apply is invoked, " +
            "then do not create party role and creation flag remains false")
    void shouldSkipPartyRoleCreationWhenManagementDisabled() {
        // Given
        setPartyManagementFeatureFlag(false);
        // When
        StateContext<String, String> context = createMockStateContext();
        StepVerifier.create(prospectPartyRoleAction.apply(context)).verifyComplete();

        // Then
        verify(partyRoleService, never()).createPartyRole(any());
        Assertions.assertTrue(
                StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PROSPECT_PARTY_ROLE_CREATED, FALSE));
    }

    private PartyRole createSamplePartyRole() {
        return PartyRole.builder()
                .engagedParty(EngagedParty.builder().id(DEFAULT_PARTY_ID).build())
                .name(DEFAULT_PARTY_NAME)
                .partyRoleSpecification(PartyRoleSpecification.builder()
                        .name(OrderCaptureConstants.PROSPECT).build())
                .build();
    }

    private void setPartyManagementFeatureFlag(boolean enabled) {
        SettingsEntity mockSettings = mock(SettingsEntity.class);
        when(mockSettings.isCheckPartyManagementEnabled()).thenReturn(enabled);
        when(settingsService.getSettings()).thenReturn(mockSettings);
    }

    private DefaultStateContext<String, String> createMockStateContext() {
        ObjectState<String, String> sourceState = new ObjectState<>("SOURCE");
        ObjectState<String, String> targetState = new ObjectState<>("TARGET");
        DefaultExternalTransition<String, String> transition =
                new DefaultExternalTransition<>(sourceState, targetState, null, "event", null, null, null);
        List<Transition<String, String>> transitions = List.of(transition);
        Collection<State<String, String>> states = List.of(sourceState, targetState);
        ExtendedState extendedState = new DefaultExtendedState();

        extendedState.getVariables().put(OrderCaptureConstants.PARTY_ID, DEFAULT_PARTY_ID);
        extendedState.getVariables().put(OrderCaptureConstants.PARTY_NAME, DEFAULT_PARTY_NAME);
        extendedState.getVariables().put(OrderCaptureConstants.PARTY_REFERRED_TYPE, "Individual");

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(states, transitions, sourceState);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null,
                extendedState, null, stateMachine, null, null, null);
    }
}