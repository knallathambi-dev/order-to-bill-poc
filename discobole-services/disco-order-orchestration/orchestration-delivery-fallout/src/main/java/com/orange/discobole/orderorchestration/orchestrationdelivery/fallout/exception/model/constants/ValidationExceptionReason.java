// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants;

public enum ValidationExceptionReason {
    INVALID_PARAMETER("Invalid parameter"),
    UTILITY_CLASS("Utility class"),
    FALLOUT_QUERY_PARAMETER_EMPTY_OR_NULL("Query param [ Fields ] should not be empty or null");
    private final String value;

    ValidationExceptionReason(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
