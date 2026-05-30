// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.eventstore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.client.MongoClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.MongoEventStore;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Component
public class MongoEventStoreImpl implements MongoEventStore {
	@Resource
	private ApplicationContext appCtx;
	private MongoEventStorageEngine engine;
	private static final Logger LOGGER = LogManager.getLogger(MongoEventStoreImpl.class);

	@PostConstruct
	public void init() {
		MongoClient client = (MongoClient) appCtx.getBean("mongo");
		LOGGER.info("MongoClient initialized: {}", client);

		client.listDatabaseNames().forEach(dbName -> LOGGER.info("Database Name: {}", dbName));

		engine = (MongoEventStorageEngine) appCtx.getBean("eventStorageEngine");
		LOGGER.info("MongoEventStorageEngine initialized: {}", engine);
	}

	/**
	 * this method will add the event into axon database.
	 * 
	 * @param event : list of DomainEventMessage
	 * @return
	 */
	@Override
	public void addEvent(List<DomainEventMessage<Event>> events) {
		LOGGER.info("Add Events :  {}", events);
		engine.appendEvents(events);
	}

	/**
	 * this method will throw UnsupportedOperationException because events are immutable.
	 *
	 *@param aggregateId
	 *@return
	 */
	@Override
	public void deleteEventsByAggreGateId(String aggregateId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * this method will read events from forward.
	 * 
	 * @param aggregateId
	 * @param head : sequence number 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DomainEventMessage<Event>> readEvents(String aggregateId, long head) {
		LOGGER.info("Read Events with aggregateIdentifier :  {}", aggregateId);
		List<DomainEventMessage<Event>> list = new ArrayList<>();
		DomainEventStream stream = engine.readEvents(aggregateId, head);
		while (stream.hasNext()) {
			DomainEventMessage<com.orange.discobole.processflow.event.Event> message = (DomainEventMessage<com.orange.discobole.processflow.event.Event>) stream
					.next();
			list.add(message);
		}
		return list;

	}

	/**
	 * this method will read events from backward.
	 * 
	 * @param aggregateId
	 * @param head : sequence number 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DomainEventMessage<Event>> readEventsBackword(String aggregateId, long head) {
		LOGGER.info("Read Events Backward with aggregateIdentifier :  {}", aggregateId);
		LinkedList<DomainEventMessage<Event>> list = new LinkedList<>();
		DomainEventStream stream = engine.readEvents(aggregateId);
		while (stream.hasNext()) {
			DomainEventMessage<com.orange.discobole.processflow.event.Event> message = (DomainEventMessage<com.orange.discobole.processflow.event.Event>) stream
					.next();
			list.push(message);
		}
		return list;

	}

}
