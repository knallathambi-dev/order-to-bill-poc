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
 * The ServiceSpecAttributeValueChangeCommand type initiate to modify the service specification.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public final class ServiceSpecAttributeValueChangeCommand {

	@TargetAggregateIdentifier
	private final String aggregateId;
    private final Event event;

    /**
     * Instantiates a new Service spec attribute value change command.
     *
     * @param event the event
     */
    public ServiceSpecAttributeValueChangeCommand(String aggregateId,Event event) {
        this.event = event;
        this.aggregateId=aggregateId;
    }
    
    @Override
	public String toString() {
		return "ServiceSpecAttributeValueChangeCommand [aggregateId=" + aggregateId + ", event=" + event + "]";
	}
    /**
     * Gets event.
     *
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

	public String getAggregateId() {
		return aggregateId;
	}
    
}
