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

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;

/**
 * This class InitiateProductOfferingPriceCommand is a command which gathers
 * input to initiate the creation of product offering price creation from the
 * user.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public final class InitiateProductOfferingPriceModificationCommand {
	
	@TargetAggregateIdentifier
	private String productOfferingPriceId;
	private final ProductOfferingPriceType productOfferingPriceType;

	public InitiateProductOfferingPriceModificationCommand(String productOfferingPriceId,ProductOfferingPriceType productOfferingPriceType) {
		this.productOfferingPriceType = productOfferingPriceType;
		this.productOfferingPriceId=productOfferingPriceId;
	}

	@Override
	public String toString() {
		return "InitiateProductOfferingPriceCommand{" + "productOfferingPriceId=" + productOfferingPriceId
				+ ", productOfferingPriceType='" + productOfferingPriceType + '\'' + '}';
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public ProductOfferingPriceType getProductOfferingPriceType() {
		return productOfferingPriceType;
	}
}
