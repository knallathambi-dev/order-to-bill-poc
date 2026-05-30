package com.orange.discobole.eventservice.controller;

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import io.swagger.annotations.Api;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.eventservice.dto.generated.Event;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The Class EventController sends event from external System to the event
 * bus(Kafka).
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Api
@RestController
@RequestMapping(value = "/topic")
public class EventController {
	private static final Logger LOGGER = LogManager.getLogger(EventController.class);

	@Autowired
	private StreamBridge bridge;

//	@Resource
//	private KafkaTemplate<String, Object> kafkaTemplate;

	/**
	 * Creates a topic external-notification and sends it to kafka.
	 *
	 * @param event the processEvent dto received in request
	 * @return the response entity showing that the topic has been created.
	 */
	@PostMapping(value = "/{topicId}/event")
	public ResponseEntity<Event> notify(@PathVariable String topicId, @Valid @RequestBody Event event) {
		LOGGER.info("External notification event method started");
		if (event.getEventId() == null) {
			event.setEventId(UUID.randomUUID().toString());
		}
		if (event.getEventTime() == null) {
			event.setEventTime(OffsetDateTime.now());
		}

		//kafkaTemplate.send("external-notification", event);
		bridge.send("producerNotification-out-0", MessageBuilder.withPayload(event)
				.setHeader("partitionKey", event.getEventId()).build());
		LOGGER.info("CFS event sent on topic external-notification: {}", event);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(event);
	}
}
