// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.event;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.OffsetDateTime;
import java.util.List;

public class ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent implements ProductOfferingPriceEvent {


	private final String productOfferingPriceId;
	private final String name;
	private final String description;
	private final String partner;
	private final String externalId;
	private final Float downPayment;
	private  final Float interestRate;
	private final ApplicationDuration applicationDuration;
	private final Money price;
	private final List<ProductOfferingPriceRelationship> productOfferingPriceRelationships;
	private final ProductOfferingPriceLifecycle lifecycle;
	private final TimePeriod validFor;
	private final OffsetDateTime lastUpdate;
	private final String href;


	public ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent() {
		this.productOfferingPriceId = null;
		this.name = null;
		this.description = null;
		this.partner = null;
		this.externalId = null;
		this.downPayment = null;
		this.interestRate = null;
		this.applicationDuration = null;
		this.price = null;
		this.productOfferingPriceRelationships = null;
		this.lifecycle = null;
		this.validFor = null;
		this.lastUpdate = null;
		this.href = null;
	}



	public ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent(String productOfferingPriceId, String name, String description, String partner, String externalId, Float downPayment, Float interestRate, ApplicationDuration applicationDuration, Money price, List<ProductOfferingPriceRelationship> productOfferingPriceRelationships, ProductOfferingPriceLifecycle lifecycle, TimePeriod validFor, OffsetDateTime lastUpdate, String href) {
		this.productOfferingPriceId = productOfferingPriceId;
		this.name = name;
		this.description = description;
		this.partner = partner;
		this.externalId = externalId;
		this.downPayment = downPayment;
		this.interestRate = interestRate;
		this.applicationDuration = applicationDuration;
		this.price = price;
		this.productOfferingPriceRelationships = productOfferingPriceRelationships;
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



	public Money getPrice() {
		return price;
	}

	public List<ProductOfferingPriceRelationship> getProductOfferingPriceRelationships() {
		return productOfferingPriceRelationships;
	}


	public String getPartner() {
		return partner;
	}

	public String getExternalId() {
		return externalId;
	}

	public Float getDownPayment() {
		return downPayment;
	}

	public Float getInterestRate() {
		return interestRate;
	}

	public ApplicationDuration getApplicationDuration() {
		return applicationDuration;
	}



}
