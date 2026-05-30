// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.repository.ReactiveOrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;

/**
 * Scheduler that processes root orchestration nodes with future
 * orderItemStartDate.
 * Runs hourly to check for nodes whose start time has arrived and transitions
 * them to IN_PROGRESS.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class RootNodeStartTimeScheduler {

    private final ReactiveOrchestrationPlanRepository reactiveOrchestrationPlanRepository;
    private final OrchestrationPlanService orchestrationPlanService;

    /**
     * Processes root nodes with passed orderItemStartDate.
     * It runs according to the configured cron expression (default: every hour).
     */
    @Scheduled(cron = "${config.rootNodeStartTimeCronExpression}")
    @Transactional
    public void processRootNodesWithPassedStartTime() {
        Instant processRootNodesStartTime = Instant.now();
        log.info(
                "Starting scheduled task to process root nodes with passed start time at {}",
                processRootNodesStartTime);

       Flux<OrchestrationPlan> plansToProcessFlux = reactiveOrchestrationPlanRepository
                .findOrchestrationPlanByStateAndOrchestrationPlanNodes_StateAndOrchestrationPlanNodes_OrchestrationNodeSchedule_OrderItemStartDateIsBefore(
                       State.IN_PROGRESS, OrchestrationPlanNodeState.ACKNOWLEDGED, processRootNodesStartTime);

        plansToProcessFlux
                .flatMap(plan -> {
                    Set<OrchestrationPlanNode> initialNodes = plan.getLeafs();

                    if (initialNodes == null || initialNodes.isEmpty()) {
                        log.debug("Plan {} has no root nodes",
                                plan.getId());
                    }

                    for (OrchestrationPlanNode node : initialNodes) {
                        if (shouldTransitionNodeToInProgress(node, plan, processRootNodesStartTime)) {
                            node.setState(OrchestrationPlanNodeState.IN_PROGRESS);
                            orchestrationPlanService.updateNodeDataInOrchestrationPlan(node);
                        }
                    }

                    return Mono.empty();
                })
                .subscribe();
    }

    /**
     * Checks if a node should be transitioned to IN_PROGRESS.
     *
     * @param node the orchestration plan node
     * @param now  current timestamp
     * @return true if the node should transition to IN_PROGRESS
     */
    private boolean shouldTransitionNodeToInProgress(OrchestrationPlanNode node, OrchestrationPlan orchestrationPlan, Instant now) {
        // Only process acknowledged nodes in inProgress plans
        if (node.getState() != OrchestrationPlanNodeState.ACKNOWLEDGED) {
            return false;
        }

        // exclude the longest branch root node because it's already handled in the plan transition handler
        // (avoiding race condition)
        boolean isNodeStartTimeEqualToPlanStartTime = node.getOrchestrationNodeSchedule().getOrderItemStartDate().equals(orchestrationPlan.getOrchestrationPlanSchedule().getOrderStartDate());
        if (isNodeStartTimeEqualToPlanStartTime) {
            return false;
        }

            // Check if the start date is now or has passed
        Instant orderItemStartDate = node.getOrchestrationNodeSchedule().getOrderItemStartDate();
        return !orderItemStartDate.isAfter(now);
    }
}
