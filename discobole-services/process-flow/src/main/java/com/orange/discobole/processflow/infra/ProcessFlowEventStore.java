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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoException;

/**
 * The interface CatalogEventStore to handle different events.
 *
 * @author Sunny Srivastava
 * @since 1.0.0
 *
 */
@ConditionalOnProperty(value = "cqrs.command-enabled", havingValue = "true", matchIfMissing = true)
public interface ProcessFlowEventStore {

	/**
	 * Adds the eventlist.
	 *
	 * @param aggregateId the aggregate id
	 * @param eventList   the event list
	 */
	void add(String aggregateId, List<Event> eventList) throws DiscoException;

	/**
	 * Retrieves the event list.
	 *
	 * @param aggregateId the aggregate id
	 * @return the event list
	 */
	List<Event> fetch(String aggregateId);

	List<Event> fetchBackward(String productSpecId);
	
	 void deleteStream(String aggregateId);

}
