// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;

import java.util.Map;

public interface DeliverSelectedNodesService {

    void startDeliverSelectedNode(OrchestrationPlan orchestrationPlan, OrchestrationPlanNode node);

    void postDeliverNodeActions(DeliveryOrderItemStatusPayloadEvent node, Map<String, String> headers);

    boolean shouldStartDeliveryForNode(OrchestrationPlanNode node, OrchestrationPlan orchestrationPlan);

    void updateNodeWithError(OrchestrationPlanNode orchestrationPlanNode);
}
