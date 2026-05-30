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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;

public class InvalidProductSpecValidForEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(InvalidProductSpecValidForEventTest.class);
	
	private InvalidProductSpecValidForEvent invalidProductSpecValidForEvent;
	private TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now())
			.endDateTime(OffsetDateTime.now().plusDays(10));
	
	@BeforeEach
	void setUp() {
		String error = "Invalid range";		
		invalidProductSpecValidForEvent = new InvalidProductSpecValidForEvent(validFor, error);
	}
	
	@Test
	void productSpecValidForVerifiedEventTest() {
		Assertions.assertAll("invalidProductSpecValidForEvent",
				() -> Assertions.assertEquals("Invalid range",invalidProductSpecValidForEvent.getError()),
				() -> Assertions.assertEquals(validFor,invalidProductSpecValidForEvent.getValidFor()));
		LOGGER.info("invalidProductSpecValidForEvent: {}", invalidProductSpecValidForEvent);
	}

	
	
}
