// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.orange.discobole.productinventory.exception.ProductInventoryException;


public enum ProductOfferingTypeEnum {
    CONTRACT("Contract"),
    ATOMIC_PRODUCT_OFFERING("AtomicProductOffering"),
    BUNDLE_PRODUCT_OFFERING("BundleProductOffering");

    private final String productOfferingType;

    ProductOfferingTypeEnum(String value) {
        this.productOfferingType = value;
    }

    @JsonCreator
    public static ProductOfferingTypeEnum fromValue(String productOfferingType) throws ProductInventoryException {
        for (ProductOfferingTypeEnum value : ProductOfferingTypeEnum.values()) {
            if (value.productOfferingType.equalsIgnoreCase(productOfferingType)) {
                return value;
            }
        }
        throw new ProductInventoryException("Invalid value: " + productOfferingType);
    }

    @JsonValue
    public String getValue() {
        return productOfferingType;
    }

}



