// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.InitiateProductSpecCommand}
 * is triggered and denotes that service specification has been selected for
 * product specification creation.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public final class ServiceSpecSelectedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final String serviceSpecId;

	private ServiceSpecSelectedEvent() {
		serviceSpecId = null;
		productSpecId=null;
	}

	/**
	 * Constructs an object of ServiceSpecSelectedEvent
	 *
	 * @param serviceSpecId service specification id
	 */
	public ServiceSpecSelectedEvent(String productSpecId, String serviceSpecId) {
		this.productSpecId = productSpecId;
		this.serviceSpecId = serviceSpecId;
	}

	/**
	 * This method represents the {@code ServiceSpecSelectedEvent} object with its
	 * data.
	 *
	 * @return returns the string representation of {@code ServiceSpecSelectedEvent}
	 *         data
	 */
	@Override
	public String toString() {
		return "ServiceSpecSelectedEvent [productSpecId=" + productSpecId + ", serviceSpecId=" + serviceSpecId + "]";
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
	 * This method is used to get aggregate Id
	 * 
	 * @return the productSpecId
	 */
	public String getProductSpecId() {
		return productSpecId;
	}
	
	

}
