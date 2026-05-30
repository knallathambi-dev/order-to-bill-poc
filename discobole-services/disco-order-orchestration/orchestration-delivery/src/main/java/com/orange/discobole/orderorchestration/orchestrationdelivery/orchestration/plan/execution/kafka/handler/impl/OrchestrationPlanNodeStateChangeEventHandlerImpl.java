// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.OrchestrationDeliveryFalloutManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config.KafkaSessionScope;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.OrchestrationPlanNodeStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.OrchestrationPlanExecutionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.validator.OrchestrationPlanNodeEventValidator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.HELD;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class OrchestrationPlanNodeStateChangeEventHandlerImpl implements OrchestrationPlanNodeStateChangeEventHandler {
    public static final String HANDLER_LOG_MESSAGE = "OrchestrationPlanNodeStateChangeEventHandlerImpl | handleEvent | Received OrchestrationPlanNodeStateChangeEvent: {}";
    private final OrchestrationPlanExecutionService orchestrationPlanExecutionService;
    private final DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;
    private final OrchestrationDeliveryFalloutManagement orchestrationDeliveryFalloutManagement;
    private final ErrorMessageMapper errorMessageMapper;
    private final OrchestrationPlanRepository orchestrationPlanRepository;
    private final UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;

    @Override
    @KafkaSessionScope
    public void handleEvent(OrchestrationPlanNodeStateChangeEvent message) {
        log.info(HANDLER_LOG_MESSAGE, message.getEventId());
        log.debug(HANDLER_LOG_MESSAGE, message);
        OrchestrationPlanNodeEventValidator.validateSanityOrchestrationPlanNodeEvent(message);
        OrchestrationPlanNode node = message.getEvent().getOrchestrationPlanNode();
        orchestrationPlanExecutionService.executeOrchestrationPlanNode(node);

        dataPersistenceKafkaSessionService.persistAll();

        log.info("OrchestrationPlanNodeStateChangeEventHandlerImpl | handleEvent | Finished consuming OrchestrationPlanNodeStateChangeEvent: {}", message.getEventId());
    }

    @Override
    public void handleDeadLetter(OrchestrationPlanNodeStateChangeEvent payload, FalloutCharacteristicWrapper characteristicWrapper) {
        OrchestrationPlanNode orchestrationPlanNode = payload.getEvent().getOrchestrationPlanNode();
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(orchestrationPlanNode.getId()).orElseThrow(
                () -> new CoodNonRecoverableAndNonRetryableException(new OrchestrationPlanNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, orchestrationPlanNode.getId()))
        );
        orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(node -> node.getId().equals(orchestrationPlanNode.getId()) && !HELD.equals(node.getState()))
                .findFirst()
                .ifPresent(node -> {
                    orchestrationDeliveryFalloutManagement.createFalloutProcessFromDLT(node, orchestrationPlan, characteristicWrapper);
                    node.addErrorMessage(errorMessageMapper.mapToNodeError(characteristicWrapper.getCoodError()));
                    node.setState(HELD);
                    updateNodeAndProductStateHandler.update(node);
                });
    }
}
