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
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelRef;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingChannelDefinedEvent;

class BundleProductOfferingChannelDefinedEventTest extends ProductOfferingApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingChannelDefinedEventTest.class);

	private BundleProductOfferingChannelDefinedEvent bundleProductOfferingChannelDefinedEvent;

	private final List<ChannelRef> characteristicList = new ArrayList<>();


	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ChannelRef channelRef = new ChannelRef();
		channelRef.setId("channel1");
		channelRef.setName("channel1");
		characteristicList.add(channelRef);
		bundleProductOfferingChannelDefinedEvent = new BundleProductOfferingChannelDefinedEvent(productOffId,
				characteristicList, lastUpdate);
	}

	@Test
	void bundleProductOfferingChannelDefinedEventTest() {
		Assertions.assertAll("bundleProductOfferingChannelDefinedEvent",
				() -> Assertions.assertEquals("PROD1", bundleProductOfferingChannelDefinedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(bundleProductOfferingChannelDefinedEvent.getChannel(),
						characteristicList));
		LOGGER.info("bundleProductOfferingChannelDefinedEvent: {}", bundleProductOfferingChannelDefinedEvent);
	}
}
