// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.servicespec;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;

public class ServiceSpecAttributeValueChangeCommandTest {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecificationStateChangeCommandTest.class);

	private Event event = null;
	private ServiceSpecification serviceSpecification = null;

	@BeforeEach
	void setUp() {
		event = new Event();
		event.setEventId(UUID.randomUUID().toString());
		event.setEventType("ServiceSpecificationAttributeValueChange");
		event.setType("cfs");
		event.setTimeOcurred(OffsetDateTime.now());
		serviceSpecification = new ServiceSpecification().id(UUID.randomUUID().toString()).lifecycleStatus("active")
				.lastUpdate(OffsetDateTime.now());
		event.setEvent(serviceSpecification);
	}

	@Test
	void attributeValueChangeCommandTest() {
		ServiceSpecAttributeValueChangeCommand attributeValueChangeCommand = new ServiceSpecAttributeValueChangeCommand(serviceSpecification.getId(),
				event);
		Assertions.assertEquals(event, attributeValueChangeCommand.getEvent());
		Assertions.assertEquals(serviceSpecification.getId(), attributeValueChangeCommand.getAggregateId());
		LOGGER.info("ServiceSpecification Attribute Change Command: {}", attributeValueChangeCommand);
	}

}
