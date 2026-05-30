// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.deserializer;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.event.EventData;

/**
 * Deserializer for EventData
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public class EventDataDeserializer extends JsonDeserializer<EventData> {

	private static final Logger LOGGER = LogManager.getLogger(EventDataDeserializer.class);

	@Override
	public EventData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException {
		final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		final ObjectNode root = mapper.readTree(jsonParser);
		final String type = root.get("type").asText();
		try {
			final Event event = (Event) mapper.readValue(root.get("event").toString(), Class.forName(type));
			return EventData.from(event);
		} catch (ClassNotFoundException e) {
			LOGGER.warn("unable to deserialize for {}", type, e);
			return null;
		}
	}

}
