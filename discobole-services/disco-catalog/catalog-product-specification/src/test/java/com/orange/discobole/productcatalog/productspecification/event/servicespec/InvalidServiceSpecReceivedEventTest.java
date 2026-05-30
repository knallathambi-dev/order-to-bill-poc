// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.InvalidServiceSpecReceivedEvent;

 class InvalidServiceSpecReceivedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(InvalidServiceSpecReceivedEventTest.class);

	private InvalidServiceSpecReceivedEvent invalidServiceSpecReceivedEvent;

	@BeforeEach
	void setUp() {
		String eventId = "1";
		com.orange.discobole.productcatalog.productspecification.dto.generated.Event event = new com.orange.discobole.productcatalog.productspecification.dto.generated.Event();
		event.setEventId(eventId);
		invalidServiceSpecReceivedEvent = new InvalidServiceSpecReceivedEvent(event, "wrong lifecyclestatus");
	}

	@Test
	void invalidStructureReceivedEventTest() {
		Assertions.assertAll("InvalidStructureReceivedEvent",
				() -> Assertions.assertEquals("1",invalidServiceSpecReceivedEvent.getEvent().getEventId()),
				() -> Assertions.assertEquals( "wrong lifecyclestatus",invalidServiceSpecReceivedEvent.getError()));
		LOGGER.info("invalidServiceSpecReceivedEvent: {}", invalidServiceSpecReceivedEvent);
	}
}