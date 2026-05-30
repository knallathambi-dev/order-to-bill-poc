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
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.records.NodeStateChangeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrchestrationPlanModificationServiceImpl implements OrchestrationPlanModificationService {
    private final OrchestrationPlanRepository orchestrationPlanRepository;
    private final EventPublisher eventPublisher;

    public OrchestrationPlanModificationServiceImpl(
            OrchestrationPlanRepository orchestrationPlanRepository,
            EventPublisher eventPublisher
    ) {
        this.orchestrationPlanRepository = orchestrationPlanRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public int updateNodeStateById(NodeStateChangeRecord stateChangeRecord) throws CoodDBException {
        try {
            int result = orchestrationPlanRepository.updateOrchestrationPlanNodesStateById(stateChangeRecord.nodeId(), stateChangeRecord.state(), stateChangeRecord.previousState());
            log.info("OrchestrationPlanModificationServiceImpl | updateNodeStateById | updated node id {} with state {} with previous state {} result {}",
                    stateChangeRecord.nodeId(), stateChangeRecord.state(), stateChangeRecord.previousState(), result);
            return result;
        } catch (Exception e) {
            log.error("OrchestrationPlanModificationServiceImpl | updateNodeStateById | error | node id {}", stateChangeRecord.nodeId(), e);
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while updating node state by id");
        }
    }

    @Override
    public int updateNodeStateAndAddErrorMessageById(NodeStateChangeRecord stateChangeRecord) throws CoodDBException {
        try {
            int result = orchestrationPlanRepository.updateOrchestrationPlanNodesStateAndErrorMessageById(stateChangeRecord.nodeId(), stateChangeRecord.errorMessage(), stateChangeRecord.state(), stateChangeRecord.previousState());
            log.info("OrchestrationPlanModificationServiceImpl | updateNodeStateAndAddErrorMessageById | nodeId: {} , errorMessage: {}, orchestrationPlanNodeState: {}, orchestrationPlanNodePreviousState: {} ,result count : {}", stateChangeRecord.nodeId(),
                    stateChangeRecord.errorMessage(), stateChangeRecord.state(), stateChangeRecord.previousState(), result);
            return result;
        } catch (Exception e) {
            log.error("OrchestrationPlanModificationServiceImpl | updateNodeStateAndAddErrorMessageById | error | node id {}", stateChangeRecord.nodeId(), e);
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while updating node state and add error message");
        }
    }

    @Override
    public int updatePlanStateById(String planId, State state) throws CoodDBException {
        log.info("OrchestrationPlanModificationServiceImpl | updatePlanStateById | planId: {} , state: {}", planId, state);
        try {
            int result = orchestrationPlanRepository.updateStateById(planId, state);
            log.info("OrchestrationPlanModificationServiceImpl | updatePlanStateById | planId: {} , state: {}, result count : {}", planId, state, result);
            return result;
        } catch (Exception e) {
            log.error("OrchestrationPlanModificationServiceImpl | updatePlanStateById | error | planId {}", planId, e);
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while updating node related product by id");
        }
    }

    @Override
    public int updatePlanStateAndAddErrorMessageById(String nodeId, List<OrchestrationPlanErrorMessage> errorMessages, State previousSate, State state) throws CoodDBException {
        try {
            int result = orchestrationPlanRepository.updateOrchestrationPlanStateAndErrorMessagesById(nodeId, errorMessages, previousSate, state);
            log.info("OrchestrationPlanModificationServiceImpl | updatePlanStateAndAddErrorMessageById | planId: {} , state: {}, result count : {}", nodeId, state, result);
            return result;
        } catch (Exception e) {
            log.error("OrchestrationPlanModificationServiceImpl | updatePlanStateAndAddErrorMessageById | error | planId {}", nodeId, e);
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while updating node state and add error message by id");
        }
    }

    @Override
    @Transactional
    public void updateNodeDataInOrchestrationPlan(OrchestrationPlanNode updatedNode, Map<String, Object> headers) throws CoodDBException {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(updatedNode.getId())
                .orElseThrow(() -> new CoodNonRecoverableAndNonRetryableException(
                        new OrchestrationPlanNodeNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, updatedNode.getId()))
                );

        OrchestrationPlanNode savedNode = orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(orchestrationNode -> orchestrationNode.getId().equals(updatedNode.getId())).findFirst()
                .orElseThrow(() -> new CoodNonRecoverableAndNonRetryableException(
                        new OrchestrationPlanNodeNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, updatedNode.getId()))
                );

        orchestrationPlan.getOrchestrationPlanNodes().remove(savedNode);
        orchestrationPlan.getOrchestrationPlanNodes().add(updatedNode);
        orchestrationPlanRepository.save(orchestrationPlan);

        //if the cpib update successfully the node then will be updated in the database and node state change event will be fired if the node state changed
        if (!savedNode.getState().equals(updatedNode.getState())) {
            eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT, orchestrationPlanRepository.findOrchestrationPlanNodeById(updatedNode.getId()).orElseThrow(
                    () -> new CoodRecoverableAndNonRetryableException(new OrchestrationPlanNodeNotFoundException(ExceptionCode.NODE_NOT_FOUND_BY_ID, updatedNode.getId()))
            ), headers);
        }
    }
}
