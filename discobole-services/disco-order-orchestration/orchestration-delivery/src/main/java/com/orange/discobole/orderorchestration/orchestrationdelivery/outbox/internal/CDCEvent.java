// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal;

import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.outbox.internal.CDCEventInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CDCEvent implements CDCEventInterface {
    ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT(EventType.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT, KafkaTopic.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_TOPIC),
    ORCHESTRATION_PLAN_STATE_CHANGE_EVENT(EventType.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, KafkaTopic.ORCHESTRATION_PLAN_STATE_CHANGE_TOPIC),
    DELIVERY_ORDER_EVENT(EventType.DELIVERY_ORDER_EVENT, KafkaTopic.DELIVERY_ORDER_TOPIC);

    private final EventType type;

    private final String topicName;

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
