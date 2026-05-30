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
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecExpurgedEvent;

public class ServiceSpecExpurgedEventTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecExpurgedEventTest.class);

	private ServiceSpecExpurgedEvent serviceSpecExpurgedEvent;

	@BeforeEach
	void setUp() {
		ServiceSpecification serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId("1");
		serviceSpecification.setLifecycleStatus("active");
		serviceSpecExpurgedEvent = new ServiceSpecExpurgedEvent(serviceSpecification.getId(),serviceSpecification);
		serviceSpecExpurgedEvent.aggregateName();
	}

	@Test
	void cfsExpurgedEventTest() {
		Assertions.assertAll("ServiceSpecExpuringData",
				() -> Assertions.assertEquals(serviceSpecExpurgedEvent.getServiceSpecification().getId(), "1"),
				() -> Assertions.assertEquals(serviceSpecExpurgedEvent.getServiceSpecification().getLifecycleStatus(),
						"active"),
				() -> Assertions.assertEquals(serviceSpecExpurgedEvent.getAggregateId(), "1"));
		LOGGER.info("ServiceSpecExpurgedEvent: {}", serviceSpecExpurgedEvent);
	}
}