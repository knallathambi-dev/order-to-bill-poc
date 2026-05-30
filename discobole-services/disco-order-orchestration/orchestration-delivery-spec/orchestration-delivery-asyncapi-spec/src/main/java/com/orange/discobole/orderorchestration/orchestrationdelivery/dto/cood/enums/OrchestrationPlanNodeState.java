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

public enum OrchestrationPlanNodeState {

    INITIALIZED("Initialized"),
    ACKNOWLEDGED("Acknowledged"),
    IN_PROGRESS("InProgress"),
    IN_DELIVERY("InDelivery"),
    HELD("Held"),
    ABORTED("Aborted"),
    REJECTED("Rejected"),
    FAILED("Failed"),
    CANCELED("Canceled"),
    COMPLETED("Completed");
    private final String value;
    private static final Map<String, OrchestrationPlanNodeState> CONSTANTS = new HashMap<>();

    static {
        for (OrchestrationPlanNodeState c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    OrchestrationPlanNodeState(String value) {
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
    public static OrchestrationPlanNodeState fromValue(String value) {
        OrchestrationPlanNodeState constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}