// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.OrderValidityService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType.ACCEPTED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component("acceptProductOrderAction")
@Slf4j
public class OrderAcceptedAction implements StateMachineStateAction<String, String> {
    private final ProductOrderService productOrderService;

    private final OrderValidityService orderValidityService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderAcceptedAction(ProductOrderService productOrderService, OrderValidityService validityCharacteristicUtil) {
        this.productOrderService = productOrderService;
        this.orderValidityService = validityCharacteristicUtil;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        try {
            log.info("Inside update product order action");
            com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.RELATED_PARTY, com.orange.discobole.processflow.dto.generated.RelatedParty.class);
            orderValidityService.adjustOrderRequestedCompletionDate(productOrder, relatedParty.getId());
            orderValidityService.handleInvalidValidityCharacteristics(productOrder, relatedParty.getId(), true);
            productOrderService.updateProductOrderInventoryState(productOrder, ACCEPTED);
            setContextVariables(context, TRUE, productOrder);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Unable to update the product order [{}]:", e.getMessage(), e);
            context.getExtendedState().getVariables().put(StateMachineUtil.DESCRIPTION, DescriptionConstants.INTERNAL_SERVER_ERROR);
            productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.HELD);
            setContextVariables(context, FALSE, null);
            return Mono.empty();
        }
    }

    private void setContextVariables(StateContext<String, String> context, boolean isUpdated, ProductOrder productOrder) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.IS_PRODUCT_ORDER_ACCEPTED, isUpdated);
        if (isUpdated) {
            context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        }
    }
}