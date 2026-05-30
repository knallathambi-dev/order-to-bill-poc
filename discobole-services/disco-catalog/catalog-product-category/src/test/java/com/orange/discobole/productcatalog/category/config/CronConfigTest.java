// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.CronConfig;


public class CronConfigTest extends CategoryApplicationTests {

@InjectMocks
CronConfig cronConfig;
@Test
 void testCronConfig() {
	cronConfig.setIntervalUnit("3");
	cronConfig.setJobInterval(0L);
	cronConfig.setProductOfferingExpression("");
	cronConfig.setProductOfferingPriceExpression("");
	cronConfig.setProductSpecExpression("");
	assertNotNull(cronConfig.getIntervalUnit());
	assertNotNull(cronConfig.getJobInterval());
	assertNotNull(cronConfig.productOfferingExpression());
	assertNotNull(cronConfig.productOfferingPriceExpression());
	assertNotNull(cronConfig.productSpecExpression());
}
}
