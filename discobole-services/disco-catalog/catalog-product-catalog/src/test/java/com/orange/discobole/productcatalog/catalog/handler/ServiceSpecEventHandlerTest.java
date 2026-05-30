// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import java.time.OffsetDateTime;

import com.orange.discobole.productcatalog.catalog.common.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.handler.ServiceSpecEventHandler;
import com.orange.discobole.productcatalog.catalog.service.ServiceSpecService;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class ServiceSpecEventHandlerTest {

	private ServiceSpecEventHandler serviceSpecEventHandler;

	private ServiceSpecService serviceSpecService;

	private ServiceSpecEvent event;

	private ServiceSpecification serviceSpecification;
	private String cfsId;
	private String lifeCycleStatus;

	@BeforeEach
	void setUp() {
		cfsId = "1";
		lifeCycleStatus = "active";
		serviceSpecEventHandler = new ServiceSpecEventHandler();
		serviceSpecService = Mockito.mock(ServiceSpecService.class);
		ReflectionTestUtils.setField(serviceSpecEventHandler, "serviceSpecService", serviceSpecService);
		serviceSpecification = new ServiceSpecification();
		serviceSpecification.id(cfsId).lifecycleStatus(lifeCycleStatus).lastUpdate(OffsetDateTime.now()).type("CFS");
		event = new ServiceSpecEvent() {
		};
		event.aggregateName();
	}

	@Test
	void handleServiceSpecReplicatedEvent() {
		serviceSpecEventHandler.handle(new ServiceSpecReplicatedEvent(serviceSpecification.getId(),serviceSpecification));
		Mockito.verify(serviceSpecService, Mockito.times(1)).saveServiceSpecification(serviceSpecification);
	}

	@Test
	void handleServiceSpecStatusUpdatedEvent() {
		Mockito.when(serviceSpecService.fetchServiceSpecificationById(cfsId)).thenReturn(serviceSpecification);
		Mockito.doNothing().when(serviceSpecService).saveServiceSpecification(serviceSpecification);
		serviceSpecEventHandler.handle(new ServiceSpecStatusUpdatedEvent(serviceSpecification.getId(),cfsId,
				ServiceSpecLifeCycleEnum.from(lifeCycleStatus), OffsetDateTime.now()));
		Mockito.verify(serviceSpecService, Mockito.times(1)).saveServiceSpecification(serviceSpecification);
	}

	@Test
	void handleServiceSpecAttributeUpdatedEvent() {
		Mockito.doNothing().when(serviceSpecService).saveServiceSpecification(serviceSpecification);
		serviceSpecEventHandler.handle(new ServiceSpecAttributeUpdatedEvent(serviceSpecification.getId(),serviceSpecification));
		Mockito.verify(serviceSpecService, Mockito.times(1)).saveServiceSpecification(serviceSpecification);
	}
}