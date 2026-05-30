// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.offering.price;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ProductOfferingPriceType {
    PRODUCTOFFERINGPRICECHARGE("ProductOfferingPriceCharge"),
    PRODUCTOFFERINGPRICEALTERATION("ProductOfferingPriceAlteration");

    private final String value;

    ProductOfferingPriceType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductOfferingPriceType fromValue(String text) {
        for (ProductOfferingPriceType b : ProductOfferingPriceType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}