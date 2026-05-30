// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering.modify;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.category.dto.generated.productoffering.ProductOfferingRelationship;

/**
 * Event raised to modify the PO relationships from the user.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class AtomicProductOfferingRelationshipModifiedEvent implements ProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductOfferingRelationship> productOfferingRelationships;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param productOfferingRelationships
	 * @param lastUpdate
	 */
	public AtomicProductOfferingRelationshipModifiedEvent(String productOfferingId,
			List<ProductOfferingRelationship> productOfferingRelationships, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.productOfferingRelationships = productOfferingRelationships;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingRelationshipModifiedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingRelationships=" + productOfferingRelationships + ", lastUpdate=" + lastUpdate + "]";
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

}
