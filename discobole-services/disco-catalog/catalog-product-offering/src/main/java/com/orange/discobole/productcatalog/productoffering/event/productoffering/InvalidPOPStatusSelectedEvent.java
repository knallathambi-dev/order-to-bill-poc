// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.util.Map;

/**
 * The Class InvalidPOPStatusSelectedEvent is raised when POP status is obsolete
 * or retired.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class InvalidPOPStatusSelectedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final Map<String, String> invalidPOPStatus;

	private InvalidPOPStatusSelectedEvent() {
		productOfferingId = null;
		invalidPOPStatus = null;
	}

	public InvalidPOPStatusSelectedEvent(String productOfferingId, Map<String, String> invalidPOPStatus) {
		this.productOfferingId = productOfferingId;
		this.invalidPOPStatus = invalidPOPStatus;
	}

	@Override
	public String toString() {
		return "InvalidPOPStatusSelectedEvent [productOfferingId=" + productOfferingId + ", invalidPOPStatus="
				+ invalidPOPStatus + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Map<String, String> getInvalidPOPStatus() {
		return invalidPOPStatus;
	}

}
