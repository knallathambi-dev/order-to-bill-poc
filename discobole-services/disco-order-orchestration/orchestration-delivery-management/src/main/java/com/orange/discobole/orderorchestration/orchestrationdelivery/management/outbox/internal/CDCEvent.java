// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal;


import com.orange.discobole.orderorchestration.outbox.internal.CDCEventInterface;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;

public enum CDCEvent implements CDCEventInterface {
    DELIVERY_ORDER_ITEM_STATUS_EVENT(EventType.DELIVERY_ORDER_ITEM_STATUS_EVENT, "disco.delivery-management.deliveryOrderItemStatus-event"),
    DELIVERY_ORDER_DLT_EVENT(EventType.DELIVERY_ORDER_EVENT, "disco.delivery-management.deliveryOrder-event-dlt"),
    SERVICE_ORDER_STATE_CHANGE_EVENT(EventType.SERVICE_ORDER_STATE_CHANGE_EVENT, "disco.service-order-management.serviceOrderStateChange-event"),
    SHIPPING_ORDER_STATE_CHANGE_EVENT(EventType.SHIPPING_ORDER_STATE_CHANGE_EVENT, "disco.shipping-order-management.shippingOrderStateChange-event");

    private final EventType type;

    private final String topicName;

    CDCEvent(EventType type, String topicName) {
        this.type = type;
        this.topicName = topicName;
    }

    public static CDCEvent fromTopicName(String topicName) {
        for (CDCEvent event : CDCEvent.values()) {
            if (event.getTopicName().equals(topicName)) {
                return event;
            }
        }
        throw new IllegalArgumentException("No enum constant with topicName " + topicName);
    }

    @Override
    public String getType() {
        return type.getValue();
    }

    @Override
    public String getTopicName() {
        return topicName;
    }
}
