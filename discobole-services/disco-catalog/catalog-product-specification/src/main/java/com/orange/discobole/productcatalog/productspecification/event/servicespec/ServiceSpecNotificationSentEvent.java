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
 * The Class ServiceSpecNotificationSentEvent.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class ServiceSpecNotificationSentEvent implements ServiceSpecEvent {

	@TargetAggregateIdentifier
	private final String aggregateId;
	private final ServiceSpecification cfs;

	/**
	 * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
	 */
	private ServiceSpecNotificationSentEvent() {
		cfs = null;
		aggregateId=null;
	}

	/**
	 * Instantiates a new service spec notification sent event.
	 *
	 * @param cfs the cfs
	 */
	public ServiceSpecNotificationSentEvent(String aggregateId,ServiceSpecification cfs) {
		this.aggregateId=aggregateId;
		this.cfs = cfs;
	}

	@Override
	public String toString() {
		return "ServiceSpecNotificationSentEvent [aggregateId=" + aggregateId + ", cfs=" + cfs + "]";
	}

	/**
	 * Gets the cfs.
	 *
	 * @return the cfs
	 */
	public ServiceSpecification getCfs() {
		return cfs;
	}

	public String getAggregateId() {
		return aggregateId;
	}

}
