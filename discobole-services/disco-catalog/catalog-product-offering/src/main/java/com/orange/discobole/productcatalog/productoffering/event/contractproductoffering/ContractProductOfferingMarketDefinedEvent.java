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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.MarketSegmentRef;

/**
 * The Class ContractProductOfferingMarketDefinedEvent.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class ContractProductOfferingMarketDefinedEvent implements ContractProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<MarketSegmentRef> marketSegments;
	private final OffsetDateTime lastUpdate;

	/**
	 * @param productOfferingId
	 * @param marketSegments
	 * @param lastUpdate
	 */
	public ContractProductOfferingMarketDefinedEvent(String productOfferingId, List<MarketSegmentRef> marketSegments,
			OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.marketSegments = marketSegments;
		this.lastUpdate = lastUpdate;
	}

	public ContractProductOfferingMarketDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.marketSegments = null;
		this.lastUpdate = null;
	}

	@Override
	public String toString() {
		return "ContractProductOfferingMarketDefinedEvent [productOfferingId=" + productOfferingId + ", marketSegments="
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
