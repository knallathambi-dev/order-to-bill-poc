// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl.OrchestrationPlanServiceImpl;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventHandlerImpl implements UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler {

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public void handleEvent(OrchestrationPlanNodeStateChangeEvent message) {
        OrchestrationPlanNode eventNode = message.getEvent().getOrchestrationPlanNode();

        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByOrchestrationPlanNodes_id(eventNode.getId()).orElseThrow(
                        () -> new CoodNonRecoverableAndNonRetryableException(
                                new OrchestrationPlanNodeNotFoundException(
                                        ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND,
                                        eventNode.getId()))
                );


        State currentState = orchestrationPlan.getState();

        if (!currentState.equals(State.IN_PROGRESS) && !currentState.equals(State.HELD)) {
            return;
        }

        PlanNodeCounts nodeStatesCount = countNodeStates(orchestrationPlan.getOrchestrationPlanNodes());

        Optional.ofNullable(deducePlanState(nodeStatesCount))
                .filter(newState -> newState != currentState)
                .ifPresent(newState -> {
                    if (newState == State.HELD) {
                        orchestrationPlan.getOrchestrationPlanSchedule().setEstimatedOrderDeliveryLeadTime(OrchestrationPlanServiceImpl.UNDEFINED_LONG_VALUE);
                    }
                    orchestrationPlan.setState(newState);
                    orchestrationPlanRepository.save(orchestrationPlan);
                    eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, orchestrationPlan);
                });
    }

    private State deducePlanState(PlanNodeCounts counts) {
        if (counts.executedCount() == counts.totalCount()) {
            return State.EXECUTED;
        }
        if (isPlanHeldState(counts)) {
            return State.HELD;
        }
        if (isPlanInProgressState(counts)) {
            return State.IN_PROGRESS;
        }
        return null;
    }

    private boolean isPlanHeldState(PlanNodeCounts counts) {
        return counts.heldCount() > 0 &&
                counts.inProgressCount() == 0 &&
                counts.inDeliveryCount() == 0;
    }

    private boolean isPlanInProgressState(PlanNodeCounts counts) {
        return counts.inProgressCount() > 0 ||
               counts.inDeliveryCount() > 0;
    }

    private PlanNodeCounts countNodeStates(Set<OrchestrationPlanNode> orchestrationNodes) {
        int executedCount = 0;
        int heldCount = 0;
        int ackCount = 0;
        int inProgressCount = 0;
        int inDeliveryCount = 0;
        int totalCount = orchestrationNodes.size();

        for (OrchestrationPlanNode node : orchestrationNodes) {
            switch (node.getState()) {
                case COMPLETED, FAILED, ABORTED -> executedCount++;
                case ACKNOWLEDGED -> ackCount++;
                case HELD -> heldCount++;
                case IN_PROGRESS -> inProgressCount++;
                case IN_DELIVERY -> inDeliveryCount++;
                default -> {
                }
            }
        }

        return new PlanNodeCounts(executedCount, heldCount, ackCount, inDeliveryCount, inProgressCount, totalCount);
    }

    private record PlanNodeCounts(int executedCount, int heldCount, int ackCount, int inDeliveryCount,
                                  int inProgressCount, int totalCount) {
    }
}
