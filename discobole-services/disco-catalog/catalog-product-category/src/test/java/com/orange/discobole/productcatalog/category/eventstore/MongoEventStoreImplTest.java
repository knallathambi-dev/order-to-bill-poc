// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.eventstore;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;



public class MongoEventStoreImplTest extends CategoryApplicationTests{
	
	@InjectMocks
	private MongoEventStoreImpl mongoEventStoreImpl;
	private ApplicationContext appCtx;
	private MongoEventStorageEngine engine;

	@BeforeEach
	void setup() {
		appCtx = Mockito.mock(ApplicationContext.class);
		engine = Mockito.mock(MongoEventStorageEngine.class);
		ReflectionTestUtils.setField(mongoEventStoreImpl, "appCtx", appCtx);
		ReflectionTestUtils.setField(mongoEventStoreImpl, "engine", engine);
	}

	@Test
	void addEventTest() {
		List<DomainEventMessage<Event>> events = new ArrayList<DomainEventMessage<Event>>();
		Mockito.doNothing().when(engine).appendEvents(events);
		mongoEventStoreImpl.addEvent(events);
		Assertions.assertNotNull(events);
	}

	@Test
	void readEventsTest() {
		
		DomainEventStream stream = Mockito.mock(DomainEventStream.class);
		Mockito.when(engine.readEvents("1", 0)).thenReturn(stream);
		mongoEventStoreImpl.readEvents("1", 0);
		Assertions.assertNotNull(stream);
	}

	@Test
	void readEventsBackwordTest() {
		DomainEventStream stream = Mockito.mock(DomainEventStream.class);
		Mockito.when(engine.readEvents("1")).thenReturn(stream);
		mongoEventStoreImpl.readEventsBackword("1", 10);
		Assertions.assertNotNull(stream);
	}

	
}
