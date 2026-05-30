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

public class ModifyProductSpecRelCommand {

	@TargetAggregateIdentifier
	private final String productSpecId;
	private final List<ProductSpecificationRelationship> productSpecificationRelationships;
	private final List<PolicyRuleRef> policyRuleRef;

	public ModifyProductSpecRelCommand(String productSpecId,
			List<ProductSpecificationRelationship> serviceSpecRelationships, List<PolicyRuleRef> policyRuleRef) {
		productSpecificationRelationships = serviceSpecRelationships;
		this.productSpecId = productSpecId;
		this.policyRuleRef = policyRuleRef;
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public List<ProductSpecificationRelationship> getProductSpecificationRelationships() {
		return productSpecificationRelationships;
	}

	public List<PolicyRuleRef> getPolicyRuleRef() {
		return policyRuleRef;
	}

	@Override
	public String toString() {
		return "ModifyProductSpecRelCommand [productSpecId=" + productSpecId + ", productSpecificationRelationships="
				+ productSpecificationRelationships + ", policyRuleRef=" + policyRuleRef + "]";
	}
}
