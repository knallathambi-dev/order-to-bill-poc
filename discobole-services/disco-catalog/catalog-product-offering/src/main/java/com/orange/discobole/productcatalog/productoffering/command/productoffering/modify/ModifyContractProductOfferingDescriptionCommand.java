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
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractIdentityData;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * The Class ContractProductOfferingDescriptionCommand used to set the
 * description for selected contract product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class ModifyContractProductOfferingDescriptionCommand {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final DefineContractIdentityData identityData;
	private final Set<String> channelIds;
	private final Set<String> marketSegmentIds;
	private final Set<RelatedParty> relatedParties;
	private final Set<ProductOfferingTerm> poTerms;
	private final TimePeriod validity;
	private final ProductOfferingType type;
	private final String statusReason;
	private final OffsetDateTime lastUpdate;
	private final ProductOfferingLifecycle lifecycleStatus;

	public ModifyContractProductOfferingDescriptionCommand(String productOfferingId, DefineContractIdentityData identityData, Set<String> channelIds,
                                                           Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
                                                           Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type,
                                                           String statusReason, OffsetDateTime lastUpdate,ProductOfferingLifecycle lifecycleStatus) {
		this.productOfferingId = productOfferingId;
		this.identityData = identityData;
		this.channelIds = channelIds;
		this.marketSegmentIds = marketSegmentIds;
		this.relatedParties = relatedParties;
		this.poTerms = poTerms;
		this.validity = validity;
		this.type = type;
		this.statusReason = statusReason;
		this.lastUpdate = lastUpdate;
		this.lifecycleStatus = lifecycleStatus;
	}

	public String ProductOfferingIdentityDataCommand() {
		return productOfferingId;
	}

	public DefineContractIdentityData getIdentityData() {
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

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public ProductOfferingLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}

	@Override
	public String toString() {
		return "ContractProductOfferingDescriptionCommand [productOfferingId=" + productOfferingId + ", identityData="
				+ identityData + ", channelIds=" + channelIds + ", marketSegmentIds=" + marketSegmentIds
				+ ", relatedParties=" + relatedParties + ", poTerms=" + poTerms + ", validity=" + validity + ", type="
				+ type + ", statusReason=" + statusReason + ", lastUpdate=" + lastUpdate + ", lifecycleStatus="+ lifecycleStatus +"]";
	}

}
