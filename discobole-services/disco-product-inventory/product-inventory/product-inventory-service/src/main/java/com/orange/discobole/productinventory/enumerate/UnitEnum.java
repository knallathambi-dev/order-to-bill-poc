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
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import lombok.Getter;

@Getter
public enum UnitEnum {
    DAY("Day"),
    MONTH("Month"),
    HOUR("Hour");

    private final String value;

    UnitEnum(String value) {
        this.value = value;
    }

    public static boolean isValidUnit(String unitOfMeasure) {
        for (UnitEnum unit : values()) {
            if (unit.getValue().equalsIgnoreCase(unitOfMeasure)) {
                return true;
            }
        }
        return false;
    }

    @JsonCreator
    public static UnitEnum fromValue(String value) throws ProductInventoryException {
        for (UnitEnum type : UnitEnum.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ProductInventoryException("Invalid Unite Enum value: " + value);
    }
}
