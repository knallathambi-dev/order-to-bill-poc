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
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static java.lang.Boolean.TRUE;

@Component("createProspectPartyRoleAction")
@Slf4j
public class ProspectPartyRoleCreationAction implements StateMachineStateAction<String, String> {
    private final PartyRoleManagementServiceImpl partyRoleManagementService;
    private final SettingsService settingsService;

    public ProspectPartyRoleCreationAction(PartyRoleManagementServiceImpl partyRoleManagementService, SettingsService settingsService) {
        this.partyRoleManagementService = partyRoleManagementService;
        this.settingsService = settingsService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        try {
            log.info("Inside of the creation of prospect party role action");
            SettingsEntity settings = settingsService.getSettings();
            PartyRole partyRole = createPartyRole(context);
            RelatedParty relatedParty;
            if (settings.isCheckPartyManagementEnabled()) {
                PartyRole createdPartyRole = partyRoleManagementService.createPartyRole(partyRole);
                relatedParty = buildRelatedParty(createdPartyRole, context);
            } else {
                relatedParty = buildRelatedParty(partyRole, context);
            }
            setContextVariables(context, true, relatedParty);
        } catch (DiscoException e) {
            StateMachineUtil.setNextTaskToNull(context);
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.PARTY_MANAGEMENT_SERVICE_UNREACHABLE);
            setContextVariables(context, false, null);
        } catch (Exception e) {
            log.error("Unable to create prospect party role [{}]:", e.getMessage(), e);
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.INTERNAL_SERVER_ERROR);
            setContextVariables(context, false, null);
        }
        return Mono.empty();
    }

    private PartyRole createPartyRole(StateContext<String, String> context) {
        String partyId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.PARTY_ID);
        String partyName = StateMachineUtil.getStringValue(context, OrderCaptureConstants.PARTY_NAME);

        return PartyRole.builder()
                .engagedParty(EngagedParty.builder().id(partyId).build())
                .name(partyName)
                .partyRoleSpecification(PartyRoleSpecification.builder().name(OrderCaptureConstants.PROSPECT).build())
                .build();
    }

    private RelatedParty buildRelatedParty(PartyRole createdPartyRole, StateContext<String, String> context) {
        String partyReferredType = StateMachineUtil.getStringValue(context, OrderCaptureConstants.PARTY_REFERRED_TYPE);
        return new RelatedParty()
                .id(createdPartyRole.getEngagedParty().getId())
                .name(createdPartyRole.getName())
                .role(createdPartyRole.getPartyRoleSpecification().getName())
                .referredType(partyReferredType);
    }

    private void setContextVariables(StateContext<String, String> context, boolean isProspectPartyRoleCreated, RelatedParty relatedParty) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.IS_PROSPECT_PARTY_ROLE_CREATED, isProspectPartyRoleCreated);
        if (isProspectPartyRoleCreated) {
            context.getExtendedState().getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);
            context.getExtendedState().getVariables().put(OrderCaptureConstants.TASK_RELATED_PARTY, Collections.singletonList(relatedParty));
        }
    }
}