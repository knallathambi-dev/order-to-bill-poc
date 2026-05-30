// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;

public class ServiceSpecStatusUpdatedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecStatusUpdatedEventTest.class);

	private ServiceSpecStatusUpdatedEvent serviceSpecStatusUpdatedEvent;
	private OffsetDateTime dateTime;

	@BeforeEach
	void setUp() {
		dateTime = OffsetDateTime.now();
		serviceSpecStatusUpdatedEvent = new ServiceSpecStatusUpdatedEvent("1","1", ServiceSpecLifeCycleEnum.LAUNCHED, dateTime);
	}

	@Test
	void serviceSpecLaunchedEventTest() {
		Assertions.assertAll("ServiceSpecLaunchedEventTest",
				() -> Assertions.assertEquals(serviceSpecStatusUpdatedEvent.getCfsId(), "1"),
				() -> Assertions.assertEquals(serviceSpecStatusUpdatedEvent.getLifecycleStatus(),
						ServiceSpecLifeCycleEnum.LAUNCHED),
				() -> Assertions.assertEquals(serviceSpecStatusUpdatedEvent.getCfsTimeOccurred(),
						dateTime),
				() -> Assertions.assertEquals(serviceSpecStatusUpdatedEvent.getAggregateId(), "1"));
		LOGGER.info("ServiceSpecLaunchedEventTest: {}", serviceSpecStatusUpdatedEvent);
	}
}