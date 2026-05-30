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

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent the initiation of product offering price
 * creation.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceModificationInitiatedEvent implements ProductOfferingPriceEvent {

	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final ProductOfferingPrice productOfferingPrice;
	private final OffsetDateTime lastUpdate;
	private final ProductOfferingPriceLifecycle lifecycle;

	private ProductOfferingPriceModificationInitiatedEvent() {
		this.productOfferingPriceId = null;
		this.productOfferingPrice = new ProductOfferingPrice();

		this.lastUpdate = null;
		this.lifecycle = null;
	}

	public ProductOfferingPriceModificationInitiatedEvent(String popID, ProductOfferingPrice pop, OffsetDateTime date,
			ProductOfferingPriceLifecycle lifecycleStatus) {
		this.productOfferingPriceId = popID;
		this.productOfferingPrice = pop;
		this.lastUpdate = date;
		this.lifecycle = lifecycleStatus;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceModificationInitiatedEvent{" + "productOfferingPriceId='" + productOfferingPriceId
				+ '\'' + ", productOfferingPriceType=" + productOfferingPrice + ", lastUpdate=" + lastUpdate
				+ ", lifecycle=" + lifecycle + '}';
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public ProductOfferingPrice getProductOfferingPrice() {
		return productOfferingPrice;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public ProductOfferingPriceLifecycle getLifecycle() {
		return lifecycle;
	}

}
