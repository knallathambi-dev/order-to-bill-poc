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
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingOperDefinedEvent;

class BundleProductOfferingOperDefinedEventTest extends ProductOfferingApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(BundleProductOfferingOperDefinedEventTest.class);

	private BundleProductOfferingOperDefinedEvent bundleProductOfferingOperDefinedEvent;

	private final List<CommercialOperation> characteristicList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		String productOffId = "PROD1";
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		CommercialOperation commercialOperation = new CommercialOperation();
		commercialOperation.setId("1");
		commercialOperation.setName("Add");
		commercialOperation.setDescription("description");
		characteristicList.add(commercialOperation);
		bundleProductOfferingOperDefinedEvent = new BundleProductOfferingOperDefinedEvent(productOffId,
				characteristicList, lastUpdate);
	}

	@Test
	void bundleProductOfferingOperDefinedEventTest() {
		Assertions.assertAll("bundleProductOfferingOperDefinedEvent",
				() -> Assertions.assertEquals("PROD1", bundleProductOfferingOperDefinedEvent.getProductOfferingId()),
				() -> Assertions.assertEquals(bundleProductOfferingOperDefinedEvent.getOperationSpecifications(),
						characteristicList));
		LOGGER.info("bundleProductOfferingOperDefinedEvent: {}", bundleProductOfferingOperDefinedEvent);
	}

}
