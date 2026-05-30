// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.ResolutionState;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProcessFlowManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.FalloutOrchestrationPlanNodeStateChangeEventHandler;
 import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FalloutHeldOrchestrationPlanNodeStateChangeEventHandlerImpl implements FalloutOrchestrationPlanNodeStateChangeEventHandler {

    private final ProcessFlowManagement processFlowManagement;

    @Value("${config.heldNodeProcessFlowCountThreshold}")
    private int heldNodeProcessFlowCountThreshold;

    private final List<String> externalServiceTopics = List.of(
            KafkaTopic.SERVICE_ORDER_STATE_CHANGE_TOPIC,
            KafkaTopic.SHIPPING_ORDER_STATE_CHANGE_TOPIC
    );

    private boolean shouldHandleEvent(OrchestrationPlanNode node, Map<String, String> headers) {
        if (!headers.containsKey(Headers.SOURCE_TOPIC_NAME) || !externalServiceTopics.contains(headers.get(Headers.SOURCE_TOPIC_NAME))) {
            return false;
        }

        List<ProcessFlow> processFlows = processFlowManagement.getProcessFlowByRelatedEntityId(node.getId());
        return !processFlows.isEmpty() && processFlows.size() >= heldNodeProcessFlowCountThreshold;
    }

    @Override
    public void handleEvent(OrchestrationPlanNode node, Map<String, String> headers) {
        if (!OrchestrationPlanNodeState.HELD.equals(node.getState())) {
            throw new CoodNonRecoverableAndNonRetryableException(new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "invalid parameter"));
        }

        if (!shouldHandleEvent(node, headers)) {
            return;
        }

        List<ProcessFlow> processFlows = processFlowManagement.getProcessFlowByRelatedEntityId(node.getId());

        IntStream.range(0, processFlows.size()).forEach(index -> {
            String nextTaskUrl = processFlows.get(index).getLinks().getNextTaskstoBePerformed().get(0).getHref();
            processFlowManagement.submitResolutionSateWithReason(nextTaskUrl, index == 0 ? ResolutionState.UNRESOLVED : ResolutionState.SKIPPED, "automatic resolution");
        });
    }
}
