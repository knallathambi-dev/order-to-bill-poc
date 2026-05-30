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
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Possible values for the status of the product
 **/


@Schema(description = "Possible values for the status of the product")
public enum ProductStatusTypeEnum {


    CREATED("Created"),
    CANCELLED("Cancelled"),
    ACTIVE("Active"),
    TERMINATED("Terminated"),
    ABORTED("Aborted");

    private final String value;

    ProductStatusTypeEnum(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductStatusTypeEnum fromValue(String status) {
        for (ProductStatusTypeEnum statusEnum : ProductStatusTypeEnum.values()) {
            if (statusEnum.value.equalsIgnoreCase(status)) {
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



