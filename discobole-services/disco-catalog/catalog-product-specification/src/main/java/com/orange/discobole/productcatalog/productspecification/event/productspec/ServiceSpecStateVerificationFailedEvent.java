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
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.InitiateProductSpecCommand}
 * is triggered and denotes that service specification state has not been
 * verified that means its not in active or launched state.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public class ServiceSpecStateVerificationFailedEvent implements ProductSpecEvent {

	private final String serviceSpecId;
	private final ServiceSpecLifeCycleEnum serviceSpecLifeCycleStatus;

	private ServiceSpecStateVerificationFailedEvent() {
		serviceSpecId = null;
		serviceSpecLifeCycleStatus = null;
	}

	/**
	 * Constructs an object of ServiceSpecStateVerificationFailedEvent
	 *
	 * @param serviceSpecId              service specification id
	 * @param serviceSpecLifeCycleStatus service specification lifecycle status
	 */
	public ServiceSpecStateVerificationFailedEvent(String serviceSpecId, ServiceSpecLifeCycleEnum serviceSpecLifeCycleStatus) {
		this.serviceSpecId = serviceSpecId;
		this.serviceSpecLifeCycleStatus = serviceSpecLifeCycleStatus;
	}

	/**
	 * This method represents the {@code ServiceSpecStateVerificationFailedEvent}
	 * object with its data.
	 *
	 * @return returns the string representation of
	 *         {@code ServiceSpecStateVerificationFailedEvent} data
	 */
	@Override
	public String toString() {
		return "ServiceSpecStateVerificationFailedEvent{"+"serviceSpecId='" + serviceSpecId + '\''
				+ ", serviceSpecLifeCycleStatus=" + serviceSpecLifeCycleStatus + '}';
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
	public ServiceSpecLifeCycleEnum getServiceSpecLifeCycleStatus() {
		return serviceSpecLifeCycleStatus;
	}

}
