// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;

/**
 * This class ProductOfferingPriceTypeCommand is a command which gathers input
 * about the product offering price type from the user.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceTypeModificationCommand {

	private final PriceType priceType;

	public ProductOfferingPriceTypeModificationCommand(PriceType priceType) {
		this.priceType = priceType;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceTypeCommand{" + "priceType=" + priceType + '}';
	}

	public PriceType getPriceType() {
		return priceType;
	}
}
