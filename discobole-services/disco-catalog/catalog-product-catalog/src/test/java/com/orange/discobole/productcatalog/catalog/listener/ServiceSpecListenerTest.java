// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.listener;

import static org.mockito.Mockito.doNothing;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.handler.ServiceSpecEventHandler;
import com.orange.discobole.productcatalog.catalog.listener.ServiceSpecListener;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;

class ServiceSpecListenerTest extends CatalogApplicationTests {

	private ServiceSpecListener serviceSpecListener;

	@Mock
	private ServiceSpecEventHandler eventHandler;

	private ServiceSpecification serviceSpecification;

	@BeforeEach
	public void setup() {
		serviceSpecListener = new ServiceSpecListener();
		ReflectionTestUtils.setField(serviceSpecListener, "eventHandler", eventHandler);
		serviceSpecification = new ServiceSpecification().id(UUID.randomUUID().toString()).lifecycleStatus("active")
				.lastUpdate(OffsetDateTime.now());
	}

	@Test
	void listenConfiguratorEventTest() {
		ServiceSpecReplicatedEvent event = new ServiceSpecReplicatedEvent(serviceSpecification.getId(),serviceSpecification);
		serviceSpecListener.register(ServiceSpecReplicatedEvent.class, eventHandler::handle);
		doNothing().when(eventHandler).handle(event);
		Assertions.assertNotNull(serviceSpecListener.serviceSpec());
	}
}
