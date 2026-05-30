// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.listener;


import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.catalog.handler.LifeCycleEventHandler;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Listener class, that will listen on consumer binding of kafka stream for Life
 * Cycle events.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Component
public class LifeCycleManagementListener {

	private static final Logger LOGGER = LogManager.getLogger(LifeCycleManagementListener.class);

	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	@Resource
	private LifeCycleEventHandler eventHandler;

	@PostConstruct
	private void init() {
		register(LifeCycleStateSelectedEvent.class, eventHandler::handle);

	}

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

	@Bean
	public Consumer<Message<EventData>> lifeCycleManagement() {
		return message -> {
			LOGGER.info("Received message on topic catalog.product-offering");
			LOGGER.debug("product-offering containing event {}", message.getPayload().getEvent());
			handle(message.getPayload().getEvent());
		};
	}

}
