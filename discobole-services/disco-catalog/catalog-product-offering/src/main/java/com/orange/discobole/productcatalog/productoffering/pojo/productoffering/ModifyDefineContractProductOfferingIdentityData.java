// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo.productoffering;

import java.util.List;

import com.orange.discobole.processflow.annotation.DefaultValue;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineEntityValidityPeriod;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingMarketSegment;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingSaleChannel;
import com.orange.discobole.productcatalog.productoffering.pojo.ManageProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectRelatedParty;

public class ModifyDefineContractProductOfferingIdentityData {

	private DefineContractIdentityData defineData;

	@DefaultValue("0")
	private Integer channelMinCardinality = 0;
	@DefaultValue("1")
	private Integer channelMaxCardinality = 1;
	private List<DefineProductOfferingSaleChannel> channels;
	@DefaultValue("0")
	private Integer marketSegmentMinCardinality = 0;
	@DefaultValue("1")
	private Integer marketSegmentMaxCardinality = 1;
	private List<DefineProductOfferingMarketSegment> marketSegments;
	@DefaultValue("0")
	private Integer relatedPartyMinCardinality = 0;
	@DefaultValue("1")
	private Integer relatedPartyMaxCardinality = 1;
	private List<SelectRelatedParty> relatedParties;
	@DefaultValue("0")
	private Integer poTermMinCardinality = 0;
	@DefaultValue("1")
	private Integer poTermMaxCardinality = 1;
	private List<ManageProductOfferingTerm> poTerms;
	@DefaultValue("1")
	private Integer validityPeriodMinCardinality = 1;
	@DefaultValue("1")
	private Integer validityPeriodMaxCardinality = 1;
	private DefineEntityValidityPeriod validityPeriod;
	private ProductOfferingLifecycle lifecycleStatus;
	@DefaultValue("0")
	private Integer lifecycleStatusMinCardinality = 0;
	@DefaultValue("1")
	private Integer lifecycleStatusMaxCardinality = 1;

	public DefineContractIdentityData getDefineData() {
		return defineData;
	}

	public void setDefineData(DefineContractIdentityData defineData) {
		this.defineData = defineData;
	}

	public List<DefineProductOfferingSaleChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<DefineProductOfferingSaleChannel> channels) {
		this.channels = channels;
	}

	public List<DefineProductOfferingMarketSegment> getMarketSegments() {
		return marketSegments;
	}

	public void setMarketSegments(List<DefineProductOfferingMarketSegment> marketSegments) {
		this.marketSegments = marketSegments;
	}

	public List<SelectRelatedParty> getRelatedParties() {
		return relatedParties;
	}

	public void setRelatedParties(List<SelectRelatedParty> relatedParties) {
		this.relatedParties = relatedParties;
	}

	public List<ManageProductOfferingTerm> getPoTerms() {
		return poTerms;
	}

	public void setPoTerms(List<ManageProductOfferingTerm> poTerms) {
		this.poTerms = poTerms;
	}

	public DefineEntityValidityPeriod getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(DefineEntityValidityPeriod validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public Integer getChannelMinCardinality() {
		return channelMinCardinality;
	}

	public Integer getChannelMaxCardinality() {
		return channelMaxCardinality;
	}

	public Integer getMarketSegmentMinCardinality() {
		return marketSegmentMinCardinality;
	}

	public Integer getMarketSegmentMaxCardinality() {
		return marketSegmentMaxCardinality;
	}

	public Integer getRelatedPartyMinCardinality() {
		return relatedPartyMinCardinality;
	}

	public Integer getRelatedPartyMaxCardinality() {
		return relatedPartyMaxCardinality;
	}

	public Integer getPoTermMinCardinality() {
		return poTermMinCardinality;
	}

	public Integer getPoTermMaxCardinality() {
		return poTermMaxCardinality;
	}

	public Integer getValidityPeriodMinCardinality() {
		return validityPeriodMinCardinality;
	}

	public Integer getValidityPeriodMaxCardinality() {
		return validityPeriodMaxCardinality;
	}
	
	public ProductOfferingLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	public Integer getLifecycleStatusMinCardinality() {
		return lifecycleStatusMinCardinality;
	}

	public Integer getLifecycleStatusMaxCardinality() {
		return lifecycleStatusMaxCardinality;
	}
	
}
