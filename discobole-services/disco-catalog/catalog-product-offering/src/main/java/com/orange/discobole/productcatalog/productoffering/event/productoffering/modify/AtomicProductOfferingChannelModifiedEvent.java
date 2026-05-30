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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelRef;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

/**
 * Event raised to modify the PO Channel from the user.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class AtomicProductOfferingChannelModifiedEvent implements ProductOfferingEvent {

	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<ChannelRef> channel;
	private final OffsetDateTime lastUpdate;

	public AtomicProductOfferingChannelModifiedEvent(){
		this.productOfferingId = null;
		this.channel = null;
		this.lastUpdate = null;
	}

	/**
	 * @param productOfferingId
	 * @param channel
	 * @param lastUpdate
	 */
	public AtomicProductOfferingChannelModifiedEvent(String productOfferingId, List<ChannelRef> channel,
			OffsetDateTime lastUpdate) {
		this.productOfferingId = productOfferingId;
		this.channel = channel;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingChannelModifiedEvent [productOfferingId=" + productOfferingId + ", channel="
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
