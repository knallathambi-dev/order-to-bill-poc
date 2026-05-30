// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.OffsetDateTime;

/**
 * Event class which represent the identity data define for product offering
 * price.
 * 
 * @author Jagriti Pahwa
 * @since 1.0
 *
 */
public class ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent implements ProductOfferingPriceEvent {
	@TargetAggregateIdentifier
	private final String productOfferingPriceId;
	private final String name;
	private final String description;
	private final Float percentage;
	private final Money price;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;
	private final String href;


	private ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent() {
		this.productOfferingPriceId = null;
		this.name = null;
		this.description = null;
		this.percentage = null;
		this.price = null;
		this.lifecycle = null;
		this.validFor = null;
		this.lastUpdate = null;
		this.href = null;
	}

	public ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent(String productOfferingPriceId, String name,
                                                                       String description, Float percentage, Money price,
                                                                       ProductOfferingPriceLifecycle lifecycle, TimePeriod validFor, OffsetDateTime lastUpdate, String href) {

		this.productOfferingPriceId = productOfferingPriceId;
		this.name = name;
		this.description = description;
		this.percentage = percentage;
		this.price = price;
		this.lifecycle = lifecycle;
		this.validFor = validFor;
		this.lastUpdate = lastUpdate;
		this.href = href;
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

	public Float getPercentage() {
		return percentage;
	}

	public Money getPrice() {
		return price;
	}



	@Override
	public String toString() {
		return "ProductOfferingPriceAlterationIdentityDataDescribedEvent [productOfferingPriceId="
				+ productOfferingPriceId + ", name=" + name + ", description=" + description + ", percentage=" + percentage + ", price=" + price + "" +
				", lifecycle=" + lifecycle + ", validFor=" + validFor + ", lastUpdate=" + lastUpdate
				+ ", href=" + href + "]";
	}

}
