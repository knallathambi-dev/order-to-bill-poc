// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.guard;

import java.util.HashMap;
import java.util.Map;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;

import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;

class ProductOfferingPriceTypeGuardAlterationTest {

	@InjectMocks
	private ProductOfferingPriceTypeGuardAlteration productOfferingPriceTypeGuardAlteration;

	@Test
	void testEvaluate() {
		productOfferingPriceTypeGuardAlteration = new ProductOfferingPriceTypeGuardAlteration();
		StateContext<String, String> context = Mockito.mock(StateContext.class);
		ExtendedState state = Mockito.mock(ExtendedState.class);
		Map<Object, Object> map = new HashMap<>();
		map.put(ProductOfferingPriceConstants.SELECT_POP_TYPE,
				ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION.toString());
		Mockito.when(context.getExtendedState()).thenReturn(state);
		Mockito.when(state.getVariables()).thenReturn(map);
		Assertions.assertTrue(productOfferingPriceTypeGuardAlteration.apply(context).block());
	}

}
