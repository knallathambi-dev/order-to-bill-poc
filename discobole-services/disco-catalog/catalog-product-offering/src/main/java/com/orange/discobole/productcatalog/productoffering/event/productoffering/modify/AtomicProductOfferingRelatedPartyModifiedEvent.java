// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering.modify;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

/**
 * The Class AtomicProductOfferingRelatedPartyModifiedEvent stores the related
 * parties modified for product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class AtomicProductOfferingRelatedPartyModifiedEvent implements ProductOfferingEvent {

	private final String productOfferingId;
	private final List<RelatedParty> relatedParties;
	private final OffsetDateTime lastUpdate;

	public AtomicProductOfferingRelatedPartyModifiedEvent(){
		this.productOfferingId = null;
		this.relatedParties = null;
		this.lastUpdate = null;
	}

	/**
	 * @param productOfferingId
	 * @param relatedParties
	 * @param lastUpdate
	 */
	public AtomicProductOfferingRelatedPartyModifiedEvent(String productOfferingId, List<RelatedParty> relatedParties,
			OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.relatedParties = relatedParties;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingRelatedPartyModifiedEvent [productOfferingId=" + productOfferingId
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
