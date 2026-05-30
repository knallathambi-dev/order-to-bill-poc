// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.ssm.guard;

import com.orange.discobole.ordermanagement.orderfollowup.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.orderfollowup.util.StateMachineUtil;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.IS_PRODUCT_UPDATED;
import static java.lang.Boolean.FALSE;

@Component("isProductUpdatedGuard")
@Slf4j
public class ProductUpdateGuard implements StateMachineGuard<String, String> {

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            log.debug("Re-execution detected for guard [{}], retrieving cached result",
                    GuardNameConstants.PRODUCT_UPDATE_GUARD);
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_UPDATE_GUARD);
        } else {
            log.info("Evaluating guard [{}] to check if the product order was updated",
                    GuardNameConstants.PRODUCT_UPDATE_GUARD);
            boolean result = StateMachineUtil.getBooleanValue(context, IS_PRODUCT_UPDATED, FALSE);

            log.info("Guard [{}] evaluation result: [{}]",
                    GuardNameConstants.PRODUCT_UPDATE_GUARD, result);

            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.PRODUCT_UPDATE_GUARD);
            return Mono.just(result);
        }
    }
}