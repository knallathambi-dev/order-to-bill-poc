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

/**
 * This class ProductOfferingPriceValidatedCommand is a command to validate the
 * creation of product offering price from the user.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public final class ModifyStatusValidityPeriodCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod;

	public ModifyStatusValidityPeriodCommand(String productOfferingId,DefinePOPStatusValidityPeriod definePOPStatusValidityPeriod) {
		this.definePOPStatusValidityPeriod = definePOPStatusValidityPeriod;
		this.productOfferingId=productOfferingId;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceValidatedCommand{" + "productOfferingId=" + productOfferingId
				+ ", definePOPStatusValidityPeriod=" + definePOPStatusValidityPeriod + '}';
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}
	   
	public DefinePOPStatusValidityPeriod getDefinePOPStatusValidityPeriod() {
		return definePOPStatusValidityPeriod;
	}
}
