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
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;

/**
 * InvalidProductOfferingRelationshipIdEvent raises invalid event when RelationShip Type is not valid
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class InvalidProductOfferingRelationshipTypeEvent implements ProductOfferingEvent {

    List<ProductOfferingRelationship> invalidProductOfferingRelationships;

    private InvalidProductOfferingRelationshipTypeEvent() {
        this.invalidProductOfferingRelationships = null;
    }

    public InvalidProductOfferingRelationshipTypeEvent(List<ProductOfferingRelationship> invalidProductOfferingRelationships) {
        this.invalidProductOfferingRelationships = invalidProductOfferingRelationships;
    }

    public List<ProductOfferingRelationship> getInvalidProductOfferingRelationships() {
        return invalidProductOfferingRelationships;
    }

    @Override
    public String toString() {
        return "InvalidProductOfferingRelationshipTypeEvent{" +
                "invalidProductOfferingRelationships=" + invalidProductOfferingRelationships +
                '}';
    }
}
