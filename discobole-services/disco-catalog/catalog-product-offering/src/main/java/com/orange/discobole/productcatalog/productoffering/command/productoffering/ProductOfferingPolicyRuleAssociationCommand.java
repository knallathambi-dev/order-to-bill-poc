// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.PolicyRuleRef;

/**
 * ProductOfferingPolicyRuleAssociationCommand to update ProductOffering PolicyRuleAssociation.
 * Map of product offering id and its dependency.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
public class ProductOfferingPolicyRuleAssociationCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<PolicyRuleRef> policyRuleRef;

	public ProductOfferingPolicyRuleAssociationCommand(String productOfferingId,
			List<PolicyRuleRef> policyRuleRef) {
		this.productOfferingId = productOfferingId;
		this.policyRuleRef = policyRuleRef;
	}

	@Override
	public String toString() {
		return "ProductOfferingRelationshipCommand [productOfferingId=" + productOfferingId
				+  ", policyRuleRef=" + policyRuleRef
				+ "]";
	}


	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<PolicyRuleRef> getPolicyRuleRef() {
		return policyRuleRef;
	}

}
