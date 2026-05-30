// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum RelatedProductType {
    SHIPMENT_PRODUCT("shipmentProduct"),

    CFS("CFS"),

    PHYSICAL_PRODUCT("physicalProduct");

    private final String value;

    RelatedProductType(String value) {
        this.value = value;
    }

    private static final Map<String, RelatedProductType> CONSTANTS = new HashMap<>();

    static {
        for (RelatedProductType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RelatedProductType fromValue(String value) {
        RelatedProductType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
