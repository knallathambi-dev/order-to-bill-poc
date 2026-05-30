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
import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;


/**
 * Possible values for the state of the order
 * 
 */
@Generated("jsonschema2pojo")
public enum ServiceOrderStateType {

    ACKNOWLEDGED("acknowledged"),
    REJECTED("rejected"),
    PENDING("pending"),
    HELD("held"),
    IN_PROGRESS("inProgress"),
    CANCELLED("cancelled"),
    COMPLETED("completed"),
    FAILED("failed"),
    PARTIAL("partial"),
    ASSESSING_CANCELLATION("assessingCancellation"),
    PENDING_CANCELLATION("pendingCancellation");
    private final String value;
    private final static Map<String, ServiceOrderStateType> CONSTANTS = new HashMap<String, ServiceOrderStateType>();

    static {
        for (ServiceOrderStateType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ServiceOrderStateType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static ServiceOrderStateType fromValue(String value) {
        ServiceOrderStateType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
