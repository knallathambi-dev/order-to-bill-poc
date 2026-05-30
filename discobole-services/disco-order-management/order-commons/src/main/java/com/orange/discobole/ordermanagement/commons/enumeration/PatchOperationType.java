// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PatchOperationType {
    TEST("test"),
    REMOVE("remove"),
    ADD("add"),
    REPLACE("replace"),
    MOVE("move"),
    COPY("copy");

    private final String value;

    PatchOperationType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PatchOperationType fromValue(String text) {
        for (PatchOperationType b : PatchOperationType.values()) {
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