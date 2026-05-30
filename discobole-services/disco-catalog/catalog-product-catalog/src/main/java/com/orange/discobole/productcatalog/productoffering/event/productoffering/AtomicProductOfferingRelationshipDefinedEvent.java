// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;

/**
 * Event raised to show the all valid selected relationship from the user, that
 * can be defined.
 *
 * @author Vivek Singh
 * @since 1.0
 */
public class AtomicProductOfferingRelationshipDefinedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final List<ProductOfferingRelationship> productOfferingRelationships;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingRelationshipDefinedEvent() {
		this.productOfferingId = null;
		this.productOfferingRelationships = null;
		this.lastUpdate = null;
	}

	public AtomicProductOfferingRelationshipDefinedEvent(String productOfferingId,
			List<ProductOfferingRelationship> productOfferingRelationships, OffsetDateTime lastUpdate) {

		this.productOfferingId = productOfferingId;
		this.productOfferingRelationships = productOfferingRelationships;
		this.lastUpdate = lastUpdate;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<ProductOfferingRelationship> getProductOfferingRelationships() {
		return productOfferingRelationships;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingRelationshipDefinedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingRelationships=" + productOfferingRelationships + ", lastUpdate=" + lastUpdate + "]";
	}

}
