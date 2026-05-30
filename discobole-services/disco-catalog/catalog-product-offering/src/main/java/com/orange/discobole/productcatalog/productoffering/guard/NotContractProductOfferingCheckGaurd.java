// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.guard;

import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * ContractProductOfferingCheckGaurd defines Condition to be executed when state
 * reaches Operation state.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("checkNotContractPo")
public class NotContractProductOfferingCheckGaurd implements StateMachineGuard<String, String>{

	@Override
	public Mono<Boolean> apply(StateContext<String, String> context) {
		return Mono.just(((String)context.getExtendedState().getVariables().get(ProductOffConstants.PRODUCTOFFERINGTYPE))
				.equalsIgnoreCase(ProductOfferingType.ATOMICPRODUCTOFFERING.toString()) ||
				((String)context.getExtendedState().getVariables().get(ProductOffConstants.PRODUCTOFFERINGTYPE))
						.equalsIgnoreCase(ProductOfferingType.BUNDLEPRODUCTOFFERING.toString()));
	}
}
