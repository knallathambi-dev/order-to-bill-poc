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

public class DefineProductOfferingPriceAlterationIdentity {

	@JsonProperty("definePOPAlterationIdentityData")
	private DefineProductOfferingPriceAlterationIdentityData definePOPAlterationIdentityData;

	@DefaultValue("1")
	private Integer validityMinCardinality = 1;
	@DefaultValue("1")
	private Integer validityMaxCardinality = 1;
	
	@JsonProperty("validity")
	private DefinePOPStatusValidityPeriod validity;

	public DefineProductOfferingPriceAlterationIdentityData getDefinePOPAlterationIdentityData() {
		return definePOPAlterationIdentityData;
	}

	public void setDefinePOPAlterationIdentityData(
			DefineProductOfferingPriceAlterationIdentityData definePOPAlterationIdentityData) {
		this.definePOPAlterationIdentityData = definePOPAlterationIdentityData;
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

	@Override
	public String toString() {
		return "DefineProductOfferingPriceAlterationIdentity [definePOPAlterationIdentityData="
				+ definePOPAlterationIdentityData + ", validityMinCardinality=" + validityMinCardinality
				+ ", validityMaxCardinality=" + validityMaxCardinality + ", validity=" + validity + "]";
	}

}
