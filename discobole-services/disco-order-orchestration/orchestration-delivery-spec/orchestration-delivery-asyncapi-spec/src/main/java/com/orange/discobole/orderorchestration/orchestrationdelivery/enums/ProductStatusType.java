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

/**
 * The ProductStatusType enumeration.
 */
public enum ProductStatusType {
    CREATED("created"),
    PENDING_ACTIVE("pendingActive"),
    CANCELLED("cancelled"),
    ACTIVE("active"),
    PENDING_TERMINATE("pendingTerminate"),

    TERMINATED("terminated"),
    SUSPENDED("suspended"),
    ABORTED("aborted");

    private final String value;

    ProductStatusType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductStatusType fromValue(String text) {
        for (ProductStatusType b : ProductStatusType.values()) {
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
