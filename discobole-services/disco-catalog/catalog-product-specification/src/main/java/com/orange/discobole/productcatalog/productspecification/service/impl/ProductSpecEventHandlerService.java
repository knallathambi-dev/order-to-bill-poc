// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.command.productspec.ModifyDefineIdentityProductSpecCommand;
import com.orange.discobole.productcatalog.productspecification.command.productspec.ProductSpecModificationValidatedCommand;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.ProductConfigurationSpec;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;
import com.orange.discobole.productcatalog.productspecification.eventstore.MongoEventStoreImpl;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductSpecEventHandlerService {
	private static final Logger LOGGER = LogManager.getLogger(ProductSpecEventHandlerService.class);

	@Resource
	Publisher publisher;
	
	@Resource
	private MongoEventStoreImpl mongoEventStoreImpl;

	@EventHandler
	public void handle(ProductSpecInitiatedEvent event) {
		LOGGER.info("handling ServiceSpecExpurgedEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductSpecIdentityDataEvent event) {
		LOGGER.info("handling ProductSpecIdentityDataEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ServiceSpecSelectedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(StockItemSelectedEvent event) {
		LOGGER.info("handling StockItemSelectedEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ServiceSpecStateVerifiedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(StockItemStateVerifiedEvent event) {
		LOGGER.info("handling StockItemStateVerifiedEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductSpecCharacteristicsDefinedEvent event) {
		LOGGER.info("handling ProductSpecCharacteristicsDefinedEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductSpecOpDefinedEvent event) {
		LOGGER.info("handling ProductSpecOpDefinedEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ServiceSpecStateVerificationFailedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(StockItemStateVerificationFailedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(ProductSpecRelationDefinedEvent event) {
		LOGGER.info("handling ProductSpecRelationDefinedEvent {}", event);
		publisher.project(List.of(event));
	}


	@EventHandler
	public void handle(ProductSpecRelatedPartySelectedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(ProductSpecValidForSelectedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(ProductSpecValidatedEvent event) {
		LOGGER.info("handling ProductSpecValidatedEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductSpecVersionCreatedEvent event) {
		LOGGER.info("handling ProductSpecVersionCreatedEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductSpecCreationCompletedEvent event) {
		// only handling event,not publishing it
	}


	@EventHandler
	public void handle(ProductSpecUsageSelectedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(ProductSpecCancelledEvent event) {
		LOGGER.info("handling ProductSpecCancelledEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductSpecRelResourceSelectedEvent event) {
		// only handling event,not publishing it
	}


	@EventHandler
	public void handle(ComputeProductConfigurationEvent event) {
		LOGGER.info("handling ComputeProductConfigurationEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(LinkProductSpecificationToStockItemEvent event) {
		LOGGER.info("handling LinkProductSpecificationToStockItemEvent {}", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductSpecStatusVerifiedEvent event) {
		// only handling event,not publishing it
	}

	@EventHandler
	public void handle(ProductSpecModificationInitiatedEvent event) {
		// only handling event,not publishing it
	}
	
	@EventHandler
	public void handle(ModifyDefineIdentityProductSpecCommand event) {
		// only handling event,not publishing it
	}

	
	@EventHandler
	public void handle(ProductSpecRelationModifiedEvent event) {
		// only handling event,not publishing it
	}

	
	@EventHandler
	public void handle(ProductSpecModificationValidatedCommand event) {
		// only handling event,not publishing it
	}
	
	
	@EventHandler
	public void handle(ProductSpecModificationValidatedEvent event) {
		LOGGER.info("handling ProductSpecModificationValidatedEvent {}", event);
		List<Event> project = new ArrayList<>();
		Set<String> duplicateCheck = new HashSet<>();

		List<DomainEventMessage<Event>> history = mongoEventStoreImpl.readEventsBackword(event.getProductSpecificationId(), 0);
		for (DomainEventMessage<Event> eventMessage : history) {
			Event psEvent = eventMessage.getPayload();
			if (duplicateCheck.add(eventMessage.getPayloadType().getSimpleName())) {
				if(psEvent instanceof ComputeProductConfigurationModificationEvent){
					for(ProductConfigurationSpec spec:((ComputeProductConfigurationModificationEvent) psEvent).getProductConfiguration()){
						project.add(new ProductConfigurationModificationEvent(event.getProductSpecificationId(),spec));
					}
				}else {
					if (psEvent instanceof ProductSpecModificationInitiatedEvent) {
						break;
					}
					project.add(psEvent);
				}

			}
		}
		Collections.reverse(project);
		publisher.project(project);
	}
	
	@EventHandler
	public void handle(ProductSpecModificationCancelledEvent event) {
		// only handling event,not publishing it
	}
	
	@EventHandler
	public void handle(ProductSpecificationDeleteEvent event) {
		LOGGER.info("handling ProductSpecificationDeleteEvent {}", event);
		publisher.project(List.of(event));
	}
	
}
