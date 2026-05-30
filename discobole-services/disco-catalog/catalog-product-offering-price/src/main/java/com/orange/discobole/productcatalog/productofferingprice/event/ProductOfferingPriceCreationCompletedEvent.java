// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;

/**
 * Event class which represent the completion of creation of product offering
 * price.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceCreationCompletedEvent implements ProductOfferingPriceEvent {
	private final String productOfferingPriceId;
	private final ProductOfferingPrice productOfferingPrice;
	
	public ProductOfferingPriceCreationCompletedEvent() {
		this.productOfferingPriceId=null; 
		this.productOfferingPrice = null;
	}

	public ProductOfferingPriceCreationCompletedEvent(String productOfferingPriceId,ProductOfferingPrice productOfferingPrice) {
		this.productOfferingPriceId=productOfferingPriceId;
		this.productOfferingPrice = productOfferingPrice;
	}

	public ProductOfferingPrice getProductOfferingPrice() {
		return productOfferingPrice;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}
	
}
