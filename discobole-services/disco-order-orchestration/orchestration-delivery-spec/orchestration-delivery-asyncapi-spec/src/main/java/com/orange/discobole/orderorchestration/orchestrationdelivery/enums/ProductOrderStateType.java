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
 * The ProductOrderStateType enumeration.
 */
public enum ProductOrderStateType {
    DRAFT("draft"),
    ACKNOWLEDGED("acknowledged"),
    ACCEPTED("accepted"),
    IN_PROGRESS("inProgress"),
    HELD("held"),
    PENDING("pending"),
    PENDING_CANCELLATION("pendingCancellation"),
    ASSESSING_CANCELLATION("assessingCancellation"),
    CANCELLED("cancelled"),
    REJECTED("rejected"),
    COMPLETED("completed"),
    FAILED("failed"),
    PARTIAL("partial");

    private final String value;

    ProductOrderStateType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductOrderStateType fromValue(String text) {
        for (ProductOrderStateType b : ProductOrderStateType.values()) {
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
