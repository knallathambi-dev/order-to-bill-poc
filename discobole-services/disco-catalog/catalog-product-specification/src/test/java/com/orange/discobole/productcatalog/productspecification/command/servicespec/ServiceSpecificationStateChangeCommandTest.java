// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.servicespec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;

public class ServiceSpecificationStateChangeCommandTest extends ProductSpecificationApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecificationStateChangeCommandTest.class);
	private final String eventType = "serviceSpecificationEventType";
	private final String lifeCycleStatus = "active";
	private Event event;
	private ServiceSpecification serviceSpecification;

	@BeforeEach
	public void setUp() {
		event = new Event();
		serviceSpecification = new ServiceSpecification();
		event.setEventId(UUID.randomUUID().toString());
		event.setEventType("serviceSpecificationEventType");
		event.setEvent(serviceSpecification);
		serviceSpecification.setId(UUID.randomUUID().toString());
		serviceSpecification.setLifecycleStatus("active");
	}

	@Test
	public void serviceSpecificationStateChangeCommandTest() throws Exception {
		ServiceSpecificationStateChangeCommand stateChangeCommand = new ServiceSpecificationStateChangeCommand(serviceSpecification.getId(),event);
		Assertions.assertAll("cfsEventProcessing",
				() -> assertEquals(stateChangeCommand.getEvent().getEventType(), eventType),
				() -> assertEquals(
						((ServiceSpecification) stateChangeCommand.getEvent().getEvent()).getLifecycleStatus(),
						lifeCycleStatus),
				() -> assertEquals(stateChangeCommand.getAggregateId(),serviceSpecification.getId()));
		LOGGER.info("ServiceSpecification Command: {}", stateChangeCommand);
	}
}