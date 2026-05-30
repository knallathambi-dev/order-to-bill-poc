// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;

/**
 * The Class InvalidStatusReceivedEvent.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class InvalidStatusReceivedEvent implements ServiceSpecEvent {

	private final String cfsId;

	private final ServiceSpecLifeCycleEnum oldLifecycleStatus;

	private final ServiceSpecLifeCycleEnum newLifecycleStatus;

	/**
	 * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
	 */
	private InvalidStatusReceivedEvent() {
		cfsId = null;
		oldLifecycleStatus = null;
		newLifecycleStatus = null;
	}

	/**
	 * Instantiates a new invalid status received event.
	 *
	 * @param cfsId              the cfs id
	 * @param oldLifecycleStatus the old lifecycle status
	 * @param newLifecycleStatus the new lifecycle status
	 */
	public InvalidStatusReceivedEvent(String cfsId, ServiceSpecLifeCycleEnum oldLifecycleStatus,
			ServiceSpecLifeCycleEnum newLifecycleStatus) {
		this.cfsId = cfsId;
		this.oldLifecycleStatus = oldLifecycleStatus;
		this.newLifecycleStatus = newLifecycleStatus;
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
	 * Gets the old lifecycle status.
	 *
	 * @return the old lifecycle status
	 */

	public ServiceSpecLifeCycleEnum getOldLifecycleStatus() {
		return oldLifecycleStatus;
	}

	/**
	 * Gets the new lifecycle status.
	 *
	 * @return the new lifecycle status
	 */
	public ServiceSpecLifeCycleEnum getNewLifecycleStatus() {
		return newLifecycleStatus;
	}

	@Override
	public String toString() {
		return "InvalidStatusReceivedEvent [cfsId=" + cfsId + ", oldLifecycleStatus=" + oldLifecycleStatus
				+ ", newLifecycleStatus=" + newLifecycleStatus + "]";
	}

}
