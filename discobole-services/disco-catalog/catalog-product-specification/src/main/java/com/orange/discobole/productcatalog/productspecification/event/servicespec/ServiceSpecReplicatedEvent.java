// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;

/**
 * The ServiceSpecReplicatedEvent type duplicate event.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
public final class ServiceSpecReplicatedEvent implements ServiceSpecEvent {

	@TargetAggregateIdentifier
	private final String aggregateId;
	private final ServiceSpecification serviceSpecification;

	/**
	 * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
	 */
	private ServiceSpecReplicatedEvent() {
		serviceSpecification = null;
		aggregateId=null;
	}

	/**
	 * Instantiates a new ServiceSpec. duplicated event.
	 *
	 * @param serviceSpecification the service specification
	 */
	public ServiceSpecReplicatedEvent(String aggregateId,ServiceSpecification serviceSpecification) {
		this.serviceSpecification = serviceSpecification;
		this.aggregateId=aggregateId;
	}

	/**
	 * Gets service specification.
	 *
	 * @return the service specification
	 */
	public ServiceSpecification getServiceSpecification() {
		return serviceSpecification;
	}
	
	public String getAggregateId() {
		return aggregateId;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "ServiceSpecReplicatedEvent [aggregateId=" + aggregateId + ", serviceSpecification="
				+ serviceSpecification + "]";
	}
}
