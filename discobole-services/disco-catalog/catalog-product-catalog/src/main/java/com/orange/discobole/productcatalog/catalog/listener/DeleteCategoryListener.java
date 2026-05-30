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
import com.orange.discobole.productcatalog.catalog.handler.DeleteCategoryEventHandler;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryLifeCycleUpdatedEvent;

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

@Component
public class DeleteCategoryListener {
    private static final Logger LOGGER = LogManager.getLogger(DeleteCategoryListener.class);

    private final Map<Class<?>, List<Consumer>> handlers = new HashMap<>();

    @Resource
    private DeleteCategoryEventHandler eventHandler;

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

        register(CategoryLifeCycleUpdatedEvent.class, eventHandler::handle);

      
    }


}
