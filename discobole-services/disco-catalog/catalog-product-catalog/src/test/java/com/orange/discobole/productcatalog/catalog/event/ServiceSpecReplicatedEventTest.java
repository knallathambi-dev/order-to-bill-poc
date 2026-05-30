// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;

class ServiceSpecReplicatedEventTest extends CatalogApplicationTests {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecReplicatedEventTest.class);

	private ServiceSpecReplicatedEvent serviceSpecReplicatedEvent;

	@BeforeEach
	void setUp() {
		ServiceSpecification serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId("1");
		serviceSpecification.setLifecycleStatus("active");
		serviceSpecReplicatedEvent = new ServiceSpecReplicatedEvent(serviceSpecification.getId(),serviceSpecification);
	}

	@Test
	void cfsDuplicatedEventTest() {
		Assertions.assertAll("DuplicateCfsData",
				() -> Assertions.assertEquals("1", serviceSpecReplicatedEvent.getServiceSpecification().getId()),
				() -> Assertions.assertEquals("active",
						serviceSpecReplicatedEvent.getServiceSpecification().getLifecycleStatus()),
				() -> Assertions.assertEquals("1", serviceSpecReplicatedEvent.getAggregateId()));
		LOGGER.debug("ServiceSpecReplicatedEvent: {}", serviceSpecReplicatedEvent);
	}
}