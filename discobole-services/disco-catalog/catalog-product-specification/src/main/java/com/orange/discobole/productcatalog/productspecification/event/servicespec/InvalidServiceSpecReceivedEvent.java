// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;

/**
 * The Class InvalidStructureReceivedEvent.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class InvalidServiceSpecReceivedEvent implements ServiceSpecEvent {

	private final com.orange.discobole.productcatalog.productspecification.dto.generated.Event event;

	private final String error;

	/**
	 * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
	 */
	private InvalidServiceSpecReceivedEvent() {
		event = null;
		error = null;
	}

	/**
	 * Instantiates a new invalid service spec received event.
	 *
	 * @param event the event
	 * @param error the error
	 */
	public InvalidServiceSpecReceivedEvent(Event event, String error) {
		this.event = event;
		this.error = error;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "InvalidServiceSpecReceivedEvent [event=" + event + ", error=" + error + "]";
	}

	/**
	 * Gets the event.
	 *
	 * @return the event
	 */
	public com.orange.discobole.productcatalog.productspecification.dto.generated.Event getEvent() {
		return event;
	}

	/**
	 * Gets the error.
	 *
	 * @return the error
	 */
	public String getError() {
		return error;
	}

}
