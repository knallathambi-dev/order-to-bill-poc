// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateProductBasedOnOrchestrationPlanNodeStatusHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Transactional
// Ensures that updating the node state and firing the orchestration plan node state change event occur within the same transaction
public class UpdateNodeAndProductStateHandlerImpl implements UpdateNodeAndProductStateHandler {

    private final UpdateProductBasedOnOrchestrationPlanNodeStatusHandler updateProductBasedOnOrchestrationPlanNodeStatusHandler;
    private final OrchestrationPlanModificationService orchestrationPlanModificationService;

    private final List<OrchestrationPlanNodeState> allowedStates = List.of(
            COMPLETED,
            FAILED,
            ABORTED,
            IN_DELIVERY,
            HELD
    );

    @Override
    public void update(OrchestrationPlanNode node) {
        this.update(node, Map.of());
    }

    @Override
    public void update(OrchestrationPlanNode node, Map<String, Object> headers) {
        if (!allowedStates.contains(node.getState())) {
            throw new RuntimeException("this state is not allowed " + node.getState());
        }

        // passes the node with the new state to the cpib update handler to update the cpib product based on the state
        updateProductBasedOnOrchestrationPlanNodeStatusHandler.handle(node);

        orchestrationPlanModificationService.updateNodeDataInOrchestrationPlan(node, headers);
    }
}
