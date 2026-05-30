// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.AggregateSpecIdLeadTimeStatisticsRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.AggregatedSpecIdLeadTimeStatistics;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class EvolveCreateOrchestrationPlanSetInitialEstimatedLeadTime {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Then("The system will create orchestration plan with order id {string}, state {string} and estimated lead time {long}")
    public void theSystemWillCreateOrchestrationPlanWithOrderIdStateAndEstimatedLeadTime(String orderId, String state, Long estimatedLeadTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(orderId));
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(orchestrationPlan).isNotNull();
            assertThat(orchestrationPlan.getState()).isEqualTo(State.fromValue(state));
            assertThat(orchestrationPlan.getOrchestrationPlanSchedule().getEstimatedOrderDeliveryLeadTime()).isEqualTo(estimatedLeadTime * (3600));
        });
    }

    @And("the system has the following aggregate spec id lead time history statistics")
    public void theSystemHasTheFollowingAggregateSpecIdLeadLeadTimeHistoryStatistics(List<AggregateSpecIdLeadTimeStatisticsRecord> rows) {
        List<AggregatedSpecIdLeadTimeStatistics> aggregatedSpecIdLeadTimeStatistics = rows.stream().map(aggregateSpecIdLeadTimeStatisticsRecord ->
                {
                    int secondsInHour = 3600;
                    return AggregatedSpecIdLeadTimeStatistics.builder()
                            .productSpecId(aggregateSpecIdLeadTimeStatisticsRecord.specificationId())
                            .averageLeadTime(aggregateSpecIdLeadTimeStatisticsRecord.averageLeadTime() * secondsInHour)
                            .build();
                })
                .toList();
        mongoTemplate.insertAll(aggregatedSpecIdLeadTimeStatistics);
    }
}
