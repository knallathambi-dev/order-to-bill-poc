// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.OrchestrationPlanRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions.fixedInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class PlannedOrchestrationPlanScheduler {

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;

    @Autowired
    private com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.PlannedOrchestrationPlanScheduler plannedOrchestrationPlanScheduler;

    @Autowired
    private com.orange.discobole.orderorchestration.orchestrationdelivery.scheduler.RootNodeStartTimeScheduler rootNodeStartTimeScheduler;

    /**
     * Helper method to create orchestration plan nodes
     */
    private Set<OrchestrationPlanNode> createPlanNodes(String planId) {
        return IntStream.range(0, 2)
                .mapToObj(i -> OrchestrationPlanNode.builder()
                        .id(UUID.randomUUID().toString())
                        .state(OrchestrationPlanNodeState.ACKNOWLEDGED)
                        .relatedProduct(List.of(RelatedProduct.builder()
                                .id("relatedProduct-" + planId + "-" + i)
                                .type(RelatedProductType.PHYSICAL_PRODUCT)
                                .relationshipType(RelatedProductRelationType.DELIVERS)
                                .productOrderItemId("productOrder-" + planId + "-" + i)
                                .build()))
                        .build())
                .collect(Collectors.toSet());
    }

    /**
     * Helper method to create and save an orchestration plan
     */
    private OrchestrationPlan createAndSavePlan(String planId, State state, Instant deliveryDate) {
        OrchestrationPlan plan = OrchestrationPlan.builder()
                .id(planId)
                .state(state)
                .requestedDeliveryDate(deliveryDate)
                .orchestrationPlanNodes(createPlanNodes(planId))
                .build();
        return orchestrationPlanRepository.save(plan);
    }

    @Given("the system has the following plans")
    public void theSystemHasTheFollowingPlans(List<OrchestrationPlanRecord> planRecords) {
        planRecords.forEach(planData -> createAndSavePlan(
                planData.planId(),
                State.fromValue(planData.state()),
                Instant.parse(planData.requestedDeliveryDate())));
    }

    /**
     * Helper method to assert plan state
     */
    private void assertPlanState(String planId, State expectedState) {
        Optional<OrchestrationPlan> planOptional = orchestrationPlanRepository.findOrchestrationPlanById(planId);
        assertThat(planOptional)
                .as("Plan %s should exist", planId)
                .isPresent();
        assertEquals(expectedState, planOptional.get().getState(),
                String.format("Plan %s should be in %s state", planId, expectedState));
    }

    @Then("the plan {string} will be updated with {string} state")
    public void thePlanWillBeUpdatedWithState(String planId, String state) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            Optional<OrchestrationPlan> orchestrationPlanOptional = orchestrationPlanRepository
                    .findOrchestrationPlanById(planId);
            Assertions.assertEquals(State.fromValue(state), orchestrationPlanOptional.get().getState());
        });

    }

    @Then("the plan {string} still with {string} state")
    public void thePlanStillWithState(String planId, String state) {
        assertPlanState(planId, State.fromValue(state));
    }

    @When("the configured cron job to check plans with {string} state is triggered")
    public void theConfiguredCronJobToCheckPlansWithStateIsTriggered(String state) {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class,
                withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            plannedOrchestrationPlanScheduler.acknowledgePlannedStateOnDeliveryDate();
        }
    }

    @When("the system cron job for changing delayed root nodes from acknowledged to inprogress runs")
    public void theConfiguredCronJobToCheckOrderItemStartDateIsTriggered() {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class,
                withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            rootNodeStartTimeScheduler.processRootNodesWithPassedStartTime();
        }
    }

    @Then("the following plans state will not be changed")
    public void theFollowingPlansStateWillNotBeChanged(List<OrchestrationPlanRecord> planRecords) {
        planRecords.forEach(planData -> assertPlanState(planData.planId(), State.fromValue(planData.state())));
    }

    @Given("the transition window is configured from {string} to {string}")
    public void theTransitionWindowIsConfiguredFromTo(String startTime, String endTime) {
        ReflectionTestUtils.setField(plannedOrchestrationPlanScheduler, "windowStartTime", startTime);
        ReflectionTestUtils.setField(plannedOrchestrationPlanScheduler, "windowEndTime", endTime);
    }

    @And("the recommended batch size is {int}")
    public void theRecommendedBatchSizeIs(int batchSize) {
        ReflectionTestUtils.setField(plannedOrchestrationPlanScheduler, "recommendedBatchSize", batchSize);

    }

    @And("the job delay is {string}")
    public void theJobDelayIs(String delay) {
        ReflectionTestUtils.setField(plannedOrchestrationPlanScheduler, "jobExecutionDelay", Duration.parse(delay));
    }
}
