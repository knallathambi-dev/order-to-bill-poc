// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;


import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;

public class ProductOfferingPriceCancelledEvent implements ProductOfferingPriceEvent {

	private final String productOfferingPriceId;
    private final ProductOfferingPrice productOfferingPrice;

    private ProductOfferingPriceCancelledEvent() {
        this.productOfferingPrice = null;
        this.productOfferingPriceId=null;
    }

    public ProductOfferingPriceCancelledEvent(String productOfferingPriceId,ProductOfferingPrice productOfferingPrice) {
        this.productOfferingPrice = productOfferingPrice;
		this.productOfferingPriceId = productOfferingPriceId;
    }

	@Override
	public String toString() {
		return "ProductOfferingPriceCancelledEvent{" + "productOfferingPriceId=" + productOfferingPriceId
				+ ", productOfferingPrice=" + productOfferingPrice + '}';
	}

    public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}
    
    public ProductOfferingPrice getProductOfferingPrice() {
        return productOfferingPrice;
    }
}
