// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductMainStatus {
    INITIAL("initial"),
    CREATED("Created"),
    CANCELLED("Cancelled"),
    ABORTED("Aborted"),
    ACTIVE("Active"),
    TERMINATED("Terminated"),
    SOLD("Sold"),
    FINAL("Final");

    private final String value;

    ProductMainStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductMainStatus fromValue(String status) {
        for (ProductMainStatus statusEnum : ProductMainStatus.values()) {
            if (String.valueOf(statusEnum.value).equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }


    @JsonValue
    public String getStatus() {
        return value;
    }

}
