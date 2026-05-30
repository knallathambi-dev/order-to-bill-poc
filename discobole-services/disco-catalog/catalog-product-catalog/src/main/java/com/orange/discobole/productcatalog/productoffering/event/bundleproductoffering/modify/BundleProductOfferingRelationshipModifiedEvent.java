// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

/**
 * Event raised to modify the PO relationships from the user.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class BundleProductOfferingRelationshipModifiedEvent implements ProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final Set<ProductOfferingRelationship> addProductOfferingRelationships;
	private final Set<ProductOfferingRelationship> deleteProductOfferingRelationships;
	private final OffsetDateTime lastUpdate;

    /**
     *
     * @param productOfferingId
     * @param addProductOfferingRelationships
     * @param deleteProductOfferingRelationships
     * @param lastUpdate
     */
	public BundleProductOfferingRelationshipModifiedEvent(String productOfferingId,
			Set<ProductOfferingRelationship> addProductOfferingRelationships,
			Set<ProductOfferingRelationship> deleteProductOfferingRelationships,
			OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.addProductOfferingRelationships = addProductOfferingRelationships;
		this.deleteProductOfferingRelationships = deleteProductOfferingRelationships;
		this.lastUpdate = lastUpdate;
	}

	public Set<ProductOfferingRelationship> getAddProductOfferingRelationships() {
		return addProductOfferingRelationships;
	}

	public Set<ProductOfferingRelationship> getDeleteProductOfferingRelationships() {
		return deleteProductOfferingRelationships;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingRelationshipModifiedEvent [productOfferingId=" + productOfferingId
				+ ", addProductOfferingRelationships=" + addProductOfferingRelationships
				+ ", deleteProductOfferingRelationships=" + deleteProductOfferingRelationships + ", lastUpdate=" + lastUpdate + "]";
	}

}
