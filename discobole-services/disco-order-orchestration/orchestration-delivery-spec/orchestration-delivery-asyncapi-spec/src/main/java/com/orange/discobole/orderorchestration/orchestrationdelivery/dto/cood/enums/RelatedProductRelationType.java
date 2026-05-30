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
public enum RelatedProductRelationType {
    DELIVERS("delivers"),
    RELIES_ON("reliesOn"),
    RELIES_FROM("reliesFrom"),
    MIGRATED_FROM("migratedFrom"),
    DELIVER_WITH("deliverWith");

    private final String value;

    RelatedProductRelationType(String value) {
        this.value = value;
    }

    private static final Map<String, RelatedProductRelationType> CONSTANTS = new HashMap<>();

    static {
        for (RelatedProductRelationType c : values()) {
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
    public static RelatedProductRelationType fromValue(String value) {
        RelatedProductRelationType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}