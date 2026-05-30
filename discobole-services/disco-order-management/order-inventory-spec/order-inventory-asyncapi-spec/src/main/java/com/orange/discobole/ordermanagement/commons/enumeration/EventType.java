// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.commons.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {

    PRODUCT_ORDER_CREATE_EVENT("ProductOrderCreateEvent"),
    PRODUCT_ORDER_ATTRIBUTE_VALUE_CHANGE_EVENT("ProductOrderAttributeValueChangeEvent"),
    PRODUCT_ORDER_DELETE_EVENT("ProductOrderDeleteEvent"),
    PRODUCT_ORDER_STATE_CHANGE_EVENT("ProductOrderStateChangeEvent"),
    PRODUCT_ORDER_INFORMATION_REQUIRED_EVENT("ProductOrderInformationRequiredEvent"),
    CANCEL_PRODUCT_ORDER_CREATE_EVENT("CancelProductOrderCreateEvent"),
    CANCEL_PRODUCT_ORDER_STATE_CHANGE_EVENT("CancelProductOrderStateChangeEvent"),
    CANCEL_PRODUCT_ORDER_INFORMATION_REQUIRED_EVENT("CancelProductOrderInformationRequiredEvent"),
    ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT("OrchestrationPlanNodeStateChangeEvent");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EventType fromValue(String text) {
        for (EventType b : EventType.values()) {
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