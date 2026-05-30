// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefinePOPStatusValidityPeriod;

/**
 * This class ProductOfferingPriceValidatedCommand is a command to validate the
 * creation of product offering price from the user.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public final class ProductOfferingPriceValidatedCommand {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod;
	
	public ProductOfferingPriceValidatedCommand(String productOfferingPriceId,
			DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod) {
		super();
		this.productOfferingPriceId = productOfferingPriceId;
		this.definePOPStatusValidityPeriod = definePOPStatusValidityPeriod;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public DefinePOPStatusValidityPeriod getDefinePOPStatusValidityPeriod() {
		return definePOPStatusValidityPeriod;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceValidatedCommand [productOfferingPriceId=" + productOfferingPriceId
				+ ", definePOPStatusValidityPeriod=" + definePOPStatusValidityPeriod + "]";
	}
	
	
}
