// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.cucumber.tests;

import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowStateType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.crons.PurgeEventsScheduler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.crons.PurgeFalloutScheduler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.cucumber.tests.records.EventRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.cucumber.tests.records.FalloutIncidentRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.cucumber.tests.records.ProcessFlowRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.cucumber.tests.records.TaskFlowRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal.EventRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.repository.FalloutRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
public class PurgeEvents {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PurgeEventsScheduler purgeEventsScheduler;

    @Autowired
    private PurgeFalloutScheduler purgeFalloutScheduler;


    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private FalloutRepository falloutRepository;

    private Instant fixedInstant;

    private List<ProcessFlow> processFlowList;
    private List<DiscoTaskFlow> taskFlowList;

    @Before
    public void cleanEventsCollection() {
        eventRepository.deleteAll();
    }

    @Given("the events collection has the following events")
    public void theEventsCollectionHasTheFollowingEvents(List<EventRecord> eventRecords) {
        for (EventRecord eventRecord : eventRecords) {
            EventEntity event = EventEntity.builder()
                    .aggregateId(eventRecord.aggregateId())
                    .timestamp(Instant.parse(eventRecord.eventTimestamp()).toEpochMilli())
                    .build();
            eventRepository.save(event);
        }
    }

    @And("events purge job deletes events older than {string}")
    public void eventsPurgeJobDeletesEventsOlderThan(String deleteEventsDuration) throws IllegalAccessException, NoSuchFieldException {
        Class<?> purgeClass = purgeEventsScheduler.getClass();
        Field field = purgeClass.getDeclaredField("deleteEventsThresholdDuration");
        field.setAccessible(true);
        field.set(purgeEventsScheduler, Duration.parse(deleteEventsDuration));
    }

    @And("the current system time is {string}")
    public void theCurrentSystemTimeIs(String currentSystemTime) {
        fixedInstant = Instant.parse(currentSystemTime);
    }

    @When("the delete events job is executed")
    public void theDeleteEventsJobIsExecuted() {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            purgeEventsScheduler.startPurgeEvents();
        }
    }

    @Then("events with the following ids no longer exist")
    public void eventsWithTheFollowingIdsNoLongerExist(List<EventRecord> expectedEvents) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            assertThat(eventRepository.findByAggregateIdIn(expectedEvents.stream().map(EventRecord::aggregateId).toList()))
                    .as("Event with ids %s should not exist", expectedEvents.stream().map(EventRecord::aggregateId))
                    .isEmpty();
        });
    }

    @And("events with the following ids still exist")
    public void eventsWithTheFollowingIdsStillExist(List<EventRecord> expectedEvents) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            List<EventEntity> allExistingEvents = eventRepository.findAll();
            log.info("allExistingEventssss" + allExistingEvents.stream()
                    .map(EventEntity::getAggregateId)
                    .toList() + expectedEvents);
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

    @Given("the system has the following fallout incidents:")
    public void theSystemHasTheFollowingFalloutIncidents(List<FalloutIncidentRecord> falloutIncidentList) {
        try (MockedStatic<OffsetDateTime> mockedOffsetDateTime = mockStatic(OffsetDateTime.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            falloutIncidentList.forEach(falloutIncident -> {
                OffsetDateTime parsedModificationDateDate = OffsetDateTime.parse(falloutIncident.modificationDate());
                mockedOffsetDateTime.when(OffsetDateTime::now).thenReturn(parsedModificationDateDate);

                FalloutIncident fallout = FalloutIncident.builder()
                        .id(falloutIncident.falloutId())
                        .state(State.fromValue(falloutIncident.state()))
                        .modificationDate(parsedModificationDateDate)
                        .build();

                falloutRepository.save(fallout);
            });
        }
    }

    @And("the system has the following process flows:")
    public void theSystemHasTheFollowingProcessFlows(List<ProcessFlowRecord> processFlowRecordList) {
        processFlowRecordList.forEach((processFlowRecord -> {
            ProcessFlow processFlow = new ProcessFlow();

            processFlow.setId(processFlowRecord.processFlowId());
            processFlow.setState(ProcessFlowStateType.fromValue(processFlowRecord.state()));
            mongoTemplate.save(processFlow);
        }));
    }

    @And("the system has the following task flows:")
    public void theSystemHasTheFollowingTaskFlows(List<TaskFlowRecord> taskFlowRecordList) {
        taskFlowRecordList.forEach((taskFlowRecord -> {
            DiscoTaskFlow taskFlow = new DiscoTaskFlow();

            taskFlow.setProcessFLowId(taskFlowRecord.taskFlowId());
            mongoTemplate.save(taskFlow);
        }));
    }

    @And("the system has delete fallout threshold configured to be {string}")
    public void theSystemHasDeleteFalloutThresholdToBe(String deleteFalloutThreshold) throws NoSuchFieldException, IllegalAccessException {
        Class<?> purgeClass = purgeFalloutScheduler.getClass();
        Field field = purgeClass.getDeclaredField("deleteFalloutThreshold");
        field.setAccessible(true);
        field.set(purgeFalloutScheduler, Duration.parse(deleteFalloutThreshold));
    }

    @When("the cron job is executed")
    public void theCronJobIsExecuted() {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            purgeFalloutScheduler.startPurgeFalloutIncident();
        }
    }

    @Then("the following fallout incidents are deleted:")
    public void theFollowingFalloutIncidentsAreDeleted(List<FalloutIncidentRecord> falloutIncidentRecords) {
        this.processFlowList = mongoTemplate.findAll(ProcessFlow.class);
        this.taskFlowList = mongoTemplate.findAll(DiscoTaskFlow.class);
        for (FalloutIncidentRecord falloutIncidentRecord : falloutIncidentRecords) {
            Optional<FalloutIncident> falloutIncident = falloutRepository.findById(falloutIncidentRecord.falloutId());
            assertThat(falloutIncident)
                    .as("Fallout incident with id %s should not exist", falloutIncidentRecord.falloutId())
                    .isEmpty();
        }
    }

    @And("the following process flows are deleted:")
    public void theFollowingProcessFlowsAreDeleted(List<ProcessFlowRecord> processFlowRecordList) {
        this.processFlowList = mongoTemplate.findAll(ProcessFlow.class);
        for (ProcessFlowRecord processFlowRecord : processFlowRecordList) {
            assertThat(this.processFlowList.stream().filter(processFlow -> Objects.equals(processFlow.getId(), processFlowRecord.processFlowId())).findFirst())
                    .as("Process flow with id %s should not exist", processFlowRecord.processFlowId())
                    .isEmpty();
        }
    }

    @And("the following task flows are deleted:")
    public void theFollowingTaskFlowsAreDeleted(List<TaskFlowRecord> taskFlowRecordList) {
        this.taskFlowList = mongoTemplate.findAll(DiscoTaskFlow.class);
        for (TaskFlowRecord taskFlowRecord : taskFlowRecordList) {
            assertThat(this.taskFlowList.stream().filter(taskFlow -> Objects.equals(taskFlow.getProcessFLowId(), taskFlowRecord.taskFlowId())).findFirst())
                    .as("Task flow with id %s should not exist", taskFlowRecord.taskFlowId())
                    .isEmpty();
        }
    }

    @And("the following fallout incidents still exist:")
    public void theFollowingFalloutIncidentsStillExist(List<FalloutIncidentRecord> falloutIncidentRecords) {
        for (FalloutIncidentRecord falloutIncidentRecord : falloutIncidentRecords) {
            Optional<FalloutIncident> falloutIncident = falloutRepository.findById(falloutIncidentRecord.falloutId());
            assertThat(falloutIncident).isPresent();
            assertThat(this.processFlowList.stream().filter(processFlow -> Objects.equals(processFlow.getId(), falloutIncidentRecord.falloutId())).findFirst())
                    .as("Fallout Incident with id %s should exist", falloutIncidentRecord.falloutId())
                    .isPresent();
        }
    }

    @And("the following process flows still exist:")
    public void theFollowingProcessFlowsStillExist(List<ProcessFlowRecord> processFlowRecords) {
        for (ProcessFlowRecord processFlowRecord : processFlowRecords) {
            assertThat(this.processFlowList.stream().filter(processFlow -> Objects.equals(processFlow.getId(), processFlowRecord.processFlowId())).findFirst())
                    .as("Process flow with id %s should exist", processFlowRecord.processFlowId())
                    .isPresent();
        }
    }

    @And("the following task flows still exist:")
    public void theFollowingTaskFlowsStillExist(List<TaskFlowRecord> taskFlowRecords) {
        for (TaskFlowRecord taskFlowRecord : taskFlowRecords) {
            assertThat(this.taskFlowList.stream().filter(taskFlow -> Objects.equals(taskFlow.getProcessFLowId(), taskFlowRecord.taskFlowId())).findFirst())
                    .as("Task flow with id %s should exist", taskFlowRecord.taskFlowId())
                    .isPresent();
        }
    }
}
