// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.CurrentServiceSpecNotAlreadyExistedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.InvalidStatusReceivedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecDuplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecEarlyTimeRejectedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecExpurgedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecNotificationSentEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.StatusVerifiedEvent;


@ExtendWith(MockitoExtension.class)
class ServiceSpecEventHandlerServiceTest {

	@InjectMocks
	ServiceSpecEventHandlerService serviceSpecEventHandlerService;
	ServiceSpecification serviceSpecification;
	private static String aggregateId = "1";
	private static String lifeCycle = "active";
	private static String cfsId = "1";

	@Mock
	Publisher publisher;

	@BeforeEach
	void setup() {
		serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId("1");
	}

	@Test
	void serviceSpecExpurgedEventHandlerTest() {
		ServiceSpecExpurgedEvent event = new ServiceSpecExpurgedEvent(aggregateId, serviceSpecification);

		Mockito.doNothing().when(publisher).project(List.of(event));
		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getAggregateId());
	}

	@Test
	void statusVerifiedEventHandlerTest() {
		StatusVerifiedEvent event = new StatusVerifiedEvent(aggregateId, cfsId,
				ServiceSpecLifeCycleEnum.from(lifeCycle), ServiceSpecLifeCycleEnum.from(lifeCycle));

		Mockito.doNothing().when(publisher).project(List.of(event));
		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getAggregateId());
	}

	@Test
	void serviceSpecNotificationSentEventHandlerTest() {
		ServiceSpecNotificationSentEvent event = new ServiceSpecNotificationSentEvent(aggregateId,
				serviceSpecification);

		Mockito.doNothing().when(publisher).project(List.of(event));
		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getAggregateId());
	}

	@Test
	void serviceSpecReplicatedEventHandlerTest() {
		ServiceSpecReplicatedEvent event = new ServiceSpecReplicatedEvent(aggregateId, serviceSpecification);

		Mockito.doNothing().when(publisher).project(List.of(event));
		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getAggregateId());
	}

	@Test
	void serviceSpecStatusUpdatedEventHandlerTest() {
		ServiceSpecStatusUpdatedEvent event = new ServiceSpecStatusUpdatedEvent(aggregateId, cfsId,
				ServiceSpecLifeCycleEnum.from(lifeCycle), OffsetDateTime.now());

		Mockito.doNothing().when(publisher).project(List.of(event));
		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getAggregateId());
	}

	@Test
	void serviceSpecAttributeUpdatedEventHandlerTest() {
		ServiceSpecAttributeUpdatedEvent event = new ServiceSpecAttributeUpdatedEvent(aggregateId,
				serviceSpecification);

		Mockito.doNothing().when(publisher).project(List.of(event));
		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getAggregateId());
	}

	@Test
	void serviceSpecDuplicatedEventHandlerTest() {
		ServiceSpecDuplicatedEvent event = new ServiceSpecDuplicatedEvent(serviceSpecification);

		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getServiceSpecification().getId());
	}

	@Test
	void invalidStatusReceivedEventHandlerTest() {
		InvalidStatusReceivedEvent event = new InvalidStatusReceivedEvent(cfsId,
				ServiceSpecLifeCycleEnum.from(lifeCycle), ServiceSpecLifeCycleEnum.from(lifeCycle));

		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getCfsId());
	}

	@Test
	void currentServiceSpecNotAlreadyExistedEventHandlerTest() {
		CurrentServiceSpecNotAlreadyExistedEvent event = new CurrentServiceSpecNotAlreadyExistedEvent(
				serviceSpecification);

		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getServiceSpecification().getId());
	}

	@Test
	void serviceSpecEarlyTimeRejectedEventHandlerTest() {
		ServiceSpecEarlyTimeRejectedEvent event = new ServiceSpecEarlyTimeRejectedEvent(cfsId, OffsetDateTime.now(),
				OffsetDateTime.now());

		serviceSpecEventHandlerService.handle(event);
		assertEquals("1", event.getCfsId());
	}
}
