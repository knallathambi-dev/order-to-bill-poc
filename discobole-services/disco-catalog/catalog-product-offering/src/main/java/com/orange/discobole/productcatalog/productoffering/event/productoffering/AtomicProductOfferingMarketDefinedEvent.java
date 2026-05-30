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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.MarketSegmentRef;

/**
 * The Class AtomicProductOfferingMarketDefinedEvent.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class AtomicProductOfferingMarketDefinedEvent implements ProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<MarketSegmentRef> marketSegments;
	private final OffsetDateTime lastUpdate;

	private AtomicProductOfferingMarketDefinedEvent() {
		this.productOfferingId = null;
		this.marketSegments = null;
		this.lastUpdate=null;
	}

	public AtomicProductOfferingMarketDefinedEvent(String productOfferingId, List<MarketSegmentRef> marketSegments
	,OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.marketSegments = marketSegments;
		this.lastUpdate=lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingMarketDefinedEvent{" +
				"productOfferingId='" + productOfferingId + '\'' +
				", marketSegments=" + marketSegments +
				", lastUpdate=" + lastUpdate +
				'}';
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
