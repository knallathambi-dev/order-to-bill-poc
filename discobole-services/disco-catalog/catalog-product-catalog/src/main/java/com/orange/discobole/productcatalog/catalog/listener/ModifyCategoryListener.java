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
import com.orange.discobole.productcatalog.catalog.handler.ModifyCategoryEventHandler;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityIndirectModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryModificationValidatedEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Listener class, that will listen on consumer binding of kafka stream for Category events
 *
 * @author Rajan Chauhan
 */
@Component
public class ModifyCategoryListener {

    private final Map<Class<?>, List<Consumer>> handlers = new HashMap<>();

    @Resource
    private ModifyCategoryEventHandler eventHandler;

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
    	register(AssociatedEntityModifiedEvent.class, eventHandler::handle);
        register(CategoryIdentityDataModifiedEvent.class, eventHandler::handle);
        register(CategoryModificationValidatedEvent.class, eventHandler::handle);
        register(AssociatedEntityIndirectModifiedEvent.class,eventHandler::handle);
    }
}
