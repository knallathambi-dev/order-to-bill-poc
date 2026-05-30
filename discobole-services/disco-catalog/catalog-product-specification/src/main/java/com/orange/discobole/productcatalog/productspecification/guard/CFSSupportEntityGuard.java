// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.guard;

import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;

import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("isSupportEntityCFS")
public class CFSSupportEntityGuard implements StateMachineGuard<String, String> {

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        return Mono.just(!(context.getExtendedState().getVariables().get(ProductSpecConstants.STOCK_ITEM_ID) != null));
    }
}