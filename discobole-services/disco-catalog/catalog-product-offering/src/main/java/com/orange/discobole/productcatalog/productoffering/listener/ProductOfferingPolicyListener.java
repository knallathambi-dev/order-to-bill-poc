// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
package com.orange.discobole.productcatalog.productoffering.listener;

import com.orange.discobole.productcatalog.productoffering.dto.policyrule.Event;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ProductOfferingPolicyListener {

    private static final Logger LOGGER = LogManager.getLogger(ProductOfferingPolicyListener.class);
    @Resource
    private ProductOfferingService productOfferingService;

    @Bean
    public Consumer<Message<Event>> policyNotification() {
        return message -> {
            LOGGER.info("Received Message on topic policy-Notification {} from partition ", message.getPayload());
            try {
                Event event = message.getPayload();
                LOGGER.info("Message DeSerialized: {}", event);
                productOfferingService.processPolicyEvent(event);
            } catch (Exception e) {
                LOGGER.error("cannot deserialize", e);
            }
        };
    }
}
