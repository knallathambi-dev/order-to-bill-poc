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

@Component("isProductOrderAcceptedGuard")
@Slf4j
public class ProductOrderAcceptedGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_ACCEPTED_GUARD);
        } else {
            log.info("Inside guard to check the product order state was updated to accepted");
            boolean result = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_ACCEPTED, FALSE);
            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.PRODUCT_ORDER_ACCEPTED_GUARD);
            return Mono.just(result);
        }
    }
}