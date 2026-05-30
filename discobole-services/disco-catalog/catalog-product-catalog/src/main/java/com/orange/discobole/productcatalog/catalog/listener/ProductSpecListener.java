// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.catalog.handler.ProductSpecEventHandler;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;

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
@Component
public class ProductSpecListener {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecListener.class);
	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductSpecEventHandler eventHandler;

	@PostConstruct
	private void init() {
		register(ProductSpecInitiatedEvent.class, eventHandler::handle);
		register(ProductSpecOpDefinedEvent.class, eventHandler::handle);
		register(ProductSpecCharacteristicsDefinedEvent.class, eventHandler::handle);
		register(ProductSpecRelationDefinedEvent.class, eventHandler::handle);
		register(ProductSpecValidatedEvent.class, eventHandler::handle);
		register(ProductSpecVersionCreatedEvent.class, eventHandler::handle);
		register(ProductSpecCancelledEvent.class, eventHandler::handle);
		register(ProductSpecDefineIdentityModifiedEvent.class, eventHandler::handle);
		register(ProductSpecCharacteristicsModifiedEvent.class, eventHandler::handle);
		register(ProductSpecRelationModifiedEvent.class, eventHandler::handle);
		register(ComputeProductConfigurationEvent.class, eventHandler::handle);
		register(LinkProductSpecificationToStockItemEvent.class, eventHandler::handle);
		register(ProductSpecificationDeleteEvent.class, eventHandler::handle);
		register(ProductSpecificationTemporaryDeleteEvent.class, eventHandler::handle);
		register(ProductSpecModificationValidatedEvent.class,eventHandler::handle);
		register(ProductSpecIdentityDataEvent.class,eventHandler::handle);
		register(LinkProductSpecificationToStockItemModificationEvent.class,eventHandler::handle);
		register(ProductConfigurationModificationEvent.class,eventHandler::handle);

	}

	//@StreamListener(ChannelConstant.PRODUCT_SPEC_CHANNEL)
	@Bean
	public Consumer<Message<EventData>> productSpec() {
		return message -> {
			LOGGER.info("Received message on topic catalog.product-spec");
			LOGGER.debug("product-spec containing event: {}", message.getPayload().getEvent());
			handle(message.getPayload().getEvent());
		};
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

}
