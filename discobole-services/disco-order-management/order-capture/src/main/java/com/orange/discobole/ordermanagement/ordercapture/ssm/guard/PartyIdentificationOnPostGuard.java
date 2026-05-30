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

@Component("isPartyIdentifiedOnPostGuard")
@Slf4j
public class PartyIdentificationOnPostGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.PARTY_IDENTIFICATION_ON_POST_GUARD);
        } else {
            log.info("Inside guard to check if party was identified on post");
            RelatedParty relatedParty = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.RELATED_PARTY, RelatedParty.class);
            boolean result = checkPartyIdentification(relatedParty);
            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.PARTY_IDENTIFICATION_ON_POST_GUARD);
            return Mono.just(result);
        }
    }

    private boolean checkPartyIdentification(RelatedParty relatedParty) {
        return Objects.nonNull(relatedParty) && !isBlank(relatedParty.getRole()) && (relatedParty.getRole().equalsIgnoreCase(CUSTOMER) || relatedParty.getRole().equalsIgnoreCase(PROSPECT));
    }
}