// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.role.PartyRole;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.SettingsService;
import com.orange.discobole.ordermanagement.ordercapture.service.impl.PartyRoleManagementServiceImpl;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;

@Component("checkPartyRolesAction")
@Slf4j
public class PartyRolesCheckAction implements StateMachineStateAction<String, String> {
    private final PartyRoleManagementServiceImpl partyRoleManagementService;
    private final SettingsService settingsService;

    public PartyRolesCheckAction(PartyRoleManagementServiceImpl partyRoleManagementService, SettingsService settingsService) {
        this.partyRoleManagementService = partyRoleManagementService;
        this.settingsService = settingsService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (StateMachineUtil.isReExecutionAction(context).equals(Boolean.TRUE)) {
            return Mono.empty();
        }
        log.info("Inside check party roles action");
        String partyId = StateMachineUtil.getStringValue(context, PARTY_ID);
        String partyName = StateMachineUtil.getStringValue(context, PARTY_NAME);

        try {
            SettingsEntity settings = settingsService.getSettings();
            List<PartyRole> partyRoles = new ArrayList<>();
            if (settings.isCheckPartyManagementEnabled()) {
                partyRoles = partyRoleManagementService.getPartyRoles(partyId, partyName);
            }
            boolean isCustomerOrProspect = checkAndAddPartyRoles(context, partyRoles);
            setContextVariables(context, isCustomerOrProspect);
            context.getExtendedState().getVariables().put(ARE_PARTY_ROLES_CHECKED, true);

        } catch (DiscoException e) {
            if (e.getReason().equals(DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE)) {
                StateMachineUtil.setNextTaskToNullInCaseOfUnreachableService(context, e.getReason(), DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE);
                StateMachineUtil.setDescriptionContext(context, DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE);
            } else {
                StateMachineUtil.setDescriptionContext(context, VALID_PARTY_IDENTIFIER_REQUIRED);
            }
            context.getExtendedState().getVariables().put(ARE_PARTY_ROLES_CHECKED, false);
        } catch (Exception e) {
            log.error("Unable to check party roles [{}]:", e.getMessage(), e);
            context.getExtendedState().getVariables().put(ARE_PARTY_ROLES_CHECKED, false);
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.INTERNAL_SERVER_ERROR);
        }

        return Mono.empty();
    }

    private boolean checkAndAddPartyRoles(StateContext<String, String> context, List<PartyRole> partyRoles) {
        for (String role : new String[]{CUSTOMER, PROSPECT}) {
            PartyRole partyRole = findPartyRole(partyRoles, role);
            if (partyRole != null) {
                addRelatedPartyToContext(partyRole, context);
                return true;
            }
        }
        return false;
    }

    private PartyRole findPartyRole(List<PartyRole> partyRoles, String targetRole) {
        return partyRoles.stream()
                .filter(partyRole -> targetRole.equalsIgnoreCase(partyRole.getPartyRoleSpecification().getName()))
                .findFirst()
                .orElse(null);
    }

    private void addRelatedPartyToContext(PartyRole partyRole, StateContext<String, String> context) {
        String partyReferredType = StateMachineUtil.getStringValue(context, PARTY_REFERRED_TYPE);
        RelatedParty relatedParty = new RelatedParty()
                .id(partyRole.getEngagedParty().getId())
                .role(partyRole.getPartyRoleSpecification().getName())
                .name(partyRole.getName())
                .referredType(partyReferredType);
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        variables.put(TASK_RELATED_PARTY, Collections.singletonList(relatedParty));
        variables.put(RELATED_PARTY, relatedParty);
    }

    private void setContextVariables(StateContext<String, String> context, boolean isCustomerOrProspect) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        variables.put(IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE, isCustomerOrProspect);
        variables.put(IS_NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE, !isCustomerOrProspect);
    }
}