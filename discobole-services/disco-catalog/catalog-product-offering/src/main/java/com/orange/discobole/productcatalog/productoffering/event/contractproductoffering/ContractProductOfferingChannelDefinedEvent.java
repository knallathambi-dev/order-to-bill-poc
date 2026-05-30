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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelRef;


/**
 * ContractProductOfferingChannelDefinedEvent
 * @author BMKJ8547
 *
 */

public class ContractProductOfferingChannelDefinedEvent implements ContractProductOfferingEvent {
	
	@TargetAggregateIdentifier
    private final String productOfferingId;
    private final List<ChannelRef> channel;
    private final OffsetDateTime lastUpdate;
	/**
	 * @param productOfferingId
	 * @param channel
	 * @param lastUpdate
	 */
	public ContractProductOfferingChannelDefinedEvent(String productOfferingId, List<ChannelRef> channel,
			OffsetDateTime lastUpdate) {
		super();
		this.productOfferingId = productOfferingId;
		this.channel = channel;
		this.lastUpdate = lastUpdate;
	
	}

	public ContractProductOfferingChannelDefinedEvent() {
		super();
		this.productOfferingId = null;
		this.channel = null;
		this.lastUpdate = null;

	}

	@Override
	public String toString() {
		return "ContractProductOfferingChannelDefinedEvent [productOfferingId=" + productOfferingId + ", channel="
				+ channel + ", lastUpdate=" + lastUpdate + "]";
	}
	public String getProductOfferingId() {
		return productOfferingId;
	}
	public List<ChannelRef> getChannel() {
		return channel;
	}
	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
    
}
