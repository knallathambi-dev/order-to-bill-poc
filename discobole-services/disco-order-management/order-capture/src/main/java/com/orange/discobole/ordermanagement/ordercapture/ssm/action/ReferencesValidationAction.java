// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.AccountManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.AppointmentManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.PaymentManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.util.ProductOrderUtil;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.VALID_PARTY_IDENTIFIER_REQUIRED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("validateReferencesAction")
@Slf4j
public class ReferencesValidationAction implements StateMachineStateAction<String, String> {
    private final PaymentManagementService paymentManagementService;
    private final AccountManagementService accountManagementService;
    private final AppointmentManagementService appointmentManagementService;
    private final SettingsService settingsService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ReferencesValidationAction(PaymentManagementService paymentManagementService, AccountManagementService accountManagementService, AppointmentManagementService appointmentManagementService, SettingsService settingsService) {
        this.paymentManagementService = paymentManagementService;
        this.accountManagementService = accountManagementService;
        this.appointmentManagementService = appointmentManagementService;
        this.settingsService = settingsService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        try {
            log.info("Inside of the validation of references action");
            RelatedParty providedRelatedParty = ProductOrderUtil.getRelatedParty(context);
            RelatedParty relatedParty = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.RELATED_PARTY, RelatedParty.class);
            String providedPartyId = Objects.nonNull(providedRelatedParty) ? providedRelatedParty.getId() : null;
            boolean isValidRelatedPartyId = Objects.nonNull(relatedParty) &&
                    !StringUtils.isBlank(relatedParty.getId()) &&
                    relatedParty.getId().equals(providedPartyId);
            if (!isValidRelatedPartyId) {
                StateMachineUtil.setDescriptionContext(context, VALID_PARTY_IDENTIFIER_REQUIRED);
                updateContextVariables(context, FALSE);
                return Mono.empty();
            }

            Map<String, List<String>> unpaidOrderItemIds = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.ORDER_ITEM_PAYMENT_REF_MAP);
            Map<String, String> orderItemIdsRequiringBARef = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.ORDER_ITEM_BILLING_ACCOUNT_REF_MAP);
            Map<String, String> orderItemIdsRequiringAppointmentRef = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.ORDER_ITEM_APPOINTMENT_REF_MAP);
            boolean isAppointmentRequired = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, FALSE);

            if (settingsService.getSettings().isCheckPaymentRefEnabled()) {
                validatePaymentRefs(unpaidOrderItemIds);
            }

            if (settingsService.getSettings().isCheckBillingAccountRefEnabled()) {
                validateBillingAccountRefs(orderItemIdsRequiringBARef);
            }

            if (settingsService.getSettings().isCheckAppointmentRefEnabled() && isAppointmentRequired) {
                validateAppointmentRefs(orderItemIdsRequiringAppointmentRef);
            }

            updateContextVariables(context, TRUE);
            return Mono.empty();
        } catch (DiscoException discoException) {
            log.error("Unable to validate references [{}]:", discoException.getMessage(), discoException);
            StateMachineUtil.setDescriptionContext(context, discoException.getReason());
            updateContextVariables(context, FALSE);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Unable to validate references [{}]:", e.getMessage(), e);
            updateContextVariables(context, FALSE);
            return Mono.empty();
        }
    }

    private void updateContextVariables(StateContext<String, String> context, boolean areReferencesValidated) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.ARE_REFERENCES_VALIDATED, areReferencesValidated);
    }

    private void validatePaymentRefs(Map<String, List<String>> paymentRefMap) {
        Set<String> uniquePaymentIds = paymentRefMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        uniquePaymentIds.forEach(paymentManagementService::checkPaymentRef);
    }

    private void validateBillingAccountRefs(Map<String, String> baRefMap) {
        Set<String> uniqueBillingAccountIds = new HashSet<>(baRefMap.values());
        uniqueBillingAccountIds.forEach(accountManagementService::checkBillingAccount);
    }

    private void validateAppointmentRefs(Map<String, String> appRefMap) {
        appRefMap.values()
                .forEach(appointmentManagementService::fetchAppointmentById);
    }
}