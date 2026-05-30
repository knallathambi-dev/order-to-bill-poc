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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.MarketSegmentRef;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

/**
 * Event raised to modify the PO Market from the user.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class AtomicProductOfferingMarketModifiedEvent implements ProductOfferingEvent {
  
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<MarketSegmentRef> marketSegments;
	private final OffsetDateTime lastUpdate;

	public AtomicProductOfferingMarketModifiedEvent() {
		this.productOfferingId = null;
		this.marketSegments = null;
		this.lastUpdate = null;	}

	/**
	 * @param productOfferingId
	 * @param marketSegments
	 * @param lastUpdate
	 */
	public AtomicProductOfferingMarketModifiedEvent(String productOfferingId, List<MarketSegmentRef> marketSegments,
			OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.marketSegments = marketSegments;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingMarketModifiedEvent [productOfferingId=" + productOfferingId + ", marketSegments="
				+ marketSegments + ", lastUpdate=" + lastUpdate + "]";
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	public List<MarketSegmentRef> getMarketSegments() {
		return marketSegments;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

}
