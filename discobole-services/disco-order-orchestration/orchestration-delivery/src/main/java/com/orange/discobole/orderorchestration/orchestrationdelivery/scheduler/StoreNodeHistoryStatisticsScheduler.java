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
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.NodeLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.repository.ReactiveOrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.NodeLeadTimeHistoryStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class StoreNodeHistoryStatisticsScheduler {

    private static final String SHIPMENT = "Shipment";

    @Value("${config.storeNodeStatisticsDuration}")
    private Duration storeNodeStatisticsDuration;

    private final NodeLeadTimeHistoryStatisticsRepository nodeLeadTimeHistoryStatisticsRepository;

    private final ReactiveOrchestrationPlanRepository reactiveOrchestrationPlanRepository;

    private ProductSpecification getProductSpecification(OrchestrationPlanNode node) {
        if (node == null || node.getRelatedProduct() == null) {
            return null;
        }

        return node.getActualRelatedProductOptional()
                .orElseThrow(() -> new RuntimeException("no related product for node with id " + node.getId()))
                .getProductSpecification();
    }

    @Scheduled(fixedRateString = "${config.storeNodeStatisticsDuration}")
    public void storeNodeStatistics() {
        Instant sampleWindowDate = Instant.now();

        Flux<OrchestrationPlan> plansHasCompletedNodes =
                reactiveOrchestrationPlanRepository.findByOrchestrationPlanNodes_StateAndOrchestrationPlanNodes_OrchestrationNodeSchedule_ActualOrderItemCompletionDateIsAfter(
                        OrchestrationPlanNodeState.COMPLETED,
                        sampleWindowDate.minus(storeNodeStatisticsDuration)
                );

        plansHasCompletedNodes
                .flatMap(plan -> Flux.fromIterable(plan.getOrchestrationPlanNodes()))
                .filter(node -> node.getState() == OrchestrationPlanNodeState.COMPLETED &&
                        node.getActualRelatedProductOptional().isPresent() &&
                        getProductSpecification(node) != null &&
                        node.getOrchestrationNodeSchedule() != null &&
                        node.getOrchestrationNodeSchedule().getActualOrderItemCompletionDate() != null &&
                        node.getOrchestrationNodeSchedule().getActualOrderItemCompletionDate().isAfter(sampleWindowDate.minus(storeNodeStatisticsDuration))
                )
                .collectList()
                .subscribe(orchestrationPlanNodes -> {
                    Map<AbstractMap.SimpleEntry<String, String>, List<OrchestrationPlanNode>> nodeBySpecId = orchestrationPlanNodes.stream()
                            .filter(node -> getProductSpecification(node).getId() != null)
                            .collect(Collectors.groupingBy(
                                    node ->
                                            new AbstractMap.SimpleEntry<>(
                                                    getProductSpecification(node).getId(),
                                                    node.isCFSOrchestrationPlanNode() ? node.getRelatedServiceOrder().getSomRef() : SHIPMENT)
                            ));

                    nodeBySpecId.forEach((specIdDeliveryFactoryEntry, nodes) -> {
                        LongSummaryStatistics deliveryTimeSummaryStatistics = nodes.stream().mapToLong(node ->
                                        node.getOrchestrationNodeSchedule().getActualOrderItemDeliveryLeadTime())
                                .summaryStatistics();

                        NodeLeadTimeHistorySampledStatistics stats =
                                NodeLeadTimeHistorySampledStatistics.builder()
                                        .productSpecId(specIdDeliveryFactoryEntry.getKey())
                                        .deliveryFactoryName(specIdDeliveryFactoryEntry.getValue())
                                        .minActualLeadTime(deliveryTimeSummaryStatistics.getMin())
                                        .maxActualLeadTime(deliveryTimeSummaryStatistics.getMax())
                                        .averageActualLeadTime((float) deliveryTimeSummaryStatistics.getAverage())
                                        .sampleSize(nodes.size())
                                        .sampleWindow(sampleWindowDate)
                                        .build();

                        nodeLeadTimeHistoryStatisticsRepository.save(stats);
                    });
                });
    }
}
