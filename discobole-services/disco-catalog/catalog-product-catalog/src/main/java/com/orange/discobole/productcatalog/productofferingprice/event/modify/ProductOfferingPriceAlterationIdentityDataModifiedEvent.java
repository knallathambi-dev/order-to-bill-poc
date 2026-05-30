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

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ApplicationDuration;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Money;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PriceType;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.event.ProductOfferingPriceEvent;

/**
 * Event class which represent the identity data modified for product offering
 * price.
 * 
 * @author Rajan Chauhan
 * @since 1.0
 *
 */
public class ProductOfferingPriceAlterationIdentityDataModifiedEvent implements ProductOfferingPriceEvent {
	private final String productOfferingPriceId;
	private final String name;
	private final String description;
	private final PriceType priceType;
	private final ApplicationDuration applicationDuration;
	private final Integer priority;
	private final Float percentage;
	private final Money price;
	private final Quantity unitOfMeasure;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;
    private final Integer applicationOffset;

	private ProductOfferingPriceAlterationIdentityDataModifiedEvent() {
		this.productOfferingPriceId = null;
		this.name = null;
		this.description = null;
		this.priceType = null;
		this.applicationDuration = null;
		this.priority = null;
		this.percentage = null;
		this.price = null;
		this.unitOfMeasure = null;
		this.lifecycle = null;
		this.validFor = null;
		this.lastUpdate = null;
        this.applicationOffset = null;
	}

	public ProductOfferingPriceAlterationIdentityDataModifiedEvent(String productOfferingPriceId, String name,
			String description, PriceType priceType, ApplicationDuration applicationDuration, Integer priority, Float percentage, Money price,
			Quantity unitOfMeasure, ProductOfferingPriceLifecycle lifecycle, TimePeriod validFor,
			OffsetDateTime lastUpdate,Integer applicationOffset) {

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

    public Integer getApplicationOffset() {
        return applicationOffset;
    }

    @Override
	public String toString() {
		return "ProductOfferingPriceAlterationIdentityDataModifiedEvent [productOfferingPriceId="
				+ productOfferingPriceId + ", name=" + name + ", description=" + description + ", priceType="
				+ priceType + ", applicationDuration=" + applicationDuration + ", priority=" + priority + ", percentage=" + percentage
				+ ", price=" + price + ", unitOfMeasure=" + unitOfMeasure + ", lifecycle=" + lifecycle + ", validFor="
				+ validFor + ", lastUpdate=" + lastUpdate + ", applicationOffset=" + applicationOffset + "]";
	}

}
