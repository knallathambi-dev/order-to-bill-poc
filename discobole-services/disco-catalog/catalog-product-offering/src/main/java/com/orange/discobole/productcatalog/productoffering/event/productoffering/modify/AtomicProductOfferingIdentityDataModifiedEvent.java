// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering.modify;


import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * The Class ProductOfferingDescribedEvent.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class AtomicProductOfferingIdentityDataModifiedEvent implements ProductOfferingEvent {

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
	private final ProductOfferingLifecycle lifecycleStatus;
	private final ProductOfferingLifecycle currentLifecycleStatus;


	public AtomicProductOfferingIdentityDataModifiedEvent(String productOfferingId, DefineIdentityData identityData, List<ChannelRef> channels, List<MarketSegmentRef> marketSegments, List<RelatedParty> relatedParties, List<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason, OffsetDateTime lastUpdate,ProductOfferingLifecycle lifecycleStatus, ProductOfferingLifecycle currentLifecycleStatus) {
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
		this.lifecycleStatus = lifecycleStatus;
		this.currentLifecycleStatus = currentLifecycleStatus;
	}

	public AtomicProductOfferingIdentityDataModifiedEvent() {
		this.productOfferingId = null;
		this.identityData = null;
		this.channels = null;
		this.marketSegments = null;
		this.relatedParties = null;
		this.poTerms = null;
		this.validity = null;
		this.type = null;
		this.statusReason=null;
		this.lastUpdate = null;
		this.lifecycleStatus=null;
		this.currentLifecycleStatus = null;
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
	
	public ProductOfferingLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	public ProductOfferingLifecycle getCurrentLifecycleStatus() {
		return currentLifecycleStatus;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingIdentityDataModifiedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", identityData=" + identityData +
				", channels=" + channels +
				", marketSegments=" + marketSegments +
				", relatedParties=" + relatedParties +
				", poTerms=" + poTerms +
				", validity=" + validity +
				", type=" + type +
				", statusReason='" + statusReason + '\'' +
				", lastUpdate=" + lastUpdate +
				", lifecycleStatus=" + lifecycleStatus +
				", currentLifecycleStatus=" + currentLifecycleStatus +
				'}';
	}
}
