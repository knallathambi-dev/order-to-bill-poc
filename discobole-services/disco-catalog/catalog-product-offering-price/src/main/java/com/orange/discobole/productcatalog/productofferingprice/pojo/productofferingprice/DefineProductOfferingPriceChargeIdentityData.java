// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
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
 */
public class DefineProductOfferingPriceChargeIdentityData {

	@NotBlank
	private String name;
	private String description;
	@NotNull
	private PriceType priceType;
	private ChargeCycle chargeCycle;
	private ProrationType prorationType;
	@NotNull
	private Boolean immediatePayment;
	private Integer recurringChargePeriodLength;
	private String recurringChargePeriodType;
	@NotNull
	@Valid
	private Money price;
	private Quantity unitOfMeasure;

	public DefineProductOfferingPriceChargeIdentityData name(String name) {
		this.name = name;
		return this;
	}

	public DefineProductOfferingPriceChargeIdentityData description(String description) {
		this.description = description;
		return this;
	}

	public DefineProductOfferingPriceChargeIdentityData priceType(PriceType priceType) {
		this.priceType = priceType;
		return this;
	}

	public DefineProductOfferingPriceChargeIdentityData recurringChargePeriodLength(
			Integer recurringChargePeriodLength) {
		this.recurringChargePeriodLength = recurringChargePeriodLength;
		return this;
	}

	public DefineProductOfferingPriceChargeIdentityData recurringChargePeriodType(String recurringChargePeriodType) {
		this.recurringChargePeriodType = recurringChargePeriodType;
		return this;
	}

	public DefineProductOfferingPriceChargeIdentityData price(Money price) {
		this.price = price;
		return this;
	}

	public DefineProductOfferingPriceChargeIdentityData unitOfMeasure(Quantity unitOfMeasure) {
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

	public Integer getRecurringChargePeriodLength() {
		return recurringChargePeriodLength;
	}

	public void setRecurringChargePeriodLength(Integer recurringChargePeriodLength) {
		this.recurringChargePeriodLength = recurringChargePeriodLength;
	}

	public String getRecurringChargePeriodType() {
		return recurringChargePeriodType;
	}

	public void setRecurringChargePeriodType(String recurringChargePeriodType) {
		this.recurringChargePeriodType = recurringChargePeriodType;
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

	public Boolean getImmediatePayment() {
		return immediatePayment;
	}

	public void setImmediatePayment(Boolean immediatePayment) {
		this.immediatePayment = immediatePayment;
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
}
