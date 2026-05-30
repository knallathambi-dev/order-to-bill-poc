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
public class DefineProductOfferingPriceTaxAlterationIdentityData {
	@NotBlank
	private String name;
	private String description;
	@PositiveOrZero
	private Float percentage;
	@Valid
	private Money price;
	@NotNull
	private PriceAlterationType priceAlterationType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DefineProductOfferingPriceTaxAlterationIdentityData name(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DefineProductOfferingPriceTaxAlterationIdentityData description(String description) {
		this.description = description;
		return this;
	}

	public Float getPercentage() {
		return percentage;
	}

	public void setPercentage(Float percentage) {
		this.percentage = percentage;
	}

	public DefineProductOfferingPriceTaxAlterationIdentityData percentage(Float percentage) {
		this.percentage = percentage;
		return this;
	}

	public Money getPrice() {
		return price;
	}

	public void setPrice(Money price) {
		this.price = price;
	}

	public DefineProductOfferingPriceTaxAlterationIdentityData price(Money price) {
		this.price = price;
		return this;
	}

	public DefineProductOfferingPriceTaxAlterationIdentityData priceAlterationType(
			PriceAlterationType priceAlterationType) {
		this.priceAlterationType = priceAlterationType;
		return this;
	}

	public PriceAlterationType getPriceAlterationType() {
		return priceAlterationType;
	}

	public void setPriceAlterationType(PriceAlterationType priceAlterationType) {
		this.priceAlterationType = priceAlterationType;
	}

}
