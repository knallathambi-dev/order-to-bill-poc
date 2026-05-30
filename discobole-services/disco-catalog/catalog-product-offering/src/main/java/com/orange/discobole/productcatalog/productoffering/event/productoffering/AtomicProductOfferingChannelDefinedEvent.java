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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelRef;


public class AtomicProductOfferingChannelDefinedEvent implements ProductOfferingEvent {
    
	@TargetAggregateIdentifier
    private final String productOfferingId;
    private final List<ChannelRef> channel;
    private final OffsetDateTime lastUpdate;

    private AtomicProductOfferingChannelDefinedEvent() {
        this.productOfferingId = null;
        this.channel = null;
        this.lastUpdate = null;
     
    }

    public AtomicProductOfferingChannelDefinedEvent(final String productOfferingId, final List<ChannelRef> channel
            , OffsetDateTime lastUpdate) {
        this.productOfferingId = productOfferingId;
        this.channel = channel;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "AtomicProductOfferingChannelDefinedEvent{" +
                "productOfferingId='" + productOfferingId + '\'' +
                ", channel=" + channel +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    /**
     * @return the productOfferingId
     */
    public String getProductOfferingId() {
        return productOfferingId;
    }

	/**
     * @return the channel
     */
    public List<ChannelRef> getChannel() {
        return channel;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

}
