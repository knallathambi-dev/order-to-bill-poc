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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;

class CronConfigTest extends ProductOfferingPriceApplicationTests {

	@InjectMocks
	private CronConfig cronConfig;

	private static String expectedUnit = "HOUR";
	private static Long expectedInterval = 123456789L;
	private static String expectedPOPId = "pop1";

	@BeforeEach
	void setup() {
		cronConfig.setIntervalUnit("HOUR");
		cronConfig.setJobInterval(123456789L);
		cronConfig.setProductOfferingPriceExpression("pop1");
	}

	@Test
	void test() {
		Assertions.assertAll("cronConfig", () -> Assertions.assertEquals(cronConfig.getIntervalUnit(), expectedUnit),
				() -> Assertions.assertEquals(cronConfig.productOfferingPriceExpression(), expectedPOPId),
				() -> Assertions.assertEquals(cronConfig.getJobInterval(), expectedInterval));
	}
}
