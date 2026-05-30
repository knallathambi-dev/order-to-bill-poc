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

public enum EventType {
    PRODUCT_ORDER_STATE_CHANGE_EVENT("ProductOrderStateChangeEvent"),
    ORCHESTRATION_PLAN_STATE_CHANGE_EVENT("OrchestrationPlanStateChangeEvent"),
    SERVICE_ORDER_STATE_CHANGE_EVENT("ServiceOrderEvent"),
    ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT("OrchestrationPlanNodeStateChangeEvent"),
    DELIVERY_ORDER_ITEM_STATUS_EVENT("DeliveryOrderItemStatusEvent"),
    DELIVERY_ORDER_EVENT("DeliveryOrderEvent"),
    SHIPPING_ORDER_STATE_CHANGE_EVENT("ShippingOrderStateChangeEvent");


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
