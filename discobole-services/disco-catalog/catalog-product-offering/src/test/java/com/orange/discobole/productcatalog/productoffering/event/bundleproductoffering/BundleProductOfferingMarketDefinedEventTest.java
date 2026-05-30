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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.MarketSegmentRef;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingMarketDefinedEvent;

class BundleProductOfferingMarketDefinedEventTest extends ProductOfferingApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingMarketDefinedEventTest.class);

	private BundleProductOfferingMarketDefinedEvent bundleProductOfferingMarketDefinedEvent;

	private final List<MarketSegmentRef> characteristicList = new ArrayList<>();
	
	
	private String aggregateId = "1";


	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		MarketSegmentRef marketRef = new MarketSegmentRef();
		marketRef.setId("mark1");
		marketRef.setName("mark1");
		characteristicList.add(marketRef);
		bundleProductOfferingMarketDefinedEvent = new BundleProductOfferingMarketDefinedEvent(productOffId,
				characteristicList, lastUpdate);
	}

	@Test
	void bundleProductOfferingMarketDefinedEventTest() {
		Assertions.assertAll("bundleProductOfferingMarketDefinedEvent",
				() -> Assertions.assertEquals("PROD1", bundleProductOfferingMarketDefinedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(bundleProductOfferingMarketDefinedEvent.getMarketSegments(),
						characteristicList));
		LOGGER.info("bundleProductOfferingMarketDefinedEvent: {}", bundleProductOfferingMarketDefinedEvent);
	}
}
