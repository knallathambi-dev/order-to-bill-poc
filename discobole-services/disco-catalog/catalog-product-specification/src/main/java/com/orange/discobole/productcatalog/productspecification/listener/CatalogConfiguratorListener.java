// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.service.ServiceSpecService;
import com.orange.discobole.productcatalog.productspecification.service.StockItemService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


/**
 * The Catalog listener that listens to message received from stream.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
//@EnableBinding(ConsumerChannels.class)
@Component
public class CatalogConfiguratorListener {

    private static final Logger LOGGER = LogManager.getLogger(CatalogConfiguratorListener.class);

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ConfigurableProperties configurableProperties;

    @Resource
    private ServiceSpecService serviceSpecService;

    @Resource
    private StockItemService stockItemService;

    /**
     * Listen on topic external-notification.
     *
     * @param message   the message received from external-notification topic
     * @param partition the partition id on which the message is received
     */

    @Bean
    public Consumer<Message<Event>> producerNotification() {

          return message -> {
              LOGGER.info("Received Message on topic external-Notification {} from partition ", message.getPayload());
              try {
                  Event event = message.getPayload();
                  LOGGER.info("Message DeSerialized: {}", event);
                  if (configurableProperties.getEventType().contains(event.getEventType()) && event.getEventType().equalsIgnoreCase(ServiceSpecConstants.STATECHANGE_EVENT)) {
                      serviceSpecService.processEvent(event);
                  } else if (configurableProperties.getEventType().contains(event.getEventType()) && event.getEventType().equalsIgnoreCase(ServiceSpecConstants.ATTRIBUTEVALUECHANGE_EVENT)) {
                      serviceSpecService.processUpdateServiceSpecEvent(event);
                  } else if (configurableProperties.getEventType().contains(event.getEventType()) && event.getEventType().equalsIgnoreCase(ServiceSpecConstants.STOCKITEMSTATECHANGE_EVENT)) {
                      stockItemService.processStockItemEvent(event);
                  } else if (configurableProperties.getEventType().contains(event.getEventType()) && event.getEventType().equalsIgnoreCase(ServiceSpecConstants.STOCKITEMATTRIBUTEVALUECHANGE_EVENT)) {
                      stockItemService.processUpdateStockItemEvent(event);
                  } else {
                      LOGGER.info("Event type not found for notification: {}", event.getEventType());
                  }
              } catch (Exception e) {
                  LOGGER.error("cannot deserialize", e);
              }
        };

    }

}
