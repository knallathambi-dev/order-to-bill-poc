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
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingCategoryModifiedEvent;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * This class sends registered events on stream.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
//@EnableBinding(ProductOfferingChannel.class)
@Component
public class ModifyProductOfferingProjector {

	/**
	 * The Constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingProjector.class);

	/**
	 * The product offering.
	 */
	@Resource
	private StreamBridge bridge;


	/**
	 * Handles AtomicProductOfferingCategoryModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingCategoryModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingCategoryModifiedEvent to catalog channel - {}", event);
		bridge.send("productOfferingCategory-out-0", MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}


}
