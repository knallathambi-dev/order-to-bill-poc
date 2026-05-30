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
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent the relationship date define for product offering
 * price.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceRelationshipModifiedEvent implements ProductOfferingPriceEvent {

	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;
	private final OffsetDateTime lastUpdate;

	public ProductOfferingPriceRelationshipModifiedEvent() {
		this.productOfferingPriceId = null;
		this.productOfferingPriceRelationships = null;
		this.lastUpdate = null;
	}

	public ProductOfferingPriceRelationshipModifiedEvent(String productOfferingPriceId,
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships, OffsetDateTime lastUpdate) {

		this.productOfferingPriceId = productOfferingPriceId;
		this.productOfferingPriceRelationships = productOfferingPriceRelationships;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceRelationshipDefinedEvent [productOfferingPriceId=" + productOfferingPriceId
				+ ", productOfferingPriceRelationships=" + productOfferingPriceRelationships + ", lastUpdate="
				+ lastUpdate + "]";
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public List<ProductOfferingPriceRelationship> getProductOfferingPriceRelationships() {
		return productOfferingPriceRelationships;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
