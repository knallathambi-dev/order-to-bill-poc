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
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.ProductUpdateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateProductBasedOnOrchestrationPlanNodeStatusHandler;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;

@Component
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UpdateProductBasedOnOrchestrationPlanNodeStatusHandlerImpl implements UpdateProductBasedOnOrchestrationPlanNodeStatusHandler {

    private final Map<OrchestrationPlanNodeState, ProductUpdateHandler> productUpdateHandlers;


    public UpdateProductBasedOnOrchestrationPlanNodeStatusHandlerImpl(CompletedNodeProductUpdateHandler completedHandler,
                                                                      FailedNodeProductUpdateHandler failedHandler,
                                                                      InDeliveryNodeProductUpdateHandler inDeliveryHandler,
                                                                      AbortedNodeProductUpdateHandler abortedHandler,
                                                                      HeldNodeProductUpdateHandler heldHandler) {
        productUpdateHandlers = Map.of(
                COMPLETED, completedHandler,
                FAILED, failedHandler,
                IN_DELIVERY, inDeliveryHandler,
                ABORTED, abortedHandler,
                HELD, heldHandler
        );
    }


    @Override
    public void handle(OrchestrationPlanNode node) {
        Optional.ofNullable(productUpdateHandlers.get(node.getState()))
                .ifPresent(handler -> handler.update(node));
    }
}
