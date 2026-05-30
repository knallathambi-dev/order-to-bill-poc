// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;


import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.GroupedSpecIdAverageLeadTimeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.LeadTimeHistorySampledStatisticsRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.ContractLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.leadtimestatistics.model.NodeLeadTimeHistorySampledStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.AggregatedSpecIdLeadTimeStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.AggregatedSpecIdLeadTimeStatisticsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.ContractLeadTimeHistoryStatisticsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.leadtimestatistics.NodeLeadTimeHistoryStatisticsRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.AggregateSpecIdAverageLeadTimeScheduler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.StoreNodeHistoryStatisticsScheduler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.StorePlanHistoryStatisticsScheduler;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.awaitility.Awaitility;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions.fixedInstant;
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class LeadTimeHistory {

    @Autowired
    private NodeLeadTimeHistoryStatisticsRepository nodeLeadTimeHistoryStatisticsRepository;

    @Autowired
    private ContractLeadTimeHistoryStatisticsRepository contractLeadTimeHistoryStatisticsRepository;

    @Autowired
    private StorePlanHistoryStatisticsScheduler storePlanHistoryStatisticsScheduler;

    @Autowired
    private StoreNodeHistoryStatisticsScheduler storeNodeHistoryStatisticsScheduler;


    @Autowired
    private AggregateSpecIdAverageLeadTimeScheduler aggregateSpecIdAverageLeadTimeScheduler;

    @Autowired
    private AggregatedSpecIdLeadTimeStatisticsRepository aggregatedSpecIdLeadTimeStatisticsRepository;

    private static final String MINUTE_DURATION_EXPRESSION = "P0DT0H%sM";
    private static final Integer NUMBER_OF_SECONDS_IN_MINUTE = 60;

    @And("the cron job of storing plans running every {int}m")
    public void theCronJobOfStoringPlansRunningEveryM(int minutesCount) throws NoSuchFieldException, IllegalAccessException {
        Class<?> statisticsSchedulerClass = storePlanHistoryStatisticsScheduler.getClass();
        Field field = statisticsSchedulerClass.getDeclaredField("storePlanStatisticsDuration");
        field.setAccessible(true);
        field.set(storePlanHistoryStatisticsScheduler, Duration.parse(MINUTE_DURATION_EXPRESSION.formatted(minutesCount)));
    }

    @And("the cron job of storing nodes running every {int}m")
    public void theCronJobOfStoringNodesRunningEveryM(int minutesCount) throws NoSuchFieldException, IllegalAccessException {
        Class<?> statisticsSchedulerClass = storeNodeHistoryStatisticsScheduler.getClass();
        Field field = statisticsSchedulerClass.getDeclaredField("storeNodeStatisticsDuration");
        field.setAccessible(true);
        field.set(storeNodeHistoryStatisticsScheduler, Duration.parse(MINUTE_DURATION_EXPRESSION.formatted(minutesCount)));
    }

    @When("cron job that collects plan statistics is executed")
    public void cronJobThatCollectsPlanStatisticsIsExecuted() {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            storePlanHistoryStatisticsScheduler.storePlanStatistics();
        }
    }

    @When("cron job that collects node statistics is executed")
    public void cronJobThatCollectsNodeStatisticsIsExecuted() {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            storeNodeHistoryStatisticsScheduler.storeNodeStatistics();
        }
    }

    @Then("the node lead time history sampled statistics should be stored")
    public void nodeLeadTimeHistorySampledStatisticsWillBeStoredWithTheFollowingData(List<LeadTimeHistorySampledStatisticsRecord> nodeLeadTimeHistorySampledStatisticsRecords) {
        await().atMost(Duration.ofSeconds(40)).untilAsserted(() -> {
            List<NodeLeadTimeHistorySampledStatistics> nodeLeadTimeHistorySampledStatistics = nodeLeadTimeHistoryStatisticsRepository.findAll();

            assertThat(nodeLeadTimeHistorySampledStatistics).hasSameSizeAs(nodeLeadTimeHistorySampledStatisticsRecords);

            List<NodeLeadTimeHistorySampledStatistics> expectedNodeLeadTimeHistorySampledStatisticsList = nodeLeadTimeHistorySampledStatisticsRecords.stream()
                    .map(this::convertToNodeLeadTimeHistorySampledStatistics)
                    .collect(Collectors.toList());

            assertThat(nodeLeadTimeHistorySampledStatistics)
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                    .containsExactlyInAnyOrderElementsOf(expectedNodeLeadTimeHistorySampledStatisticsList);
        });
    }

    private NodeLeadTimeHistorySampledStatistics convertToNodeLeadTimeHistorySampledStatistics(LeadTimeHistorySampledStatisticsRecord sampledStatisticsRecord) {
        return NodeLeadTimeHistorySampledStatistics.builder()
                .productSpecId(sampledStatisticsRecord.specificationId())
                .deliveryFactoryName(sampledStatisticsRecord.deliveryFactoryName())
                .minActualLeadTime(Long.parseLong(sampledStatisticsRecord.minActualLeadTime()) * NUMBER_OF_SECONDS_IN_MINUTE)
                .maxActualLeadTime(Long.parseLong(sampledStatisticsRecord.maxActualLeadTime()) * NUMBER_OF_SECONDS_IN_MINUTE)
                .averageActualLeadTime(Float.parseFloat(sampledStatisticsRecord.averageActualLeadTime()) * NUMBER_OF_SECONDS_IN_MINUTE)
                .sampleSize(parseInt(sampledStatisticsRecord.sampleSize()))
                .sampleWindow(Instant.parse(sampledStatisticsRecord.sampleWindow()))
                .build();
    }

    @Then("the following plan lead time history sampled statistics will be stored")
    public void planLeadTimeHistorySampledStatisticsWillBeStoredWithTheFollowingData(List<LeadTimeHistorySampledStatisticsRecord> contractLeadTimeHistorySampledStatisticsRecords) {
        await().atMost(Duration.ofSeconds(40)).untilAsserted(() -> {
            List<ContractLeadTimeHistorySampledStatistics> contractLeadTimeHistorySampledStatistics = contractLeadTimeHistoryStatisticsRepository.findAll();
            assertThat(contractLeadTimeHistorySampledStatistics).hasSameSizeAs(contractLeadTimeHistorySampledStatisticsRecords);

            IntStream.range(0, contractLeadTimeHistorySampledStatistics.size()).forEach(i -> {
                ContractLeadTimeHistorySampledStatistics actual = contractLeadTimeHistorySampledStatistics.get(i);
                LeadTimeHistorySampledStatisticsRecord expected = contractLeadTimeHistorySampledStatisticsRecords.get(i);

                assertThat(actual.getContractName()).isEqualTo(expected.contractName());
                assertThat(actual.getMinActualLeadTime()).isEqualTo(Long.parseLong(expected.minActualLeadTime()) * NUMBER_OF_SECONDS_IN_MINUTE);
                assertThat(actual.getMaxActualLeadTime()).isEqualTo(Long.parseLong(expected.maxActualLeadTime()) * NUMBER_OF_SECONDS_IN_MINUTE);
                assertThat(actual.getAverageActualLeadTime()).isEqualTo(Float.parseFloat(expected.averageActualLeadTime()) * NUMBER_OF_SECONDS_IN_MINUTE);
                assertThat(actual.getSampleSize()).isEqualTo(parseInt(expected.sampleSize()));
                assertThat(actual.getSampleWindow()).isEqualTo(expected.sampleWindow());
            });
        });
    }

    @Given("the system has the following node lead time statistics")
    public void theSystemHasTheFollowingNodeLeadTimeStatistics(List<LeadTimeHistorySampledStatisticsRecord> sampledStatisticsRecords) {
        List<NodeLeadTimeHistorySampledStatistics> sampledStatisticsEntities = sampledStatisticsRecords.stream().map(sampledStatisticsRecord -> {
            NodeLeadTimeHistorySampledStatistics nodeLeadTimeHistorySampledStatistics = new NodeLeadTimeHistorySampledStatistics();

            nodeLeadTimeHistorySampledStatistics.setProductSpecId(sampledStatisticsRecord.specificationId());
            nodeLeadTimeHistorySampledStatistics.setAverageActualLeadTime(Float.valueOf(sampledStatisticsRecord.averageActualLeadTime()));
            nodeLeadTimeHistorySampledStatistics.setSampleSize(Integer.valueOf(sampledStatisticsRecord.sampleSize()));
            nodeLeadTimeHistorySampledStatistics.setSampleWindow(Instant.parse(sampledStatisticsRecord.sampleWindow()));

            return nodeLeadTimeHistorySampledStatistics;
        }).toList();

        nodeLeadTimeHistoryStatisticsRepository.saveAll(sampledStatisticsEntities);
    }

    @And("the system has the following grouped spec id lead time statistics")
    public void theSystemHasTheFollowingGroupedSpecIdLeadTimeStatistics(List<GroupedSpecIdAverageLeadTimeRecord> groupedSpecIdAverageLeadTimeRecords) {
        var groupedSpecIdAverageLeadTimeEntities = groupedSpecIdAverageLeadTimeRecords.stream().map(
                groupedSpecIdAverageLeadTimeRecord -> AggregatedSpecIdLeadTimeStatistics.builder()
                        .productSpecId(groupedSpecIdAverageLeadTimeRecord.specificationId())
                        .averageLeadTime(groupedSpecIdAverageLeadTimeRecord.averageLeadTime())
                        .lastUpdatedDate(Instant.parse(groupedSpecIdAverageLeadTimeRecord.lastUpdated()))
                        .build()
        ).toList();

        aggregatedSpecIdLeadTimeStatisticsRepository.saveAll(groupedSpecIdAverageLeadTimeEntities);
    }

    @And("the cron job of grouping spec id average lead times has scan window of 2 day")
    public void theCronJobOfGroupingSpecIdAverageLeadTimesHasScanWindowOfDay() throws NoSuchFieldException, IllegalAccessException {
        Class<?> statisticsSchedulerClass = aggregateSpecIdAverageLeadTimeScheduler.getClass();
        Field field = statisticsSchedulerClass.getDeclaredField("aggregateSpecIdAverageLeadTimeWindow");
        field.setAccessible(true);
        field.set(aggregateSpecIdAverageLeadTimeScheduler, Duration.parse("P2D"));
    }

    @When("cron job that groups average leadtime stats for spec ids is executed")
    public void cronJobThatGroupsAverageLeadtimeStatsForSpecIdsIsExecuted() {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            aggregateSpecIdAverageLeadTimeScheduler.storeSpecIdAverageLeadTime();
        }
    }

    @Then("the system will have the following grouped weighted average spec id lead times")
    public void theSystemWillHaveTheFollowingGroupedWeightedAverageSpecIdLeadTimes(List<GroupedSpecIdAverageLeadTimeRecord> expectedGroupedSpecIdAverageLeadTimeRecords) {
        Awaitility.await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            var actualSpecIdLeadTimeStatisticsGroupedList = aggregatedSpecIdLeadTimeStatisticsRepository.findAll();

            assertThat(actualSpecIdLeadTimeStatisticsGroupedList).isNotNull();
            assertThat(actualSpecIdLeadTimeStatisticsGroupedList).hasSameSizeAs(expectedGroupedSpecIdAverageLeadTimeRecords);

            actualSpecIdLeadTimeStatisticsGroupedList.sort(Comparator.comparing(AggregatedSpecIdLeadTimeStatistics::getProductSpecId));

            var sortedExpectedList = new ArrayList<>(expectedGroupedSpecIdAverageLeadTimeRecords);
            sortedExpectedList.sort(Comparator.comparing(GroupedSpecIdAverageLeadTimeRecord::specificationId));

            IntStream.range(0, expectedGroupedSpecIdAverageLeadTimeRecords.size()).forEach(i -> {
                var actual = actualSpecIdLeadTimeStatisticsGroupedList.get(i);
                var expected = sortedExpectedList.get(i);

                assertThat(actual.getProductSpecId()).isEqualTo(expected.specificationId());
                assertThat(actual.getAverageLeadTime())
                        .isCloseTo(expected.averageLeadTime(), within(0.01f));
                assertThat(actual.getLastUpdatedDate()).isEqualTo(Instant.parse(expected.lastUpdated()));
            });

        });
    }
}