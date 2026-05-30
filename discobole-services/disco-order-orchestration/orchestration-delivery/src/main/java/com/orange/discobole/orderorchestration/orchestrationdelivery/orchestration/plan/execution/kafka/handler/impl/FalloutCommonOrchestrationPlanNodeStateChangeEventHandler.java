// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl;

import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProcessFlowManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.FalloutOrchestrationPlanNodeStateChangeEventHandler;
 import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class FalloutCommonOrchestrationPlanNodeStateChangeEventHandler implements FalloutOrchestrationPlanNodeStateChangeEventHandler {
    protected final ProcessFlowManagement processFlowManagement;

    private final List<String> externalServiceTopics = List.of(
            KafkaTopic.SERVICE_ORDER_STATE_CHANGE_TOPIC,
            KafkaTopic.SHIPPING_ORDER_STATE_CHANGE_TOPIC
    );

    protected boolean shouldHandleEvent(OrchestrationPlanNode node, Map<String, String> headers) {
        if (Objects.isNull(headers) || !headers.containsKey(Headers.SOURCE_TOPIC_NAME) || !externalServiceTopics.contains(headers.get(Headers.SOURCE_TOPIC_NAME))) {
            return false;
        }

        boolean isPreviousStateHeld = OrchestrationPlanNodeState.HELD.equals(node.getPreviousState());
        if (!isPreviousStateHeld) {
            return false;
        }

        List<ProcessFlow> processFlows = processFlowManagement.getProcessFlowByRelatedEntityId(node.getId());
        return !processFlows.isEmpty();
    }

}
