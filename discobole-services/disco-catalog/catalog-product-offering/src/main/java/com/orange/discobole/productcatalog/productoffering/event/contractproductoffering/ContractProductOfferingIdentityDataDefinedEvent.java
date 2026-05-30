// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.MarketSegmentRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractIdentityData;

/**
 * The Class ContractProductOfferingDescribedEvent.
 *
 * @author Vishal Vachaspati
 *
 */
public final class ContractProductOfferingIdentityDataDefinedEvent implements ContractProductOfferingEvent {
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final DefineContractIdentityData identityData;
	private final List<ChannelRef> channels;

	private final List<MarketSegmentRef> marketSegments;
	private final List<RelatedParty> relatedParties;
	private final List<ProductOfferingTerm> poTerms;

	private final TimePeriod validity;

	private final ProductOfferingType type;
	private final String statusReason;

	private final OffsetDateTime lastUpdate;
	private final String href;

	public ContractProductOfferingIdentityDataDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.identityData = null;
		this.channels = null;
		this.marketSegments = null;
		this.relatedParties = null;
		this.poTerms = null;
		this.validity = null;
		this.type = null;
		this.statusReason = null;
		this.lastUpdate = null;
		this.href = null;
	}

	public ContractProductOfferingIdentityDataDefinedEvent(String productOfferingId,
			DefineContractIdentityData identityData, List<ChannelRef> channels, List<MarketSegmentRef> marketSegments,
			List<RelatedParty> relatedParties, List<ProductOfferingTerm> poTerms, TimePeriod validity,
			ProductOfferingType type, String statusReason, OffsetDateTime lastUpdate, String href) {
		this.productOfferingId = productOfferingId;
		this.identityData = identityData;
		this.channels = channels;
		this.marketSegments = marketSegments;
		this.relatedParties = relatedParties;
		this.poTerms = poTerms;
		this.validity = validity;
		this.type = type;
		this.statusReason = statusReason;
		this.lastUpdate = lastUpdate;
		this.href = href;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public DefineContractIdentityData getIdentityData() {
		return identityData;
	}

	public List<ChannelRef> getChannels() {
		return channels;
	}

	public List<MarketSegmentRef> getMarketSegments() {
		return marketSegments;
	}

	public List<RelatedParty> getRelatedParties() {
		return relatedParties;
	}

	public List<ProductOfferingTerm> getPoTerms() {
		return poTerms;
	}

	public TimePeriod getValidity() {
		return validity;
	}

	public ProductOfferingType getType() {
		return type;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public String getHref() {
		return href;
	}

	@Override
	public String toString() {
		return "ContractProductOfferingIdentityDataDefinedEvent [productOfferingId=" + productOfferingId
				+ ", identityData=" + identityData + ", channels=" + channels + ", marketSegments=" + marketSegments
				+ ", relatedParties=" + relatedParties + ", poTerms=" + poTerms + ", validity=" + validity + ", type="
				+ type + ", statusReason=" + statusReason + ", lastUpdate=" + lastUpdate + ", href=" + href + "]";
	}

}
