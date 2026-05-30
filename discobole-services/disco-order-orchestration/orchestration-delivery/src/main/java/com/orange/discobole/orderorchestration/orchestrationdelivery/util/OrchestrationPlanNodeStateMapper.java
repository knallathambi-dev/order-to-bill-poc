// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItem;

import java.util.Map;

public class OrchestrationPlanNodeStateMapper {

    private static final Map<ServiceOrderItem.State, OrchestrationPlanNodeState> ORCHESTRATION_PLAN_NODE_STATE_MAP;

    static {
        ORCHESTRATION_PLAN_NODE_STATE_MAP = Map.of(
                ServiceOrderItem.State.HELD, OrchestrationPlanNodeState.HELD,
                ServiceOrderItem.State.FAILED, OrchestrationPlanNodeState.FAILED,
                ServiceOrderItem.State.COMPLETED, OrchestrationPlanNodeState.COMPLETED
        );
    }

    public static OrchestrationPlanNodeState getFromServiceOrderItemStateOrDefault(ServiceOrderItem.State serviceOrderItemState, OrchestrationPlanNodeState orchestrationPlanNodeState) {
        return ORCHESTRATION_PLAN_NODE_STATE_MAP.getOrDefault(serviceOrderItemState, orchestrationPlanNodeState);
    }
}
