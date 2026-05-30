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

package com.orange.discobole.processflow.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.orange.discobole.processflow.deserializer.EventDataDeserializer;

/**
 * Wrapper for Event
 *
 * @author Saurabh Shakya
 * @since 1.0
 */

@JsonDeserialize(using = EventDataDeserializer.class)
public class EventData {

	private final Event event;
	private final String type;

	private EventData(Event event, String type) {
		this.event = event;
		this.type = type;
	}

	public static EventData from(Event event) {
		return new EventData(event, event.getClass().getCanonicalName());
	}

	@Override
	public String toString() {
		return "EventData{" + "event=" + event + ", type='" + type + '\'' + '}';
	}

	public Event getEvent() {
		return event;
	}

	public String getType() {
		return type;
	}

}
