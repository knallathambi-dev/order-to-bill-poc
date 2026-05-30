// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.eventstore;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.MongoEventStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
@Component
public class MongoEventStoreImpl implements MongoEventStore {
	@Resource
	private ApplicationContext appCtx;
	private MongoEventStorageEngine engine;
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
	engine=(MongoEventStorageEngine) appCtx.getBean("eventStorageEngine");
	}

	@Override
	public void addEvent(List<DomainEventMessage<Event>> events) {
		engine.appendEvents(events);
	}

	@Override
	public void deleteEventsByAggreGateId(String aggregateId) {	
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DomainEventMessage<Event>> readEvents(String aggregateId, long head) {
		List<DomainEventMessage<Event>> list=new ArrayList<>();
		DomainEventStream stream=engine.readEvents(aggregateId,head);
		while(stream.hasNext()) {
			DomainEventMessage<com.orange.discobole.processflow.event.Event> message=(DomainEventMessage<com.orange.discobole.processflow.event.Event>) stream.next();
			list.add(message);
		}
		return list;

	}

	@Override
	public List<DomainEventMessage<Event>> readEventsBackword(String aggregateId, long head) {

		LinkedList<DomainEventMessage<Event>> list=new LinkedList<>();
		DomainEventStream stream=engine.readEvents(aggregateId);
		while(stream.hasNext()) {
			DomainEventMessage<com.orange.discobole.processflow.event.Event> message=(DomainEventMessage<com.orange.discobole.processflow.event.Event>) stream.next();
			list.push(message);
		}
		return list;

	}

	
}