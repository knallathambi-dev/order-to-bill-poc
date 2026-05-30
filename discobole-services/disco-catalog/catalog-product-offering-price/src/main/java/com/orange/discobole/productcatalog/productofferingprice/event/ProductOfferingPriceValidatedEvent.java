// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import java.time.OffsetDateTime;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;

/**
 * Event class which represent the validation of product offering price.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceValidatedEvent implements ProductOfferingPriceEvent {

	private final String productOfferingPriceId;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;

	private ProductOfferingPriceValidatedEvent() {

		this.productOfferingPriceId = null;
		this.lifecycle = null;
		this.validFor = null;
		this.lastUpdate = null;
	}

	public ProductOfferingPriceValidatedEvent(String productOfferingPriceId, ProductOfferingPriceLifecycle lifecycle,
			TimePeriod validFor, OffsetDateTime lastUpdate) {

		this.productOfferingPriceId = productOfferingPriceId;
		this.lifecycle = lifecycle;
		this.validFor = validFor;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceValidatedEvent [productOfferingPriceId=" + productOfferingPriceId + ", lifecycle="
				+ lifecycle + ", validFor=" + validFor + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public ProductOfferingPriceLifecycle getLifecycle() {
		return lifecycle;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
