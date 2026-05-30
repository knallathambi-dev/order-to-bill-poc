// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;

public class ModifyProductOfferingRelationshipCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductOfferingRelationship> productOfferingRelationships;

	/**
	 * @param productOfferingRelationships
	 */
	public ModifyProductOfferingRelationshipCommand(String productOfferingId,
			List<ProductOfferingRelationship> productOfferingRelationships) {
		super();
		this.productOfferingId = productOfferingId;
		this.productOfferingRelationships = productOfferingRelationships;
	}

	public List<ProductOfferingRelationship> getProductOfferingRelationships() {
		return productOfferingRelationships;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingRelationshipCommand [productOfferingId=" + productOfferingId
				+ ", productOfferingRelationships=" + productOfferingRelationships + ", policyRuleRef=" + "]";
	}

}
