// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.repository;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface ReactiveOrchestrationPlanRepository extends ReactiveMongoRepository<OrchestrationPlan, String> {

    Flux<OrchestrationPlan> findOrchestrationPlansByStateAndOrchestrationPlanSchedule_OrderStartDateIsBeforeOrStateAndRequestedDeliveryDateIsBefore(State state, Instant orderStartDate, State state2, Instant requestedDeliveryDate);

    Flux<OrchestrationPlan> findOrchestrationPlanByStateAndOrchestrationPlanNodes_OrchestrationNodeSchedule_ActualOrderItemCompletionDateIsAfter(State state, Instant instant);

    Flux<OrchestrationPlan> findByOrchestrationPlanNodes_StateAndOrchestrationPlanNodes_OrchestrationNodeSchedule_ActualOrderItemCompletionDateIsAfter(
            OrchestrationPlanNodeState state,
            Instant instant
    );

    Flux<OrchestrationPlan> findOrchestrationPlanByStateAndOrchestrationPlanNodes_StateAndOrchestrationPlanNodes_OrchestrationNodeSchedule_OrderItemStartDateIsBefore(
            State planState,
            OrchestrationPlanNodeState state,
            Instant instant
    );
    
    Mono<Long> countOrchestrationPlansByStateAndOrchestrationPlanSchedule_OrderStartDateIsBeforeOrStateAndRequestedDeliveryDateIsBefore(State state, Instant orderStartDate, State state2, Instant requestedDeliveryDate);

    //fetch plans by state and orderStartDate is before now
    //if orderStartDate doesn't exist, fetch by requestedDeliveryDate is before now
    default Flux<OrchestrationPlan> findByStateAndDateBeforeNow(State state, Instant orderStartDate, Instant requestedDeliveryDate) {
        return findOrchestrationPlansByStateAndOrchestrationPlanSchedule_OrderStartDateIsBeforeOrStateAndRequestedDeliveryDateIsBefore(state, orderStartDate, state, requestedDeliveryDate);
    }

    default Mono<Long> countByStateAndDateBeforeNow(State state, Instant orderStartDate, Instant requestedDeliveryDate) {
        return countOrchestrationPlansByStateAndOrchestrationPlanSchedule_OrderStartDateIsBeforeOrStateAndRequestedDeliveryDateIsBefore(state, orderStartDate, state, requestedDeliveryDate);
    }
}