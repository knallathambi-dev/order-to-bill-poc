// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Action {
    READ("read"),

    READ_AND_WRITE("readAndWrite"),

    MODIFY("Modify"),

    LIFECYCLE_CHANGE("LifecycleChange"),

    CONSULTATION("Consultation"),

    CREATE("Create");

    private final String value;

    Action(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Action fromValue(String text) {
        for (Action b : Action.values()) {
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
