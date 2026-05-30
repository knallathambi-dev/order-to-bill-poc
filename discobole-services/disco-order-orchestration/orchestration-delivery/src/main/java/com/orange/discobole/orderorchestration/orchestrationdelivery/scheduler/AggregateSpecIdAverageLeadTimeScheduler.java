// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler;

import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.NodeLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.AggregatedSpecIdLeadTimeStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.ReactiveNodeLeadTimeHistorySampledStatisticsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.ReactiveAggregatedSpecIdLeadTimeStatisticsRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregateSpecIdAverageLeadTimeScheduler {
    @Value("${config.aggregateSpecIdAverageLeadTimeWindow}")
    private Duration aggregateSpecIdAverageLeadTimeWindow;

    @Value("${config.aggregateSpecIdAverageLeadTimeSchedulerDuration}")
    private Duration aggregateSpecIdAverageLeadTimeSchedulerDuration;

    private final ReactiveAggregatedSpecIdLeadTimeStatisticsRepository reactiveAggregatedSpecIdLeadTimeStatisticsRepository;

    private final ReactiveNodeLeadTimeHistorySampledStatisticsRepository reactiveNodeLeadTimeHistorySampledStatisticsRepository;

    @PostConstruct
    public void validate() {
        // Example: Ensure the aggregate product average lead time duration is more than 1 hour
        if (Objects.isNull(aggregateSpecIdAverageLeadTimeWindow)
                || aggregateSpecIdAverageLeadTimeWindow.compareTo(Duration.ofHours(1)) < 0
        ) {
            throw new
                    IllegalArgumentException(
                    "Property 'config.specIdAverageLeadTimePeriod' must be a valid duration more than or equal to one hour."
            );
        }
    }

    @Scheduled(fixedRateString = "${config.aggregateSpecIdAverageLeadTimeSchedulerDuration}")
    public void storeSpecIdAverageLeadTime() {
        log.info("Start storing spec id average lead time, aggregateSpecIdAverageLeadTimeWindow: {} aggregateSpecIdAverageLeadTimeSchedulerDuration: {}",
                aggregateSpecIdAverageLeadTimeWindow,
                aggregateSpecIdAverageLeadTimeSchedulerDuration
        );

        Instant now = Instant.now();

        Flux<NodeLeadTimeHistorySampledStatistics> nodeLeadTimeHistoryStatistics =
                reactiveNodeLeadTimeHistorySampledStatisticsRepository.findNodeLeadTimeHistorySampledStatisticsBySampleWindowIsAfter(
                        now.minus(aggregateSpecIdAverageLeadTimeWindow)
                );

        nodeLeadTimeHistoryStatistics
                // group node statistics by product spec id
                .groupBy(NodeLeadTimeHistorySampledStatistics::getProductSpecId)
                .flatMap(groupedFlux ->
                        groupedFlux
                                // for each group of spec id statistics, we create a new accumulator then start adding to it
                                .reduce(new WeightedAverageAccumulator(groupedFlux.key()),
                                        WeightedAverageAccumulator::addStatistics
                                )
                                // after the weighted average of the stats for product spec id map to another more compact model
                                .map(reducedStats -> AggregatedSpecIdLeadTimeStatistics.builder()
                                        .productSpecId(reducedStats.getProductSpecId())
                                        .averageLeadTime(reducedStats.getWeightedAverage())
                                        .lastUpdatedDate(now)
                                        .build())
                )
                // for each grouped statistics for a spec id, store it in a separate collection
                .flatMap(result ->
                        reactiveAggregatedSpecIdLeadTimeStatisticsRepository.save(result)
                                .doOnSuccess(saved -> log.debug("Saved statistics for spec: {}", saved.getProductSpecId()))
                                .onErrorResume(error -> {
                                    log.error("Failed to save statistics for spec: {}", result.getProductSpecId(), error);
                                    return Mono.empty(); // Skip failed saves
                                })
                )
                .subscribe(
                        saved -> {}, // Already logged in doOnSuccess
                        error -> log.error("Stream processing failed", error),
                        () -> log.debug("All statistics processing completed")
                );
    }

    private static class WeightedAverageAccumulator {
        @Getter
        private final String productSpecId;
        private float totalWeightedSum = 0f;
        private int totalSampleSize = 0;

        public WeightedAverageAccumulator(String productSpecId) {
            this.productSpecId = productSpecId;
        }

        // weighted average is:
        // sum of (average * sampleSize) / sum of (sampleSize)
        public WeightedAverageAccumulator addStatistics(NodeLeadTimeHistorySampledStatistics stats) {
            if (stats.getAverageActualLeadTime() != null && stats.getSampleSize() != null) {
                float weightedValue = stats.getAverageActualLeadTime() * stats.getSampleSize();
                this.totalWeightedSum += weightedValue;
                this.totalSampleSize += stats.getSampleSize();
            }
            return this;
        }

        public float getWeightedAverage() {
            return totalSampleSize > 0 ? totalWeightedSum / totalSampleSize : 0f;
        }
    }
}
