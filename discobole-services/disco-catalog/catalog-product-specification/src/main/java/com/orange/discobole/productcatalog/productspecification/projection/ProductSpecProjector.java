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
import com.orange.discobole.productcatalog.productspecification.constant.OdacaConstants;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * This class sends data on stream on registered events.
 *
 * @author Piyush Goel
 * @since 1.0
 */
@Component
//@EnableBinding(ProducerChannels.class)
public class ProductSpecProjector {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecProjector.class);
	private static final String PSOUT = "ProductSpec-out-0";  
	@Autowired
	private StreamBridge bridge;

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecInitiatedEvent event) {
		LOGGER.info("Sending ProductSpecInitiatedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());

	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecIdentityDataEvent event) {
		LOGGER.info("Sending ProductSpecDescriptionElementsDefinedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecOpDefinedEvent event) {
		LOGGER.info("Sending ProductSpecOperDefinedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecCharacteristicsDefinedEvent event) {
		LOGGER.info("Sending ProductSpecCharacteristicsDefinedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}


	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecRelationDefinedEvent event) {
		LOGGER.info("Sending ProductSpecRelDefEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}


	/**
	 * Handles ProductSpecValidatedEvent.
	 *
	 * @author Vivek Singh
	 * @param event the event
	 */
	public void handle(final ProductSpecValidatedEvent event) {
		LOGGER.info("Sending ProductSpecValidatedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecificationId()).build());
	}

	/**
	 * Handles AtomicProductOfferingVersionCreatedEvent.
	 *
	 * @author Vivek Singh
	 * @param event the event
	 */
	public void handle(final ProductSpecVersionCreatedEvent event) {
		LOGGER.info("Sending ProductSpecVersionCreatedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecificationId()).build());
	}

	/**
	 * Handles {@link ProductSpecCancelledEvent}.
	 *
	 * @param event the event
	 * @author Ankur Singh
	 */
	
	public void handle(ProductSpecCancelledEvent event) {
		LOGGER.info("Sending ProductSpecCancelledEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecification().getId()).build());
	}

	public void handle(final ProductSpecDefineIdentityModifiedEvent event) {
		LOGGER.info("Sending ProductSpecDefineIdentityModifiedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}

	public void handle(final ProductSpecCharacteristicsModifiedEvent event) {
		LOGGER.info("Sending ProductSpecCharacteristicsModifiedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}

	public void handle(final ProductSpecRelationModifiedEvent event) {
		LOGGER.info("Sending ProductSpecRelationModifiedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}


	public void handle(final ProductSpecModificationValidatedEvent event) {
		LOGGER.info("Sending ProductSpecModificationValidatedEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecificationId()).build());
	}
	
	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final ComputeProductConfigurationEvent event) {
		LOGGER.info("Sending ComputeProductConfigurationEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProdSpecId()).build());
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final LinkProductSpecificationToStockItemEvent event) {
		LOGGER.info("Sending LinkProductSpecificationToStockItemEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecificationId()).build());
	}

	/**
	 * Handler LinkProductSpecificationToStockItemModificationEvent
	 * @param event
	 */
	public void handle(final LinkProductSpecificationToStockItemModificationEvent event) {
		LOGGER.info("Sending LinkProductSpecificationToStockItemModificationEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecificationId()).build());
	}
	
	public void handle(final ProductSpecificationDeleteEvent event){
		LOGGER.info("Sending ProductSpecificationDeleteEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getAggregateId()).build());
	}
	public void handle(final ProductSpecificationTemporaryDeleteEvent event){
			LOGGER.info("Sending ProductSpecificationTemporaryDeleteEvent to catalogProductSpec channel");
			bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
					.setHeader(OdacaConstants.PARTITION_KEY, event.getAggregateId()).build());
	
	}

	public void handle(ProductConfigurationModificationEvent event) {
		LOGGER.info("Sending ProductSpecificationTemporaryDeleteEvent to catalogProductSpec channel");
		bridge.send(PSOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProdSpecId()).build());

	}
}
