// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingType;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * The Class BundleProductOfferingDescribedEvent.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public final class BundleProductOfferingIdentityDataDefinedEvent implements BundleProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final DefineIdentityData identityData;
	private final List<ChannelRef> channels;
	private final List<MarketSegmentRef> marketSegments;
	private final List<RelatedParty> relatedParties;
	private final List<ProductOfferingTerm> poTerms;
	private final TimePeriod validity;
	private final ProductOfferingType type;
	private final String statusReason;
	private final OffsetDateTime lastUpdate;
	private final String href;

	public BundleProductOfferingIdentityDataDefinedEvent(String productOfferingId, DefineIdentityData identityData,
			List<ChannelRef> channels, List<MarketSegmentRef> marketSegments, List<RelatedParty> relatedParties,
			List<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason,
			OffsetDateTime lastUpdate, String href) {
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

	public BundleProductOfferingIdentityDataDefinedEvent() {
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

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public DefineIdentityData getIdentityData() {
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
		return "BundleProductOfferingIdentityDataDefinedEvent [productOfferingId=" + productOfferingId
				+ ", identityData=" + identityData + ", channels=" + channels + ", marketSegments=" + marketSegments
				+ ", relatedParties=" + relatedParties + ", poTerms=" + poTerms + ", validity=" + validity + ", type="
				+ type + ", statusReason=" + statusReason + ", lastUpdate=" + lastUpdate + ", href=" + href + "]";
	}

}
