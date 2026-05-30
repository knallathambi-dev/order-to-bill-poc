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
 * The ProductOrderItemActionType enumeration.
 */
public enum ProductOrderItemActionType {
    ADD("add"),
    MODIFY("modify"),
    DELETE("delete"),
    NO_CHANGE("noChange"),
    MIGRATE("migrate");

    private final String value;

    ProductOrderItemActionType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductOrderItemActionType fromValue(String text) {
        for (ProductOrderItemActionType b : ProductOrderItemActionType.values()) {
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
