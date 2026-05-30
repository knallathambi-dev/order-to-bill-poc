// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.event;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.common.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;

/**
 * The ServiceSpecStatusUpdatedEventTest type verify the status event.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
class ServiceSpecStatusUpdatedEventTest extends CatalogApplicationTests {

	private ServiceSpecStatusUpdatedEvent serviceSpecStatusUpdatedEvent;

	@BeforeEach
	void setUp() {
		String cfsId = "1";
		serviceSpecStatusUpdatedEvent = new ServiceSpecStatusUpdatedEvent(cfsId,cfsId, ServiceSpecLifeCycleEnum.ACTIVE,
				OffsetDateTime.now());
	}

	@Test
	void cfsDuplicatedEventTest() {
		Assertions.assertAll("ServiceSpecStatus",
				() -> Assertions.assertEquals("1", serviceSpecStatusUpdatedEvent.getCfsId()),
				() -> Assertions.assertEquals(ServiceSpecLifeCycleEnum.ACTIVE,
						serviceSpecStatusUpdatedEvent.getLifecycleStatus()),
				() -> Assertions.assertEquals("1", serviceSpecStatusUpdatedEvent.getAggregateId()),
				() -> Assertions.assertNotNull(serviceSpecStatusUpdatedEvent.toString()));
	}

}
