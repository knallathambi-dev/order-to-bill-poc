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

public enum CharacteristicsValueType {
    STRING("string"),
    DATE_TIME("DateTime");
    private final String value;

    CharacteristicsValueType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CharacteristicsValueType fromValue(String value) throws ProductInventoryException {
        for (CharacteristicsValueType type : CharacteristicsValueType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ProductInventoryException("Invalid Characteristics Value Type value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}