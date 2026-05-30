// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/**
 * Event raised to modify the PO relationships from the user.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BundleProductOfferingRelationshipModifiedEvent implements ProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final Set<ProductOfferingRelationship> addProductOfferingRelationships;
	private final Set<ProductOfferingRelationship> deleteProductOfferingRelationships;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param productOfferingRelationships
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

	public BundleProductOfferingRelationshipModifiedEvent() {
		super();
		this.productOfferingId = null;
		this.addProductOfferingRelationships = null;
		this.deleteProductOfferingRelationships = null;
		this.lastUpdate = null;
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
