// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;

/**
 * Event class which represent the cancellation of product offering price.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceCancelledEvent implements ProductOfferingPriceEvent {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final ProductOfferingPrice productOfferingPrice;

	private ProductOfferingPriceCancelledEvent() {
		this.productOfferingPriceId=null;
		this.productOfferingPrice = null;
	}

	public ProductOfferingPriceCancelledEvent(String productOfferingPriceId,ProductOfferingPrice productOfferingPrice) {
		this.productOfferingPriceId=productOfferingPriceId;
		this.productOfferingPrice = productOfferingPrice;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public ProductOfferingPrice getProductOfferingPrice() {
		return productOfferingPrice;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceCancelledEvent [productOfferingPriceId=" + productOfferingPriceId
				+ ", productOfferingPrice=" + productOfferingPrice + "]";
	}


}
