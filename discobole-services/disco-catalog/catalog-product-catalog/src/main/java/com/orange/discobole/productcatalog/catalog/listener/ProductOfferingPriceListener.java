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
import com.orange.discobole.productcatalog.catalog.handler.ProductOfferingPriceEventHandler;
import com.orange.discobole.productcatalog.productofferingprice.event.*;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.*;

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

@Component

public class ProductOfferingPriceListener {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingPriceListener.class);
	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductOfferingPriceEventHandler eventHandler;

	@PostConstruct
	private void init() {
		register(ProductOfferingPriceInitiatedEvent.class, eventHandler::handle);
		register(ProductOfferingPriceAlterationIdentityDataDescribedEvent.class, eventHandler::handle);
		register(ProductOfferingPriceVersionCreatedEvent.class, eventHandler::handle);
		register(ProductOfferingPriceChargeIdentityDataDescribedEvent.class, eventHandler::handle);
		register(ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent.class, eventHandler::handle);
		register(ProductOfferingPriceCancelledEvent.class, eventHandler::handle);
		register(ProductOfferingPriceAlterationIdentityDataModifiedEvent.class, eventHandler::handle);
		register(ProductOfferingPriceChargeIdentityDataModifiedEvent.class, eventHandler::handle);
		register(ProductOfferingPriceDeleteEvent.class, eventHandler::handle);
		register(ProductOfferingPriceModificationValidatedEvent.class, eventHandler::handle);
        register(ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent.class, eventHandler::handle);
	}

	@Bean
	public Consumer<Message<EventData>> productOfferingPrice() {
		return message -> {
			LOGGER.info("Received message on topic catalog.productOfferingPrice");
			LOGGER.debug("productOfferingPrice containing event: {}", message.getPayload().getEvent());
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
