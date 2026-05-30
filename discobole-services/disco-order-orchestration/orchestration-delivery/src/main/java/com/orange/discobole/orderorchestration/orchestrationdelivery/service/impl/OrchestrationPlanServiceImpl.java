// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;

import java.time.Instant;
import java.util.Map;

/**
 * Implementation of the OrchestrationPlanStateService interface.
 */
@Slf4j
@Service
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class OrchestrationPlanServiceImpl implements OrchestrationPlanService {
    public static final Instant UNDEFINED_DATE_VALUE = Instant.parse("9999-12-31T23:59:59.999Z");
    public static final long UNDEFINED_LONG_VALUE = Long.MAX_VALUE;

    private final DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;

    private final OrchestrationPlanModificationService orchestrationPlanModificationService;

    private final EventPublisher eventPublisher;

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    /**
     * Updates the state of the given orchestration plan node.
     *
     * @param orchestrationPlanNode the orchestration plan node to update
     * @param state                 the new state of the orchestration plan node
     */
    @Override
    public void updateOrchestrationPlanNodeState(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlanNodeState state) {
        orchestrationPlanNode.setState(state);
        updateNodeDataInOrchestrationPlan(orchestrationPlanNode);
        log.debug("Updated and published event for node {} with state {}", orchestrationPlanNode.getId(), orchestrationPlanNode.getState());
    }

    /**
     * Updates the state of the given orchestration plan node with an error message.
     *
     * @param orchestrationPlanNode the orchestration plan node to update
     * @param state                 the new state of the orchestration plan node
     */
    @Override
    public void updateOrchestrationPlanNodeStateWithErrorMessage(OrchestrationPlanNode orchestrationPlanNode, OrchestrationPlanNodeState state) {
        log.info("OrchestrationPlanStateServiceImpl | updateOrchestrationPlanNodeStateWithErrorMessage | updating node {} with error message {} with state {}", orchestrationPlanNode.getId(), orchestrationPlanNode.getErrorMessage(), state);
        dataPersistenceKafkaSessionService.addNodeStateChange(orchestrationPlanNode);
        log.info("OrchestrationPlanStateServiceImpl | updateOrchestrationPlanNodeStateWithErrorMessage | updated and published event for node {} with error message {} with state {}", orchestrationPlanNode.getId(), orchestrationPlanNode.getErrorMessage(), state);
    }

    /**
     * Sets the next state of the given orchestration plan node.
     *
     * @param orchestrationPlanNode the orchestration plan node to update
     */
    @Override
    public void setNextOrchestrationPlanNodeState(OrchestrationPlanNode orchestrationPlanNode) {
        if (orchestrationPlanNode == null || orchestrationPlanNode.getState() == null) {
            return;
        }
        log.info("OrchestrationPlanStateServiceImpl | setNextOrchestrationPlanNodeState | update Orchestration Plan Node {} state", orchestrationPlanNode.getId());

        OrchestrationPlanNodeState newState = determineNewState(orchestrationPlanNode.getState());

        if (newState != null) {
            orchestrationPlanNode.setState(newState);
            log.info("OrchestrationPlanStateServiceImpl | setNextOrchestrationPlanNodeState | Orchestration Plan Node id {} state updated to {}", orchestrationPlanNode.getId(), newState);
            dataPersistenceKafkaSessionService.addNodeStateChange(orchestrationPlanNode);
            log.info("OrchestrationPlanStateServiceImpl | setNextOrchestrationPlanNodeState | event published for Orchestration Plan Node id {}", orchestrationPlanNode.getId());
            log.debug("OrchestrationPlanStateServiceImpl | setNextOrchestrationPlanNodeState | event published for Orchestration Plan Node : {}", orchestrationPlanNode);
        }
    }

    /**
     * Determines the new state for the given orchestration plan node state.
     *
     * @param currentState the current state of the orchestration plan node
     * @return the new state of the orchestration plan node
     */
    private OrchestrationPlanNodeState determineNewState(OrchestrationPlanNodeState currentState) {
        switch (currentState) {
            case INITIALIZED:
                return OrchestrationPlanNodeState.ACKNOWLEDGED;
            case ACKNOWLEDGED:
                return OrchestrationPlanNodeState.IN_PROGRESS;
            case IN_PROGRESS:
                return OrchestrationPlanNodeState.IN_DELIVERY;
            default:
                return OrchestrationPlanNodeState.HELD;
        }
    }

    /**
     * Updates the node related products.
     *
     * @param node the orchestration plan node
     * @throws CoodDBException if there is an issue with the database
     */
    @Override
    public void updateNodeRelatedProducts(OrchestrationPlanNode node) throws CoodDBException {
        log.info("OrchestrationPlanStateServiceImpl | updateNodeRelatedProducts | Updating related products for node id: {}", node.getId());
        log.debug("OrchestrationPlanStateServiceImpl | updateNodeRelatedProducts | Node details: {}", node);
        dataPersistenceKafkaSessionService.addNodeRelatedProductChange(node);
        log.info("OrchestrationPlanStateServiceImpl | updateNodeRelatedProducts | Related products updated for node id: {}", node.getId());
    }

    /**
     * Saves the given orchestration plan.
     *
     * @param orchestrationPlan the orchestration plan to save
     */
    @Override
    public void savePlan(OrchestrationPlan orchestrationPlan) {
        orchestrationPlanRepository.save(orchestrationPlan);
        eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, orchestrationPlan);
        log.debug("Orchestration plan with id: {} has been saved", orchestrationPlan.getId());
    }

    @Override
    public void updateOrchestrationPlan(OrchestrationPlan orchestrationPlan, State state) {
        orchestrationPlan.setState(state);
        savePlan(orchestrationPlan);
        log.info("OrchestrationPlanStateServiceImpl | updatePlanState | orchestration plan saved in DB: orchestration plan id: " + orchestrationPlan.getId());
    }

    /**
     * Helper class to hold the counts of different states of the orchestration plan nodes.
     */

    @Override
    @Transactional
    public void updateNodeDataInOrchestrationPlan(OrchestrationPlanNode updatedNode) throws CoodDBException {
        orchestrationPlanModificationService.updateNodeDataInOrchestrationPlan(updatedNode, Map.of());
    }
}