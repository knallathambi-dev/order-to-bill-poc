// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent the cancellation of product offering price.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceModificationCancelledEvent implements ProductOfferingPriceEvent {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final ProductOfferingPrice productOfferingPrice;

	private ProductOfferingPriceModificationCancelledEvent() {
		this.productOfferingPrice = null;
		this.productOfferingPriceId=null;
	}

	public ProductOfferingPriceModificationCancelledEvent(String productOfferingPriceId,ProductOfferingPrice productOfferingPrice) {
		this.productOfferingPrice = productOfferingPrice;
		this.productOfferingPriceId = productOfferingPriceId;
	}
	
	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceCancelledEvent{" + "productOfferingPriceId=" + productOfferingPriceId
				+ "productOfferingPrice=" + productOfferingPrice + '}';
	}

	public ProductOfferingPrice getProductOfferingPrice() {
		return productOfferingPrice;
	}
}
