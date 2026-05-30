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

package com.orange.discobole.processflow.infra;

import java.util.List;

import org.axonframework.eventhandling.DomainEventMessage;

import com.orange.discobole.processflow.event.Event;

public interface MongoEventStore {
	public List<DomainEventMessage<Event>> readEvents(String aggregateId,long head);
	public List<DomainEventMessage<Event>> readEventsBackword(String aggregateId,long head);
	public void deleteEventsByAggreGateId(String aggregateId);
	public void addEvent(List<DomainEventMessage<Event>> events);
}
