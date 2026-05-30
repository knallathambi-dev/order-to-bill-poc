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
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.repository.ReactiveOrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.INITIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

/**
 * Unit tests for PlannedOrchestrationPlanScheduler.
 * Tests state transitions from PLANNED to ACKNOWLEDGED.
 */
@ExtendWith(MockitoExtension.class)
class PlannedOrchestrationPlanSchedulerTests {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ReactiveOrchestrationPlanRepository reactiveOrchestrationPlanRepository;

    private PlannedOrchestrationPlanScheduler scheduler;

    private static final int DEFAULT_BATCH_SIZE = 10;
    private static final Duration DEFAULT_JOB_DELAY = Duration.ofHours(1);
    private static final String DEFAULT_WINDOW_START = "01:00";
    private static final String DEFAULT_WINDOW_END = "06:00";
    private static final String DEFAULT_TIMEZONE = "UTC";

    @BeforeEach
    void setUp() {
        scheduler = new PlannedOrchestrationPlanScheduler(eventPublisher, reactiveOrchestrationPlanRepository);
        ReflectionTestUtils.setField(scheduler, "recommendedBatchSize", DEFAULT_BATCH_SIZE);
        ReflectionTestUtils.setField(scheduler, "jobExecutionDelay", DEFAULT_JOB_DELAY);
        ReflectionTestUtils.setField(scheduler, "windowStartTime", DEFAULT_WINDOW_START);
        ReflectionTestUtils.setField(scheduler, "windowEndTime", DEFAULT_WINDOW_END);
    }

    private void executeSchedulerAtTime(int hour, int minute, int seconds) {
        // Use UTC to match the configured timezone for consistent test behavior
        Instant fixedTime = LocalDate.now().atTime(LocalTime.of(hour, minute, seconds))
                .atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant();
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class,
                withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedTime);
            scheduler.acknowledgePlannedStateOnDeliveryDate();
        }
    }

    private OrchestrationPlan.OrchestrationPlanBuilder buildPlannedOrchestrationPlan(String id,
                                                                                     Instant requestedDeliveryDate) {
        return OrchestrationPlan.builder()
                .id(id)
                .state(State.PLANNED)
                .requestedDeliveryDate(requestedDeliveryDate)
                .orchestrationPlanNodes(
                        Set.of(OrchestrationPlanNode.builder().state(INITIALIZED).build()));
    }

    private List<OrchestrationPlan> createPlans(int count, Instant deliveryDate) {
        List<OrchestrationPlan> plans = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            plans.add(buildPlannedOrchestrationPlan("plan" + i, deliveryDate).build());
        }
        return plans;
    }

    // ==================== Configuration Validation Tests ====================

    @Test
    void givenInvalidBatchSize_whenValidate_thenThrowException() {
        // Given
        ReflectionTestUtils.setField(scheduler, "recommendedBatchSize", 0);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    @Test
    void givenInvalidJobDelay_whenValidate_thenThrowException() {
        // Given
        ReflectionTestUtils.setField(scheduler, "jobExecutionDelay", Duration.ofMinutes(30));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    @Test
    void givenInvalidWindowStartTimeFormat_whenValidate_thenThrowException() {
        // Given - invalid format (should be HH:mm)
        ReflectionTestUtils.setField(scheduler, "windowStartTime", "1:00");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    @Test
    void givenInvalidWindowEndTimeFormat_whenValidate_thenThrowException() {
        // Given - invalid format (should be HH:mm)
        ReflectionTestUtils.setField(scheduler, "windowEndTime", "6:00");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    @Test
    void givenWindowStartTimeWithInvalidCharacters_whenValidate_thenThrowException() {
        // Given - invalid characters
        ReflectionTestUtils.setField(scheduler, "windowStartTime", "01:XX");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    @Test
    void givenWindowEndTimeAfterMidnight_whenValidate_thenThrowException() {
        // Given - invalid time (25:00 doesn't exist)
        ReflectionTestUtils.setField(scheduler, "windowEndTime", "25:00");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    @Test
    void givenWindowStartAfterWindowEnd_whenValidate_thenThrowException() {
        // Given - start time is after end time
        ReflectionTestUtils.setField(scheduler, "windowStartTime", "06:00");
        ReflectionTestUtils.setField(scheduler, "windowEndTime", "01:00");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    @Test
    void givenWindowStartEqualsWindowEnd_whenValidate_thenThrowException() {
        // Given - start time equals end time
        ReflectionTestUtils.setField(scheduler, "windowStartTime", "06:00");
        ReflectionTestUtils.setField(scheduler, "windowEndTime", "06:00");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> scheduler.validate());
    }

    // ==================== State Transition Tests ====================

    @Test
    void givenNoEligiblePlans_whenSchedulerRuns_thenNoProcessing() {
        // Given
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(0L));

        // When execute Scheduler Within Window
        executeSchedulerAtTime(3, 0,1);

        // Then
        verify(reactiveOrchestrationPlanRepository, never())
                .findByStateAndDateBeforeNow(any(), any(), any());
        verify(reactiveOrchestrationPlanRepository, never()).saveAll(anyList());
    }

    @Test
    void givenSinglePlan_whenSchedulerRuns_thenPlanTransitionedToAcknowledged() {
        // Given
        OrchestrationPlan plan = buildPlannedOrchestrationPlan("plan1", Instant.now().minusSeconds(3600))
                .build();
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(1L));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.just(plan));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When execute Scheduler Within Window
        executeSchedulerAtTime(3, 0,1);

        // Then
        assertThat(plan.getState()).isEqualTo(State.ACKNOWLEDGED);
        verify(reactiveOrchestrationPlanRepository, times(1)).saveAll(anyList());
    }

    private static Stream<Arguments> provideSchedulerProcessingScenarios() {
        return Stream.of(
                Arguments.of("Multiple plans at mid-window", 5, 3, 0),
                Arguments.of("Plans at window start", 5, 1, 0),
                Arguments.of("Plans one minute before window end", 5, 5, 59));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideSchedulerProcessingScenarios")
    void givenPlansWithinWindow_whenSchedulerRuns_thenAllPlansTransitioned(
            String scenario, int planCount, int hour, int minute) {
        // Given
        List<OrchestrationPlan> plans = createPlans(planCount, Instant.now().minusSeconds(3600));
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just((long) planCount));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.fromIterable(plans));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When execute Scheduler Within Window
        executeSchedulerAtTime(hour, minute, 1);

        // Then
        assertThat(plans).allMatch(p -> p.getState() == State.ACKNOWLEDGED);
        verify(reactiveOrchestrationPlanRepository, times(1)).saveAll(anyList());
    }

    @Test
    void givenLargePlanCount_whenSchedulerRuns_thenHandlesGracefully() {
        // Given
        ReflectionTestUtils.setField(scheduler, "recommendedBatchSize", 100);
        List<OrchestrationPlan> plans = createPlans(2, Instant.now().minusSeconds(3600));
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(1_000_000L));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.fromIterable(plans));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When execute Scheduler Within Window
        executeSchedulerAtTime(3, 0,1);

        // Then
        assertThat(plans).allMatch(p -> p.getState() == State.ACKNOWLEDGED);
    }

    // ==================== Adaptive Algorithm Tests ====================

    @Test
    void givenRequiredBatchExceedsRecommended_whenSchedulerRuns_thenUsesRequiredBatch() {
        // Given: window 01:00-06:00 (5 hours), job delay 1 hour = 5 runs
        // At 01:00, remaining runs = 5
        // 60 eligible plans / 5 runs = 12 required batch (exceeds recommended 10)
        // Expected: batch size = 12 (uses required, not recommended)

        // Create 13 plans (the expected batch size + 1)
        List<OrchestrationPlan> plans = createPlans(13, Instant.now().minusSeconds(3600));
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(60L));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.fromIterable(plans));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When - execute at 01:00 (start of window, 5 remaining runs)
        executeSchedulerAtTime(1, 0,1);

        // Then - verify 12 plans were transitioned (required batch, not recommended 10)
        ArgumentCaptor<List<OrchestrationPlan>> captor = ArgumentCaptor.forClass(List.class);
        verify(reactiveOrchestrationPlanRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(12);
        assertThat(captor.getValue()).allMatch(p -> p.getState() == State.ACKNOWLEDGED);
        // Verify the 13th plan (index 12) is still in PLANNED state
        assertThat(plans.get(12).getState()).isEqualTo(State.PLANNED);
    }

    @Test
    void givenRecommendedBatchExceedsRequired_whenSchedulerRuns_thenUsesRecommendedBatch() {
        // Given: window 01:00-06:00 (5 hours), job delay 1 hour = 5 runs
        // At 01:00, remaining runs = 5
        // 20 eligible plans / 5 runs = 4 required batch (less than recommended 10)
        // Expected: batch size = 10 (uses recommended, not required)

        // Create 10 plans (the expected batch size)
        List<OrchestrationPlan> plans = createPlans(10, Instant.now().minusSeconds(3600));
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(20L));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.fromIterable(plans));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When - execute at 01:00 (start of window, 5 remaining runs)
        executeSchedulerAtTime(1, 0,1);

        // Then - verify 10 plans were transitioned (recommended batch, not required 4)
        ArgumentCaptor<List<OrchestrationPlan>> captor = ArgumentCaptor.forClass(List.class);
        verify(reactiveOrchestrationPlanRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(10);
        assertThat(captor.getValue()).allMatch(p -> p.getState() == State.ACKNOWLEDGED);
    }

    @Test
    void givenFewerRemainingRuns_whenSchedulerRuns_thenIncreasesRequiredBatch() {
        // Given: window 01:00-06:00, job delay 1 hour
        // At 05:00, remaining runs = 1 (only 1 hour left)
        // 60 eligible plans / 1 run = 60 required batch
        // Expected: batch size = 60 (urgency increases batch)

        // Create 60 plans (the expected batch size at end of window)
        List<OrchestrationPlan> plans = createPlans(60, Instant.now().minusSeconds(3600));
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(60L));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.fromIterable(plans));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When - execute at 05:00 (near end of window, only 1 remaining run)
        executeSchedulerAtTime(5, 0,0);

        // Then - verify 60 plans were transitioned (all plans in single batch)
        ArgumentCaptor<List<OrchestrationPlan>> captor = ArgumentCaptor.forClass(List.class);
        verify(reactiveOrchestrationPlanRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(60);
        assertThat(captor.getValue()).allMatch(p -> p.getState() == State.ACKNOWLEDGED);
    }

    @Test
    void givenBatchSizeExceedsTotalPlans_whenSchedulerRuns_thenCapsToTotalPlans() {
        // Given: calculated batch = 100, but only 5 plans available
        // Expected: batch size = 5 (capped to available plans)
        ReflectionTestUtils.setField(scheduler, "recommendedBatchSize", 100);

        List<OrchestrationPlan> plans = createPlans(5, Instant.now().minusSeconds(3600));
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(5L));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.fromIterable(plans));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When executing within window
        executeSchedulerAtTime(3, 0,1);

        // Then - verify only 5 plans were transitioned (capped to available)
        ArgumentCaptor<List<OrchestrationPlan>> captor = ArgumentCaptor.forClass(List.class);
        verify(reactiveOrchestrationPlanRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(5);
        assertThat(captor.getValue()).allMatch(p -> p.getState() == State.ACKNOWLEDGED);
    }

    @Test
    void givenMidWindowExecution_whenSchedulerRuns_thenCalculatesCorrectBatch() {
        // Given: window 01:00-06:00, job delay 1 hour
        // At 03:00, remaining runs = 3 (3 hours left)
        // 30 eligible plans / 3 runs = 10 required batch (equals recommended)
        // Expected: batch size = 10

        List<OrchestrationPlan> plans = createPlans(10, Instant.now().minusSeconds(3600));
        when(reactiveOrchestrationPlanRepository.countByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Mono.just(30L));
        when(reactiveOrchestrationPlanRepository.findByStateAndDateBeforeNow(
                eq(State.PLANNED), any(Instant.class), any(Instant.class)))
                .thenReturn(Flux.fromIterable(plans));
        when(reactiveOrchestrationPlanRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // When - execute at 03:00 (mid window, 3 remaining runs)
        executeSchedulerAtTime(3, 0,0);

        // Then - verify 10 plans were transitioned
        ArgumentCaptor<List<OrchestrationPlan>> captor = ArgumentCaptor.forClass(List.class);
        verify(reactiveOrchestrationPlanRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(10);
        assertThat(captor.getValue()).allMatch(p -> p.getState() == State.ACKNOWLEDGED);
    }

    // ==================== Time Window Tests ====================

    private static Stream<Arguments> provideOutsideWindowScenarios() {
        return Stream.of(
                Arguments.of("Time before window start", 0, 30),
                Arguments.of("Time after window end", 7, 0),
                Arguments.of("Time exactly at window end (exclusive)", 6, 0));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideOutsideWindowScenarios")
    void givenTimeOutsideWindow_whenSchedulerRuns_thenNoProcessing(
            String scenario, int hour, int minute) {
        // Given: window 01:00-06:00, current time is outside the window

        // When - execute at specified time outside window
        executeSchedulerAtTime(hour, minute,0);

        // Then - no repository calls should be made
        verify(reactiveOrchestrationPlanRepository, never())
                .countByStateAndDateBeforeNow(any(), any(), any());
        verify(reactiveOrchestrationPlanRepository, never())
                .findByStateAndDateBeforeNow(any(), any(), any());
        verify(reactiveOrchestrationPlanRepository, never()).saveAll(anyList());
    }

}
