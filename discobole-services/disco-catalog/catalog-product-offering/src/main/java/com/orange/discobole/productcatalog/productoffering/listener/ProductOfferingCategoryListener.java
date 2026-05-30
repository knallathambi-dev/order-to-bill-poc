// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;
import com.orange.discobole.productcatalog.productoffering.handler.ProductOfferingCategoryEventHandler;

@Component
//@EnableBinding(ProductOfferingCategoryChannel.class)
public class ProductOfferingCategoryListener {



    private static final Logger LOGGER = LogManager.getLogger(ProductOfferingCategoryListener.class);

    private final Map<Class, List<Consumer>> handlers = new HashMap<>();

    @Resource
    private ProductOfferingCategoryEventHandler eventHandler;
    
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
    	register(ProductOfferingCategoryAssociationEvent.class, eventHandler::handle);
    	register(ProductOfferingCategoryAssociationDeletedEvent.class, eventHandler::handle);
    }

    @Bean
    public Consumer<Message<EventData>> productOfferingCategory() {
        return message -> {
            LOGGER.info("Received message on topic productOffering.productOfferingCategory containing event {}", message.getPayload().getEvent());
            handle(message.getPayload().getEvent());
        };
    }


}
