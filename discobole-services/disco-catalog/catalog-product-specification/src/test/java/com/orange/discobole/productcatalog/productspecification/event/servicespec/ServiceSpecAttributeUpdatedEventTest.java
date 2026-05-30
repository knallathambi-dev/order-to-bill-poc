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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;

public class ServiceSpecAttributeUpdatedEventTest {

	private ServiceSpecification serviceSpecification;

	@BeforeEach
	void setUp() {
		serviceSpecification = new ServiceSpecification().id("1").lifecycleStatus("active")
				.lastUpdate(OffsetDateTime.now());
	}

	@Test
	void serviceSpecAttributeValueModifiedTest() {
		ServiceSpecAttributeUpdatedEvent attributeUpdatedEvent = new ServiceSpecAttributeUpdatedEvent(serviceSpecification.getId(),
				serviceSpecification);
		Assertions.assertEquals(serviceSpecification, attributeUpdatedEvent.getServiceSpecification());
		Assertions.assertEquals(serviceSpecification.getId(), attributeUpdatedEvent.getAggregateId());
		Assertions.assertNotNull(attributeUpdatedEvent.toString());
	}

}
