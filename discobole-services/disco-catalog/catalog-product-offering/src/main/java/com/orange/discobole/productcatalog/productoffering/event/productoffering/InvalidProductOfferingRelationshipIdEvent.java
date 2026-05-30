// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;

/**
 * InvalidProductOfferingRelationshipIdEvent raises invalid event when RelationShip id is not valid
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class InvalidProductOfferingRelationshipIdEvent implements ProductOfferingEvent {

    private ProductOfferingRelationship productOfferingRelationship;

    private InvalidProductOfferingRelationshipIdEvent() {
        this.productOfferingRelationship = null;
    }

    public InvalidProductOfferingRelationshipIdEvent(ProductOfferingRelationship productOfferingRelationship) {
        this.productOfferingRelationship = productOfferingRelationship;
    }

    public ProductOfferingRelationship getProductOfferingRelationship() {
        return productOfferingRelationship;
    }

    @Override
    public String toString() {
        return "InvalidProductOfferingRelationshipIdEvent{" +
                "productOfferingRelationship=" + productOfferingRelationship +
                '}';
    }
}
