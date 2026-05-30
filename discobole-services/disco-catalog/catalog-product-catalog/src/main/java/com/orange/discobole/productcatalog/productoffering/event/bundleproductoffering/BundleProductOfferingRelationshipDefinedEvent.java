// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;

/**
 * Event raised to show the all valid selected relationship from the user, that
 * can be defined.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class BundleProductOfferingRelationshipDefinedEvent implements BundleProductOfferingEvent {

	private final String productOfferingId;
	private final List<ProductOfferingRelationship> productOfferingRelationships;
	private final OffsetDateTime lastUpdate;

	public BundleProductOfferingRelationshipDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.productOfferingRelationships = null;
		this.lastUpdate = null;
	}

	/**
	 * @param productOfferingId
	 * @param productOfferingRelationships
	 * @param lastUpdate
	 */
	public BundleProductOfferingRelationshipDefinedEvent(String productOfferingId,
			List<ProductOfferingRelationship> productOfferingRelationships, OffsetDateTime lastUpdate) {
		super();
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
		return "BundleProductOfferingRelationshipDefinedEvent [productOfferingId=" + productOfferingId
				+ ", productOfferingRelationships=" + productOfferingRelationships + ", lastUpdate=" + lastUpdate + "]";
	}


}
