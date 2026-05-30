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
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProcessFlowManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FalloutCompletedOrchestrationPlanNodeStateChangeEventHandlerImpl extends FalloutCommonOrchestrationPlanNodeStateChangeEventHandler {

    public FalloutCompletedOrchestrationPlanNodeStateChangeEventHandlerImpl(ProcessFlowManagement processFlowManagement) {
        super(processFlowManagement);
    }

    @Override
    public void handleEvent(OrchestrationPlanNode node, Map<String, String> headers) {
        if (!OrchestrationPlanNodeState.COMPLETED.equals(node.getState())) {
            throw new CoodNonRecoverableAndNonRetryableException(new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "invalid parameter"));
        }

        if (!shouldHandleEvent(node, headers)) {
            return;
        }

        boolean isPreviousStateHeld = OrchestrationPlanNodeState.HELD.equals(node.getPreviousState());

        List<ProcessFlow> processFlows = processFlowManagement.getProcessFlowByRelatedEntityId(node.getId());

        if (isPreviousStateHeld) {
            String nextTaskUrl = processFlows.get(0).getLinks().getNextTaskstoBePerformed().get(0).getHref();
            processFlowManagement.submitResolutionSateWithReason(nextTaskUrl, ResolutionState.SKIPPED, "automatic resolution");
        }
    }
}
