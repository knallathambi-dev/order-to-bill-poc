// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductStatusType {
    ABORTED("aborted"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    CREATED("created"),
    TERMINATED("terminated"),
    PENDINGACTIVE("pendingActive"),
    PENDINGTERMINATE("pendingTerminate"),
    SUSPENDED("suspended");

    private final String value;

    ProductStatusType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @JsonCreator
    public static ProductStatusType fromValue(String value) {
        for (ProductStatusType b : values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}