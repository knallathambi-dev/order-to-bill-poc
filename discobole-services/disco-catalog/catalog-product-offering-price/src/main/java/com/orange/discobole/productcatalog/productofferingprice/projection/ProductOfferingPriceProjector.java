// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.projection;

import com.orange.discobole.productcatalog.productofferingprice.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.productofferingprice.constant.OdacaConstants;

/**
 * This class sends registered events on stream.
 *
 * @author Ankur Singh
 * @since 1.0
 */
//@EnableBinding(ProductOfferingPriceChannels.class)
@Component
public class ProductOfferingPriceProjector {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingPriceProjector.class);
	private static final String POPOUTO = "productOfferingPrice-out-0";  


	private StreamBridge bridge;

	@Autowired
	public ProductOfferingPriceProjector(StreamBridge bridge){
		this.bridge=bridge;
	}
	/**
	 * Handles ProductOfferingPriceInitiatedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceInitiatedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceInitiatedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}
	/**
	 * Handles ProductOfferingPriceChargeIdentityDataDescribedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceChargeIdentityDataDescribedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceChargeIdentityDataDescribedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}
	/**
	 * Handles ProductOfferingPriceRelationshipDefinedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceRelationshipDefinedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceRelationshipDefinedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}
	/**
	 * Handles ProductOfferingPriceValidatedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceValidatedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceValidatedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}

	/**
	 * Handles ProductOfferingPriceVersionCreatedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceVersionCreatedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceVersionCreatedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}

	/**
	 * Handles ProductOfferingPriceCreationCompletedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceCreationCompletedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceCreationCompletedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPrice().getId()).build());
	}

	/**
	 * Handles ProductOfferingPriceAlterationIdentityDataDescribedEvent.
	 *
	 * @param event the event
	 */
	public void handle(ProductOfferingPriceAlterationIdentityDataDescribedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceAlterationIdentityDataDescribedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}

	/**
	 * Handles ProductOfferingPriceAlterationIdentityDataDescribedEvent.
	 *
	 * @param event the event
	 */
	public void handle(ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}


	public void handle(ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}


	/**
	 * Handles ProductOfferingPriceCancelledEvent.
	 *
	 * @param event the event
	 */
	public void handle(ProductOfferingPriceCancelledEvent event) {
		LOGGER.info("Sending ProductOfferingPriceCancelledEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPrice().getId()).build());
	}

	/**
	 * Handles ProductOfferingPriceDeleteEvent.
	 *
	 * @param event the event
	 */
	public void handle(ProductOfferingPriceDeleteEvent event){
		LOGGER.info("Sending ProductOfferingPriceDeleteEvent to productOfferingPrice channel");
		bridge.send(POPOUTO, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getAggregateId()).build());
	
	}
}
