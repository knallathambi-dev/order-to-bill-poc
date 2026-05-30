// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.servicespec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;

/**
 * The ServiceSpecificationStateChangeCommand instructs to catalog to initiate
 * an active CFSSpec.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
public final class ServiceSpecificationStateChangeCommand {
	@TargetAggregateIdentifier
	private final String aggregateId;
	private final Event event;

	/**
	 * Instantiates a new Service specification state change command.
	 *
	 * @param event the event
	 */
	public ServiceSpecificationStateChangeCommand(String aggregateId, Event event) {
		super();
		this.aggregateId = aggregateId;
		this.event = event;
	}

	public String getAggregateId() {
		return aggregateId;
	}

	/**
	 * Gets event.
	 *
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return "ServiceSpecificationStateChangeCommand [aggregateId=" + aggregateId + ", event=" + event + "]";
	}
}
