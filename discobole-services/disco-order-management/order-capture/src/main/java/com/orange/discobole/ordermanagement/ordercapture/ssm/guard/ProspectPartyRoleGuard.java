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
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.PROSPECT;

@Component("isProspectPartyRoleGuard")
@Slf4j
public class ProspectPartyRoleGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.PROSPECT_PARTY_ROLE_GUARD);
        } else {
            log.info("Inside guard to check party role was prospect");
            ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
            boolean result = isPartyRoleProspect(productOrder);
            StateMachineUtil.setGuardContext(context, result, GuardNameConstants.PROSPECT_PARTY_ROLE_GUARD);
            return Mono.just(result);
        }
    }

    private boolean isPartyRoleProspect(ProductOrder productOrder) {
        if (Objects.nonNull(productOrder) && !CollectionUtils.isEmpty(productOrder.getRelatedParty())) {
            String partyRole = productOrder.getRelatedParty().get(0).getRole();
            return PROSPECT.equalsIgnoreCase(partyRole);
        }
        return false;
    }
}