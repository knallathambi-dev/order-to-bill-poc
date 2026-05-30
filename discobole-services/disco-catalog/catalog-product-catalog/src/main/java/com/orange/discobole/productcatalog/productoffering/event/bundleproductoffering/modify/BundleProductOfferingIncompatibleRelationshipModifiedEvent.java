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

public class BundleProductOfferingIncompatibleRelationshipModifiedEvent implements ProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final Set<ProductOfferingRelationship> addProductOfferingRelationships;
	private final Set<ProductOfferingRelationship> deleteProductOfferingRelationships;
	private final OffsetDateTime lastUpdate;
	private final List<PolicyRuleRef> policyRuleRef;

	public BundleProductOfferingIncompatibleRelationshipModifiedEvent(String productOfferingId,
			Set<ProductOfferingRelationship> addProductOfferingRelationships,
			Set<ProductOfferingRelationship> deleteProductOfferingRelationships, OffsetDateTime lastUpdate,
			List<PolicyRuleRef> policyRuleRef) {
		this.productOfferingId = productOfferingId;
		this.addProductOfferingRelationships = addProductOfferingRelationships;
		this.deleteProductOfferingRelationships = deleteProductOfferingRelationships;
		this.lastUpdate = lastUpdate;
		this.policyRuleRef = policyRuleRef;
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

	public List<PolicyRuleRef> getPolicyRuleRef() {
		return policyRuleRef;
	}

	public BundleProductOfferingIncompatibleRelationshipModifiedEvent() {
		this.productOfferingId = null;
		this.addProductOfferingRelationships = null;
		this.deleteProductOfferingRelationships = null;
		this.lastUpdate = null;
		this.policyRuleRef = null;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingIncompatibleRelationshipModifiedEvent [productOfferingId=" + productOfferingId
				+ ", addProductOfferingRelationships=" + addProductOfferingRelationships
				+ ", deleteProductOfferingRelationships=" + deleteProductOfferingRelationships + ", lastUpdate="
				+ lastUpdate + ", policyRuleRef=" + policyRuleRef + "]";
	}

}
