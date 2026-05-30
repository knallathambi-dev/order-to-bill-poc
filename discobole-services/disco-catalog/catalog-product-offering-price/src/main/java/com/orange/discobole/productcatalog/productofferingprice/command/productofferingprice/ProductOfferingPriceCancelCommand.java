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

/**
 * This class ProductOfferingPriceCancelCommand is a command to cancel the
 * ongoing process of creation of product offering price.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public final class ProductOfferingPriceCancelCommand {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;

	public ProductOfferingPriceCancelCommand(String productOfferingPriceId) {
		this.productOfferingPriceId = productOfferingPriceId;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceCancelCommand{" + "productOfferingPriceId='" + productOfferingPriceId + '\'' + '}';
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}
}
