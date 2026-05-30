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

public enum ProductSpecificationRelationshipType {

    PREREQUISITE("prerequisite"),
    INCOMPATIBLE("incompatible"),

    RELIES_ON("reliesOn"),
    RELIES_FROM("reliesFrom"),
    REQUIRES("requires");


    private final String value;

    ProductSpecificationRelationshipType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductSpecificationRelationshipType fromValue(String text) {
        for (ProductSpecificationRelationshipType b : ProductSpecificationRelationshipType.values()) {
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
