// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;

/**
 * This class ProductOfferingPriceAlterationIdentityDataCommand is a command
 * which gathers input about the rprice altered value for product offering price
 * from the user.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ModifyProductOfferingPriceAlterationIdentityDataCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final DefineProductOfferingPriceAlterationIdentityData identityData;
	private final DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod;

	public ModifyProductOfferingPriceAlterationIdentityDataCommand(String productOfferingId,
			DefineProductOfferingPriceAlterationIdentityData identityData,
			DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod) {
		this.identityData = identityData;
		this.productOfferingId = productOfferingId;
		this.definePOPStatusValidityPeriod = definePOPStatusValidityPeriod;
	}

	public DefineProductOfferingPriceAlterationIdentityData getIdentityData() {
		return identityData;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public DefinePOPStatusValidityPeriod getDefinePOPStatusValidityPeriod() {
		return definePOPStatusValidityPeriod;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingPriceAlterationIdentityDataCommand [productOfferingId=" + productOfferingId
				+ ", identityData=" + identityData + ", definePOPStatusValidityPeriod=" + definePOPStatusValidityPeriod
				+ "]";
	}

}
