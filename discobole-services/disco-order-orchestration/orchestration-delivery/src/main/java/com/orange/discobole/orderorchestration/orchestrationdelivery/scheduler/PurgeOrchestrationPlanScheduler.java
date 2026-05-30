// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler;


import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.ABORTED;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.FAILED;

@ConditionalOnProperty(
        value = "config.enableOrchestrationPlanPurgeCronExpression",
        havingValue = "true",
        matchIfMissing = true
)
@Component
@RequiredArgsConstructor
@FieldNameConstants
public class PurgeOrchestrationPlanScheduler {

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    @Value("${config.deletePlanThresholdDuration}")
    private Duration deletePlanThreshold;

    @Value("${config.archivePlanThresholdDuration}")
    private Duration archivePlanThreshold;

    @Value("${config.deleteArchivedPlanThresholdDuration}")
    private Duration deleteArchivedPlanThreshold;

    @Scheduled(cron = "${config.orchestrationPlanPurgeCronExpression}")
    public void startPurgeOrchestrationPlan() {
        List<OrchestrationPlan> orchestrationPlans = orchestrationPlanRepository.findOrchestrationPlanByStateIn(List.of(State.EXECUTED, State.ABORTED, State.REJECTED));
        orchestrationPlans.forEach(orchestrationPlan -> {
            if (Objects.isNull(orchestrationPlan.getLastModifiedDate())) {
                return;
            }
            boolean containsFailedOrAbortedNodes = orchestrationPlan.getOrchestrationPlanNodes().stream().anyMatch(orchestrationPlanNode -> List.of(FAILED, ABORTED).contains(orchestrationPlanNode.getState()));
            long timeDiffInSeconds = ChronoUnit.SECONDS.between(Objects.nonNull(orchestrationPlan.getLastModifiedDate()) ? orchestrationPlan.getLastModifiedDate() : Instant.now(), Instant.now());
            boolean isExecutedPlanNotContainsFailedOrAbortedNodesAndLastModifiedDateAfterThreshold = State.EXECUTED.equals(orchestrationPlan.getState()) && !containsFailedOrAbortedNodes && (timeDiffInSeconds >= deletePlanThreshold.getSeconds());
            boolean isPlanAbortedOrRejectedOrContainsFailedOrAbortedNodes = containsFailedOrAbortedNodes || List.of(State.ABORTED, State.REJECTED).contains(orchestrationPlan.getState());
            boolean isPlanAbortedOrRejectedOrContainsFailedOrAbortedNodesAndLastModifiedDateAfterThreshold = isPlanAbortedOrRejectedOrContainsFailedOrAbortedNodes && (timeDiffInSeconds >= archivePlanThreshold.getSeconds());
            boolean isPlanArchivedAndLastModifiedDateAfterThreshold = orchestrationPlan.getArchived() && (timeDiffInSeconds >= deleteArchivedPlanThreshold.getSeconds());
            if (isPlanArchivedAndLastModifiedDateAfterThreshold || isExecutedPlanNotContainsFailedOrAbortedNodesAndLastModifiedDateAfterThreshold) {
                orchestrationPlanRepository.delete(orchestrationPlan);
            } else if (isPlanAbortedOrRejectedOrContainsFailedOrAbortedNodesAndLastModifiedDateAfterThreshold) {
                orchestrationPlan.setArchived(true);
                orchestrationPlanRepository.save(orchestrationPlan);
            }
        });
    }
}
