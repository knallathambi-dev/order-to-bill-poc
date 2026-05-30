// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationRelationship;

public class ProductSpecRelationModifiedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final List<ProductSpecificationRelationship> productSpecRelationships;
	private final List<PolicyRuleRef> policyRuleRef;
	private final OffsetDateTime lastUpdate;

	private ProductSpecRelationModifiedEvent() {
		productSpecId = null;
		productSpecRelationships = null;
		policyRuleRef = null;
		lastUpdate = null;
	}

	public ProductSpecRelationModifiedEvent(String productSpecId,
			List<ProductSpecificationRelationship> productSpecRelationships, List<PolicyRuleRef> policyRuleRef,
			OffsetDateTime lastUpdate) {
		this.productSpecId = productSpecId;
		this.productSpecRelationships = productSpecRelationships;
		this.policyRuleRef = policyRuleRef;
		this.lastUpdate = lastUpdate;
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<ProductSpecificationRelationship> getProductSpecRelationships() {
		return productSpecRelationships;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public List<PolicyRuleRef> getPolicyRuleRef() {
		return policyRuleRef;
	}

	@Override
	public String toString() {
		return "ProductSpecRelationModifiedEvent [productSpecId=" + productSpecId + ", productSpecRelationships="
				+ productSpecRelationships + ", policyRuleRef=" + policyRuleRef + ", lastUpdate=" + lastUpdate + "]";
	}

}
