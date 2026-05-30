// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model.job.enumerate;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessedProductStatusEnum {
    FAILED("Failed"),
    COMPLETED("Completed");
    private final String value;

    ProcessedProductStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ProcessedProductStatusEnum fromValue(String value) {
        for (ProcessedProductStatusEnum status : ProcessedProductStatusEnum.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
