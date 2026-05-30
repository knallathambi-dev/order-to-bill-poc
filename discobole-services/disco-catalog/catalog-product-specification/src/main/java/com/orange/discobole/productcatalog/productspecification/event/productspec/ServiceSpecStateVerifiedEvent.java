// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.InitiateProductSpecCommand}
 * is triggered and denotes that service specification state has been verified
 * that means its in active or launched state.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public final class ServiceSpecStateVerifiedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final String serviceSpecId;
	private final String serviceSpecLifeCycleStatus;

	private ServiceSpecStateVerifiedEvent() {
		serviceSpecId = null;
		serviceSpecLifeCycleStatus = null;
		productSpecId=null;
	}

	/**
	 * Constructs an object of ServiceSpecStateVerifiedEvent
	 *
	 * @param serviceSpecId              service specification id
	 * @param serviceSpecLifeCycleStatus service specification lifecycle status
	 */
	public ServiceSpecStateVerifiedEvent(String productSpecId,String serviceSpecId, String serviceSpecLifeCycleStatus) {
		this.serviceSpecId = serviceSpecId;
		this.serviceSpecLifeCycleStatus = serviceSpecLifeCycleStatus;
		this.productSpecId = productSpecId;
	}

	/**
	 * This method represents the {@code ServiceSpecStateVerifiedEvent} object with
	 * its data.
	 *
	 * @return returns the string representation of
	 *         {@code ServiceSpecStateVerifiedEvent} data
	 */
	@Override
	public String toString() {
		return "ServiceSpecStateVerifiedEvent{"+"productSpecId" +productSpecId+ ", serviceSpecId='" + serviceSpecId + '\''
				+ ", serviceSpecLifeCycleStatus='" + serviceSpecLifeCycleStatus + '\'' + '}';
	}

	/**
	 * This method is used to get service specification id.
	 *
	 * @return returns service specification id
	 */
	public String getServiceSpecId() {
		return serviceSpecId;
	}

	/**
	 * This method is used to get service specification lifecycle status.
	 *
	 * @return returns service specification lifecycle status
	 */
	public String getServiceSpecLifeCycleStatus() {
		return serviceSpecLifeCycleStatus;
	}
	
	/**
	 * This method is used to get service specification productSpecId.
	 *
	 * @return returns service specification productSpecId
	 */
	public String getProductSpecId() {
		return productSpecId;
	}
	
	
}
