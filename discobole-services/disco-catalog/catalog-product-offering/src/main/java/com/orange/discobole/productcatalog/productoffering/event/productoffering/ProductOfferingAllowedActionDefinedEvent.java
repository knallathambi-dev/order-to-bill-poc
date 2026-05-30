// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;


import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.AllowedProductAction;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.OffsetDateTime;
import java.util.List;

public class ProductOfferingAllowedActionDefinedEvent implements ProductOfferingEvent {


    @TargetAggregateIdentifier
    private final String productOfferingId;

    private final List<AllowedProductAction> allowedAction;

    private final OffsetDateTime lastUpdate;

    /* Required by Axon / Jackson */
    private ProductOfferingAllowedActionDefinedEvent() {
        this.productOfferingId = null;
        this.allowedAction = null;
        this.lastUpdate = null;
    }

    public ProductOfferingAllowedActionDefinedEvent(
            String productOfferingId,
            List<AllowedProductAction> allowedAction,
            OffsetDateTime lastUpdate) {
        this.productOfferingId = productOfferingId;
        this.allowedAction = allowedAction;
        this.lastUpdate = lastUpdate;
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public List<AllowedProductAction> getAllowedAction() {
        return allowedAction;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "ProductOfferingAllowedActionDefinedEvent{" +
                "productOfferingId='" + productOfferingId + '\'' +
                ", allowedAction=" + allowedAction +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
