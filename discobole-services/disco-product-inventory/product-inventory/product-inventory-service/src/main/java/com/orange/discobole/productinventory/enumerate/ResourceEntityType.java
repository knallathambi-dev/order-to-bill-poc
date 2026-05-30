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

public enum ResourceEntityType {

    LOGICAL_RESOURCE("LogicalResource"),
    PHYSICAL_RESOURCE("PhysicalResource");
    private final String value;

    ResourceEntityType(String value) {
        this.value = value;
    }


    @JsonCreator
    public static ResourceEntityType fromValue(String type) throws ProductInventoryException {
        for (ResourceEntityType resourceEntityType : ResourceEntityType.values()) {
            if (resourceEntityType.value.equalsIgnoreCase(type)) {
                return resourceEntityType;
            }
        }
        throw new ProductInventoryException("Invalid Resource Entity Type value: " + type);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
