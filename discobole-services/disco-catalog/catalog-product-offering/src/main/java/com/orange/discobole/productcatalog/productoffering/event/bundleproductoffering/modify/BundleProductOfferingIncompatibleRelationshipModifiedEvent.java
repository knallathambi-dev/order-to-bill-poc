// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

import java.time.OffsetDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BundleProductOfferingIncompatibleRelationshipModifiedEvent implements ProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final Set<ProductOfferingRelationship> addProductOfferingRelationships;
	private final Set<ProductOfferingRelationship> deleteProductOfferingRelationships;
	private final OffsetDateTime lastUpdate;

	public BundleProductOfferingIncompatibleRelationshipModifiedEvent(String productOfferingId,
			Set<ProductOfferingRelationship> addProductOfferingRelationships,
			Set<ProductOfferingRelationship> deleteProductOfferingRelationships, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.addProductOfferingRelationships = addProductOfferingRelationships;
		this.deleteProductOfferingRelationships = deleteProductOfferingRelationships;
		this.lastUpdate = lastUpdate;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Set<ProductOfferingRelationship> getAddProductOfferingRelationships() {
		return addProductOfferingRelationships;
	}

	public Set<ProductOfferingRelationship> getDeleteProductOfferingRelationships() {
		return deleteProductOfferingRelationships;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public BundleProductOfferingIncompatibleRelationshipModifiedEvent() {
		this.productOfferingId = null;
		this.addProductOfferingRelationships = null;
		this.deleteProductOfferingRelationships = null;
		this.lastUpdate = null;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingIncompatibleRelationshipModifiedEvent [productOfferingId=" + productOfferingId
				+ ", addProductOfferingRelationships=" + addProductOfferingRelationships
				+ ", deleteProductOfferingRelationships=" + deleteProductOfferingRelationships + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
