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

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;

/**
 * This class ProductOfferingPriceChargeIdentityDataCommand is a command which
 * gathers input about the price charge for product offering price from the
 * user.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ModifyProductOfferingPriceChargeIdentityDataCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final DefineProductOfferingPriceChargeIdentityData identityData;
	private final DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod;
	private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;

	public ModifyProductOfferingPriceChargeIdentityDataCommand(String productOfferingId,
			DefineProductOfferingPriceChargeIdentityData identityData,
			DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod,
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships) {
		this.identityData = identityData;
		this.productOfferingId = productOfferingId;
		this.definePOPStatusValidityPeriod = definePOPStatusValidityPeriod;
		this.productOfferingPriceRelationships = productOfferingPriceRelationships;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public DefineProductOfferingPriceChargeIdentityData getIdentityData() {
		return identityData;
	}

	public DefinePOPStatusValidityPeriod getDefinePOPStatusValidityPeriod() {
		return definePOPStatusValidityPeriod;
	}

	public List<ProductOfferingPriceRelationship> getProductOfferingPriceRelationships() {
		return productOfferingPriceRelationships;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingPriceChargeIdentityDataCommand [productOfferingId=" + productOfferingId
				+ ", identityData=" + identityData + ", definePOPStatusValidityPeriod=" + definePOPStatusValidityPeriod
				+ ", productOfferingPriceRelationships=" + productOfferingPriceRelationships + "]";
	}

}