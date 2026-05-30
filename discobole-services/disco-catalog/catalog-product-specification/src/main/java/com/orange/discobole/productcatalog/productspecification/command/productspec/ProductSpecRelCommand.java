// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationRelationship;

public final class ProductSpecRelCommand {

	@TargetAggregateIdentifier
	private String productSpecId;
	private final List<ProductSpecificationRelationship> productSpecificationRelationships;
	private final List<PolicyRuleRef> policyRuleRef;

	public ProductSpecRelCommand(String productSpecId, List<ProductSpecificationRelationship> serviceSpecRelationships,
			List<PolicyRuleRef> policyRuleRef) {
		this.productSpecificationRelationships = serviceSpecRelationships;
		this.productSpecId = productSpecId;
		this.policyRuleRef = policyRuleRef;
	}

	public List<ProductSpecificationRelationship> getProductSpecificationRelationships() {
		return productSpecificationRelationships;
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<PolicyRuleRef> getPolicyRuleRef() {
		return policyRuleRef;
	}

	public void setProductSpecId(String productSpecId) {
		this.productSpecId = productSpecId;
	}

	@Override
	public String toString() {
		return "ProductSpecRelCommand [productSpecId=" + productSpecId + ", productSpecificationRelationships="
				+ productSpecificationRelationships + ", policyRuleRef=" + policyRuleRef + "]";
	}

}
