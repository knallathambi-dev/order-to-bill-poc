// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The ProductOrderItemRelationshipType enumeration.
 */
public enum ProductOrderItemRelationshipType {
    BUNDLES("bundles"),
    IS_CHILD("isChild"),
    SELLS("sells"),
    IS_SOLD("isSold"),

    RELIES_ON("reliesOn"),
    IS_PREREQUISITE("isPrerequisite");

    private final String value;

    ProductOrderItemRelationshipType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductOrderItemRelationshipType fromValue(String text) {
        for (ProductOrderItemRelationshipType b : ProductOrderItemRelationshipType.values()) {
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
