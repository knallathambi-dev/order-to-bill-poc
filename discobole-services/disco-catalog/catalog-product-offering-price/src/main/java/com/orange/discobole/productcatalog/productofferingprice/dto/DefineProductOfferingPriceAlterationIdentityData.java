// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;

public class DefineProductOfferingPriceAlterationIdentityData {
	private String name;
	private String description;
	private PriceType priceType;
	private ProrationType prorationType;
	private ApplicationDuration applicationDuration;
	private Integer priority;
	private Float percentage;
	private Money price;
	private Quantity unitOfMeasure;
	private PriceAlterationType priceAlterationType;
	private ChargeCycle chargeCycle;
    private Integer applicationOffset;

	public DefineProductOfferingPriceAlterationIdentityData name(String name) {
		this.name = name;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData description(String description) {
		this.description = description;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData priceType(PriceType priceType) {
		this.priceType = priceType;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData prorationType(ProrationType prorationType) {
		this.prorationType = prorationType;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData applicationDuration(
			ApplicationDuration applicationDuration) {
		this.applicationDuration = applicationDuration;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData priority(Integer priority) {
		this.priority = priority;
		return this;
	}

	public Float getPercentage() {
		return percentage;
	}

	public void setPercentage(Float percentage) {
		this.percentage = percentage;
	}

	public DefineProductOfferingPriceAlterationIdentityData percentage(Float percentage) {
		this.percentage = percentage;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData price(Money price) {
		this.price = price;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData chargeCycle(ChargeCycle chargeCycle) {
		this.chargeCycle = chargeCycle;
		return this;
	}

	public DefineProductOfferingPriceAlterationIdentityData unitOfMeasure(Quantity unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public ApplicationDuration getApplicationDuration() {
		return applicationDuration;
	}

	public void setApplicationDuration(ApplicationDuration applicationDuration) {
		this.applicationDuration = applicationDuration;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Money getPrice() {
		return price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

	public Quantity getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(Quantity unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public PriceAlterationType getPriceAlterationType() {
		return priceAlterationType;
	}

	public void setPriceAlterationType(PriceAlterationType priceAlterationType) {
		this.priceAlterationType = priceAlterationType;
	}

	public DefineProductOfferingPriceAlterationIdentityData priceAlterationType(
			PriceAlterationType priceAlterationType) {
		this.priceAlterationType = priceAlterationType;
		return this;
	}

	public ChargeCycle getChargeCycle() {
		return chargeCycle;
	}

	public void setChargeCycle(ChargeCycle chargeCycle) {
		this.chargeCycle = chargeCycle;
	}

	public ProrationType getProrationType() {
		return prorationType;
	}

	public void setProrationType(ProrationType prorationType) {
		this.prorationType = prorationType;
	}

    public Integer getApplicationOffset() {
        return applicationOffset;
    }

    public void setApplicationOffset(Integer applicationOffset) {
        this.applicationOffset = applicationOffset;
    }

    public DefineProductOfferingPriceAlterationIdentityData applicationOffset(Integer applicationOffset) {
        this.applicationOffset = applicationOffset;
        return this;
    }

    @Override
	public String toString() {
		return "DefineProductOfferingPriceAlterationIdentityData [name=" + name + ", description=" + description
				+ ", priceType=" + priceType + ", prorationType=" + prorationType + ", applicationDuration="
				+ applicationDuration + ", priority=" + priority + ", percentage=" + percentage + ", price=" + price
				+ ", unitOfMeasure=" + unitOfMeasure + ", priceAlterationType=" + priceAlterationType + ", chargeCycle="
				+ chargeCycle + ", applicationOffset=" + applicationOffset + "]";
	}

}
