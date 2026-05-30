// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.projection;

import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.category.constant.OdacaConstants;
import com.orange.discobole.productcatalog.category.event.category.*;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryDeletedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.CategoryLifeCycleUpdatedEvent;
import com.orange.discobole.productcatalog.category.event.category.delete.ParentCategoryModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityIndirectModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.category.event.category.modify.CategoryIdentityDataModifiedEvent;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


/**
 * This class sends registered category events on stream.
 * @author BMKJ8547
 *
 */
@Component
//@EnableBinding(CategoryChannel.class)
public class CategoryProjector {
	
private static final String CATEGORY_OUT_TOPIC = "category-out-0";

private static final Logger LOGGER = LogManager.getLogger(CategoryProjector.class);

	@Resource
	private StreamBridge bridge;

/**
 *handles EntityTypeSelectedEvent
 * @param event the event
 */
public void handle(final EntityTypeSelectedEvent event) {
	LOGGER.info("Sending EntityTypeSelectedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
/**
 *handles CategoryIdentityDataDefinedEvent
 * @param event the event
 */
public void handle(final CategoryIdentityDataDefinedEvent event) {
	LOGGER.info("Sending CategoryIdentityDataDefinedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
/**
 *handles CategoryIdentityDataDefinedEvent
 * @param event the event
 */
public void handle(final AssociateEntitySelectedEvent event) {
	LOGGER.info("Sending EntityDefinedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
/**
 *handles CategoryCancelledEvent
 * @param event the event
 */
public void handle(final CategoryCancelledEvent event) {
	LOGGER.info("Sending CategoryCancelledEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
/**
 *handles CategoryCreationEvent
 * @param event the event
 */
public void handle(final CategoryCreationEvent  event) {
	LOGGER.info("Sending CategoryCreationEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}

/**
 *handles ParentCategoryUpdatedEvent
 * @param event the event
 */
public void handle(final ParentCategoryUpdatedEvent  event) {
	LOGGER.info("Sending ParentCategoryUpdatedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
/**
 * handles ModifyCategoryAssociateEntityEvent
 * @param event the event
 */
public void handle(final AssociatedEntityModifiedEvent  event) {
	LOGGER.info("Sending AssociatedEntityModifiedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
/**
 * handles CategoryIdentityDataModifiedEvent
 * @param event the event
 */
public void handle(final CategoryIdentityDataModifiedEvent  event) {
	LOGGER.info("Sending CategoryIdentityDataModifiedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}

public void handle(final CategoryLifeCycleUpdatedEvent  event) {
	LOGGER.info("Sending CategoryLifeCycleUpdatedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
public void handle(final ParentCategoryModifiedEvent  event) {
	LOGGER.info("Sending ParentCategoryModifiedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}
public void handle(final CategoryAssociationDeletedEvent  event) {
	LOGGER.info("Sending CategoryAssociationDeletedEvent to category channel - {}", event);
	bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}

	public void handle(final CategoryDeletedEvent event) {
		LOGGER.info("Sending CategoryDeleteEvent to category channel - {}", event);
		bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

	}
	public void handle(final AssociatedEntityIndirectModifiedEvent event) {
		LOGGER.info("Sending AssociatedEntityIndirectModifiedEvent to category channel - {}", event);
		bridge.send(CATEGORY_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

	}
}
