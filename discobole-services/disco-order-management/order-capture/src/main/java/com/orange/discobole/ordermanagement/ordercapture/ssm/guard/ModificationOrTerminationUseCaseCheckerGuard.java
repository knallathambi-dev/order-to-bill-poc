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

@Component("isModificationOrTerminationUseCaseGuard")
@Slf4j
public class ModificationOrTerminationUseCaseCheckerGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.MODIFICATION_OR_TERMINATION_USE_CASE_CHECKER_GUARD);
        }
        log.info("Inside guard to check if use case was modification or termination");
        boolean result = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_MODIFICATION_OR_TERMINATION_OR_MIGRATION_USE_CASE, FALSE);
        StateMachineUtil.setGuardContext(context, result, GuardNameConstants.MODIFICATION_OR_TERMINATION_USE_CASE_CHECKER_GUARD);
        return Mono.just(result);
    }
}