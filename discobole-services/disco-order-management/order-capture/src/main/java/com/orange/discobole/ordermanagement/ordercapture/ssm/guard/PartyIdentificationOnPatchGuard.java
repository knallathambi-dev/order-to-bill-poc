// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.ProductOrderUtil;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.CUSTOMER;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.PROSPECT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component("isPartyIdentifiedOnPatchGuard")
@Slf4j
public class PartyIdentificationOnPatchGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_PATCH_GUARD);
        } else {
            log.info("Inside guard to check if party was identified on patch");
            RelatedParty relatedParty = ProductOrderUtil.getRelatedParty(context);
            boolean result = checkPartyIdentification(relatedParty);

            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.PARTY_IDENTIFICATION_ON_PATCH_GUARD);

            if (result) {
                setContextVariables(context, relatedParty);
            }
            return Mono.just(result);
        }
    }

    private boolean checkPartyIdentification(RelatedParty relatedParty) {
        if (Objects.nonNull(relatedParty) && isValidRelatedParty(relatedParty)) {
            String partyRole = relatedParty.getRole();
            return partyRole.equalsIgnoreCase(CUSTOMER) || partyRole.equalsIgnoreCase(PROSPECT);
        }
        return false;
    }

    private void setContextVariables(StateContext<String, String> context, RelatedParty relatedParty) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);
    }

    private boolean isValidRelatedParty(RelatedParty relatedParty) {
        return !isBlank(relatedParty.getRole()) && !isBlank(relatedParty.getId());
    }
}