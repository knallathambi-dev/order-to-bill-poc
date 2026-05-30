// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProcessEntity {
    PRODUCTSPECIFICATION("ProductSpecification"),
    PRODUCTOFFERING("ProductOffering"),
    PRODUCTOFFERINGPRICE("ProductOfferingPrice"),
    CATEGORY("Category");
    private String value;
    ProcessEntity(String value) {
        this.value =value;
    }
    @JsonCreator
    public static ProcessEntity fromValue(String text) {
        for (ProcessEntity b : ProcessEntity.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
