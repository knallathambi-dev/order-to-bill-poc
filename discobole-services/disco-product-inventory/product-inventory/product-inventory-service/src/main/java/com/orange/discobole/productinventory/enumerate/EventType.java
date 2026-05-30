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

public enum EventType {
    PRODUCT_ATTRIBUTE_VALUE_CHANGE_EVENT("ProductAttributeValueChangeEvent"), PRODUCT_STATE_CHANGE_EVENT("ProductStateChangeEvent"),
    PRODUCT_DELETE_EVENT("ProductDeleteEvent"), PRODUCT_CREATE_EVENT("ProductCreateEvent");
    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EventType fromValue(String value) throws ProductInventoryException {
        for (EventType type : EventType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ProductInventoryException("Invalid Event type value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
