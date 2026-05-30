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
import com.orange.discobole.productcatalog.catalog.handler.ModifyProductOfferingEventHandler;
import com.orange.discobole.productcatalog.catalog.handler.ProductOfferingEventHandler;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;

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
 * Listener class, that will listen on consumer binding of kafka stream for
 * Product Offering events.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Component
public class ProductOfferingListener {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingListener.class);

	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	@Resource
	private ProductOfferingEventHandler eventHandler;

	@Resource
	private ModifyProductOfferingEventHandler modifyProductOfferingEventHandler;

	@PostConstruct
	private void init() {
		register(ProductOfferingTypeSelectedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingInitiatedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingIdentityDataDefinedEvent.class, eventHandler::handle);
		register(BundleProductOfferingIdentityDataDefinedEvent.class, eventHandler::handle);
		register(ContractProductOfferingIdentityDataDefinedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingCategoryDefinedEvent.class, eventHandler::handle);
		register(BundleProductOfferingCategoryDefinedEvent.class, eventHandler::handle);
		register(ContractProductOfferingCategoryDefinedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingCharacteristicsDefinedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingRelationshipDefinedEvent.class, eventHandler::handle);
		register(BundleProductOfferingRelationshipDefinedEvent.class, eventHandler::handle);
		register(ContractProductOfferingRelationshipDefinedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingValidatedEvent.class, eventHandler::handle);
		register(BundleProductOfferingValidatedEvent.class, eventHandler::handle);
		register(ContractProductOfferingValidatedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingVersionCreatedEvent.class, eventHandler::handle);
		register(BundleProductOfferingVersionCreatedEvent.class, eventHandler::handle);
		register(ContractProductOfferingVersionCreatedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingBundleDefinedEvent.class, eventHandler::handle);
		register(ProductOffCancelledEvent.class, eventHandler::handle);
		register(AtomicProductOfferingOperDefinedEvent.class, eventHandler::handle);
		register(LinkPOPtoOperEvent.class, eventHandler::handle);
		register(CreateBundleProductOfferingEvent.class, eventHandler::handle);
		register(BundleProductOfferingOperDefinedEvent.class, eventHandler::handle);
		register(ContractProductOfferingOperDefinedEvent.class, eventHandler::handle);
		register(BundleProductOfferingSelectedEvent.class, eventHandler::handle);
		register(ContractProductOfferingSelectedEvent.class, eventHandler::handle);
		register(ProductOfferingDeleteEvent.class,eventHandler::handle );
		register(ProductOfferingTemporaryDeleteEvent.class,eventHandler::handle);


		register(AtomicProductOfferingCategoryModifiedEvent.class, modifyProductOfferingEventHandler::handle);
		register(AtomicProductOfferingIdentityDataModifiedEvent.class, modifyProductOfferingEventHandler::handle);
		register(AtomicProductOfferingOperationModifiedEvent.class, modifyProductOfferingEventHandler::handle);
		register(LinkPOPtoOperModifiedEvent.class, modifyProductOfferingEventHandler::handle);
		register(AtomicProductOfferingCharacteristicsModifiedEvent.class, modifyProductOfferingEventHandler::handle);
		register(AtomicProductOfferingRelationshipModifiedEvent.class, modifyProductOfferingEventHandler::handle);
		register(AtomicProductOfferingIncompatibleRelationshipModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingIncompatibleRelationshipModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingIncompatibleRelationshipModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingCategoryModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingSelectedModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingRelationshipModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingModificationValidatedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingIdentityDataModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingOperModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingCategoryModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingSelectedModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingRelationshipModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingModificationValidatedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingIdentityDataModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingOperModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(AtomicProductOfferingIndirectCategoryModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(BundleProductOfferingIndirectCategoryModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingIndirectCategoryModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ContractProductOfferingIndirectCategoryModifiedEvent.class,modifyProductOfferingEventHandler::handle);
		register(ProductOfferingPolicyRuleAssociationDefinedEvent.class,eventHandler::handle);
		register(ProductOfferingPolicyRuleAssociationModifiedEvent.class,eventHandler::handle);
		register(AtomicProductOfferingModificationValidatedEvent.class,eventHandler::handle);
		register(ProductOfferingAllowedActionDefinedEvent.class,eventHandler::handle);
		register(ModifyProductOfferingAllowedActionDefinedEvent.class,modifyProductOfferingEventHandler::handle);
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
	public Consumer<Message<EventData>> productOffering() {
		return message -> {
			LOGGER.info("Received message on topic catalog.product-offering");
			LOGGER.info("Payload type = {}", message.getPayload().getClass());
			LOGGER.debug("product-offering containing event: {}", message.getPayload().getEvent());
			handle(message.getPayload().getEvent());
		};
	}

}
