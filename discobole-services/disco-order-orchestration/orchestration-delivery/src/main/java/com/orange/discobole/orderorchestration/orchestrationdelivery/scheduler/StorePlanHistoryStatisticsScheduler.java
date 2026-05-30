// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.ContractLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.repository.ReactiveOrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.ContractLeadTimeHistoryStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class StorePlanHistoryStatisticsScheduler {

    @Value("${config.storePlanStatisticsDuration}")
    private Duration storePlanStatisticsDuration;

    private final ContractLeadTimeHistoryStatisticsRepository contractLeadTimeHistoryStatisticsRepository;

    private final ReactiveOrchestrationPlanRepository reactiveOrchestrationPlanRepository;

    private static Predicate<OrchestrationPlan> hasRelatedContract() {
        return p -> p != null
                && p.getRelatedContractName() != null
                && !p.getRelatedContractName().isBlank();
    }

    private static Predicate<OrchestrationPlan> allNodesCompleted() {
        return p -> {
            Set<OrchestrationPlanNode> nodes = p.getOrchestrationPlanNodes();
            return nodes != null
                    && !nodes.isEmpty()
                    && nodes.stream().allMatch(n -> n != null && n.getState() == OrchestrationPlanNodeState.COMPLETED);
        };
    }

    @Scheduled(fixedRateString = "${config.storePlanStatisticsDuration}")
    public void storePlanStatistics() {
        final int CHUNK_SIZE = 100;

        Instant sampleWindowDate = Instant.now();

        // get executed orchestration plans that contains nodes with actual completion date after last sample window
        Flux<OrchestrationPlan> executedPlansFlux =
                reactiveOrchestrationPlanRepository.findOrchestrationPlanByStateAndOrchestrationPlanNodes_OrchestrationNodeSchedule_ActualOrderItemCompletionDateIsAfter(
                        State.EXECUTED, sampleWindowDate.minus(storePlanStatisticsDuration)
                );

        executedPlansFlux
                .limitRate(CHUNK_SIZE)
                .filter(hasRelatedContract().and(allNodesCompleted()))
                .buffer(CHUNK_SIZE)
                .subscribe(orchestrationPlanList -> {
                    Map<String, List<OrchestrationPlan>> plansByContractName = orchestrationPlanList.stream()
                            .collect(Collectors.groupingBy(
                                    OrchestrationPlan::getRelatedContractName
                            ));

                    plansByContractName.forEach((contractName, plans) -> {
                        LongSummaryStatistics deliveryTimeSummaryStatistics = plans.stream().mapToLong(plan -> plan.getOrchestrationPlanSchedule().getActualOrderDeliveryLeadTime()).summaryStatistics();
                        ContractLeadTimeHistorySampledStatistics statistics =
                                ContractLeadTimeHistorySampledStatistics.builder()
                                        .contractName(contractName)
                                        .minActualLeadTime(deliveryTimeSummaryStatistics.getMin())
                                        .maxActualLeadTime(deliveryTimeSummaryStatistics.getMax())
                                        .averageActualLeadTime((float) deliveryTimeSummaryStatistics.getAverage())
                                        .sampleSize(plans.size())
                                        .sampleWindow(sampleWindowDate)
                                        .build();

                        contractLeadTimeHistoryStatisticsRepository.save(statistics);
                    });
                });
    }
}
