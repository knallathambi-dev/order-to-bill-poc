// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;

class ApplicationContextConfigTest extends ProductOfferingPriceApplicationTests {

	@InjectMocks
	private ApplicationContextConfig applicationContextConfig;

	@Test
	void testApplicationContextConfig() {
		Assertions.assertNotNull(applicationContextConfig.getContext());
	}
}
