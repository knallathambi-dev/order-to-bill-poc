// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.projection;


import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.productoffering.constant.OdacaConstants;
import com.orange.discobole.productcatalog.productoffering.event.category.CategoryProductOfferingAssociationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
public class CategoryProductOfferingProjector {
	/**
	 * The Constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger(CategoryProductOfferingProjector.class);

	/**
	 * The product offering.
	 */
//	@Resource



	private StreamBridge bridge;

	/**
	 * Handles ProductOfferingTypeSelectedEvent.
	 *
	 * @param event the event
	 */
	@Autowired
	public CategoryProductOfferingProjector(StreamBridge bridge) {
		this.bridge = bridge;

	}
	public void handle(final CategoryProductOfferingAssociationEvent event) {
		LOGGER.info("In handle method of CategoryProductOfferingAssociationEvent");
		bridge.send("categoryProductOffering-out-0", MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());
	}
}
