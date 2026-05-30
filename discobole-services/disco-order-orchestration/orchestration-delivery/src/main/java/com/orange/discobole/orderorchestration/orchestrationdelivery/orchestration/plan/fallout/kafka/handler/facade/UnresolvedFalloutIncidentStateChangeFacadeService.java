// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.fallout.kafka.handler.facade;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.enums.ResolutionState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.RelatedEntityRole;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.RelatedEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config.KafkaSessionScope;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.ORCHESTRATION_PLAN_NODE_NOT_HELD;
import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.ORCHESTRATION_PLAN_NOT_HELD;

@Service
@Slf4j
public class UnresolvedFalloutIncidentStateChangeFacadeService {

    private final OrchestrationPlanRepository orchestrationPlanRepository;
    private final OrchestrationPlanService orchestrationPlanService;
    private final DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;
    private final UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;
    private final EventPublisher eventPublisher;

    @SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    public UnresolvedFalloutIncidentStateChangeFacadeService(OrchestrationPlanRepository orchestrationPlanRepository, OrchestrationPlanService orchestrationPlanService, DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService, UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler, EventPublisher eventPublisher) {
        this.orchestrationPlanRepository = orchestrationPlanRepository;
        this.orchestrationPlanService = orchestrationPlanService;
        this.dataPersistenceKafkaSessionService = dataPersistenceKafkaSessionService;
        this.updateNodeAndProductStateHandler = updateNodeAndProductStateHandler;
        this.eventPublisher = eventPublisher;
    }

    private static void checkFalloutOrchestrationPlanNodeState(OrchestrationPlanNode initiatorOrchestrationPlanNode) {
        if (!initiatorOrchestrationPlanNode.getState().equals(OrchestrationPlanNodeState.HELD)) {
            throw new CoodTechnicalException(ORCHESTRATION_PLAN_NODE_NOT_HELD, initiatorOrchestrationPlanNode.getId());
        }
    }

    private static void checkFalloutOrchestrationPlanState(OrchestrationPlan orchestrationPlanIdInFalloutIncident, OrchestrationPlan orchestrationPlan) {
        if (!orchestrationPlan.getState().equals(State.HELD)) {
            throw new CoodTechnicalException(ORCHESTRATION_PLAN_NOT_HELD, orchestrationPlanIdInFalloutIncident.getId());
        }
    }

    private static boolean isRelatedEntityOrchestrationPlanNode(RelatedEntity initiatorRelatedEntity) {
        return initiatorRelatedEntity.getAtReferredType().equalsIgnoreCase(OrchestrationPlanNode.class.getSimpleName());
    }

    private static boolean isRelatedEntityOrchestrationPlan(RelatedEntity initiatorRelatedEntity) {
        return initiatorRelatedEntity.getAtReferredType().equalsIgnoreCase(OrchestrationPlan.class.getSimpleName());
    }

    private void falloutOrchestrationPlanProcess(OrchestrationPlan falloutIncidentOrchestrationPlan) {
        checkFalloutOrchestrationPlanState(falloutIncidentOrchestrationPlan, falloutIncidentOrchestrationPlan);
        if (falloutIncidentOrchestrationPlan.getOrchestrationPlanNodes().isEmpty()) {
            orchestrationPlanService.updateOrchestrationPlan(falloutIncidentOrchestrationPlan, State.REJECTED);
        } else {
            orchestrationPlanService.updateOrchestrationPlan(falloutIncidentOrchestrationPlan, State.ABORTED);
            falloutIncidentOrchestrationPlan.getOrchestrationPlanNodes().stream()
                    .filter(node -> !(node.getState().equals(OrchestrationPlanNodeState.FAILED) || node.getState().equals(OrchestrationPlanNodeState.CANCELED) || node.getState().equals(OrchestrationPlanNodeState.COMPLETED)))
                    .forEach(nodeNotDone -> orchestrationPlanService.updateOrchestrationPlanNodeState(nodeNotDone, OrchestrationPlanNodeState.ABORTED)
                    );
        }
        eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, falloutIncidentOrchestrationPlan);
    }

    @KafkaSessionScope
    public void processEvent(FalloutIncident falloutIncident, String eventId) {
        if (isFalloutNotOrchestrationPlanOrNode(falloutIncident)) {
            return;
        }
        OrchestrationPlan orchestrationPlanIdInFalloutIncident = findOrchestrationPlanIdInFalloutIncident(falloutIncident, eventId);
        RelatedEntity initiatorRelatedEntity = getIntiatorRelatedEntity(falloutIncident, eventId);
        if (isFalloutToBeIgnored(falloutIncident, orchestrationPlanIdInFalloutIncident, initiatorRelatedEntity)) {
            return;
        }
        if (isRelatedEntityOrchestrationPlan(initiatorRelatedEntity)) {
            falloutOrchestrationPlanProcess(orchestrationPlanIdInFalloutIncident);
        } else if (isRelatedEntityOrchestrationPlanNode(initiatorRelatedEntity)) {
            falloutOrchestrationPlanNodeProcess(orchestrationPlanIdInFalloutIncident, initiatorRelatedEntity);
        }
        dataPersistenceKafkaSessionService.persistAll();
    }

    private boolean isFalloutNotOrchestrationPlanOrNode(FalloutIncident falloutIncident) {
        return falloutIncident.getRelatedEntity().stream()
                .filter(relatedEntity -> relatedEntity.getRole().equals(RelatedEntityRole.INITIATOR))
                .noneMatch(relatedEntity -> (relatedEntity.getAtReferredType().equals(OrchestrationPlan.class.getSimpleName()) ||
                        (relatedEntity.getAtReferredType().equals(OrchestrationPlanNode.class.getSimpleName()))));

    }

    private boolean isFalloutToBeIgnored(FalloutIncident falloutIncident, OrchestrationPlan orchestrationPlanIdInFalloutIncident, RelatedEntity initiatorRelatedEntity) {
        boolean isInitiatorStateNotHeld = false;
        if (initiatorRelatedEntity.getAtReferredType().equalsIgnoreCase(OrchestrationPlan.class.getSimpleName()) && !orchestrationPlanIdInFalloutIncident.getState().equals(State.HELD)) {
            isInitiatorStateNotHeld = true;
        } else if (initiatorRelatedEntity.getAtReferredType().equalsIgnoreCase(OrchestrationPlanNode.class.getSimpleName())) {
            Optional<OrchestrationPlanNode> initiatorNode = orchestrationPlanIdInFalloutIncident.getOrchestrationPlanNodeById(initiatorRelatedEntity.getId());
            if (initiatorNode.isPresent() && !initiatorNode.get().getState().equals(OrchestrationPlanNodeState.HELD)) {
                isInitiatorStateNotHeld = true;
            }
        }
        return !falloutIncident.getResolution().getStatus().equals(ResolutionState.UNRESOLVED) || isInitiatorStateNotHeld;
    }

    private void falloutOrchestrationPlanNodeProcess(OrchestrationPlan orchestrationPlanIdInFalloutIncident, RelatedEntity initiatorRelatedEntity) {
        OrchestrationPlanNode initiatorOrchestrationPlanNode = orchestrationPlanIdInFalloutIncident.getOrchestrationPlanNodes().stream().filter(orchestrationPlanNode -> orchestrationPlanNode.getId().equals(initiatorRelatedEntity.getId()))
                .findFirst().orElseThrow(() -> new OrchestrationPlanNotFoundException(ExceptionCode.INVALID_FALLOUT_EVENT_NO_ORCHESTRATION_PLAN_NODE));
        checkFalloutOrchestrationPlanNodeState(initiatorOrchestrationPlanNode);
        initiatorOrchestrationPlanNode.setState(OrchestrationPlanNodeState.FAILED);
        updateNodeAndProductStateHandler.update(initiatorOrchestrationPlanNode);
    }

    private RelatedEntity getIntiatorRelatedEntity(FalloutIncident falloutIncident, String eventId) {
        return falloutIncident.getRelatedEntity().stream().filter(relatedEntity -> relatedEntity.getRole().equals(RelatedEntityRole.INITIATOR))
                .findFirst().orElseThrow(() -> new CoodTechnicalException(ExceptionCode.INVALID_FALLOUT_EVENT_NO_INITIATOR, eventId));
    }

    private OrchestrationPlan findOrchestrationPlanIdInFalloutIncident(FalloutIncident falloutIncident, String eventId) {
        String id = falloutIncident.getRelatedEntity().stream()
                .filter(relatedEntity -> relatedEntity.getAtReferredType().equals(OrchestrationPlan.class.getSimpleName()))
                .map(RelatedEntity::getId)
                .findFirst()
                .orElseThrow(() -> new CoodTechnicalException(ExceptionCode.INVALID_FALLOUT_EVENT_NO_ORCHESTRATION_PLAN, eventId));
        return orchestrationPlanRepository.findOrchestrationPlanById(id).orElseThrow(() -> new OrchestrationPlanNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND, id));
    }
}
