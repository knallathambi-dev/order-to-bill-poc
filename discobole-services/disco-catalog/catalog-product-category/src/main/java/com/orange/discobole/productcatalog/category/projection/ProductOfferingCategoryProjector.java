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
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationDeletedEvent;
import com.orange.discobole.productcatalog.category.event.productoffering.ProductOfferingCategoryAssociationEvent;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
//@EnableBinding(ProductOfferingCategoryChannel.class)
public class ProductOfferingCategoryProjector {
	@Resource
	private StreamBridge bridge;
	
	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingCategoryProjector.class);
	
	public void handle(final ProductOfferingCategoryAssociationEvent event) {
		LOGGER.info("Sending ProductOfferingCategoryAssociationEvent to productOfferingCategory channel - {}", event);
		bridge.send("productOfferingCategory-out-0", MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());

	}
	public void handle(final ProductOfferingCategoryAssociationDeletedEvent event) {
		LOGGER.info("Sending ProductOfferingCategoryAssociationEvent to productOfferingCategory channel - {}", event);
		bridge.send("productOfferingCategory-out-0", MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());

	}
	
}
