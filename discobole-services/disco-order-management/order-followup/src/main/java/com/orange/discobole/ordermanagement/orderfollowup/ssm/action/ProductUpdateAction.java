// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.ssm.action;

import com.orange.discobole.ordermanagement.orderfollowup.enums.OfupStateType;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.orderfollowup.service.ProductOrderEventService;
import com.orange.discobole.ordermanagement.orderfollowup.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage.ERROR_UPDATING_PRODUCTS_IN_INVENTORY;
import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("updateProductStateAction")
@Slf4j
public class ProductUpdateAction implements StateMachineStateAction<String, String> {
    private final ProductOrderEventService productOrderEventService;
    private final ProductInventoryService productInventoryService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductUpdateAction(ProductOrderEventService productOrderEventService, ProductInventoryService productInventoryService) {
        this.productOrderEventService = productOrderEventService;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            log.debug("Skipping re-execution of updateProductStateAction");
            return Mono.empty();
        }
        String productOrderId = null;
        String productOrderItemId = null;
        String eventId = null;
        try {
            productOrderId = StateMachineUtil.getStringValue(context, PRODUCT_ORDER_ID);
            productOrderItemId = StateMachineUtil.getStringValue(context, PRODUCT_ORDER_ITEM_ID);
            eventId = StateMachineUtil.getStringValue(context, PRODUCT_ORDER_ITEM_EVENT_ID);
            ProductOrderStateType productOrderState = StateMachineUtil.getObjectValue(context, PRODUCT_ORDER_STATE, ProductOrderStateType.class);

            log.info("Starting product update action for orderId={}, itemId={}, eventId={}, product order state={}",
                    productOrderId, productOrderItemId, eventId, productOrderState);

            productInventoryService.updateProductsHierarchy(productOrderId, productOrderItemId, productOrderState);
            setContextVariables(context, TRUE);
            productOrderEventService.updateProductItemOfupState(productOrderId, eventId, OfupStateType.COMPLETED);

            log.info("Successfully updated products in inventory for orderId={}, itemId={}, eventId={}",
                    productOrderId, productOrderItemId, eventId);
            return Mono.empty();
        } catch (DiscoException e) {
            if (e.getReason().equals(ERROR_UPDATING_PRODUCTS_IN_INVENTORY)) {
                log.error("Failed to update products in inventory for orderId={}, itemId={}, eventId={}: {}",
                        productOrderId, productOrderItemId, eventId, e.getMessage(), e);
                productOrderEventService.updateProductItemOfupState(productOrderId, eventId, OfupStateType.FAILED);
            } else {
                log.error("Recoverable error updating products for orderId={}, itemId={}, eventId={}, reason={}: {}",
                        productOrderId, productOrderItemId, eventId, e.getReason(), e.getMessage(), e);
                productOrderEventService.updateProductItemOfupState(productOrderId, eventId, OfupStateType.NEW);
            }
            setContextVariables(context, FALSE);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Unexpected error updating products for orderId={}, itemId={}, eventId={}: {}",
                    productOrderId, productOrderItemId, eventId, e.getMessage(), e);
            setContextVariables(context, FALSE);
            productOrderEventService.updateProductItemOfupState(productOrderId, eventId, OfupStateType.FAILED);
            return Mono.empty();
        }
    }

    private void setContextVariables(StateContext<String, String> context, boolean isProductUpdated) {
        context.getExtendedState().getVariables().put(IS_PRODUCT_UPDATED, isProductUpdated);
    }
}