// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.CategoryRef;


class BundleProductOfferingCategoryDefinedEventTest extends ProductOfferingApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingCategoryDefinedEventTest.class);

	private BundleProductOfferingCategoryDefinedEvent bundleProductOfferingCategoryDefinedEvent;

	private final Set<CategoryRef> characteristicList = new HashSet<>();
	


	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		CategoryRef categoryRef = new CategoryRef();
		categoryRef.setId("cat1");
		categoryRef.setName("cat1");
		characteristicList.add(categoryRef);
		bundleProductOfferingCategoryDefinedEvent = new BundleProductOfferingCategoryDefinedEvent(productOffId,
				characteristicList, lastUpdate);
	}

	@Test
	void bundleProductOfferingCategoryDefinedEventTest() {
		Assertions.assertAll("bundleProductOfferingCategoryDefinedEvent",
				() -> Assertions.assertEquals("PROD1",
						bundleProductOfferingCategoryDefinedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(bundleProductOfferingCategoryDefinedEvent.getCategories(),
						characteristicList));
		LOGGER.info("bundleProductOfferingCategoryDefinedEvent: {}", bundleProductOfferingCategoryDefinedEvent);
	}
}
