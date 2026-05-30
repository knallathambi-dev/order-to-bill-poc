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
import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Money;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ChargeCycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PriceType;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProrationType;

public class ProductOfferingPriceChargeIdentityDataDescribedEvent implements ProductOfferingPriceEvent {

	private final String productOfferingPriceId;
	private final String name;
	private final String description;
	private final PriceType priceType;
	private final ProrationType prorationType;
	private final Boolean immediatePayment;
	private final Integer recurringChargePeriodLength;
	private final String recurringChargePeriodType;
	private final Money price;
	private final Quantity unitOfMeasure;
	private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;
	private final String href;
	private final ChargeCycle chargeCycle;

	public ProductOfferingPriceChargeIdentityDataDescribedEvent() {
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
		this.href = null;
		this.chargeCycle = null;
		this.prorationType = null;
	}

	public ProductOfferingPriceChargeIdentityDataDescribedEvent(String productOfferingPriceId, String name,
			String description, PriceType priceType, Boolean immediatePayment, Integer recurringChargePeriodLength,
			String recurringChargePeriodType, Money price, Quantity unitOfMeasure,
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships, ChargeCycle chargeCycle,
			ProrationType prorationType, ProductOfferingPriceLifecycle lifecycle, TimePeriod validFor,
			OffsetDateTime lastUpdate, String href) {

		this.productOfferingPriceId = productOfferingPriceId;
		this.name = name;
		this.description = description;
		this.priceType = priceType;
		this.immediatePayment = immediatePayment;
		this.recurringChargePeriodLength = recurringChargePeriodLength;
		this.recurringChargePeriodType = recurringChargePeriodType;
		this.price = price;
		this.prorationType = prorationType;
		this.unitOfMeasure = unitOfMeasure;
		this.productOfferingPriceRelationships = productOfferingPriceRelationships;
		this.lifecycle = lifecycle;
		this.validFor = validFor;
		this.lastUpdate = lastUpdate;
		this.href = href;
		this.chargeCycle = chargeCycle;
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

	public String getHref() {
		return href;
	}

	public ChargeCycle getChargeCycle() {
		return chargeCycle;
	}

	public ProrationType getProrationType() {
		return prorationType;
	}

	@Override
	public String toString() {
		return "ProductOfferingPriceChargeIdentityDataDescribedEvent [productOfferingPriceId=" + productOfferingPriceId
				+ ", name=" + name + ", description=" + description + ", priceType=" + priceType + ", prorationType="
				+ prorationType + ", immediatePayment=" + immediatePayment + ", recurringChargePeriodLength="
				+ recurringChargePeriodLength + ", recurringChargePeriodType=" + recurringChargePeriodType + ", price="
				+ price + ", unitOfMeasure=" + unitOfMeasure + ", productOfferingPriceRelationships="
				+ productOfferingPriceRelationships + ", lifecycle=" + lifecycle + ", validFor=" + validFor
				+ ", lastUpdate=" + lastUpdate + ", href=" + href + ", chargeCycle=" + chargeCycle + "]";
	}

}
