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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

/**
 * The Class InvalidPOOprSpecValidForDefinedEvent generated when valid for is
 * not within range of CFS.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

public class InvalidProdOffOprSpecValidForDefinedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final Map<String, TimePeriod> invalidValidForDefined;

	private InvalidProdOffOprSpecValidForDefinedEvent() {
		this.productOfferingId = null;
		this.invalidValidForDefined = null;
	}

	public InvalidProdOffOprSpecValidForDefinedEvent(String productOfferingId,
			Map<String, TimePeriod> invalidValidForSelected) {
		this.productOfferingId = productOfferingId;
		invalidValidForDefined = invalidValidForSelected;
	}

	@Override
	public String toString() {
		return "InvalidPOOprSpecValidForDefinedEvent [productOfferingId=" + productOfferingId
				+ ", invalidValidForDefined=" + invalidValidForDefined + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Map<String, TimePeriod> getInvalidValidForDefined() {
		return invalidValidForDefined;
	}

}
