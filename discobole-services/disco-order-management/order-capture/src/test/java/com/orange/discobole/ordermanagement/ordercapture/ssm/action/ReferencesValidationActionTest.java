// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.AccountManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.AppointmentManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.PaymentManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.service.dto.ResponseResult;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.exception.DiscoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferencesValidationActionTest {

    private static final String DEFAULT_PAYMENT_REF_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_BILLING_ACCOUNT_REF_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String INVALID_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String VALID_APPOINTMENT_REF_ID = "14" + RandomStringUtils.randomNumeric(8);

    @Mock
    private PaymentManagementService paymentManagementService;
    @Mock
    private AccountManagementService accountManagementService;
    @Mock
    private SettingsService settingsService;
    @Mock
    private AppointmentManagementService appointmentManagementService;

    @InjectMocks
    private ReferencesValidationAction referencesValidationAction;

    private StateContext<String, String> context;

    @BeforeEach
    void setup() {
        context = mockStateContext(RELATED_PARTY_ID, false);
    }

    @Test
    @DisplayName("Given a re-executed product order payment, " +
            "when applying validation, " +
            "then no context variables are set")
    void shouldNotSetSSMContextVariablesWhenReExecutedProductOrderPayment() {
        // Given
        context = mockStateContext(RELATED_PARTY_ID, true);

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_REFERENCES_VALIDATED, FALSE));
    }

    @Test
    @DisplayName("Given unpaid product order items with invalid payment ref ID, " +
            "when validating, " +
            "then description is set to VALID_PAYMENT_REFERENCE_REQUIRED")
    void shouldSetVariablesAndDescriptionToInvalidPaymentReference() {
        // Given
        mockSettingsService(true, false, false);
        doThrow(new DiscoException(DescriptionConstants.VALID_PAYMENT_REFERENCE_REQUIRED)).when(paymentManagementService).checkPaymentRef(any());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, false, DescriptionConstants.VALID_PAYMENT_REFERENCE_REQUIRED);
    }

    @Test
    @DisplayName("Given unpaid product order items with check payment ref disabled, " +
            "when validating, " +
            "then variables are set correctly")
    void shouldSetSSMContextVariablesCorrectlyWhenCheckPaymentRefDisabled() {
        // Given
        mockSettingsService(false, false, false);

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, true, null);
        verify(paymentManagementService, times(0)).checkPaymentRef(anyString());
    }

    @Test
    @DisplayName("Given unpaid product order items with unreachable payment service, " +
            "when validating, " +
            "then description is set to PAYMENT_SERVICE_UNREACHABLE")
    void shouldSetDescriptionToPaymentServiceUnreachable() {
        // Given
        mockSettingsService(true, false, false);
        doThrow(new DiscoException(DescriptionConstants.PAYMENT_SERVICE_UNREACHABLE)).when(paymentManagementService).checkPaymentRef(any());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, false, DescriptionConstants.PAYMENT_SERVICE_UNREACHABLE);
    }

    @Test
    @DisplayName("Given unpaid product order items with invalid related party ID, " +
            "when validating, " +
            "then description is set to VALID_PARTY_IDENTIFIER_REQUIRED")
    void shouldSetDescriptionToInvalidRelatedPartyId() {
        // Given
        context = mockStateContext(INVALID_RELATED_PARTY_ID, false);

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, false, DescriptionConstants.VALID_PARTY_IDENTIFIER_REQUIRED);
    }

    @Test
    @DisplayName("Given valid related party ID and valid payment ref ID, " +
            "when validating, " +
            "then variables and description are null")
    void shouldSetVariablesAndDescriptionToNull() {
        // Given
        mockSettingsService(true, false, false);
        when(paymentManagementService.checkPaymentRef(DEFAULT_PAYMENT_REF_ID))
                .thenReturn(ResponseResult.builder().result(true).build());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, true, null);
    }

    @Test
    @DisplayName("Given product order items requiring billing account with invalid billing account ref ID, " +
            "when validating, " +
            "then description is set to INVALID_BILLING_ACCOUNT_REFERENCE")
    void shouldSetVariablesAndDescriptionToInvalidBillingAccountRef() {
        // Given
        mockSettingsService(false, true, false);
        doThrow(new DiscoException(DescriptionConstants.INVALID_BILLING_ACCOUNT_REFERENCE)).when(accountManagementService).checkBillingAccount(any());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, false, DescriptionConstants.INVALID_BILLING_ACCOUNT_REFERENCE);
    }

    @Test
    @DisplayName("Given billing account check disabled, " +
            "when validating, " +
            "then variables are set correctly")
    void shouldSetSSMContextVariablesCorrectlyWhenCheckBillingAccountRefDisabled() {
        // Given
        mockSettingsService(false, false, false);

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, true, null);
        verify(accountManagementService, times(0)).checkBillingAccount(anyString());
    }

    @Test
    @DisplayName("Given billing account unreachable, " +
            "when validating, " +
            "then description is set to ACCOUNT_SERVICE_UNREACHABLE")
    void shouldSetDescriptionToAccountServiceUnreachable() {
        // Given
        mockSettingsService(false, true, false);
        doThrow(new DiscoException(DescriptionConstants.ACCOUNT_SERVICE_UNREACHABLE)).when(accountManagementService).checkBillingAccount(any());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, false, DescriptionConstants.ACCOUNT_SERVICE_UNREACHABLE);
    }

    @Test
    @DisplayName("Given valid billing account ref ID, " +
            "when validating, " +
            "then variables and description are null")
    void shouldSetVariablesAndDescriptionToNullWhenValidBillingAccountRefId() {
        // Given
        mockSettingsService(false, true, false);
        when(accountManagementService.checkBillingAccount(DEFAULT_BILLING_ACCOUNT_REF_ID))
                .thenReturn(ResponseResult.builder().result(true).build());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, true, null);
    }

    @Test
    @DisplayName("Given items needing appointment, " +
            "valid related-party and appointment ref, when validating, " +
            "then references are validated successfully")
    void shouldValidateAppointmentRefSuccessfully() {
        // Given
        mockSettingsService(false, false, true);
        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.ORDER_ITEM_APPOINTMENT_REF_MAP, createOrderItemAppointmentRefMap());
        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, Boolean.TRUE);

        when(appointmentManagementService.fetchAppointmentById(VALID_APPOINTMENT_REF_ID))
                .thenReturn(ResponseResult.builder().result(true).build());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, true, null);
        verify(appointmentManagementService, times(1)).fetchAppointmentById(VALID_APPOINTMENT_REF_ID);
    }

    @Test
    @DisplayName("Given appointment service unreachable, " +
            "when validating, " +
            "then references are invalid and description is set")
    void shouldSetDescriptionWhenAppointmentServiceUnreachable() {
        // Given
        mockSettingsService(false, false, true);
        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.ORDER_ITEM_APPOINTMENT_REF_MAP, createOrderItemAppointmentRefMap());
        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, Boolean.TRUE);

        doThrow(new DiscoException(DescriptionConstants.APPOINTMENT_SERVICE_UNREACHABLE))
                .when(appointmentManagementService).fetchAppointmentById(anyString());

        // When
        Mono<Void> result = referencesValidationAction.apply(context);

        // Then
        verifyResultAndDescription(result, false, DescriptionConstants.APPOINTMENT_SERVICE_UNREACHABLE);
    }

    private Map<String, String> createOrderItemAppointmentRefMap() {
        Map<String, String> map = new HashMap<>();
        map.put(CatalogDrivenTasksActionTest.DEFAULT_ORDER_ITEM_ID, VALID_APPOINTMENT_REF_ID);
        return map;
    }

    private DefaultStateContext<String, String> mockStateContext(String relatedPartyId, boolean isReExecutedAction) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.ORDER_ITEM_PAYMENT_REF_MAP, createOrderItemPaymentRefMap());
        extendedState.getVariables().put(OrderCaptureConstants.ORDER_ITEM_BILLING_ACCOUNT_REF_MAP, createOrderItemBillingAccountRefMap());
        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, createValidTaskRelatedParty());
        extendedState.getVariables().put(OrderCaptureConstants.TASK_RELATED_PARTY, createTaskRelatedPartyList(relatedPartyId));
        if (isReExecutedAction) {
            extendedState.getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);
        }

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null, extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null, null, null);
    }

    private Map<String, List<String>> createOrderItemPaymentRefMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put(CatalogDrivenTasksActionTest.DEFAULT_ORDER_ITEM_ID, List.of(DEFAULT_PAYMENT_REF_ID));
        return map;
    }

    private Map<String, String> createOrderItemBillingAccountRefMap() {
        Map<String, String> map = new HashMap<>();
        map.put(CatalogDrivenTasksActionTest.DEFAULT_ORDER_ITEM_ID, DEFAULT_BILLING_ACCOUNT_REF_ID);
        return map;
    }

    private RelatedParty createValidTaskRelatedParty() {
        return new RelatedParty().id(RELATED_PARTY_ID).role("customer");
    }

    private List<RelatedParty> createTaskRelatedPartyList(String relatedPartyId) {
        return List.of(new RelatedParty().id(relatedPartyId).role("customer"));
    }

    private void mockSettingsService(boolean checkPaymentRefEnabled, boolean checkBillingAccountRefEnabled, boolean checkAppointmentRefEnabled) {
        SettingsEntity settingsEntity = new SettingsEntity();
        settingsEntity.setCheckPaymentRefEnabled(checkPaymentRefEnabled);
        settingsEntity.setCheckBillingAccountRefEnabled(checkBillingAccountRefEnabled);
        settingsEntity.setCheckAppointmentRefEnabled(checkAppointmentRefEnabled);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
    }

    private void verifyResultAndDescription(Mono<Void> result, boolean expectedResult, String expectedDescription) {
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertEquals(expectedResult, StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_REFERENCES_VALIDATED, FALSE));
        Assertions.assertEquals(expectedDescription, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }
}