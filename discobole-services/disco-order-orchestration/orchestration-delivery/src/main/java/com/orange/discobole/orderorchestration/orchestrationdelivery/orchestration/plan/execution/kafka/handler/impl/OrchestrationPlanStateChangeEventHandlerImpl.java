// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.OrchestrationDeliveryFalloutManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.OrchestrationPlanEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.OrchestrationPlanExecutionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.validator.OrchestrationPlanEventValidator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State.HELD;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State.IN_PROGRESS;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class OrchestrationPlanStateChangeEventHandlerImpl implements OrchestrationPlanEventHandler {
    private final OrchestrationPlanExecutionService orchestrationPlanExecutorService;
    private final OrchestrationDeliveryFalloutManagement orchestrationDeliveryFalloutManagement;
    private final OrchestrationPlanModificationService orchestrationPlanModificationService;
    private final ErrorMessageMapper errorMessageMapper;
    private final EventPublisher eventPublisher;
    private final OrchestrationPlanService orchestrationPlanService;
    private final OrchestrationPlanRepository orchestrationPlanRepository;

    @Override
    @Transactional // all operations need to happen in a trasnaction for idempotency
    public void handleEvent(OrchestrationPlanStateChangeEvent message) {
        log.info("OrchestrationPlanStateChangeEventHandlerImpl | handleEvent | start handling the event orchestration plan id {}", message.getEvent().getOrchestrationPlan().getId());
        OrchestrationPlanEventValidator.checkOrchestrationPlanEvent(message);
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanById(message.getEvent().getOrchestrationPlan().getId())
                .orElseThrow(() -> new CoodNotFoundException(ORCHESTRATION_PLAN_NOT_FOUND, message.getEvent().getOrchestrationPlan().getId()));
        if (IN_PROGRESS.equals(orchestrationPlan.getState())) {
            orchestrationPlanExecutorService.executeLeaf(orchestrationPlan);
        } else if (State.ACKNOWLEDGED.equals(orchestrationPlan.getState())) {
            orchestrationPlan.getOrchestrationPlanSchedule().setActualOrderStartDate(Instant.now());
            orchestrationPlanService.updateOrchestrationPlan(orchestrationPlan, IN_PROGRESS);
        }
    }

    @Override
    public void handleDeadLetter(OrchestrationPlanStateChangeEvent payload, FalloutCharacteristicWrapper characteristicWrapper) {
        OrchestrationPlan orchestrationPlan = payload.getEvent().getOrchestrationPlan();
        orchestrationDeliveryFalloutManagement.createFalloutProcessFromDLT(orchestrationPlan, characteristicWrapper);
        orchestrationPlan.addErrorMessage(errorMessageMapper.mapToPlanError(characteristicWrapper.getCoodError()));
        orchestrationPlanModificationService.updatePlanStateAndAddErrorMessageById(orchestrationPlan.getId(), orchestrationPlan.getErrorMessage(), orchestrationPlan.getState(), HELD);
        orchestrationPlan.setState(HELD);
        eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, orchestrationPlan);
    }
}
