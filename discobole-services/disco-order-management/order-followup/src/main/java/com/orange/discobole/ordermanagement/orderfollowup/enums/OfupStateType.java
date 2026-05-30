// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OfupStateType {

    NEW("new"),
    PENDING("pending"),
    COMPLETED("completed"),
    DEPRECATED("deprecated"),
    FAILED("failed"),
    IN_PROGRESS("inProgress");

    private final String value;

    OfupStateType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static OfupStateType fromValue(String text) {
        for (OfupStateType b : OfupStateType.values()) {
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