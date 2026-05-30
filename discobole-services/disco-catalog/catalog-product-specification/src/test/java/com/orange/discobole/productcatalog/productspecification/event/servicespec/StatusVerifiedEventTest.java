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
import com.orange.discobole.productcatalog.productspecification.event.servicespec.StatusVerifiedEvent;

public class StatusVerifiedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(StatusVerifiedEventTest.class);

	private StatusVerifiedEvent statusVerifiedEvent;

	@BeforeEach
	void setUp() {
		statusVerifiedEvent = new StatusVerifiedEvent("1","1", ServiceSpecLifeCycleEnum.LAUNCHED, ServiceSpecLifeCycleEnum.UNAVAILABLE);
	}

	@Test
	void activeStatusVerifiedEventTest() {
		Assertions.assertAll("CfsStatusVerify", () -> Assertions.assertEquals(statusVerifiedEvent.getCfsId(), "1"),
				() -> Assertions.assertEquals(statusVerifiedEvent.getLifecycleStatus(), ServiceSpecLifeCycleEnum.LAUNCHED),
				() -> Assertions.assertEquals(statusVerifiedEvent.getOldLifecycleStatus(),
						ServiceSpecLifeCycleEnum.UNAVAILABLE),
				() -> Assertions.assertEquals(statusVerifiedEvent.getAggregateId(), "1"));
		LOGGER.info("StatusVerifiedEvent: {}", statusVerifiedEvent);
	}
}