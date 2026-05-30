// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event.modify;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent the completion of creation of product offering
 * price.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceModificationCompletedEvent implements ProductOfferingPriceEvent {
	private final ProductOfferingPrice productOfferingPrice;

	private ProductOfferingPriceModificationCompletedEvent() {
		this.productOfferingPrice = null;
	}

	public ProductOfferingPriceModificationCompletedEvent(ProductOfferingPrice productOfferingPrice) {
		this.productOfferingPrice = productOfferingPrice;
	}

	public ProductOfferingPrice getProductOfferingPrice() {
		return productOfferingPrice;
	}
}
