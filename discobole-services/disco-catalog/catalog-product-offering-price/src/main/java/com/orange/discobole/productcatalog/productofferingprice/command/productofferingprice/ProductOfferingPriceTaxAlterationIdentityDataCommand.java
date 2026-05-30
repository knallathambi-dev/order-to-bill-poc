// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceTaxAlterationIdentityData;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * This class ProductOfferingPriceAlterationIdentityDataCommand is a command
 * which gathers input about the rprice altered value for product offering price
 * from the user.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceTaxAlterationIdentityDataCommand {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final DefineProductOfferingPriceTaxAlterationIdentityData identityData;
	private final DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod;

	public ProductOfferingPriceTaxAlterationIdentityDataCommand(String productOfferingPriceId,
                                                                DefineProductOfferingPriceTaxAlterationIdentityData identityData,
                                                                DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod) {
		super();
		this.productOfferingPriceId = productOfferingPriceId;
		this.identityData = identityData;
		this.definePOPStatusValidityPeriod = definePOPStatusValidityPeriod;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public DefineProductOfferingPriceTaxAlterationIdentityData getIdentityData() {
		return identityData;
	}

	public DefinePOPStatusValidityPeriod getDefinePOPStatusValidityPeriod() {
		return definePOPStatusValidityPeriod;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceTaxAlterationIdentityDataCommand [productOfferingPriceId=" + productOfferingPriceId
				+ ", identityData=" + identityData + ", definePOPStatusValidityPeriod=" + definePOPStatusValidityPeriod
				+ "]";
	}

}
