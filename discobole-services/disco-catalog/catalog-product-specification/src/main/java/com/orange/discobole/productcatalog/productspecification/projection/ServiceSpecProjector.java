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
import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.*;

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
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
@Component
//@EnableBinding(ProducerChannels.class)
public class ServiceSpecProjector {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecProjector.class);
	private static final String SERVICESPECOUT = "serviceSpec-out-0";
	
	@Autowired
	private StreamBridge bridge;

	/**
	 * Sends ServiceSpecReplicatedEvent to stream.
	 *
	 * @param event the serviceSpec. duplicated event
	 */
	public void handle(final ServiceSpecReplicatedEvent event) {
		LOGGER.info("Sending ServiceSpecReplicatedEvent to catalogServiceSpec channel");
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getServiceSpecification().getId()).build());
	}

	/**
	 * Sends ServiceSpecNotificationSentEvent to stream.
	 *
	 * @param event the service spec notification sent event
	 */
	public void handle(final ServiceSpecNotificationSentEvent event) {
		Event externalNotificationEvent = new Event();
		externalNotificationEvent.setEventId(UUID.randomUUID().toString());
		externalNotificationEvent.setEventType("ServiceSpecificationCreationNotification");
		externalNotificationEvent.setEventTime(OffsetDateTime.now());
		externalNotificationEvent.setDescription("Event describing a Service specification state change");
		externalNotificationEvent.setDomain("ServiceCatalog");
		externalNotificationEvent.setEvent(event);
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(externalNotificationEvent)
				.setHeader(OdacaConstants.PARTITION_KEY, event.getCfs().getId()).build());
	}

	/**
	 * Handles {@link ServiceSpecDuplicatedEvent} to send on stream.
	 *
	 * @param event the Event
	 */
	public void handle(final ServiceSpecDuplicatedEvent event) {
		LOGGER.info("Sending ServiceSpecDuplicatedEvent to catalogServiceSpec channel");
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getServiceSpecification().getId()).build());
	}

	/**
	 * Handles {@link CurrentServiceSpecNotAlreadyExistedEvent} to send on stream.
	 *
	 * @param event the Event
	 */
	public void handle(final CurrentServiceSpecNotAlreadyExistedEvent event) {
		LOGGER.info("CurrentServiceSpecNotAlreadyExistedEvent");
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getServiceSpecification().getId()).build());
	}

	/**
	 * Handles {@link ServiceSpecEarlyTimeRejectedEvent} to send on stream.
	 *
	 * @param event the Event
	 */
	public void handle(final ServiceSpecEarlyTimeRejectedEvent event) {
		LOGGER.info("ServiceSpecEarlyTimeRejectedEvent");
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getCfsId()).build());
	}

	/**
	 * Handles {@link InvalidStatusReceivedEvent} to send on stream.
	 *
	 * @param event the Event
	 */
	public void handle(final InvalidStatusReceivedEvent event) {
		LOGGER.info("InvalidStatusReceivedEvent");
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getCfsId()).build());
	}

	/**
	 * Handles {@link ServiceSpecStatusUpdatedEvent} to send on stream.
	 *
	 * @param event the Event
	 */
	public void handle(final ServiceSpecStatusUpdatedEvent event) {
		LOGGER.info("Sending ServiceSpecStatusUpdatedEvent to catalogServiceSpec channel");
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getCfsId()).build());
	}

	/**
	 * Handles {@link ServiceSpecAttributeUpdatedEvent} to send on stream.
	 *
	 * @param event the Event
	 */
	public void handle(ServiceSpecAttributeUpdatedEvent event) {
		LOGGER.info("Sending ServiceSpecAttributeUpdatedEvent to catalogServiceSpec channel");
		bridge.send(SERVICESPECOUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getServiceSpecification().getId()).build());
	}
}
