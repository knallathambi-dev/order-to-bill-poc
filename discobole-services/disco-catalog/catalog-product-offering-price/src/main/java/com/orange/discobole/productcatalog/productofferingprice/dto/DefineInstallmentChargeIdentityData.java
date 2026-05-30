// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto;


import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Money;


import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Pojo as per schema defination.
 *
 * @author Ankur Singh
 * @since 1.0
 *
 */
public class DefineInstallmentChargeIdentityData {
	@NotBlank
	private String name;
	private String description;
	@DefaultValue("Orange")
	private String partner;
	private String externalId;
	private Float downPayment;
	private Float interestRate;
	private ApplicationDuration applicationDuration;
	@NotNull
	private Money price;

	public @NotBlank String getName() {
		return name;
	}

	public void setName(@NotBlank String name) {
		this.name = name;
	}

	public DefineInstallmentChargeIdentityData name(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DefineInstallmentChargeIdentityData description(String description) {
		this.description = description;
		return this;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}


	public DefineInstallmentChargeIdentityData partner(String partner) {
		this.partner = (partner == null || partner.trim().isEmpty())
				? "Orange"
				: partner;
		return this;
	}
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public DefineInstallmentChargeIdentityData externalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public Float getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(Float downPayment) {
		this.downPayment = downPayment;
	}

	public DefineInstallmentChargeIdentityData downPayment(Float downPayment) {
		this.downPayment = downPayment;
		return this;
	}

	public Float getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Float interestRate) {
		this.interestRate = interestRate;
	}

	public DefineInstallmentChargeIdentityData interestRate(Float interestRate) {
		this.interestRate = interestRate;
		return this;
	}

	public ApplicationDuration getApplicationDuration() {
		return applicationDuration;
	}

	public void setApplicationDuration(ApplicationDuration applicationDuration) {
		this.applicationDuration = applicationDuration;
	}

	public DefineInstallmentChargeIdentityData applicationDuration(ApplicationDuration applicationDuration) {
		this.applicationDuration = applicationDuration;
		return this;
	}

	public @NotNull Money getPrice() {
		return price;
	}

	public DefineInstallmentChargeIdentityData price(Money price) {
		this.price = price;
		return this;
	}


	public void setPrice(@NotNull Money price) {
		this.price = price;
	}


}
