// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Pojo as per schema defination.
 *
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class DefineProductOfferingPriceAlterationIdentityData {
	@NotBlank
	private String name;
	private String description;
	@NotNull
	private PriceType priceType;
	private ProrationType prorationType;
	private ApplicationDuration applicationDuration;
	@NotNull
	private Integer priority;
	@PositiveOrZero
	private Float percentage;
	@Valid
	private Money price;
	private Quantity unitOfMeasure;
	@NotNull
	private PriceAlterationType priceAlterationType;
    private Integer applicationOffset;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DefineProductOfferingPriceAlterationIdentityData name(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DefineProductOfferingPriceAlterationIdentityData description(String description) {
		this.description = description;
		return this;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public DefineProductOfferingPriceAlterationIdentityData priceType(PriceType priceType) {
		this.priceType = priceType;
		return this;
	}

	public ApplicationDuration getApplicationDuration() {
		return applicationDuration;
	}

	public void setApplicationDuration(ApplicationDuration applicationDuration) {
		this.applicationDuration = applicationDuration;
	}

	public DefineProductOfferingPriceAlterationIdentityData applicationDuration(
			ApplicationDuration applicationDuration) {
		this.applicationDuration = applicationDuration;
		return this;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
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

	public Money getPrice() {
		return price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

	public DefineProductOfferingPriceAlterationIdentityData price(Money price) {
		this.price = price;
		return this;
	}

	public Quantity getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(Quantity unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public DefineProductOfferingPriceAlterationIdentityData unitOfMeasure(Quantity unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
		return this;
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
}
