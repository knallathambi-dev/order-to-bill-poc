// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.validations.OrchestrationPlanNodeValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanNodeValidationService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;

@Service
public class OrchestrationPlanNodeValidationServiceImpl implements OrchestrationPlanNodeValidationService {

    private static final Map<OrchestrationPlanNodeState, Set<OrchestrationPlanNodeState>> ORCHESTRATION_PLAN_NODE_EXPECTED_STATE = Map.of(
            INITIALIZED, Set.of(REJECTED, ACKNOWLEDGED),
            ABORTED, Set.of(ABORTED),
            ACKNOWLEDGED, Set.of(IN_PROGRESS, ABORTED),
            IN_PROGRESS, Set.of(IN_DELIVERY, HELD, COMPLETED),
            IN_DELIVERY, Set.of(COMPLETED, FAILED, HELD),
            COMPLETED, Set.of(COMPLETED),
            FAILED, Set.of(FAILED),
            HELD, Set.of(IN_DELIVERY, COMPLETED, FAILED, IN_PROGRESS),
            REJECTED, Set.of()
    );

    @Override
    public void validateStateUpdate(OrchestrationPlanNode node, OrchestrationPlanNodeState newState) {
        if (!ORCHESTRATION_PLAN_NODE_EXPECTED_STATE.get(node.getState()).contains(newState)) {
            throw new OrchestrationPlanNodeValidationException(ExceptionCode.ORCHESTRATION_PLAN_NODE_STATE_NOT_VALID, node.getState(), newState);
        }
    }
}
