// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.DefaultValue;

import java.util.List;

public class DefineInstallmentChargeProductOfferingPriceIdentity {




	@JsonProperty("defineInstallmentChargeIdentityData")
	private DefineInstallmentChargeIdentityData defineInstallmentChargeIdentityData;

	@DefaultValue("1")
	private Integer validityMinCardinality = 1;
	@DefaultValue("1")
	private Integer validityMaxCardinality = 1;
	
	@JsonProperty("validity")
	private DefinePOPStatusValidityPeriod validity;

	@JsonProperty("relationships")
	private List<DefineRelationship> relationships;

	public DefineInstallmentChargeIdentityData getDefineProductOfferingPriceInstallmentChargeIdentityData() {
		return defineInstallmentChargeIdentityData;
	}

	public void setDefineProductOfferingPriceInstallmentChargeIdentityData(DefineInstallmentChargeIdentityData defineInstallmentChargeIdentityData) {
		this.defineInstallmentChargeIdentityData = defineInstallmentChargeIdentityData;
	}

	public Integer getValidityMinCardinality() {
		return validityMinCardinality;
	}

	public void setValidityMinCardinality(Integer validityMinCardinality) {
		this.validityMinCardinality = validityMinCardinality;
	}

	public Integer getValidityMaxCardinality() {
		return validityMaxCardinality;
	}

	public void setValidityMaxCardinality(Integer validityMaxCardinality) {
		this.validityMaxCardinality = validityMaxCardinality;
	}

	public DefinePOPStatusValidityPeriod getValidity() {
		return validity;
	}

	public void setValidity(DefinePOPStatusValidityPeriod validity) {
		this.validity = validity;
	}

	public List<DefineRelationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<DefineRelationship> relationships) {
		this.relationships = relationships;
	}

	@Override
	public String toString() {
		return "DefineInstallmentChargeProductOfferingPriceIdentity{" +
				"defineProductOfferingPriceInstallmentChargeIdentityData=" + defineInstallmentChargeIdentityData +
				", validityMinCardinality=" + validityMinCardinality +
				", validityMaxCardinality=" + validityMaxCardinality +
				", validity=" + validity +
				'}';
	}
}
