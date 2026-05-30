// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto.generated;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
class EventTest {

	@InjectMocks
	private Event event;

	@BeforeEach
	void setup() {
		event = new Event();
		event.setAnalyticCharacteristic(new ArrayList<Object>());
		event.setBaseType("test");
		event.setCorrelationId("1");
		event.setDescription("testing state change");
		event.setDomain("configurator");
		event.setEvent(new Object());
		event.setEventId("1");
		event.setEventTime(OffsetDateTime.now());
		event.setEventType("state_change");
		event.setPriority("4");
		event.setRelatedParty(new ArrayList<Object>());
		event.setSchemaLocation("test");
		event.setSource("test");
		event.setTimeOcurred(OffsetDateTime.now());
		event.setTitle("TEST Purpose");
		event.setType("test");
		event.setReportingSystem(new Object());
	}

	@Test
	void testEvent() {
		Assertions.assertNotNull(event.getAnalyticCharacteristic());
		Assertions.assertNotNull(event.getBaseType());
		Assertions.assertNotNull(event.getCorrelationId());
		Assertions.assertNotNull(event.getDescription());
		Assertions.assertNotNull(event.getDomain());
		Assertions.assertNotNull(event.getEvent());
		Assertions.assertNotNull(event.getEventId());
		Assertions.assertNotNull(event.getEventTime());
		Assertions.assertNotNull(event.getEventType());
		Assertions.assertNotNull(event.getPriority());
		Assertions.assertNotNull(event.getRelatedParty());
		Assertions.assertNotNull(event.getSchemaLocation());
		Assertions.assertNotNull(event.getSource());
		Assertions.assertNotNull(event.getTimeOcurred());
		Assertions.assertNotNull(event.getTitle());
		Assertions.assertNotNull(event.getType());
		Assertions.assertNotNull(event.getReportingSystem());
		Assertions.assertNotNull(event.toString());

	}
}
