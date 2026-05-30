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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Event raised to show the selected relationship from the user.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AtomicProductOfferingRelationshipSelectedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final Map<String, String> productOfferingRelationships;

	private AtomicProductOfferingRelationshipSelectedEvent() {
		this.productOfferingId = null;
		this.productOfferingRelationships = null;
	}

	public AtomicProductOfferingRelationshipSelectedEvent(String productOfferingId,
			Map<String, String> productOfferingRelationships) {

		this.productOfferingId = productOfferingId;
		this.productOfferingRelationships = productOfferingRelationships;
	}

	@Override
	public String toString() {
		return "ProductOfferingRelationshipSelectedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingRelationships=" + productOfferingRelationships + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Map<String, String> getProductOfferingRelationships() {
		return productOfferingRelationships;
	}

}
