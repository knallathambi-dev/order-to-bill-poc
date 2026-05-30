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
 * The ServiceSpecExpurgedEvent type do service spec. expurge event.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
public class ServiceSpecExpurgedEvent implements ServiceSpecEvent {

	@TargetAggregateIdentifier
	private final String aggregateId;
	private final ServiceSpecification serviceSpecification;

	/**
	 * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
	 */
	private ServiceSpecExpurgedEvent() {
		serviceSpecification = null;
		aggregateId=null;
	}

	/**
	 * Instantiates a new Service Specification expurged event.
	 *
	 * @param serviceSpecification the service specification
	 */
	public ServiceSpecExpurgedEvent(String aggregateId,ServiceSpecification serviceSpecification) {
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
		return "ServiceSpecExpurgedEvent [aggregateId=" + aggregateId + ", serviceSpecification=" + serviceSpecification
				+ "]";
	}
}
