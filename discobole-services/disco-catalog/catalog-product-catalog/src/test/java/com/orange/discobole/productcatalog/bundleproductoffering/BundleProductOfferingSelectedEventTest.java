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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.BundledProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.BundledProductOfferingOption;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingSelectedEvent;

class BundleProductOfferingSelectedEventTest extends CatalogApplicationTests {
	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingSelectedEventTest.class);

	private BundleProductOfferingSelectedEvent bundleProductOfferingSelectedEvent;

	private final List<BundledProductOffering> characteristicList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		BundledProductOffering bundlePo = new BundledProductOffering();
		bundlePo.setId("1");
		bundlePo.setName("ADD");
		bundlePo.setLifecycleStatus(ProductOfferingLifecycle.INSTUDY.toString());
		BundledProductOfferingOption bundledProductOfferingOption = new BundledProductOfferingOption();
		bundledProductOfferingOption.setNumberRelOfferDefault(0);
		bundledProductOfferingOption.setNumberRelOfferLowerLimit(0);
		bundledProductOfferingOption.setNumberRelOfferUpperLimit(1);
		bundlePo.setBundledProductOfferingOption(bundledProductOfferingOption);
		characteristicList.add(bundlePo);
		bundleProductOfferingSelectedEvent = new BundleProductOfferingSelectedEvent(productOffId, characteristicList,
				lastUpdate, 0, 1);

	}

	@Test
	void bundleProductOfferingSelectedEventTest() {
		Assertions.assertAll("bundleProductOfferingSelectedEvent",
				() -> Assertions.assertEquals("PROD1", bundleProductOfferingSelectedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(bundleProductOfferingSelectedEvent.getBundleProductOffering(),
						characteristicList));
		LOGGER.debug("bundleProductOfferingSelectedEvent: {}", bundleProductOfferingSelectedEvent);
	}

}
