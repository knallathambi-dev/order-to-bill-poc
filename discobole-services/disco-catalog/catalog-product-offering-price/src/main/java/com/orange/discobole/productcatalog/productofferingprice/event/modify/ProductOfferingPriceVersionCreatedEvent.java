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

import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent version define for product offering price after
 * creation.
 * 
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class ProductOfferingPriceVersionCreatedEvent implements ProductOfferingPriceEvent {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final String version;
	private final OffsetDateTime lastUpdate;

	private ProductOfferingPriceVersionCreatedEvent() {
		this.productOfferingPriceId = null;
		this.version = null;
		this.lastUpdate = null;
	}

	public ProductOfferingPriceVersionCreatedEvent(String productOfferingPriceId, String version,
			OffsetDateTime lastUpdate) {
		this.productOfferingPriceId = productOfferingPriceId;
		this.version = version;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceVersionCreatedEvent{" + "productOfferingPriceId='" + productOfferingPriceId + '\''
				+ ", version='" + version + '\'' + ", lastUpdate=" + lastUpdate + '}';
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public String getVersion() {
		return version;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
}
