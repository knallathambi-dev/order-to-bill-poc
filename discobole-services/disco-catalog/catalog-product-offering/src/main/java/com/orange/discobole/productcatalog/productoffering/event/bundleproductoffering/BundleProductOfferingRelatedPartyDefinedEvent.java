// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;

/**
 * The Class BundleProductOfferingRelatedPartyDefinedEvent stores the related
 * parties defined for bundle product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class BundleProductOfferingRelatedPartyDefinedEvent implements BundleProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<RelatedParty> relatedParties;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param relatedParties
	 * @param lastUpdate
	 */
	public BundleProductOfferingRelatedPartyDefinedEvent(String productOfferingId, List<RelatedParty> relatedParties,
			OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.relatedParties = relatedParties;
		this.lastUpdate = lastUpdate;
	}

	public BundleProductOfferingRelatedPartyDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.relatedParties = null;
		this.lastUpdate = null;
	}

	@Override
	public String toString() {
		return "BundleProductOfferingRelatedPartyDefinedEvent [productOfferingId=" + productOfferingId
				+ ", relatedParties=" + relatedParties + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}


	public List<RelatedParty> getRelatedParties() {
		return relatedParties;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
