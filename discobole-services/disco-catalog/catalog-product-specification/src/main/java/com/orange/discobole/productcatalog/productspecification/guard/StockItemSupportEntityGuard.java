// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.guard;


import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;

import reactor.core.publisher.Mono;

/**
 * StockItemSupportEntityGuard defines Condition to be executed when state
 * reaches to proceed according to the Stock Item as support entity.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
@Component("isSupportEntityStockItemType")
public class StockItemSupportEntityGuard implements StateMachineGuard<String, String> {

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        return Mono.just(context.getExtendedState().getVariables().get(ProductSpecConstants.STOCK_ITEM_ID) != null);
    }
}
