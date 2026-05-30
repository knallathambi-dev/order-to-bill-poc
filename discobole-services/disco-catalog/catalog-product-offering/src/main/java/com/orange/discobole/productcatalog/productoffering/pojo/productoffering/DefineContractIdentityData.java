// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import java.util.Objects;

import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.BillingType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class DefineContractIdentityData {

	@NotBlank
	private String name;
	@NotBlank
	private String description;
	/** Brand associated with this Product specification. */
	@DefaultValue("Orange")
	private String brand;
	private String productNumber;
	@DefaultValue("true")
	private Boolean isInstallable;
	@DefaultValue("true")
	private Boolean isSellable;
	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	@DefaultValue("true")
	private Boolean isVisible;
	private Boolean isBundle;
	@NotNull
	private BillingType billingType;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsInstallable() {
		return isInstallable;
	}

	public void setIsInstallable(Boolean installable) {
		isInstallable = installable;
	}

	public BillingType getBillingType() {
		return billingType;
	}

	public void setBillingType(BillingType billingType) {
		this.billingType = billingType;
	}

	public Boolean getIsSellable() {
		return isSellable;
	}

	public void setIsSellable(Boolean isSellable) {
		this.isSellable = isSellable;
	}

	public Boolean getIsBundle() {
		return isBundle;
	}

	public void setIsBundle(Boolean isBundle) {
		this.isBundle = isBundle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DefineContractIdentityData))
			return false;
		DefineContractIdentityData that = (DefineContractIdentityData) o;
		return Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription())
				&& Objects.equals(getBrand(), that.getBrand())
				&& Objects.equals(getProductNumber(), that.getProductNumber())
				&& Objects.equals(getIsSellable(), that.getIsSellable())
				&& Objects.equals(getIsVisible(), that.getIsVisible())
				&& Objects.equals(getIsBundle(), that.getIsBundle())
				&& Objects.equals(getIsInstallable(), that.getIsInstallable())
				&& Objects.equals(getBillingType(), that.getBillingType());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDescription(), getIsSellable(),getIsVisible(),getBrand(), getProductNumber(), getIsBundle(),
				getIsInstallable(), getBillingType());
	}
}
