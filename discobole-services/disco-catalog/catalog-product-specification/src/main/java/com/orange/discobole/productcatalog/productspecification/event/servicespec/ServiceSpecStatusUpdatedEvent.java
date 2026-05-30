// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import java.time.OffsetDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;

/**
 * The ServiceSpecStatusUpdatedEvent type capture the updated life cycle status and cfs time.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
public final class ServiceSpecStatusUpdatedEvent implements ServiceSpecEvent {

	@TargetAggregateIdentifier
	private final String aggregateId;
	private final String cfsId;
	private final ServiceSpecLifeCycleEnum lifecycleStatus;
	private final OffsetDateTime cfsTimeOccurred;

	/**
	 * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
	 */
	private ServiceSpecStatusUpdatedEvent() {
		cfsId = null;
		lifecycleStatus = null;
		cfsTimeOccurred = null;
		aggregateId=null;
	}

	/**
	 * Instantiates a new service spec status changed event.
	 *  @param cfsId           the cfs id
	 * @param lifecycleStatus the lifecycle status
	 * @param cfsTimeOccurred
	 */
	public ServiceSpecStatusUpdatedEvent(String aggregateId,String cfsId, ServiceSpecLifeCycleEnum lifecycleStatus, OffsetDateTime cfsTimeOccurred) {
		this.cfsId = cfsId;
		this.lifecycleStatus = lifecycleStatus;
		this.cfsTimeOccurred = cfsTimeOccurred;
		this.aggregateId=aggregateId;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "ServiceSpecStatusUpdatedEvent [aggregateId=" + aggregateId + ", cfsId=" + cfsId + ", lifecycleStatus="
				+ lifecycleStatus + ", cfsTimeOccurred=" + cfsTimeOccurred + "]";
	}

	/**
	 * Gets the cfs id.
	 *
	 * @return the cfs id
	 */
	public String getCfsId() {
		return cfsId;
	}

	/**
	 * Gets the lifecycle status.
	 *
	 * @return the lifecycle status
	 */
	public ServiceSpecLifeCycleEnum getLifecycleStatus() {
		return lifecycleStatus;
	}

	public OffsetDateTime getCfsTimeOccurred() {
		return cfsTimeOccurred;
	}

	public String getAggregateId() {
		return aggregateId;
	}
	
}
