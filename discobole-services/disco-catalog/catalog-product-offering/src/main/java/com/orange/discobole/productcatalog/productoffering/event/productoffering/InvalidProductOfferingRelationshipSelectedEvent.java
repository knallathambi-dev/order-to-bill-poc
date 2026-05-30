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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Event raised to show the all invalid selected relationship from the user, that can not be defined.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvalidProductOfferingRelationshipSelectedEvent implements ProductOfferingEvent {

    private final String productOfferingId;
    private final List<String> invalidProductRelationships;

    private InvalidProductOfferingRelationshipSelectedEvent() {
        this.productOfferingId = null;
        this.invalidProductRelationships = null;
    }

    public InvalidProductOfferingRelationshipSelectedEvent(String productOfferingId,
                                                           List<String> invalidProductRelationships) {

        this.productOfferingId = productOfferingId;
        this.invalidProductRelationships = invalidProductRelationships;
    }

    @Override
    public String toString() {
        return "InvalidProductOfferingRelationshipSelectedEvent [productOfferingId=" + productOfferingId
                + ", invalidProductRelationships=" + invalidProductRelationships + "]";
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public List<String> getInvalidProductRelationships() {
        return invalidProductRelationships;
    }


}
