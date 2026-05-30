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
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.InvalidStatusReceivedEvent;

 class InvalidStatusReceivedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(InvalidStatusReceivedEventTest.class);

	private InvalidStatusReceivedEvent invalidStatusReceivedEvent;

	@BeforeEach
	void setUp() {
		String cfsId = "1";
		invalidStatusReceivedEvent = new InvalidStatusReceivedEvent(cfsId, ServiceSpecLifeCycleEnum.ACTIVE,
				ServiceSpecLifeCycleEnum.LAUNCHED);
	}

	@Test
	void invalidStatusReceivedEventTest() {
		Assertions.assertAll("InvalidStatusReceivedEvent",
				() -> Assertions.assertEquals("1",invalidStatusReceivedEvent.getCfsId()),
				() -> Assertions.assertEquals(ServiceSpecLifeCycleEnum.ACTIVE,invalidStatusReceivedEvent.getOldLifecycleStatus()),
				() -> Assertions.assertEquals(ServiceSpecLifeCycleEnum.LAUNCHED,invalidStatusReceivedEvent.getNewLifecycleStatus()));
		LOGGER.info("InvalidStatusReceivedEventTest: {}", invalidStatusReceivedEvent);
	}
}