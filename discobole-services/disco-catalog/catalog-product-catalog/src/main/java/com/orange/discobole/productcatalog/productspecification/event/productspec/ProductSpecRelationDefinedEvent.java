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

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;

/**
 * Event that will be raised when product specification relationships is valid
 * and get configured.
 * 
 * @author Vishal Vachaspati
 * @since 1.0
 *
 */

public final class ProductSpecRelationDefinedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final List<ProductSpecificationRelationship> productSpecRelationships;
	private final OffsetDateTime lastUpdate;
	private final List<PolicyRuleRef> policyRuleRef;

	private ProductSpecRelationDefinedEvent() {
		productSpecId = null;
		productSpecRelationships = null;
		lastUpdate = null;
		policyRuleRef = null;
	}

	public ProductSpecRelationDefinedEvent(String productSpecId,
			List<ProductSpecificationRelationship> productSpecRelationships, OffsetDateTime lastUpdate,
			List<PolicyRuleRef> policyRuleRef) {

		this.productSpecId = productSpecId;
		this.productSpecRelationships = productSpecRelationships;
		this.lastUpdate = lastUpdate;
		this.policyRuleRef = policyRuleRef;
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
		return "ProductSpecRelationDefinedEvent [productSpecId=" + productSpecId + ", productSpecRelationships="
				+ productSpecRelationships + ", lastUpdate=" + lastUpdate + ", policyRuleRef=" + policyRuleRef + "]";
	}

}
