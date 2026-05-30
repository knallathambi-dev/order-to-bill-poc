// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;
import com.orange.discobole.productcatalog.productspecification.event.productspec.ProductSpecUsageSelectedEvent;

class ProductSpecUsageSelectedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecUsageSelectedEventTest.class);

	private ProductSpecUsageSelectedEvent productSpecUsageSelectedEvent;
	private final List<UsageSpecification> usageSpecifications = new ArrayList<>();

	@BeforeEach
	void setUp() {
		String productSpecId = "PROD1";

		UsageSpecification usageSpec = new UsageSpecification();
		usageSpec.id("1").name("usage").validFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		usageSpecifications.add(usageSpec);
		productSpecUsageSelectedEvent = new ProductSpecUsageSelectedEvent(productSpecId, usageSpecifications);
	}

	@Test
	void productSpecUsageSelectedEventTest() {
		Assertions.assertAll("productSpecUsageSelectedEvent",
				() -> Assertions.assertEquals("PROD1", productSpecUsageSelectedEvent.getProductSpecId()),
				() -> Assertions.assertEquals(productSpecUsageSelectedEvent.getUsageSpecs(), usageSpecifications));
		LOGGER.info("productSpecUsageDefinedEvent: {}", productSpecUsageSelectedEvent);
	}
}
