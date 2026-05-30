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
 * Valid values for the lifecycle state of the service
 * 
 */
@Generated("jsonschema2pojo")
public enum ServiceStateType {

    FEASIBILITY_CHECKED("feasibilityChecked"),
    DESIGNED("designed"),
    RESERVED("reserved"),
    INACTIVE("inactive"),
    ACTIVE("active"),
    TERMINATED("terminated");
    private final String value;
    private final static Map<String, ServiceStateType> CONSTANTS = new HashMap<String, ServiceStateType>();

    static {
        for (ServiceStateType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ServiceStateType(String value) {
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
    public static ServiceStateType fromValue(String value) {
        ServiceStateType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
