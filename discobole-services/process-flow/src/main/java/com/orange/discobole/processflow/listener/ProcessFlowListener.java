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

package com.orange.discobole.processflow.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.constant.ProcessFlowChannelConstant;
import com.orange.discobole.processflow.event.*;
import com.orange.discobole.processflow.handler.ProcessFlowEventHandler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The class ProcessFlowListener which listens to events on ProcessFlow channel and
 * calls the corresponding handler.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@ConditionalOnExpression("${cqrs.query-enabled:true} and '${cqrs.command-enabled:true}'.equals('false')")
//@EnableBinding(ProcessFlowChannel.class)
@Component
public class ProcessFlowListener {

	private static final Logger LOGGER = LogManager.getLogger(ProcessFlowListener.class);

	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	@Resource
	private ProcessFlowEventHandler eventHandler;

	@PostConstruct
	private void init() {
		register(ProcessFlowCreatedEvent.class, eventHandler::handle);
		register(TaskFlowUpdatedEvent.class, eventHandler::handle);
		register(ProcessFlowUpdatedEvent.class, eventHandler::handle);
	}

	/**
	 * Registers the events with their corresponding event handler.
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

	/**
	 * calls the handler of the corresponding event.
	 *
	 * @param event the event
	 */
	private void handle(final Event event) {
		if (handlers.containsKey(event.getClass())) {
			handlers.get(event.getClass()).forEach(handler -> handler.accept(event));
		}
	}

	/**
	 * Listens ProcessFlow event from the event bus.
	 *
	 * @param eventData the event data
	 */
//	@StreamListener(ProcessFlowChannelConstant.CONSUMER_CHANNEL)
//	public void listenProcessFlowMessage(@Payload final EventData eventData) {
//		LOGGER.info("Received message on topic processflow containing event {}", eventData.getEvent());
//		handle(eventData.getEvent());
//	}

	@Bean
	Consumer<Message<EventData>> handle() {
		return message -> {
			LOGGER.info("Received message on topic processflow containing event {}", message.getPayload().getEvent().aggregateName());
			handle(message.getPayload().getEvent());
			//supressed code for simplicity
		};
	}

}
