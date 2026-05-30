// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.bundleproductoffering;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.CreateBundleProductOfferingEvent;

class CreateBundleProductOfferingEventTest extends CatalogApplicationTests{
	
private static final Logger LOGGER = LogManager.getLogger(CreateBundleProductOfferingEventTest.class);
	
	private CreateBundleProductOfferingEvent createBundleProductOfferingEvent;
	
	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		createBundleProductOfferingEvent = new CreateBundleProductOfferingEvent(productOffId, lastUpdate, true, true,
				true, ProductOfferingType.BUNDLEPRODUCTOFFERING);
	}

	@Test
	void createBundleProductOfferingEventTest() {
		Assertions.assertAll("createBundleProductOfferingEvent",
				() -> Assertions.assertEquals("PROD1", createBundleProductOfferingEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(ProductOfferingType.BUNDLEPRODUCTOFFERING,
						createBundleProductOfferingEvent.getType()));
		LOGGER.debug("createBundleProductOfferingEvent: {}", createBundleProductOfferingEvent);
	}

}
