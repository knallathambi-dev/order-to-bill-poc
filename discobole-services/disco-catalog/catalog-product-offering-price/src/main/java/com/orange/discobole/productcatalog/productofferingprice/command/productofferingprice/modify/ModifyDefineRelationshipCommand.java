// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
/**
 * This class DefineRelationshipCommand is a command which gathers
 * input about the relationship of POPC and POPA from the user.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ModifyDefineRelationshipCommand {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;
    private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;

    public ModifyDefineRelationshipCommand(String productOfferingId,List<ProductOfferingPriceRelationship> productOfferingPriceRelationships) {
        this.productOfferingPriceRelationships = productOfferingPriceRelationships;
		this.productOfferingId = productOfferingId;
    }

    @Override
    public String toString() {
        return "DefineRelationshipCommand{" +"productOfferingId="+productOfferingId+
                ", productOfferingPriceRelationships=" + productOfferingPriceRelationships +
                '}';
    }

    public String getProductOfferingId() {
		return productOfferingId;
	}
    
    public List<ProductOfferingPriceRelationship> getProductOfferingPriceRelationships() {
        return productOfferingPriceRelationships;
    }
}
