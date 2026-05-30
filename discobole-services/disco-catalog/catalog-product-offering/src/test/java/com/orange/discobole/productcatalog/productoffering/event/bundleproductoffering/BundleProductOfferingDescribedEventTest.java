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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;


class BundleProductOfferingDescribedEventTest extends ProductOfferingApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingDescribedEventTest.class);

	private BundleProductOfferingDescribedEvent bundleProductOfferingDescribedEvent;
	


	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		bundleProductOfferingDescribedEvent = new BundleProductOfferingDescribedEvent(productOffId, "desc", "status", "name", "brand", ProductOfferingType.BUNDLEPRODUCTOFFERING, true, lastUpdate);
	}
	
	@Test
	void bundleProductOfferingDescribedEventTest() {
		Assertions.assertAll("bundleProductOfferingDescribedEvent",
				() -> Assertions.assertEquals("PROD1", bundleProductOfferingDescribedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(ProductOfferingType.BUNDLEPRODUCTOFFERING,
						bundleProductOfferingDescribedEvent.getType()));
		LOGGER.info("bundleProductOfferingDescribedEvent: {}", bundleProductOfferingDescribedEvent);
	}
}

