// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;


import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;

/**
 * The Class AtomicProductOfferingRelatedPartyDefinedEvent stores the related
 * parties defined for product offering.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class AtomicProductOfferingRelatedPartyDefinedEvent implements ProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<RelatedParty> relatedParties;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingRelatedPartyDefinedEvent() {
		this.productOfferingId = null;
		this.relatedParties = null;
		this.lastUpdate=null;
	}

	public AtomicProductOfferingRelatedPartyDefinedEvent(String productOfferingId, List<RelatedParty> relatedParties
			, OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.relatedParties = relatedParties;
		this.lastUpdate=lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingRelatedPartyDefinedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", relatedParties=" + relatedParties +
				", lastUpdate=" + lastUpdate +
				'}';
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
