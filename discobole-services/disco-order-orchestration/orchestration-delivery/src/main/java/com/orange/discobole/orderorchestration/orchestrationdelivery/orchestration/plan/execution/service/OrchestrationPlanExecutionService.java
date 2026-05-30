// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;


public interface OrchestrationPlanExecutionService {
    OrchestrationPlanNode findOrchestrationPlanNode(OrchestrationPlan orchestrationPlan, String serviceOrder, String serviceOrderItemId);

    void updateOrchestrationPlanNodeStatusBasedOnServiceOrder(ServiceOrder serviceOrder, OrchestrationPlanNode orchestrationPlanNode);

   void executeLeaf(OrchestrationPlan orchestrationPlan);

    void executeOrchestrationPlanNode(OrchestrationPlanNode node);
}
