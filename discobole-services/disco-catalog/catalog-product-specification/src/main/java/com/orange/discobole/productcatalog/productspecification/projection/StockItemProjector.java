// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.projection;

import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.productspecification.constant.HttpStatusCodeConstants;
import com.orange.discobole.productcatalog.productspecification.constant.OdacaConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.*;
import com.orange.discobole.productcatalog.productspecification.exception.BosInvalidEventException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * This class sends data on stream on registered events.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

@Component
//@EnableBinding(ProducerChannels.class)
public class StockItemProjector {

    private static final Logger LOGGER = LogManager.getLogger(StockItemProjector.class);
    private static final String STOCKITEMOUT = "stockItem-out-0";  

    @Autowired
    private StreamBridge bridge;

    /**
     * Sends StockItemReplicatedEvent to stream.
     *
     * @param event the stockItem duplicated event
     */
    public void handle(final StockItemReplicatedEvent event) {
        LOGGER.info("handling StockItemReplicatedEvent");
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(EventData.from(event))
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItem().getId()).build());
    }

    /**
     * Sends StockItemNotificationSentEvent to stream.
     *
     * @param event the stock item notification sent event
     */
    public void handle(final StockItemNotificationSentEvent event) {
        LOGGER.info("handling StockItemNotificationSentEvent");
        Event externalNotificationEvent = new Event();
        externalNotificationEvent.setEventId(UUID.randomUUID().toString());
        externalNotificationEvent.setEventType("StockItemCreationNotification");
        externalNotificationEvent.setEventTime(OffsetDateTime.now());
        externalNotificationEvent.setDescription("Event describing a Stock Item state change");
        externalNotificationEvent.setDomain("StockItem");
        externalNotificationEvent.setEvent(event);
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(externalNotificationEvent)
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItem().getId()).build());
    }

    /**
     * Handles {@link StockItemDuplicatedEvent} to send on stream.
     *
     * @param event the Event
     */
    public void handle(final StockItemDuplicatedEvent event) {
        LOGGER.info("handling StockItemDuplicatedEvent");
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(EventData.from(event))
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItem().getId()).build());
        throw new BosInvalidEventException(HttpStatusCodeConstants.INVALID_BODY_FIELD,
                "Duplicate Stock Item",
                "Please enter a different Stock Item ID to be replicated");
    }

    /**
     * Handles {@link InvalidStatusReceivedStockItemEvent} to send on stream.
     *
     * @param event the Event
     */
    public void handle(final InvalidStatusReceivedStockItemEvent event) {
        LOGGER.info("handling InvalidStatusReceivedEvent");
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(EventData.from(event))
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItemId()).build());
        throw new BosInvalidEventException(HttpStatusCodeConstants.INVALID_BODY_FIELD,
                "Invalid stock item status received",
                "Please enter the lifecycle status according to business rules");
    }

    /**
     * Handles {@link StockItemStatusUpdatedEvent} to send on stream.
     *
     * @param event the Event
     */
    public void handle(final StockItemStatusUpdatedEvent event) {
        LOGGER.info("handling StockItemStatusUpdatedEvent");
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(EventData.from(event))
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItemId()).build());
    }

    /**
     * Handles {@link StockItemAttributeUpdatedEvent} to send on stream.
     *
     * @param event the Event
     */
    public void handle(StockItemAttributeUpdatedEvent event) {
        LOGGER.info("handling StockItemAttributeUpdatedEvent");
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(EventData.from(event))
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItem().getId()).build());
    }

    public void handle(CurrentStockItemNotAlreadyExistedEvent event) {
        LOGGER.info("handling CurrentServiceSpecNotAlreadyExistedEvent");
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(EventData.from(event))
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItem().getId()).build());
        throw new BosInvalidEventException(HttpStatusCodeConstants.INVALID_BODY_FIELD,
                "No Stock Item exists with the given ID",
                "Please replicate the Stock Item with the given ID");
    }

    public void handle(StockItemEarlyTimeRejectedEvent event) {
        LOGGER.info("handling ServiceSpecEarlyTimeRejectedEvent");
        bridge.send(STOCKITEMOUT, MessageBuilder.withPayload(EventData.from(event))
                .setHeader(OdacaConstants.PARTITION_KEY, event.getStockItemId()).build());
        throw new BosInvalidEventException(HttpStatusCodeConstants.INVALID_BODY_FIELD,
                "This Stock Item has already been received",
                "The Stock Item has already been updated");
    }
}
