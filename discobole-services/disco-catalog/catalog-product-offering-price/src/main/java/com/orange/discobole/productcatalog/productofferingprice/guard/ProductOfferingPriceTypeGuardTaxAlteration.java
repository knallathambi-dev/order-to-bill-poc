// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.guard;

import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * ProductOfferingPriceTypeGuard defines Condition to be executed when state
 * reaches to proceed according to the price type state.
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Component("isPriceTypeTax")
public class ProductOfferingPriceTypeGuardTaxAlteration implements StateMachineGuard<String,String> {
	
	/**
	 * this guard check if selected product offering price is of type pop charge, if
	 * yes then it return true ,otherwise false.
	 * 
	 * @param StateContext 
	 * @return boolean
	 */
	@Override
	public Mono<Boolean> apply(StateContext<String, String> context) {
		return Mono.just(context.getExtendedState().getVariables().get(ProductOfferingPriceConstants.SELECT_POP_TYPE)
				.equals(ProductOfferingPriceType.PRODUCTOFFERINGTAXALTERATION.toString()));
	}
}
