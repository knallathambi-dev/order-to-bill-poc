// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.user.guards;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util.StateMachineUtil;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static java.lang.Boolean.FALSE;

@Component("resolutionGuard")
@Slf4j
public class ResolutionGuard implements StateMachineGuard<String, String> {

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, "resolutionGuard");
        } else {
            log.info("Inside guard to check if resolution was required");
            boolean result = StateMachineUtil.getBooleanValue(context, "resolutionGuard", FALSE);
            StateMachineUtil.setGuardContext(context, result, "resolutionGuard");
            return Mono.just(result);
        }
    }
}