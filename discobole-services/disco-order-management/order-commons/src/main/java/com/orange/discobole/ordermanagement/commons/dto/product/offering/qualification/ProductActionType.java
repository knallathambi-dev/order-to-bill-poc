// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.dto.product.offering.qualification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductActionType {
    ADD("add"),
    MODIFY("modify"),
    DELETE("delete"),
    NOCHANGE("noChange");

    private final String action;

    ProductActionType(String action) {
        this.action = action;
    }

    @JsonCreator
    public static ProductActionType fromValue(String action) {
        for (ProductActionType b : ProductActionType.values()) {
            if (String.valueOf(b.action).equals(action)) {
                return b;
            }
        }
        return null;
    }

    @JsonValue
    public String getAction() {
        return action;
    }
}