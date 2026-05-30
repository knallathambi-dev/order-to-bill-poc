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

import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;

/**
 * The StatusVerifiedEvent type capture the current and old life cycle status of service spec event.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
public final class StatusVerifiedEvent implements ServiceSpecEvent {

	@TargetAggregateIdentifier
	private final String aggregateId;
	
	private final String cfsId;

	private final ServiceSpecLifeCycleEnum lifecycleStatus;

	private final ServiceSpecLifeCycleEnum oldLifecycleStatus;
	
	
	/**
	 * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
	 */
	private StatusVerifiedEvent() {
		cfsId = null;
		lifecycleStatus = null;
		oldLifecycleStatus = null;
		aggregateId=null;
	}

	/**
	 * Instantiates a new status verified event.
	 *
	 * @param cfsId              the cfs id
	 * @param lifecycleStatus    the lifecycle status
	 * @param oldLifecycleStatus the old lifecycle status
	 */
	public StatusVerifiedEvent(String aggregateId,String cfsId, ServiceSpecLifeCycleEnum lifecycleStatus,
			ServiceSpecLifeCycleEnum oldLifecycleStatus) {
		this.aggregateId=aggregateId;
		this.cfsId = cfsId;
		this.lifecycleStatus = lifecycleStatus;
		this.oldLifecycleStatus = oldLifecycleStatus;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "StatusVerifiedEvent [aggregateId=" + aggregateId + ", cfsId=" + cfsId + ", lifecycleStatus="
				+ lifecycleStatus + ", oldLifecycleStatus=" + oldLifecycleStatus + "]";
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

	/**
	 * Gets the old lifecycle status.
	 *
	 * @return the old lifecycle status
	 */
	public ServiceSpecLifeCycleEnum getOldLifecycleStatus() {
		return oldLifecycleStatus;
	}

	public String getAggregateId() {
		return aggregateId;
	}
}
