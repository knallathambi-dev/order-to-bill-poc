// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.fallout.kafka.handler.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.generated.fallout.FalloutIncidentStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProcessFlowManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.exception.model.CoodNotFoundException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.RelatedEntityRole;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.RelatedEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.enums.ResolutionState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config.KafkaSessionScope;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.fallout.kafka.handler.ResolvedFalloutIncidentStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.FalloutStateChangeEventReExecutionHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.COOD_TECHNICAL_EXCEPTION;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResolvedFalloutIncidentStateChangeEventHandlerImpl implements ResolvedFalloutIncidentStateChangeEventHandler {

    private static final String EVENT_TOPIC = "eventTopic";

    private static final String EVENT_PAYLOAD = "eventPayload";

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    private final OrchestrationPlanModificationService orchestrationPlanModificationService;

    private final FalloutStateChangeEventReExecutionHandler falloutStateChangeEventReExecutionHandler;

    private final ProcessFlowManagement processFlowManagement;

    private final UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;

    @Override
    @KafkaSessionScope
    public void handle(FalloutIncidentStateChangeEvent event) {
        FalloutIncident falloutIncident = event.getEvent();
        if (ResolutionState.RESOLVED.equals(falloutIncident.getResolution().getStatus())) {
            RelatedEntity initiatorRelatedEntity = falloutIncident.getRelatedEntity().stream().filter(relatedEntity -> RelatedEntityRole.INITIATOR.equals(relatedEntity.getRole())).findFirst()
                    .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(new CoodNotFoundException(COOD_TECHNICAL_EXCEPTION, "FalloutIncident initiator related entity not found")));
            if (OrchestrationPlan.class.getSimpleName().equals(initiatorRelatedEntity.getAtReferredType())) {
                handleOrchestrationPlan(initiatorRelatedEntity, falloutIncident);
            } else if (OrchestrationPlanNode.class.getSimpleName().equals(initiatorRelatedEntity.getAtReferredType())) {
                handleOrchestrationPlanNode(initiatorRelatedEntity, falloutIncident);
            }
        }
    }

    private void handleOrchestrationPlan(RelatedEntity relatedEntity, FalloutIncident falloutIncident) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanById(relatedEntity.getId())
                .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(new CoodNotFoundException(COOD_TECHNICAL_EXCEPTION, "No OrchestrationPlan found with id: " + relatedEntity.getId())));
        if (State.HELD.equals(orchestrationPlan.getState())) {
            orchestrationPlan.setState(orchestrationPlan.getPreviousState());
            orchestrationPlanModificationService.updatePlanStateById(orchestrationPlan.getId(), orchestrationPlan.getPreviousState());
            reExecuteFalloutIncident(falloutIncident.getId());
        }
    }

    private void handleOrchestrationPlanNode(RelatedEntity relatedEntity, FalloutIncident falloutIncident) {
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlanRepository.findOrchestrationPlanNodeById(relatedEntity.getId())
                .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(new CoodNotFoundException(COOD_TECHNICAL_EXCEPTION, "No OrchestrationPlanNode found with id: " + relatedEntity.getId())));
        if (OrchestrationPlanNodeState.HELD.equals(orchestrationPlanNode.getState())) {
            orchestrationPlanNode.setState(orchestrationPlanNode.getPreviousState());
            updateNodeAndProductStateHandler.update(orchestrationPlanNode);
            reExecuteFalloutIncident(falloutIncident.getId());
        }
    }

    private void reExecuteFalloutIncident(String falloutId) {
        ProcessFlow processFlow = processFlowManagement.getProcessFlowById(falloutId);
        StringCharacteristic topicName = (StringCharacteristic) getCharacteristicFrom(processFlow, EVENT_TOPIC);
        ObjectCharacteristic payload = (ObjectCharacteristic) getCharacteristicFrom(processFlow, EVENT_PAYLOAD);
        falloutStateChangeEventReExecutionHandler.handle(topicName.getValue(), payload);
    }

    private Characteristic getCharacteristicFrom(ProcessFlow processFlow, String name) {
        return processFlow.getCharacteristic().stream().filter(characteristic -> name.equals(characteristic.getName())).findFirst()
                .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(new CoodNotFoundException(COOD_TECHNICAL_EXCEPTION, "No characteristic found with name: " + name)));
    }
}
