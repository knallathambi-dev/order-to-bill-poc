// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.DefaultValue;

public class DefineProductOfferingPriceChargeIdentity {

	@JsonProperty("definePOPChargeIdentityData")
	private DefineProductOfferingPriceChargeIdentityData definePOPChargeIdentityData;

	@DefaultValue("0")
	private Integer relationshipMinCardinality = 0;
	@DefaultValue("1")
	private Integer relationshipMaxCardinality = 1;
	
	@JsonProperty("relationships")
	private List<DefineRelationship> relationships;

	@DefaultValue("1")
	private Integer validityMinCardinality = 1;
	@DefaultValue("1")
	private Integer validityMaxCardinality = 1;
	
	@JsonProperty("validity")
	private DefinePOPStatusValidityPeriod validity;

	public DefineProductOfferingPriceChargeIdentityData getDefinePOPChargeIdentityData() {
		return definePOPChargeIdentityData;
	}

	public void setDefinePOPChargeIdentityData(
			DefineProductOfferingPriceChargeIdentityData definePOPChargeIdentityData) {
		this.definePOPChargeIdentityData = definePOPChargeIdentityData;
	}

	public Integer getRelationshipMinCardinality() {
		return relationshipMinCardinality;
	}

	public void setRelationshipMinCardinality(Integer relationshipMinCardinality) {
		this.relationshipMinCardinality = relationshipMinCardinality;
	}

	public Integer getRelationshipMaxCardinality() {
		return relationshipMaxCardinality;
	}

	public void setRelationshipMaxCardinality(Integer relationshipMaxCardinality) {
		this.relationshipMaxCardinality = relationshipMaxCardinality;
	}

	public List<DefineRelationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<DefineRelationship> relationships) {
		this.relationships = relationships;
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

	public DefinePOPStatusValidityPeriod getDefinePOPStatusValidityPeriod() {
		return validity;
	}

	public void setDefinePOPStatusValidityPeriod(DefinePOPStatusValidityPeriod validity) {
		this.validity = validity;
	}

	@Override
	public String toString() {
		return "DefineProductOfferingPriceChargeIdentity [definePOPChargeIdentityData=" + definePOPChargeIdentityData
				+ ", relationshipMinCardinality=" + relationshipMinCardinality + ", relationshipMaxCardinality="
				+ relationshipMaxCardinality + ", relationships=" + relationships + ", validityMinCardinality="
				+ validityMinCardinality + ", validityMaxCardinality=" + validityMaxCardinality + ", validity="
				+ validity + "]";
	}

}
