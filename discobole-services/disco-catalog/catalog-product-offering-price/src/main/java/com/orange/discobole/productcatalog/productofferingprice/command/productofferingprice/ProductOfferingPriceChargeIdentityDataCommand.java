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

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;

/**
 * This class ProductOfferingPriceChargeIdentityDataCommand is a command which
 * gathers input about the price charge for product offering price from the
 * user.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceChargeIdentityDataCommand {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final DefineProductOfferingPriceChargeIdentityData identityData;
	private final DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod;
	private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;

	public ProductOfferingPriceChargeIdentityDataCommand(String productOfferingPriceId,
			DefineProductOfferingPriceChargeIdentityData identityData,
			DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod,
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships) {
		super();
		this.productOfferingPriceId = productOfferingPriceId;
		this.identityData = identityData;
		this.definePOPStatusValidityPeriod = definePOPStatusValidityPeriod;
		this.productOfferingPriceRelationships = productOfferingPriceRelationships;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
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
		return "ProductOfferingPriceChargeIdentityDataCommand [productOfferingPriceId=" + productOfferingPriceId
				+ ", identityData=" + identityData + ", definePOPStatusValidityPeriod=" + definePOPStatusValidityPeriod
				+ ", productOfferingPriceRelationships=" + productOfferingPriceRelationships + "]";
	}

}