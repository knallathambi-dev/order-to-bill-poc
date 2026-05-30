// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingEvent;

import java.time.OffsetDateTime;
import java.util.Set;

public class ContractProductOfferingIndirectCategoryModifiedEvent implements ProductOfferingEvent {

    @TargetAggregateIdentifier
    private final String productOfferingId;

    private final Set<CategoryRef> addCategories;
    private final Set<CategoryRef> delCategories;
    private final OffsetDateTime lastUpdate;

    /**
     * @param productOfferingId
     * @param addCategories
     * @param delCategories
     * @param lastUpdate
     */

    public ContractProductOfferingIndirectCategoryModifiedEvent(String productOfferingId, Set<CategoryRef> addCategories,
                                                        Set<CategoryRef> delCategories, OffsetDateTime lastUpdate) {
        super();
        this.productOfferingId = productOfferingId;
        this.addCategories = addCategories;
        this.delCategories = delCategories;
        this.lastUpdate = lastUpdate;
    }

    public ContractProductOfferingIndirectCategoryModifiedEvent() {
        super();
        this.productOfferingId = null;
        this.addCategories = null;
        this.delCategories = null;
        this.lastUpdate = null;
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public Set<CategoryRef> getAddCategories() {
        return addCategories;
    }

    public Set<CategoryRef> getDelCategories() {
        return delCategories;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "ContractProductOfferingIndirectCategoryModifiedEvent [productOfferingId=" + productOfferingId + ", addCategories="
                + addCategories + ", delCategories=" + delCategories + ", lastUpdate=" + lastUpdate + "]";
    }

}
