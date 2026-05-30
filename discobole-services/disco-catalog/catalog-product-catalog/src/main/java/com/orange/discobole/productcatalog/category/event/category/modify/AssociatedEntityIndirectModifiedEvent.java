// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category.modify;


import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingRef;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.OffsetDateTime;
import java.util.Set;

public class AssociatedEntityIndirectModifiedEvent implements CategoryEvent {

    @TargetAggregateIdentifier
    private final String categoryId;
    private final Set<ProductOfferingRef> addProductOfferings;
    private final Set<ProductOfferingRef> delProductOfferings;
    private final OffsetDateTime lastUpdate;
    public AssociatedEntityIndirectModifiedEvent(String categoryId, Set<ProductOfferingRef> addProductOfferings,
                                                 Set<ProductOfferingRef> delProductOfferings, OffsetDateTime lastUpdate) {
        super();
        this.categoryId = categoryId;
        this.addProductOfferings = addProductOfferings;
        this.delProductOfferings = delProductOfferings;
        this.lastUpdate = lastUpdate;
    }
    public AssociatedEntityIndirectModifiedEvent() {
        super();
        this.categoryId = null;
        this.addProductOfferings = null;
        this.delProductOfferings = null;
        this.lastUpdate = null;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public Set<ProductOfferingRef> getAddProductOfferings() {
        return addProductOfferings;
    }
    public Set<ProductOfferingRef> getDelProductOfferings() {
        return delProductOfferings;
    }
    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }
    @Override
    public String toString() {
        return "AssociatedEntityIndirectModifiedEvent [categoryId=" + categoryId + ", addProductOfferings="
                + addProductOfferings + ", delProductOfferings=" + delProductOfferings + ", lastUpdate=" + lastUpdate
                + "]";
    }

}
