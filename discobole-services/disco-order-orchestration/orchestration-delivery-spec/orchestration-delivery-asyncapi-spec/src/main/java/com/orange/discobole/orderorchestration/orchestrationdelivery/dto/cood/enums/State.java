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

public enum State {

    INITIALIZED("Initialized"),
    ACKNOWLEDGED("Acknowledged"),
    PLANNED("Planned"),
    IN_PROGRESS("InProgress"),
    EXECUTED("Executed"),
    HELD("Held"),
    REJECTED("Rejected"),
    ACCEPTED("Accepted"),
    ABORTED("Aborted"),;
    private final String value;
    private static final Map<String, State> CONSTANTS = new HashMap<>();

    static {
        for (State c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    State(String value) {
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
    public static State fromValue(String value) {
        State constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
