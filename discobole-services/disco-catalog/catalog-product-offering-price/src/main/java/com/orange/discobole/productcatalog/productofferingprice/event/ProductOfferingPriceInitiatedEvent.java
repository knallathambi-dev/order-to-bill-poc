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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;

/**
 * Event class which represent the initiation of product offering price
 * creation.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceInitiatedEvent implements ProductOfferingPriceEvent {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final ProductOfferingPriceType productOfferingPriceType;
	private final OffsetDateTime lastUpdate;
	private final ProductOfferingPriceLifecycle lifecycle;

	public ProductOfferingPriceInitiatedEvent() {
		this.productOfferingPriceId = null;
		this.productOfferingPriceType = null;
		this.lastUpdate = null;
		this.lifecycle = null;
	}

	public ProductOfferingPriceInitiatedEvent(String productOfferingPriceId,
			ProductOfferingPriceType productOfferingPriceType, OffsetDateTime lastUpdate,
			ProductOfferingPriceLifecycle lifecycle) {
		this.productOfferingPriceId = productOfferingPriceId;
		this.productOfferingPriceType = productOfferingPriceType;
		this.lastUpdate = lastUpdate;
		this.lifecycle = lifecycle;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceInitiatedEvent{" + "productOfferingPriceId='" + productOfferingPriceId + '\''
				+ ", productOfferingPriceType=" + productOfferingPriceType + ", lastUpdate=" + lastUpdate
				+ ", lifecycle=" + lifecycle + '}';
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public ProductOfferingPriceType getProductOfferingPriceType() {
		return productOfferingPriceType;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public ProductOfferingPriceLifecycle getLifecycle() {
		return lifecycle;
	}
}
