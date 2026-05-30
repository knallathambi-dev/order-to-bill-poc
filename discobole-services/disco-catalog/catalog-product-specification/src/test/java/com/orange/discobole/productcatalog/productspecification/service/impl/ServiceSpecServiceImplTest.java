// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import java.time.OffsetDateTime;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.aggregate.ServiceSpecAggregate;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.service.ServiceSpecService;


class ServiceSpecServiceImplTest {



	@InjectMocks
	ServiceSpecService serviceSpecService;

	private Publisher publisher = Mockito.mock(Publisher.class);

	@Mock
	ServiceSpecAggregate serviceSpecAggregate;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	CommandGateway commandGateway;

	public ServiceSpecServiceImplTest() {
		commandGateway = Mockito.mock(CommandGateway.class);
		serviceSpecService = new ServiceSpecServiceImpl(commandGateway);
	}

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(serviceSpecService, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(serviceSpecService, "publisher", publisher);
	}

	@Test
	void processCreateServiceSpecEvent() {
		Event event = new Event();
		event.setEventId("eventId1");
		event.setEventType("ServiceSpecificationStateChange");
		ServiceSpecification serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId("1");
		serviceSpecification.setLifecycleStatus("Active");
		event.setEvent(serviceSpecification);
		serviceSpecService.processEvent(event);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

	@Test
	void processUpdateServiceSpecEvent() {
		ServiceSpecification existingServiceSpecification = new ServiceSpecification();
		existingServiceSpecification.setId("1");
		existingServiceSpecification.setLifecycleStatus("Active");

		Event event = new Event();
		event.setEventId("eventId1");
		event.setEventType("ServiceSpecificationStateChange");
		event.setTimeOcurred(OffsetDateTime.now());
		ServiceSpecification serviceSpecification = new ServiceSpecification();
		serviceSpecification.setId("1");
		serviceSpecification.setLifecycleStatus("Active");
		event.setEvent(serviceSpecification);
		serviceSpecService.processUpdateServiceSpecEvent(event);
		Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(Mockito.any());
	}

}
