// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.util.Set;

/**
 * The Class InvalidLinkPOPtoOperEvent generated when productOfferingPriceId is not
 * equal to operationSpecId.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

public class InvalidLinkPOPtoOperEvent implements ProductOfferingEvent{

    private final String productOfferingId;
    private final Set<String> invalidOpsSelected;

    private InvalidLinkPOPtoOperEvent() {
        this.productOfferingId = null;
        this.invalidOpsSelected = null;
    }

    public InvalidLinkPOPtoOperEvent(String productOfferingId, Set<String> invalidOpsSelected) {
        this.productOfferingId = productOfferingId;
        this.invalidOpsSelected = invalidOpsSelected;
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public Set<String> getInvalidOpsSelected() {
        return invalidOpsSelected;
    }

    @Override
    public String toString() {
        return "InvalidLinkPOPtoOperEvent{" +
                "productOfferingId='" + productOfferingId + '\'' +
                ", invalidOpsSelected=" + invalidOpsSelected +
                '}';
    }
}
