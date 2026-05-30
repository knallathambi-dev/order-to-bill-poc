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
import com.orange.discobole.ordermanagement.ordercapture.util.MiscUtil;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component("isReservationNeededGuard")
@Slf4j
public class ReservationRequirementGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.RESERVATION_REQUIREMENT_GUARD);
        } else {
            log.info("Inside guard to check if reservation was needed");
            Map<String, List<String>> productOrderItemLogicalResourcesMap =
                    StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES);
            List<String> physicalProductOrderItems =
                    StateMachineUtil.getListValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, String.class);
            boolean result = !MiscUtil.isMapEmptyOrContainsNull(productOrderItemLogicalResourcesMap) || !physicalProductOrderItems.isEmpty();
            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.RESERVATION_REQUIREMENT_GUARD);
            return Mono.just(result);
        }
    }
}