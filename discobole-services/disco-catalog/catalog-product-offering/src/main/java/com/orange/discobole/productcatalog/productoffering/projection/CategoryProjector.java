// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.projection;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.category.event.category.modify.AssociatedEntityModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.constant.OdacaConstants;

/**
 * This class sends registered category events on stream.
 * @author BMKJ8547
 *
 */
@Component
//@EnableBinding(CategoryChannel.class)
public class CategoryProjector {
	
private static final Logger LOGGER = LogManager.getLogger(CategoryProjector.class);




	private StreamBridge bridge;


	@Autowired
	public CategoryProjector(StreamBridge bridge) {
		this.bridge = bridge;


	}

/**
 * handles ModifyCategoryAssociateEntityEvent
 * @param event the event
 */
public void handle(final AssociatedEntityModifiedEvent  event) {
	LOGGER.info("Sending AssociatedEntityModifiedEvent to category channel");
	bridge.send("category-out-0", MessageBuilder.withPayload(EventData.from(event))
			.setHeader(OdacaConstants.PARTITION_KEY, event.getCategoryId()).build());

}


}
