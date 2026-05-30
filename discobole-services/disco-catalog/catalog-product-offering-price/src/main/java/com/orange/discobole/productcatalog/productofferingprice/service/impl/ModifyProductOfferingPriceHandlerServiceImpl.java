// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationInitiatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationValidatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.eventstore.MongoEventStoreImpl;

import jakarta.annotation.Resource;

@Service
public class ModifyProductOfferingPriceHandlerServiceImpl {

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingPriceHandlerServiceImpl.class);
	
	@Resource
	private Publisher publisher;
	
	@Resource
	private MongoEventStoreImpl mongoEventStoreImpl;

	/**
	 * this function will publish all the events of POP modification.
	 * 
	 * @param event : ProductOfferingPriceModificationValidatedEvent
	 */
	@EventHandler
	public void handle(ProductOfferingPriceModificationValidatedEvent event) {
		List<DomainEventMessage<Event>> history = mongoEventStoreImpl.readEventsBackword(event.getProductOfferingPriceId(), 0);
		List<Event> project = new ArrayList<>();
		Set<String> duplicateCheck = new HashSet<>();
		for (DomainEventMessage<Event> eventMessage : history) {
			Event eventPOP = eventMessage.getPayload();
			LOGGER.debug("Events to publish - {}", eventMessage.getPayloadType().getSimpleName());
			if (duplicateCheck.add(eventMessage.getPayloadType().getSimpleName())) {
				if (eventPOP instanceof ProductOfferingPriceModificationInitiatedEvent) {
					break;
				}
				project.add(eventPOP);
			}
		}

		publisher.project(project);
	}

}
