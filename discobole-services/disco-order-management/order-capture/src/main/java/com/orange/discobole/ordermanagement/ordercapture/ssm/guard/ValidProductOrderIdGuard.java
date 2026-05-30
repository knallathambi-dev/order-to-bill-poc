// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.ProductOrderUtil;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component("isValidProductOrderIdGuard")
@Slf4j
public class ValidProductOrderIdGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);
        }
        log.info("Inside guard to check if the product order id and related party id were validated");
        String productOrderId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.PRODUCT_ORDER_ID);
        ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        RelatedParty relatedParty = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.RELATED_PARTY, RelatedParty.class);
        RelatedParty providedRelatedParty = ProductOrderUtil.getRelatedParty(context);

        String providedPartyId = Objects.nonNull(providedRelatedParty) ? providedRelatedParty.getId() : null;

        boolean isValidProductOrderId = Objects.nonNull(productOrder) &&
                !StringUtils.isBlank(productOrder.getId()) &&
                productOrder.getId().equals(productOrderId);

        boolean isValidRelatedPartyId = Objects.nonNull(relatedParty) &&
                !StringUtils.isBlank(relatedParty.getId()) &&
                relatedParty.getId().equals(providedPartyId);


        if (!isValidProductOrderId) {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED);
            StateMachineUtil.setGuardContext(context, false, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);
            return Mono.just(false);
        }
        if (!isValidRelatedPartyId) {
            StateMachineUtil.setDescriptionContext(context, DescriptionConstants.VALID_PARTY_IDENTIFIER_REQUIRED);
            StateMachineUtil.setGuardContext(context, false, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);
            return Mono.just(false);
        }
        StateMachineUtil.setGuardContext(context, true, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);
        return Mono.just(true);
    }
}