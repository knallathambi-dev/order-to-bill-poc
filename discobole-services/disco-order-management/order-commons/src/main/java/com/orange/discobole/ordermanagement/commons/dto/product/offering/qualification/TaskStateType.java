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

public enum TaskStateType {
    ACKNOWLEDGED("acknowledged"),
    TERMINATEDWITHERROR("terminatedWithError"),
    INPROGRESS("inProgress"),
    DONE("done"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    APPROVED("approved");

    private final String state;

    TaskStateType(String state) {
        this.state = state;
    }

    @JsonCreator
    public static TaskStateType fromValue(String state) {
        for (TaskStateType b : TaskStateType.values()) {
            if (String.valueOf(b.state).equals(state)) {
                return b;
            }
        }
        return null;
    }

    @JsonValue
    public String getState() {
        return state;
    }
}