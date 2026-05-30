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

public enum ProductOfferingPriceLifecycle {
    LAUNCHED("launched"),
    UNAVAILABLE("unavailable"),
    RETIRED("retired"),
    OBSOLETE("obsolete"),
    INSTUDY("inStudy");

    private final String value;

    ProductOfferingPriceLifecycle(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductOfferingPriceLifecycle fromValue(String text) {
        for (ProductOfferingPriceLifecycle b : ProductOfferingPriceLifecycle.values()) {
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