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
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.PRODUCT_ORDER_STATE;

@Component("isProductOrderStatePartialGuard")
@Slf4j
public class ProductOrderStateGuard implements StateMachineGuard<String, String> {

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            log.debug("Re-execution detected for guard [{}], retrieving cached result",
                    GuardNameConstants.PRODUCT_ORDER_STATE_GUARD);
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCT_ORDER_STATE_GUARD);
        } else {
            log.info("Evaluating guard [{}] to check if product order state is partial",
                    GuardNameConstants.PRODUCT_ORDER_STATE_GUARD);
            ProductOrderStateType productOrderState = StateMachineUtil.getObjectValue(context, PRODUCT_ORDER_STATE, ProductOrderStateType.class);
            log.debug("Retrieved product order state: [{}]", productOrderState);

            boolean isPartialProductOrderState = isPartialProductOrderState(productOrderState);

            log.info("Guard [{}] evaluation result: [{}] (productOrderState={})",
                    GuardNameConstants.PRODUCT_ORDER_STATE_GUARD, isPartialProductOrderState, productOrderState);

            StateMachineUtil.setGuardContext(context, isPartialProductOrderState, GuardNameConstants.PRODUCT_ORDER_STATE_GUARD);
            return Mono.just(isPartialProductOrderState);
        }
    }

    private boolean isPartialProductOrderState(ProductOrderStateType productOrderState) {
        if (productOrderState == null) {
            log.warn("Product order state is null, guard will return false");
            return false;
        }
        boolean result = productOrderState.equals(ProductOrderStateType.PARTIAL);
        log.debug("Product order state [{}] is{} PARTIAL", productOrderState, result ? "" : " not");
        return result;
    }
}