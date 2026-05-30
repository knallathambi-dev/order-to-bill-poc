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

import java.util.HashMap;
import java.util.Map;

public enum RelatedOrchestrationPlanNodeRelationshipType {

    DELIVER_WITH("DeliverWith"),
    DELIVER_AFTER("DeliverAfter");

    private final String value;
    private static final Map<String, RelatedOrchestrationPlanNodeRelationshipType> CONSTANTS = new HashMap<>();

    static {
        for (RelatedOrchestrationPlanNodeRelationshipType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RelatedOrchestrationPlanNodeRelationshipType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static RelatedOrchestrationPlanNodeRelationshipType fromValue(String value) {
        RelatedOrchestrationPlanNodeRelationshipType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
