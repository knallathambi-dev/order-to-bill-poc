// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.projection;

import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.OdacaConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * This class sends registered events on stream.
 *
 * @author Ankur Singh
 * @since 1.0
 */
//@EnableBinding(LifeCycleManagementChannel.class)
@Component
public class LifeCycleManagerProjector {

	private static final Logger LOGGER = LogManager.getLogger(LifeCycleManagerProjector.class);

	private static final String LIFE_CYCLE_MANAGEMENT_OUT_TOPIC = "lifeCycleManagement-out-0";

	private StreamBridge bridge;

	public LifeCycleManagerProjector() {

	}


	@Autowired
	public LifeCycleManagerProjector(StreamBridge bridge) {
		this.bridge = bridge;

	}

	/**
	 * Handles LifeCycleEntitySelectEvent.
	 *
	 * @param event the event
	 */
	public void handle(final LifeCycleEntitySelectEvent event) {
		LOGGER.info("Handling LifeCycleEntitySelectEvent");
		bridge.send(LIFE_CYCLE_MANAGEMENT_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getEntityId()).build());
	}
	
	public void handle(final InvalidLifeCycleEntitySelectedEvent event) {
		LOGGER.info("Handling InvalidLifeCycleEntitySelectedEvent");
		bridge.send(LIFE_CYCLE_MANAGEMENT_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getEntityId()).build());
		
	}

	/**
	 * Handles InvalidLifeCycleStateSelectedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidLifeCycleStateSelectedEvent event) {
		LOGGER.info("Handling InvalidLifeCycleStateSelectedEvent");
		bridge.send(LIFE_CYCLE_MANAGEMENT_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getEntityId()).build());
		
	}

	/**
	 * Handles LifeCycleStateSelectedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final LifeCycleStateSelectedEvent event) {
		LOGGER.info("Handling LifeCycleStateSelectedEvent");
		bridge.send(LIFE_CYCLE_MANAGEMENT_OUT_TOPIC, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getEntityId()).build());
	}

	/**
	 * Handles ProductSpecificationStateChangeEvent.
	 *
	 * @param event the event
	 */
	
	public void handle(final ProductSpecificationStateChangeEvent event) {
		//comment
		 }
	 

}
