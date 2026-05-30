// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.util.List;

public class InvalidProductOfferingChannelSelectedEvent implements ProductOfferingEvent {

    private final String productOfferingId;
    private final List<String> invalidChannel;

    private InvalidProductOfferingChannelSelectedEvent() {
        this.productOfferingId = null;
        this.invalidChannel = null;
    }

    public InvalidProductOfferingChannelSelectedEvent(final String productOfferingId,
                                                      final List<String> invalidChannel) {
        this.productOfferingId = productOfferingId;
        this.invalidChannel = invalidChannel;
    }

    /**
     * @return the productOfferingId
     */
    public String getProductOfferingId() {
        return productOfferingId;
    }

    /**
     * @return the invalidChannel
     */
    public List<String> getInvalidChannel() {
        return invalidChannel;
    }

    @Override
    public String toString() {
        return "InvalidProductOfferingChannelSelectedEvent{" + "productOfferingId='" + productOfferingId + '\''
                + ", invalidChannel='" + invalidChannel + '\'' + '}';
    }

}
