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

public final class InvalidProductOfferingCategorySelectedEvent implements ProductOfferingEvent {

    private final String productOfferingId;
    private final List<String> invalidCategories;

    private InvalidProductOfferingCategorySelectedEvent() {
        this.productOfferingId = null;
        this.invalidCategories = null;
    }

    public InvalidProductOfferingCategorySelectedEvent(String productOfferingId, List<String> invalidCategories) {
        this.productOfferingId = productOfferingId;
        this.invalidCategories = invalidCategories;
    }

    @Override
    public String toString() {
        return "InvalidProductOfferingCategorySelectedEvent{" +
                "productOfferingId='" + productOfferingId + '\'' +
                ", invalidCategories='" + invalidCategories + '\'' +
                '}';
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public List<String> getInvalidCategories() {
        return invalidCategories;
    }
}
