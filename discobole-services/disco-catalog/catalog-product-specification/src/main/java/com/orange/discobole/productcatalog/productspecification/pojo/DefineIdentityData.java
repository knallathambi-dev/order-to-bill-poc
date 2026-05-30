// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.pojo;

import java.util.List;

import com.orange.discobole.processflow.annotation.DefaultValue;

public class DefineIdentityData {

	private IdentityData identityData;

	private List<SelectRelatedParty> relatedParty;

	private List<SelectRelatedResource> relatedResource;

	private DefineEntityValidityPeriod validityPeriod;

	@DefaultValue("1")
	private Integer identityDataMinCardinality = 1;
	@DefaultValue("1")
	private Integer identityDataMaxCardinality = 1;

	@DefaultValue("0")
	private Integer relatedPartyMinCardinality = 0;
	@DefaultValue("1")
	private Integer relatedPartyMaxCardinality = 1;

	@DefaultValue("0")
	private Integer relatedResourceMinCardinality = 0;
	@DefaultValue("1")
	private Integer relatedResourceMaxCardinality = 1;

	@DefaultValue("1")
	private Integer validityPeriodMinCardinality = 1;
	@DefaultValue("1")
	private Integer validityPeriodMaxCardinality = 1;

	public IdentityData getIdentityData() {
		return identityData;
	}

	public void setIdentityData(IdentityData identityData) {
		this.identityData = identityData;
	}

	public List<SelectRelatedParty> getRelatedParty() {
		return relatedParty;
	}

	public void setRelatedParty(List<SelectRelatedParty> relatedParty) {
		this.relatedParty = relatedParty;
	}

	public List<SelectRelatedResource> getRelatedResource() {
		return relatedResource;
	}

	public void setRelatedResource(List<SelectRelatedResource> relatedResource) {
		this.relatedResource = relatedResource;
	}

	public DefineEntityValidityPeriod getValidityPeriod() {
		return validityPeriod;
	}

	public void setDefineEntityValidityPeriod(DefineEntityValidityPeriod validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public Integer getIdentityDataMinCardinality() {
		return identityDataMinCardinality;
	}

	public void setIdentityDataMinCardinality(Integer identityDataMinCardinality) {
		this.identityDataMinCardinality = identityDataMinCardinality;
	}

	public Integer getIdentityDataMaxCardinality() {
		return identityDataMaxCardinality;
	}

	public void setIdentityDataMaxCardinality(Integer identityDataMaxCardinality) {
		this.identityDataMaxCardinality = identityDataMaxCardinality;
	}

	public Integer getRelatedPartyMinCardinality() {
		return relatedPartyMinCardinality;
	}

	public void setRelatedPartyMinCardinality(Integer relatedPartyMinCardinality) {
		this.relatedPartyMinCardinality = relatedPartyMinCardinality;
	}

	public Integer getRelatedPartyMaxCardinality() {
		return relatedPartyMaxCardinality;
	}

	public void setRelatedPartyMaxCardinality(Integer relatedPartyMaxCardinality) {
		this.relatedPartyMaxCardinality = relatedPartyMaxCardinality;
	}

	public Integer getRelatedResourceMinCardinality() {
		return relatedResourceMinCardinality;
	}

	public void setRelatedResourceMinCardinality(Integer relatedResourceMinCardinality) {
		this.relatedResourceMinCardinality = relatedResourceMinCardinality;
	}

	public Integer getRelatedResourceMaxCardinality() {
		return relatedResourceMaxCardinality;
	}

	public void setRelatedResourceMaxCardinality(Integer selectRelatedResourceMaxCardinality) {
		this.relatedResourceMaxCardinality = selectRelatedResourceMaxCardinality;
	}

	public Integer getValidityPeriodMinCardinality() {
		return validityPeriodMinCardinality;
	}

	public void setValidityPeriodMinCardinality(Integer validityPeriodMinCardinality) {
		this.validityPeriodMinCardinality = validityPeriodMinCardinality;
	}

	public Integer getValidityPeriodMaxCardinality() {
		return validityPeriodMaxCardinality;
	}

	public void setValidityPeriodMaxCardinality(Integer validityPeriodMaxCardinality) {
		this.validityPeriodMaxCardinality = validityPeriodMaxCardinality;
	}

}
