// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
/**
 * This class DefineRelationshipCommand is a command which gathers
 * input about the relationship of POPC and POPA from the user.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class DefineRelationshipCommand {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
    private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;
    
	public DefineRelationshipCommand(String productOfferingPriceId,
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships) {
		super();
		this.productOfferingPriceId = productOfferingPriceId;
		this.productOfferingPriceRelationships = productOfferingPriceRelationships;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public List<ProductOfferingPriceRelationship> getProductOfferingPriceRelationships() {
		return productOfferingPriceRelationships;
	}

	@Override
	public String toString() {
		return "DefineRelationshipCommand [productOfferingPriceId=" + productOfferingPriceId
				+ ", productOfferingPriceRelationships=" + productOfferingPriceRelationships + "]";
	}

   
}
