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
import com.orange.discobole.productcatalog.catalog.handler.ModifyProductOfferingEventHandler;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Listener class, that will listen on consumer binding of kafka stream for
 * Modified Product Offering events.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component
public class ModifyProductOfferingListener {

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingListener.class);

	private final Map<Class, List<Consumer>> handlers = new HashMap<>();

	@Resource
	private ModifyProductOfferingEventHandler eventHandler;

	@PostConstruct
	private void init() {
		register(AtomicProductOfferingCategoryModifiedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingIdentityDataModifiedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingOperationModifiedEvent.class, eventHandler::handle);
		register(LinkPOPtoOperModifiedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingCharacteristicsModifiedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingRelationshipModifiedEvent.class, eventHandler::handle);
		register(AtomicProductOfferingIncompatibleRelationshipModifiedEvent.class,eventHandler::handle);
		register(BundleProductOfferingIncompatibleRelationshipModifiedEvent.class,eventHandler::handle);
		register(ContractProductOfferingIncompatibleRelationshipModifiedEvent.class,eventHandler::handle);
		register(ContractProductOfferingCategoryModifiedEvent.class,eventHandler::handle);
		register(ContractProductOfferingSelectedModifiedEvent.class,eventHandler::handle);
		register(ContractProductOfferingRelationshipModifiedEvent.class,eventHandler::handle);
		register(ContractProductOfferingModificationValidatedEvent.class,eventHandler::handle);
		register(ContractProductOfferingIdentityDataModifiedEvent.class,eventHandler::handle);
		register(ContractProductOfferingOperModifiedEvent.class,eventHandler::handle);
		register(BundleProductOfferingCategoryModifiedEvent.class,eventHandler::handle);
		register(BundleProductOfferingSelectedModifiedEvent.class,eventHandler::handle);
		register(BundleProductOfferingRelationshipModifiedEvent.class,eventHandler::handle);
		register(BundleProductOfferingModificationValidatedEvent.class,eventHandler::handle);
		register(BundleProductOfferingIdentityDataModifiedEvent.class,eventHandler::handle);
		register(BundleProductOfferingOperModifiedEvent.class,eventHandler::handle);
		register(AtomicProductOfferingIndirectCategoryModifiedEvent.class,eventHandler::handle);
		register(BundleProductOfferingIndirectCategoryModifiedEvent.class,eventHandler::handle);
		register(ContractProductOfferingIndirectCategoryModifiedEvent.class,eventHandler::handle);
		register(ModifyProductOfferingAllowedActionDefinedEvent.class,eventHandler::handle);

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
		LOGGER.debug("Handle Event {}",event);
		if (handlers.containsKey(event.getClass())) {
			handlers.get(event.getClass()).forEach(handler -> handler.accept(event));
		}
	}


}