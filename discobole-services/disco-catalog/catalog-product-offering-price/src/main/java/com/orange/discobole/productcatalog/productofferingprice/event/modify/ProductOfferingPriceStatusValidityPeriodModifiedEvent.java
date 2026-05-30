// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event.modify;

import java.time.OffsetDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent the validation of product offering price.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceStatusValidityPeriodModifiedEvent implements ProductOfferingPriceEvent {

	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;

	private ProductOfferingPriceStatusValidityPeriodModifiedEvent() {

		this.productOfferingPriceId = null;
		this.lifecycle = null;
		this.validFor = null;
		this.lastUpdate = null;
	}

	public ProductOfferingPriceStatusValidityPeriodModifiedEvent(String productOfferingPriceId, ProductOfferingPriceLifecycle lifecycle,
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
