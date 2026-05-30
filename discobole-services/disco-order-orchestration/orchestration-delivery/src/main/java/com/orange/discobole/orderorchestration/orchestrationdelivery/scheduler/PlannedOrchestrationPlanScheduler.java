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
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.repository.ReactiveOrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State.PLANNED;

@Slf4j
@Component
public class PlannedOrchestrationPlanScheduler {
    private static final ZoneOffset UTC = ZoneOffset.UTC;

    private final EventPublisher eventPublisher;
    private final ReactiveOrchestrationPlanRepository reactiveOrchestrationPlanRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Value("${config.plannedToAcknowledgedTransition.recommendedBatchSize:100}")
    private Integer recommendedBatchSize;

    @Value("${config.plannedToAcknowledgedTransition.jobExecutionDelay:PT1H}")
    private Duration jobExecutionDelay;

    @Value("${config.plannedToAcknowledgedTransition.windowStartTimeUtc:01:00}")
    private String windowStartTime;

    @Value("${config.plannedToAcknowledgedTransition.windowEndTimeUtc:06:00}")
    private String windowEndTime;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public PlannedOrchestrationPlanScheduler(EventPublisher eventPublisher, ReactiveOrchestrationPlanRepository reactiveOrchestrationPlanRepository) {
        this.eventPublisher = eventPublisher;
        this.reactiveOrchestrationPlanRepository = reactiveOrchestrationPlanRepository;
    }

    @PostConstruct
    public void validate() {
        if (recommendedBatchSize == null || recommendedBatchSize < 1) {
            throw new IllegalArgumentException(
                    "Property 'config.plannedToAcknowledgedTransition.recommendedBatchSize' must be at least 1");
        }

        if (jobExecutionDelay == null || jobExecutionDelay.compareTo(Duration.ofHours(1)) < 0) {
            throw new IllegalArgumentException(
                    "Property 'config.plannedToAcknowledgedTransition.jobExecutionDelay' must be at least 1 hour. Current value: "
                            + jobExecutionDelay);
        }

        // Validate window times (UTC)
        if (windowStartTime == null || windowStartTime.isBlank()) {
            throw new IllegalArgumentException(
                    "Property 'config.plannedToAcknowledgedTransition.windowStartTimeUtc' must not be null or empty");
        }

        if (windowEndTime == null || windowEndTime.isBlank()) {
            throw new IllegalArgumentException(
                    "Property 'config.plannedToAcknowledgedTransition.windowEndTimeUtc' must not be null or empty");
        }

        LocalTime startTime;
        LocalTime endTime;
        try {
            startTime = LocalTime.parse(windowStartTime, timeFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Property 'config.plannedToAcknowledgedTransition.windowStartTimeUtc' must be in HH:mm format (e.g., '01:00'). Current value: "
                            + windowStartTime,
                    e);
        }
        try {
            endTime = LocalTime.parse(windowEndTime, timeFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Property 'config.plannedToAcknowledgedTransition.windowEndTimeUtc' must be in HH:mm format (e.g., '06:00'). Current value: "
                            + windowEndTime,
                    e);
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "Property 'config.plannedToAcknowledgedTransition.windowStartTimeUtc' must be before 'windowEndTimeUtc'. Current values: "
                            + windowStartTime + " - " + windowEndTime);
        }

        log.debug(
                "Planned state transition configuration loaded and validated: batch={}, delay={}, window={}-{} UTC",
                recommendedBatchSize, jobExecutionDelay, windowStartTime, windowEndTime);
    }

    @Scheduled(fixedRateString = "${config.plannedToAcknowledgedTransition.jobExecutionDelay}")
    @Transactional
    public void acknowledgePlannedStateOnDeliveryDate() {
        Instant now = Instant.now();

        // Check if we're within the configured time window (UTC)
        if (!isWithinTimeWindow(now)) {
            log.debug("Scheduler triggered outside time window (UTC). Current time: {}, Window: {}-{}",
                    LocalTime.ofInstant(now, UTC),
                    windowStartTime,
                    windowEndTime);
            return;
        }

        log.info(">>> Starting adaptive plan state transition from PLANNED state at {}", now);

        // Count total eligible plans
        Long totalEligiblePlans = reactiveOrchestrationPlanRepository
                .countByStateAndDateBeforeNow(PLANNED, now, now)
                .doOnNext(count -> log.debug("Count query returned: {} plans", count))
                .doOnError(error -> log.error("Error counting eligible plans: {}", error.getMessage(), error))
                .block();

        if (totalEligiblePlans == null || totalEligiblePlans == 0) {
            log.info("No eligible plans found for state transition. Current time: {}, State: {}", now, PLANNED);
            return;
        }

        // Calculate adaptive batch size using smart comparison
        int batchSize = calculateAdaptiveBatchSize(totalEligiblePlans, now);

        log.info("Processing adaptive batch of {} out of {} eligible plans",
                batchSize, totalEligiblePlans);

        // Fetch and process the calculated batch
        processPlanBatch(now, batchSize);
    }

    /**
     * Calculates adaptive batch size by comparing recommended vs required rates.
     * Uses the LARGER of the two to ensure all plans are processed.
     *
     * @param totalEligiblePlans total number of plans eligible for transition
     * @param now                current timestamp
     * @return calculated adaptive batch size
     */
    private int calculateAdaptiveBatchSize(Long totalEligiblePlans, Instant now) {
        // Get the recommended batch size from configuration
        int recommendedBatch = recommendedBatchSize;
        int remainingRuns = calculateRemainingRunsInWindow(now);
        // Ensure at least 1 run to prevent division by zero
        int effectiveRemainingRuns = Math.max(remainingRuns, 1);
        int requiredBatch = (int) Math.ceil((double) totalEligiblePlans / effectiveRemainingRuns);
        // Use the LARGER of the two batch sizes
        int batchSize = Math.max(recommendedBatch, requiredBatch);
        // Ensure we don't exceed available plans
        batchSize = Math.min(batchSize, totalEligiblePlans.intValue());
        return batchSize;
    }

    /**
     * Checks if the given instant is within the configured time window in UTC.
     * Always uses UTC timezone for consistent behavior across all environments.
     *
     * @param now current timestamp
     * @return true if within window, false otherwise
     */
    private boolean isWithinTimeWindow(Instant now) {
        LocalTime currentTime = LocalTime.ofInstant(now, UTC);
        LocalTime windowStart = LocalTime.parse(windowStartTime, timeFormatter);
        LocalTime windowEnd = LocalTime.parse(windowEndTime, timeFormatter);

        return currentTime.isAfter(windowStart) && currentTime.isBefore(windowEnd);
    }

    private int calculateRemainingRunsInWindow(Instant now) {
        LocalTime currentTime = LocalTime.ofInstant(now, UTC);
        LocalTime windowEnd = LocalTime.parse(windowEndTime, timeFormatter);

        long remainingMinutes = ChronoUnit.MINUTES.between(currentTime, windowEnd);
        long delayMinutes = jobExecutionDelay.toMinutes();
        return (int) Math.ceil((double) remainingMinutes / delayMinutes);
    }

    private void processPlanBatch(Instant now, int batchSize) {
        Flux<OrchestrationPlan> orchestrationPlansFlux = reactiveOrchestrationPlanRepository
                .findByStateAndDateBeforeNow(PLANNED, now, now)
                .take(batchSize);

        orchestrationPlansFlux
                .collectList()
                .doOnNext(plans -> {
                    if (plans.isEmpty()) {
                        log.debug("No plans to process in this batch");
                        return;
                    }

                    plans.forEach(plan -> plan.setState(State.ACKNOWLEDGED));
                    log.debug("Transitioning {} plans to ACKNOWLEDGED state", plans.size());

                    eventPublisher.publishEvents(CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, plans);
                    log.debug("Published state change events for {} plans", plans.size());

                    reactiveOrchestrationPlanRepository.saveAll(plans)
                            .doOnError(throwable -> {
                                log.error("Error persisting plan states: {}", throwable.getMessage());
                                throw new CoodNonRecoverableAndNonRetryableException(
                                        new CoodTechnicalException(ExceptionCode.PLANS_CHUNK_STATE_CHANGE_EXCEPTION));
                            })
                            .subscribe();
                    log.debug("Persisted {} plans with new state", plans.size());
                })
                .doOnError(throwable -> {
                    log.error("Error processing plan batch: {}", throwable.getMessage());
                    throw new CoodNonRecoverableAndNonRetryableException(
                            new CoodTechnicalException(ExceptionCode.PLANS_CHUNK_STATE_CHANGE_EXCEPTION));
                })
                .subscribe();
    }

}
