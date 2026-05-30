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
import com.orange.discobole.productcatalog.catalog.handler.CategoryEventHandler;
import com.orange.discobole.productcatalog.catalog.handler.ModifyCategoryEventHandler;
import com.orange.discobole.productcatalog.category.event.category.*;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityIndirectModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModificationValidatedEvent;

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
 * Listener class, that will listen on consumer binding of kafka stream for Category events
 *
 * @author Varshika Choudhary
 */
@Component
public class CategoryListener {

    private static final Logger LOGGER = LogManager.getLogger(CategoryListener.class);

    private final Map<Class<?>, List<Consumer>> handlers = new HashMap<>();

    @Resource
    private CategoryEventHandler eventHandler;

    @Resource
    private ModifyCategoryEventHandler modifyCategoryEventHandler;

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

    @PostConstruct
    private void init(){
        register(CategoryIdentityDataDefinedEvent.class, eventHandler::handle);
        register(EntityTypeSelectedEvent.class, eventHandler::handle);
        register(AssociateEntitySelectedEvent.class, eventHandler::handle);
        register(CategoryCreationEvent.class, eventHandler::handle);
        register(CategoryCancelledEvent.class, eventHandler::handle);
        register(AssociatedEntityModifiedEvent.class, modifyCategoryEventHandler::handle);
        register(CategoryIdentityDataModifiedEvent.class, modifyCategoryEventHandler::handle);
        register(CategoryModificationValidatedEvent.class, modifyCategoryEventHandler::handle);
        register(AssociatedEntityIndirectModifiedEvent.class,modifyCategoryEventHandler::handle);
        register(CategoryDeletedEvent.class, eventHandler::handle);
        register(CategoryAssociationDeletedEvent.class, eventHandler::handle);
    }

    @Bean
    public Consumer<Message<EventData>> category() {
        return message -> {
            LOGGER.info("Received message on topic catalog.category");
            LOGGER.debug("category containing event {}", message.getPayload().getEvent());
            handle(message.getPayload().getEvent());
        };
    }
}
