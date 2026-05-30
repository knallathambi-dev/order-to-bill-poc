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
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("isProductOfferingIdProvidedOnModificationGuard")
@Slf4j
public class ExistenceProductOfferingIdOnModificationGuard implements StateMachineGuard<String, String> {
    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return StateMachineUtil.getGuardResult(context, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_ID_ON_MODIFICATION_GUARD);
        } else {
            log.info("Inside guard to check if product offering was provided on modification");
            String productOfferingId = StateMachineUtil.getStringValue(context, OrderCaptureConstants.OPTIONAL_PRODUCT_OFFERING_ID);
            boolean isProductOfferingIdProvidedOnModification = StringUtils.isNotBlank(productOfferingId);
            StateMachineUtil.setGuardContext(context, isProductOfferingIdProvidedOnModification, GuardNameConstants.EXISTENCE_PRODUCT_OFFERING_ID_ON_MODIFICATION_GUARD);
            return Mono.just(isProductOfferingIdProvidedOnModification);
        }
    }
}