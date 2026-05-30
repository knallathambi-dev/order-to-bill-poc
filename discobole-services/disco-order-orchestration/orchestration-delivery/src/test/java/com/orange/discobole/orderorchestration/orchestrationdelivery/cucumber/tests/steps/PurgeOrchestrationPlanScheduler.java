// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.EventRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.OrchestrationPlanRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.PurgeEventsScheduler;
import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@RequiredArgsConstructor
public class PurgeOrchestrationPlanScheduler {

    private static final String DAY_DURATION_EXPRESSION = "P%sDT0H0M";

    private final com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.PurgeOrchestrationPlanScheduler purgeOrchestrationPlanScheduler;

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    private final EventRepository eventRepository;

    private final PurgeEventsScheduler purgeEventsScheduler;

    private Instant fixedInstant;

    @Before
    public void cleanEventsCollection() {
        eventRepository.deleteAll();
    }

    @Given("the system has the following plans with {int} nodes with {string} state")
    public void theSystemHasTheFollowingPlansWithNodesWithState(int nodesCount, String nodesState, List<OrchestrationPlanRecord> planRecords) {
        for (OrchestrationPlanRecord planData : planRecords) {
            OrchestrationPlan plan = OrchestrationPlan.builder()
                    .id(planData.planId())
                    .state(State.fromValue(planData.state()))
                    .lastModifiedDate(Instant.parse(planData.lastModifiedDate()))
                    .archived(Boolean.valueOf(planData.archived()))
                    .orchestrationPlanNodes(IntStream.range(0, nodesCount).mapToObj(operand -> OrchestrationPlanNode.builder()
                            .id(UUID.randomUUID().toString())
                            .state("any".equalsIgnoreCase(nodesState)
                                    ? OrchestrationPlanNodeState.values()[new Random().nextInt(OrchestrationPlanNodeState.values().length)] : OrchestrationPlanNodeState.fromValue(nodesState)
                            )
                            .build()).collect(Collectors.toSet()))
                    .build();
            orchestrationPlanRepository.save(plan);
        }
    }

    @And("the current system time is {string}")
    public void theCurrentSystemTimeIs(String currentSystemTime) {
        fixedInstant = Instant.parse(currentSystemTime);
    }

    @And("the system has deletion orchestration plan threshold configured to be deleted after {int} day")
    public void theSystemHasDeletionOrchestrationPlanThresholdConfiguredToBeDeletedAfterDay(int daysCount) throws NoSuchFieldException, IllegalAccessException {
        Class<?> purgeClass = purgeOrchestrationPlanScheduler.getClass();
        Field field = purgeClass.getDeclaredField(com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.PurgeOrchestrationPlanScheduler.Fields.deletePlanThreshold);
        field.setAccessible(true);
        field.set(purgeOrchestrationPlanScheduler, Duration.parse(DAY_DURATION_EXPRESSION.formatted(daysCount)));
    }

    @When("the cron job running every day at {int}:{int} am")
    public void theCronJobRunningEveryDayAtAm(int h, int m) {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            purgeOrchestrationPlanScheduler.startPurgeOrchestrationPlan();
        }
    }

    @Then("the following plans will deleted")
    public void theFollowingPlansWillDeleted(List<OrchestrationPlanRecord> planRecords) {
        for (OrchestrationPlanRecord planRecord : planRecords) {
            Optional<OrchestrationPlan> orchestrationPlanOptional = orchestrationPlanRepository.findOrchestrationPlanById(planRecord.planId());
            assertThat(orchestrationPlanOptional).isEmpty();
        }
    }

    @And("the follow plans will be still exists")
    public void theFollowPlansWillBeStillExists(List<OrchestrationPlanRecord> planRecords) {
        for (OrchestrationPlanRecord planRecord : planRecords) {
            Optional<OrchestrationPlan> orchestrationPlanOptional = orchestrationPlanRepository.findOrchestrationPlanById(planRecord.planId());
            assertThat(orchestrationPlanOptional).isPresent();
        }
    }

    @And("the system has archiving orchestration plan threshold configured to be deleted after {int} day")
    public void theSystemHasArchivingOrchestrationPlanThresholdConfiguredToBeDeletedAfterDay(int daysCount) throws NoSuchFieldException, IllegalAccessException {
        Class<?> purgeClass = purgeOrchestrationPlanScheduler.getClass();
        Field field = purgeClass.getDeclaredField(com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.PurgeOrchestrationPlanScheduler.Fields.archivePlanThreshold);
        field.setAccessible(true);
        field.set(purgeOrchestrationPlanScheduler, Duration.parse(DAY_DURATION_EXPRESSION.formatted(daysCount)));
    }

    @Then("the following plans will archived")
    public void theFollowingPlansWillArchived(List<OrchestrationPlanRecord> planRecords) {
        for (OrchestrationPlanRecord planRecord : planRecords) {
            Optional<OrchestrationPlan> orchestrationPlanOptional = orchestrationPlanRepository.findOrchestrationPlanById(planRecord.planId());
            assertThat(orchestrationPlanOptional).isPresent();
            assertThat(orchestrationPlanOptional.get().getArchived()).isTrue();
        }
    }

    @And("the follow plans will be still not archived")
    public void theFollowPlansWillBeStillNotArchived(List<OrchestrationPlanRecord> planRecords) {
        for (OrchestrationPlanRecord planRecord : planRecords) {
            Optional<OrchestrationPlan> orchestrationPlanOptional = orchestrationPlanRepository.findOrchestrationPlanById(planRecord.planId());
            assertThat(orchestrationPlanOptional).isPresent();
            assertThat(orchestrationPlanOptional.get().getArchived()).isFalse();
        }
    }

    @And("the system has delete archived orchestration plan threshold configured to be deleted after {int} day")
    public void theSystemHasDeleteArchivedOrchestrationPlanThresholdConfiguredToBeDeletedAfterDay(int daysCount) throws NoSuchFieldException, IllegalAccessException {
        Class<?> purgeClass = purgeOrchestrationPlanScheduler.getClass();
        Field field = purgeClass.getDeclaredField(com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.PurgeOrchestrationPlanScheduler.Fields.deleteArchivedPlanThreshold);
        field.setAccessible(true);
        field.set(purgeOrchestrationPlanScheduler, Duration.parse(DAY_DURATION_EXPRESSION.formatted(daysCount)));
    }

    @Given("the events collection has the following events")
    public void theEventsCollectionHasTheFollowingEvents(List<EventRecord> eventRecords) {
        eventRecords.stream().map(eventRecord -> EventEntity.builder()
                        .aggregateId(eventRecord.aggregateId())
                        .timestamp(Instant.parse(eventRecord.eventTimestamp()).toEpochMilli())
                        .build())
                .forEach(eventRepository::save);
    }

    @And("events purge job deletes events older than {string}")
    public void eventsPurgeJobDeletesEventsOlderThan(String deleteEventsDuration) throws IllegalAccessException, NoSuchFieldException {
        Class<?> purgeClass = purgeEventsScheduler.getClass();
        Field field = purgeClass.getDeclaredField("deleteEventsThresholdDuration");
        field.setAccessible(true);
        field.set(purgeEventsScheduler, Duration.parse(deleteEventsDuration));
    }

    @When("the delete events job is executed")
    public void theDeleteEventsJobIsExecuted() {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            purgeEventsScheduler.startPurgeEvents();
        }
    }

    @Then("events with the following ids no longer exist")
    public void eventsWithTheFollowingIdsNoLongerExist(List<EventRecord> expectedDeletedEvents) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            assertThat(eventRepository.findByAggregateIdIn(expectedDeletedEvents.stream().map(EventRecord::aggregateId).toList()))
                    .as("Event with ids %s should not exist", expectedDeletedEvents.stream().map(EventRecord::aggregateId))
                    .isEmpty();
        });
    }

    @And("events with the following ids still exist")
    public void eventsWithTheFollowingIdsStillExist(List<EventRecord> expectedEvents) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            List<EventEntity> allExistingEvents = eventRepository.findAll();
            assertThat(allExistingEvents)
                    .as("All existing events must be the same size as expected events")
                    .hasSameSizeAs(expectedEvents);

            assertThat(allExistingEvents)
                    .as("All existing events must be the same ids as expected events")
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .ignoringExpectedNullFields()
                    .ignoringActualNullFields()
                    .isEqualTo(expectedEvents);
        });
    }
}
