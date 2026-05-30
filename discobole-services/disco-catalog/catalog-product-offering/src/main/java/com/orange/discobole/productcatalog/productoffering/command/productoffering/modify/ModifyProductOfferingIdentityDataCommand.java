// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;

import java.util.Set;

/**
 * The Class AtomicProductOfferingDescriptionCommand used to set the description
 * for selected product offering.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class ModifyProductOfferingIdentityDataCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final DefineIdentityData identityData;
	private final Set<String> channelIds;
	private final Set<String> marketSegmentIds;
	private final Set<RelatedParty> relatedParties;
	private final Set<ProductOfferingTerm> poTerms;
	private final TimePeriod validity;
	private final ProductOfferingType type;
	private final String statusReason;
	private final ProductOfferingLifecycle lifecycleStatus;

	public ModifyProductOfferingIdentityDataCommand(String productOfferingId, DefineIdentityData identityData, Set<String> channelIds,
                                                    Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
                                                    Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type,
                                                    String statusReason,ProductOfferingLifecycle lifecycleStatus) {
		this.productOfferingId = productOfferingId;
		this.identityData = identityData;
		this.channelIds = channelIds;
		this.marketSegmentIds = marketSegmentIds;
		this.relatedParties = relatedParties;
		this.poTerms = poTerms;
		this.validity = validity;
		this.type = type;
		this.statusReason = statusReason;
		this.lifecycleStatus = lifecycleStatus;
	}

	public String ProductOfferingIdentityDataCommand() {
		return productOfferingId;
	}

	public DefineIdentityData getIdentityData() {
		return identityData;
	}

	public Set<String> getChannelIds() {
		return channelIds;
	}

	public Set<RelatedParty> getRelatedParties() {
		return relatedParties;
	}

	public Set<ProductOfferingTerm> getPoTerms() {
		return poTerms;
	}

	public TimePeriod getValidity() {
		return validity;
	}

	public ProductOfferingType getType() {
		return type;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public Set<String> getMarketSegmentIds() {
		return marketSegmentIds;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public ProductOfferingLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	@Override
	public String toString() {
		return "ProductOfferingIdentityDataCommand [productOfferingId=" + productOfferingId + ", identityData="
				+ identityData + ", channelIds=" + channelIds + ", marketSegmentIds=" + marketSegmentIds
				+ ", relatedParties=" + relatedParties + ", poTerms=" + poTerms + ", validity=" + validity + ", type="
				+ type + ", statusReason=" + statusReason + ", lifecycleStatus=" + lifecycleStatus + "]";
	}
}
