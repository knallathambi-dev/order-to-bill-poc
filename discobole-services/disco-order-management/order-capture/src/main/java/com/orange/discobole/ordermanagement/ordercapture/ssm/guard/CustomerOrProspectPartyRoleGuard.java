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
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("isCustomerOrProspectPartyRoleGuard")
@Slf4j
public class CustomerOrProspectPartyRoleGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.CUSTOMER_OR_PROSPECT_PARTY_ROLE_GUARD);
        } else {
            log.info("Inside guard to check if party role was customer or prospect");
            boolean result = Boolean.TRUE.equals(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE, Boolean.FALSE));
            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.CUSTOMER_OR_PROSPECT_PARTY_ROLE_GUARD);
            return Mono.just(result);
        }
    }
}