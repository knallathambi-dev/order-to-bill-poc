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

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ApplicationDuration;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Money;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PriceType;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProrationType;

public class ProductOfferingPriceAlterationIdentityDataDescribedEvent implements ProductOfferingPriceEvent {
	private final String productOfferingPriceId;
	private final String name;
	private final String description;
	private final PriceType priceType;
	private final ProrationType prorationType;
	private final ApplicationDuration applicationDuration;
	private final Integer priority;
	private final Float percentage;
	private final Money price;
	private final Quantity unitOfMeasure;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;
	private final String href;
    private final Integer applicationOffset;

	private ProductOfferingPriceAlterationIdentityDataDescribedEvent() {
		this.productOfferingPriceId = null;
		this.name = null;
		this.description = null;
		this.priceType = null;
		this.prorationType = null;
		this.applicationDuration = null;
		this.priority = null;
		this.percentage = null;
		this.price = null;
		this.unitOfMeasure = null;
		this.lifecycle = null;
		this.validFor = null;
		this.lastUpdate = null;
		this.href = null;
        this.applicationOffset = null;
	}

	public ProductOfferingPriceAlterationIdentityDataDescribedEvent(String productOfferingPriceId, String name,
			String description, PriceType priceType, ApplicationDuration applicationDuration, Integer priority,
			ProrationType prorationType, Float percentage, Money price, Quantity unitOfMeasure,
			ProductOfferingPriceLifecycle lifecycle, TimePeriod validFor, OffsetDateTime lastUpdate, String href, Integer applicationOffset) {

		this.productOfferingPriceId = productOfferingPriceId;
		this.name = name;
		this.description = description;
		this.priceType = priceType;
		this.applicationDuration = applicationDuration;
		this.priority = priority;
		this.percentage = percentage;
		this.price = price;
		this.unitOfMeasure = unitOfMeasure;
		this.lifecycle = lifecycle;
		this.validFor = validFor;
		this.lastUpdate = lastUpdate;
		this.href = href;
		this.prorationType = prorationType;
        this.applicationOffset = applicationOffset;
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

	public ApplicationDuration getApplicationDuration() {
		return applicationDuration;
	}

	public Integer getPriority() {
		return priority;
	}

	public Float getPercentage() {
		return percentage;
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

	public ProductOfferingPriceLifecycle getLifecycle() {
		return lifecycle;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public String getHref() {
		return href;
	}

	public ProrationType getProrationType() {
		return prorationType;
	}

    public Integer getApplicationOffset() {
        return applicationOffset;
    }

    @Override
	public String toString() {
		return "ProductOfferingPriceAlterationIdentityDataDescribedEvent [productOfferingPriceId="
				+ productOfferingPriceId + ", name=" + name + ", description=" + description + ", priceType="
				+ priceType + ", prorationType=" + prorationType + ", applicationDuration=" + applicationDuration
				+ ", priority=" + priority + ", percentage=" + percentage + ", price=" + price + ", unitOfMeasure="
				+ unitOfMeasure + ", lifecycle=" + lifecycle + ", validFor=" + validFor + ", lastUpdate=" + lastUpdate
				+ ", href=" + href + ", applicationOffset=" + applicationOffset + "]";
	}

}
