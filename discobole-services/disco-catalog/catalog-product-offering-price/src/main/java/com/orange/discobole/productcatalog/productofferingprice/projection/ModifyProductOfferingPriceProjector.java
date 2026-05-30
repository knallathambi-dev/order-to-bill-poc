// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.projection;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.productofferingprice.constant.OdacaConstants;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceCancelledEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceAlterationIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceChargeIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationCompletedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationValidatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceStatusValidityPeriodModifiedEvent;

/**
 * This class sends registered events on stream.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */

@Component
public class ModifyProductOfferingPriceProjector {

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingPriceProjector.class);

	private static final String POPOUT = "productOfferingPrice-out-0";


	private StreamBridge bridge;

	@Autowired
	public ModifyProductOfferingPriceProjector(StreamBridge bridge){
		this.bridge=bridge;
	}
	/**
	 * Handles ProductOfferingPriceChargeIdentityDataModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceChargeIdentityDataModifiedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceChargeIdentityDataModifiedEvent to productOfferingPrice channel");
		bridge.send(POPOUT, MessageBuilder.withPayload(EventData.from(event)).setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}

	/**
	 * Handles ProductOfferingPriceRelationshipDefinedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceRelationshipModifiedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceRelationshipModifiedEvent to productOfferingPrice channel");
		bridge.send(POPOUT, MessageBuilder.withPayload(EventData.from(event)).setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}

	/**
	 * Handles ProductOfferingPriceStatusValidityPeriodModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceStatusValidityPeriodModifiedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceStatusValidityPeriodModifiedEvent to productOfferingPrice channel");
		bridge.send(POPOUT, MessageBuilder.withPayload(EventData.from(event)).setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}

	/**
	 * Handles ProductOfferingPriceModificationCompletedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPriceModificationCompletedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceModificationCompletedEvent to productOfferingPrice channel");
		bridge.send(POPOUT, MessageBuilder.withPayload(EventData.from(event)).setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPrice().getId()).build());
	}

	/**
	 * Handles ProductOfferingPriceAlterationIdentityDataModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(ProductOfferingPriceAlterationIdentityDataModifiedEvent event) {
		LOGGER.info(
				"Sending ProductOfferingPriceAlterationIdentityDataModifiedEvent to productOfferingPrice channel");
		bridge.send(POPOUT, MessageBuilder.withPayload(EventData.from(event)).setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPriceId()).build());
	}

	/**
	 * Handles ProductOfferingPriceCancelledEvent.
	 *
	 * @param event the event
	 */
	public void handle(ProductOfferingPriceCancelledEvent event) {
		LOGGER.info("Sending ProductOfferingPriceCancelledEvent to productOfferingPrice channel");
		bridge.send(POPOUT, MessageBuilder.withPayload(EventData.from(event)).setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPrice().getId()).build());
	}
	
	/**
	 * Handles ProductOfferingPriceModificationValidatedEvent.
	 *
	 * @param event the event
	 */
	public void handle(ProductOfferingPriceModificationValidatedEvent event) {
		LOGGER.info("Sending ProductOfferingPriceModificationValidatedEvent to productOfferingPrice channel");
		bridge.send(POPOUT, MessageBuilder.withPayload(EventData.from(event)).setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingPrice().getId()).build());
	}
}
