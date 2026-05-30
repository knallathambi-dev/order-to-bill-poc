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
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingVersionCreatedEvent;

class BundleProductOfferingVersionCreatedEventTest extends ProductOfferingApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingVersionCreatedEventTest.class);

	private BundleProductOfferingVersionCreatedEvent bundleProductOfferingVersionCreatedEvent;

	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		bundleProductOfferingVersionCreatedEvent = new BundleProductOfferingVersionCreatedEvent(productOffId,
				"1.0.0", lastUpdate);
	}

	@Test
	void bundleProductOfferingVersionCreatedEventTest() {
		Assertions.assertAll("bundleProductOfferingCategoryDefinedEvent",
				() -> Assertions.assertEquals("PROD1", bundleProductOfferingVersionCreatedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals("1.0.0",
						bundleProductOfferingVersionCreatedEvent.getProductOfferingVersion()));
		LOGGER.info("bundleProductOfferingVersionCreatedEvent: {}", bundleProductOfferingVersionCreatedEvent);
	}
}
