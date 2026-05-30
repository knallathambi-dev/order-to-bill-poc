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

import static java.lang.Boolean.FALSE;

@Component("areReferencesValidated")
@Slf4j
public class ReferencesValidationGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.REFERENCES_VALIDATION_GUARD);
        } else {
            log.info("Inside guard to check if references were validated");
            boolean result = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_REFERENCES_VALIDATED, FALSE);
            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.REFERENCES_VALIDATION_GUARD);
            return Mono.just(result);
        }
    }
}