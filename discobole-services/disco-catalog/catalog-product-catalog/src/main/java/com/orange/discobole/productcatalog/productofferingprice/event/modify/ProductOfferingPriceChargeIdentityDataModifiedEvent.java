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

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Money;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PriceType;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent the price charge data define for product offering
 * price.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceChargeIdentityDataModifiedEvent implements ProductOfferingPriceEvent {

	private final String productOfferingPriceId;
	private final String name;
	private final String description;
	private final PriceType priceType;
	private final Boolean immediatePayment;
	private final Integer recurringChargePeriodLength;
	private final String recurringChargePeriodType;
	private final Money price;
	private final Quantity unitOfMeasure;
	private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;

	public ProductOfferingPriceChargeIdentityDataModifiedEvent() {
		this.productOfferingPriceId = null;
		this.name = null;
		this.description = null;
		this.priceType = null;
		this.immediatePayment = null;
		this.recurringChargePeriodLength = null;
		this.recurringChargePeriodType = null;
		this.price = null;
		this.unitOfMeasure = null;
		this.productOfferingPriceRelationships = null;
		this.lifecycle = null;
		this.validFor = null;
		this.lastUpdate = null;
	}

	public ProductOfferingPriceChargeIdentityDataModifiedEvent(String productOfferingPriceId, String name,
			String description, PriceType priceType, Boolean immediatePayment, Integer recurringChargePeriodLength,
			String recurringChargePeriodType, Money price, Quantity unitOfMeasure,
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships,
			ProductOfferingPriceLifecycle lifecycle, TimePeriod validFor, OffsetDateTime lastUpdate) {

		this.productOfferingPriceId = productOfferingPriceId;
		this.name = name;
		this.description = description;
		this.priceType = priceType;
		this.immediatePayment = immediatePayment;
		this.recurringChargePeriodLength = recurringChargePeriodLength;
		this.recurringChargePeriodType = recurringChargePeriodType;
		this.price = price;
		this.unitOfMeasure = unitOfMeasure;
		this.productOfferingPriceRelationships = productOfferingPriceRelationships;
		this.lifecycle = lifecycle;
		this.validFor = validFor;
		this.lastUpdate = lastUpdate;
	}

	public String getProductOfferingPriceId() {
		return productOfferingPriceId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public Integer getRecurringChargePeriodLength() {
		return recurringChargePeriodLength;
	}

	public String getRecurringChargePeriodType() {
		return recurringChargePeriodType;
	}

	public Money getPrice() {
		return price;
	}

	public Quantity getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public Boolean getImmediatePayment() {
		return immediatePayment;
	}

	public List<ProductOfferingPriceRelationship> getProductOfferingPriceRelationships() {
		return productOfferingPriceRelationships;
	}

	public ProductOfferingPriceLifecycle getLifecycle() {
		return lifecycle;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceChargeIdentityDataModifiedEvent [productOfferingPriceId=" + productOfferingPriceId
				+ ", name=" + name + ", description=" + description + ", priceType=" + priceType + ", immediatePayment="
				+ immediatePayment + ", recurringChargePeriodLength=" + recurringChargePeriodLength
				+ ", recurringChargePeriodType=" + recurringChargePeriodType + ", price=" + price + ", unitOfMeasure="
				+ unitOfMeasure + ", productOfferingPriceRelationships=" + productOfferingPriceRelationships
				+ ", lifecycle=" + lifecycle + ", validFor=" + validFor + ", lastUpdate=" + lastUpdate + "]";
	}

}
