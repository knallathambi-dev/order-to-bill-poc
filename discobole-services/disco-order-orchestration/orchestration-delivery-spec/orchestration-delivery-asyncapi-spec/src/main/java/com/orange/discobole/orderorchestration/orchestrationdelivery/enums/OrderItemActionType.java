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
 * action to be performed on the product
 * 
 */
@Generated("jsonschema2pojo")
public enum OrderItemActionType {

    ADD("add"),
    MODIFY("modify"),
    DELETE("delete"),
    NO_CHANGE("noChange");
    private final String value;
    private final static Map<String, OrderItemActionType> CONSTANTS = new HashMap<String, OrderItemActionType>();

    static {
        for (OrderItemActionType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    OrderItemActionType(String value) {
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
    public static OrderItemActionType fromValue(String value) {
        OrderItemActionType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
