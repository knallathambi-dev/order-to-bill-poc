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
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingRelatedPartyDefinedEvent;

class BundleProductOfferingRelatedPartyDefinedEventTest extends ProductOfferingApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingRelatedPartyDefinedEventTest.class);

	private BundleProductOfferingRelatedPartyDefinedEvent bundleProductOfferingRelatedPartyDefinedEvent;

	private final List<RelatedParty> characteristicList = new ArrayList<>();
	
	private String aggregateId = "1";

	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		RelatedParty relatedParty = new RelatedParty();
		relatedParty.setId("rel1");
		relatedParty.setName("rel1");
		characteristicList.add(relatedParty);
		bundleProductOfferingRelatedPartyDefinedEvent = new BundleProductOfferingRelatedPartyDefinedEvent(productOffId,
				characteristicList, lastUpdate);
	}

	@Test
	void bundleProductOfferingRelatedPartyDefinedEventTest() {
		Assertions.assertAll("bundleProductOfferingRelatedPartyDefinedEvent",
				() -> Assertions.assertEquals("PROD1",
						bundleProductOfferingRelatedPartyDefinedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(bundleProductOfferingRelatedPartyDefinedEvent.getRelatedParties(),
						characteristicList));
		LOGGER.info("bundleProductOfferingRelatedPartyDefinedEvent: {}", bundleProductOfferingRelatedPartyDefinedEvent);
	}
}
