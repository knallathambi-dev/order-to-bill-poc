// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.infra;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is used to register the handlers and publish the events on the
 * basis of aggregate id aka cfs id.
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
@Component
@ConditionalOnProperty(value = "cqrs.command-enabled", havingValue = "true", matchIfMissing = true)
public class Publisher {

	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	/**
	 * The event store.
	 */


	public Publisher() {
		
	}

	/**
	 * This method is used to publish events on the basis of aggregate id aka cfs
	 * id.
	 *
	 * @param aggregateId aggregate id represents the id on which basis events get
	 *                    stored in event store.
	 * @param eventList   eventList represents the events that need to be stored in
	 *                    the event store.
	 */
	public void publish(final String aggregateId, final List<Event> eventList) {
		
		eventList.forEach(this::handle);
	}

	/**
	 * This method is used to project  events on the basis of aggregate id aka cfs
	 * id.
	 *
	 * @param eventList   eventList represents the events that need to be send
	 *                    to query side
	 */
	public void project(final List<Event> eventList) {
		eventList.forEach(this::handle);
	}

	
	/**
	 * This method is used to register the handlers.
	 *
	 * @param <T>          the generic type
	 * @param eventClass   the event class
	 * @param eventHandler the event handler
	 */
	public <T> void register(final Class<T> eventClass, final Consumer<T> eventHandler) {
		List<Consumer> handlerList = new ArrayList<>();
		if (handlers.containsKey(eventClass)) {
			handlerList = handlers.get(eventClass);
		}
		handlerList.add(eventHandler);
		handlers.put(eventClass, handlerList);

	}

	private void handle(final Event event) {
		if (handlers.containsKey(event.getClass())) {
			handlers.get(event.getClass()).forEach(handler -> handler.accept(event));
		}
	}
}
