// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;

class ServiceSpecAttributeUpdatedEventTest {

	ServiceSpecAttributeUpdatedEvent serviceSpecAttributeUpdatedEvent;

	@BeforeEach
	void setUp() {
		serviceSpecAttributeUpdatedEvent = new ServiceSpecAttributeUpdatedEvent("1", new ServiceSpecification());
	}

	@Test
	void serviceSpecAttributeUpdatedEventTest() {
		Assertions.assertAll("serviceSpecAttributeUpdatedEvent",
				() -> Assertions.assertEquals("1", serviceSpecAttributeUpdatedEvent.getAggregateId()),
				() -> Assertions.assertNotNull(serviceSpecAttributeUpdatedEvent.toString()),
				() -> Assertions.assertNotNull(serviceSpecAttributeUpdatedEvent.getServiceSpecification()));
	}

}
