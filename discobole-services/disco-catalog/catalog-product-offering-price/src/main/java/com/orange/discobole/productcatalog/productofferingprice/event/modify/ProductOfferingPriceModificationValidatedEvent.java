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
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.productofferingprice.command.productofferingprice.modify.ProductOfferingPriceModificationValidatedCommand}
 * is triggered.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
public class ProductOfferingPriceModificationValidatedEvent implements ProductOfferingPriceEvent {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final ProductOfferingPriceLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;
	private final ProductOfferingPrice productOfferingPrice;
	private final String version;

	public ProductOfferingPriceModificationValidatedEvent() {
		productOfferingPriceId = null;
		lifecycleStatus = null;
		lastUpdate = null;
		productOfferingPrice = null;
		version = null;
	}

	public ProductOfferingPriceModificationValidatedEvent(String productOfferingPriceId,
			ProductOfferingPrice productOfferingPrice, ProductOfferingPriceLifecycle lifecycleStatus,
			OffsetDateTime lastUpdate, String version) {
		this.productOfferingPriceId = productOfferingPriceId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
		this.productOfferingPrice = productOfferingPrice;
		this.version = version;

	}

	@Override
	public String toString() {
		return "ProductOfferingPriceModificationValidatedEvent [productOfferingPriceId=" + productOfferingPriceId
				+ "productOfferingPrice" + productOfferingPrice + ", lifecycleStatus=" + lifecycleStatus
				+ ", lastUpdate=" + lastUpdate + ", version" + version + "]";
	}

	public ProductOfferingPrice getProductOfferingPrice() {
		return productOfferingPrice;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public ProductOfferingPriceLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
	
	public String getVersion() {
		return version;
	}

}
