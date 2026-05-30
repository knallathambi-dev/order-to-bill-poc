// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import java.util.List;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;

/**
 * Event class which represent the list of invalid product offering price
 * association.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class InvalidPOPAHavingSamePriorityEvent implements ProductOfferingPriceEvent {
	private List<ProductOfferingPrice> productOfferingPrices;
	private String message;

	public InvalidPOPAHavingSamePriorityEvent() {
		this.productOfferingPrices = null;
		this.message = null;
	}

	public InvalidPOPAHavingSamePriorityEvent(List<ProductOfferingPrice> productOfferingPrices, String message) {
		this.productOfferingPrices = productOfferingPrices;
		this.message = message;
	}

	@Override
	public String toString() {
		return "InvalidMorePOPAHaveSamePriorityEvent{" + "productOfferingPrices=" + productOfferingPrices
				+ ", message='" + message + '\'' + '}';
	}

	public List<ProductOfferingPrice> getProductOfferingPrices() {
		return productOfferingPrices;
	}

	public void setProductOfferingPrices(List<ProductOfferingPrice> productOfferingPrices) {
		this.productOfferingPrices = productOfferingPrices;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
