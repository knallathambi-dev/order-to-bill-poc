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
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;

/**
 * ProductOfferingRelationshipCommand to update ProductOffering relationship.
 * Map of product offering id and its dependency.
 *
 * @author Vivek Singh
 * @since 1.0
 */
public class ProductOfferingRelationshipCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ProductOfferingRelationship> productOfferingRelationships;


	public ProductOfferingRelationshipCommand(String productOfferingId,
			List<ProductOfferingRelationship> productOfferingRelationships) {
		this.productOfferingId = productOfferingId;
		this.productOfferingRelationships = productOfferingRelationships;
	}

	@Override
	public String toString() {
		return "ProductOfferingRelationshipCommand [productOfferingId=" + productOfferingId
				+ ", productOfferingRelationships=" + productOfferingRelationships + "]";
	}

	public List<ProductOfferingRelationship> getProductOfferingRelationships() {
		return productOfferingRelationships;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

}
