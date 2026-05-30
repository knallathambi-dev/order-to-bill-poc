// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.kafka.handler.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.OrchestrationDeliveryFalloutManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.FalloutCharacteristicWrapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.kafka.handler.DeliveryOrderItemStatusEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliverSelectedNodesService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.HELD;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class DeliveryOrderItemStatusHandlerImpl implements DeliveryOrderItemStatusEventHandler {
    private final DeliverSelectedNodesService deliverSelectedNodesService;
    private final OrchestrationDeliveryFalloutManagement orchestrationDeliveryFalloutManagement;
    private final OrchestrationPlanRepository orchestrationPlanRepository;
    private final ErrorMessageMapper errorMessageMapper;
    private final UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;
    private final OrchestrationPlanModificationService orchestrationPlanModificationService;

    @Override
    public void handleEvent(DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent, Map<String, String> headers) {
        log.info("trigger the delivery status event consumer node id: {}", deliveryOrderItemStatusEvent.getEvent().getOrderItemRef().getOrchestrationNodeId());
        log.debug("trigger the delivery status event consumer: {}", deliveryOrderItemStatusEvent);
        DeliveryOrderItemStatusPayloadEvent eventPayload = deliveryOrderItemStatusEvent.getEvent();
        deliverSelectedNodesService.postDeliverNodeActions(eventPayload, headers);
        log.info("finished triggering the delivery status event consumer node id: {}", deliveryOrderItemStatusEvent.getEvent().getOrderItemRef().getOrchestrationNodeId());
    }

    @Override
    public void handleDeadLetter(DeliveryOrderItemStatusEvent deliveryOrderItemStatusEvent, FalloutCharacteristicWrapper characteristicWrapper, Map<String, String> headers) {
        String orchestrationNodeId = deliveryOrderItemStatusEvent.getEvent().getOrderItemRef().getOrchestrationNodeId();
        log.info("DeliveryStatusHandlerImpl | handleEvent | Handling the delivery status event dead letter consumer node id: {}", orchestrationNodeId);

        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(orchestrationNodeId).orElseThrow(
                () -> new CoodNonRecoverableAndNonRetryableException(
                        new OrchestrationPlanNodeNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, orchestrationNodeId))
        );

        orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(node -> node.getId().equals(orchestrationNodeId) && !HELD.equals(node.getState()))
                .findFirst()
                .ifPresent(node -> {
                    orchestrationDeliveryFalloutManagement.createFalloutProcessFromDLT(node, orchestrationPlan, characteristicWrapper);
                    node.addErrorMessage(errorMessageMapper.mapToNodeError(characteristicWrapper.getCoodError()));
                    node.setState(HELD);
                    updateNodeAndProductStateHandler.update(node, headers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                });
    }
}
