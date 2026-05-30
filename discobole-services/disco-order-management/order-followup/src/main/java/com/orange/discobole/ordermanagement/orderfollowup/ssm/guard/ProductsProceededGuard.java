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
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderfollowup.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.PRODUCT_ORDER_ID;
import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.PRODUCT_ORDER_STATE;
import static java.lang.Boolean.FALSE;

@Component("isAllProductsProcessedGuard")
@Slf4j
public class ProductsProceededGuard implements StateMachineGuard<String, String> {
    private final ProductOrderEventService productOrderEventService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductsProceededGuard(ProductOrderEventService productOrderEventService) {
        this.productOrderEventService = productOrderEventService;
    }

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            log.debug("Re-execution detected for guard [{}], retrieving cached result",
                    GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);
        } else {
            try {
                log.info("Evaluating guard [{}] to check if all products were proceeded",
                        GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);
                ProductOrderStateType productOrderState = StateMachineUtil.getObjectValue(context, PRODUCT_ORDER_STATE, ProductOrderStateType.class);
                log.debug("Retrieved product order state: [{}]", productOrderState);

                boolean isAllProductProceeded = isAllProductProceeded(productOrderState);

                log.info("Guard [{}] evaluation result: [{}] (productOrderState={})",
                        GuardNameConstants.PRODUCTS_PROCEEDED_GUARD, isAllProductProceeded, productOrderState);

                StateMachineUtil.setGuardContext(context, isAllProductProceeded, GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);
                removeProceededProductOrderEvent(context, isAllProductProceeded);
                return Mono.just(isAllProductProceeded);
            } catch (Exception e) {
                log.error("Guard [{}] failed unexpectedly - unable to check if all products were proceeded: [{}]",
                        GuardNameConstants.PRODUCTS_PROCEEDED_GUARD, e.getMessage(), e);
                StateMachineUtil.setGuardContext(context, FALSE, GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);
                return Mono.just(false);
            }
        }
    }

    private boolean isAllProductProceeded(ProductOrderStateType productOrderState) {
        if (productOrderState == null) {
            log.warn("Product order state is null, guard [{}] will return false",
                    GuardNameConstants.PRODUCTS_PROCEEDED_GUARD);
            return false;
        }
        boolean result = productOrderState.equals(ProductOrderStateType.FAILED)
                || productOrderState.equals(ProductOrderStateType.PARTIAL)
                || productOrderState.equals(ProductOrderStateType.COMPLETED);
        log.debug("Product order state [{}] is{} a terminal state (FAILED|PARTIAL|COMPLETED)",
                productOrderState, result ? "" : " not");
        return result;
    }

    private void removeProceededProductOrderEvent(StateContext<String, String> context, boolean isAllProductProceeded) {
        if (isAllProductProceeded) {
            String productOrderId = StateMachineUtil.getStringValue(context, PRODUCT_ORDER_ID);
            log.info("All products proceeded - removing product order events for productOrderId: [{}]", productOrderId);
            productOrderEventService.deleteByProductOrderId(productOrderId);
            log.debug("Successfully deleted product order events for productOrderId: [{}]", productOrderId);
        } else {
            log.debug("Not all products proceeded, skipping product order event cleanup");
        }
    }
}