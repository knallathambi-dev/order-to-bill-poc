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
import com.orange.discobole.productcatalog.catalog.handler.ServiceSpecEventHandler;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;

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
 * Listener class, that will listen on cosumer binding of kafka stream.
 *
 * @author Vivek Singh
 * @since 1.0
 */
//@EnableBinding(ServiceSpecificationChannel.class)
@Component
public class ServiceSpecListener {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecListener.class);

	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	@Resource
	private ServiceSpecEventHandler eventHandler;

	@PostConstruct
	private void init() {
		register(ServiceSpecReplicatedEvent.class, eventHandler::handle);
		register(ServiceSpecStatusUpdatedEvent.class, eventHandler::handle);
		register(ServiceSpecAttributeUpdatedEvent.class, eventHandler::handle);
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
		if (event == null) {
			LOGGER.warn("Received null event, skipping handler");
			return;
		}
		
		if (handlers.containsKey(event.getClass())) {
			handlers.get(event.getClass()).forEach(handler -> handler.accept(event));
		} else {
			LOGGER.debug("No handler registered for event type: {}", event.getClass().getCanonicalName());
		}
	}

	//@StreamListener(ChannelConstant.SERVICE_SPEC_CHANNEL)
	@Bean
	public Consumer<Message<EventData>> serviceSpec() {
		return message -> {
			try {
				LOGGER.info("Received message on topic catalog.service-spec");
				
				Event event = message.getPayload().getEvent();
				
				if (event == null) {
					LOGGER.error("Failed to deserialize event. EventData type: {}", 
						message.getPayload() != null ? message.getPayload().getType() : "null");
					return;
				}
				
				LOGGER.debug("service-spec containing event {}", event);
				handle(event);
				
			} catch (Exception e) {
				LOGGER.error("Error processing service-spec event from topic catalog.service-spec", e);
				// Don't rethrow - prevents Kafka consumer crash and infinite retries
			}
		};
	}
}
