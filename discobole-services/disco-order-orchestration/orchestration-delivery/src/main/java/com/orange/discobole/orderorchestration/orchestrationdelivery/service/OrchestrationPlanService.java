// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;

public interface OrchestrationPlanService {
    void updateOrchestrationPlanNodeStateWithErrorMessage(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlanNodeState state);

    void setNextOrchestrationPlanNodeState(OrchestrationPlanNode orchestrationPlanNode);

    void updateOrchestrationPlanNodeState(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlanNodeState state);

    void updateNodeRelatedProducts(OrchestrationPlanNode node) throws CoodDBException;

    void savePlan(OrchestrationPlan orchestrationPlan);

    void updateOrchestrationPlan(OrchestrationPlan orchestrationPlan, State state);

    void updateNodeDataInOrchestrationPlan(OrchestrationPlanNode updatedNode) throws CoodDBException;
}
