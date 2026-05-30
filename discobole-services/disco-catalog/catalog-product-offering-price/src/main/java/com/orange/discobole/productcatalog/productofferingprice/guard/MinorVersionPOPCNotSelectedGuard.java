// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.guard;

import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.VersionType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;

import reactor.core.publisher.Mono;

@Component("minorVersionPOPCNotSelectedGuard")
public class MinorVersionPOPCNotSelectedGuard implements StateMachineGuard<String,String> {

	/**
	 * This guard checks POP type which must be POPC and the version which must not be minor.
	 * 
	 * @return boolean
	 */
	@Override
	public Mono<Boolean> apply(StateContext<String, String> context) {
		return Mono.just(context.getExtendedState().getVariables().get(ProductOfferingPriceConstants.SELECT_POP_TYPE)
				.equals(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.toString())
				&& !context.getExtendedState().getVariables().get(ProductOfferingPriceConstants.VERSION_TYPE)
						.equals(VersionType.MINOR.toString()));
	}

}