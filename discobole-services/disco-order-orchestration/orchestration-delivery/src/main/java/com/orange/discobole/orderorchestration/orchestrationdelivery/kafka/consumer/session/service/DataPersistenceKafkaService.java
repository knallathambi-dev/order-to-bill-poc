// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service;

import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.records.NodeStateChangeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.MongoTemplateWrapperService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPersistenceKafkaService {

    private final OrchestrationPlanRepository orchestrationPlanRepository;
    private final OrchestrationPlanModificationService orchestrationPlanModificationService;
    private final EventPublisher eventPublisher;
    private final MongoTemplateWrapperService mongoTemplateWrapperService;

    @Retryable(retryFor = CoodDBException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000))
    public void persistRelatedProductChange(KafkaConsumerSession session) {
        List<OrchestrationPlanNode> nodeRelatedProductChangeList = session.getNodeRelatedProductChangeList();
        try {
            for (OrchestrationPlanNode orchestrationPlanNode : nodeRelatedProductChangeList) {
                orchestrationPlanRepository.updateOrchestrationPlanNodesRelatedProductById(orchestrationPlanNode.getId(), orchestrationPlanNode.getRelatedProduct());
            }
        } catch (Exception e) {
            log.info("Persist failure cause {}", e.getMessage());
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while updating node by related product id");
        }
    }

    public void persistNodesStateChange(KafkaConsumerSession session) {
        List<NodeStateChangeRecord> nodeStateChangeList = session.getNodeStateChangeList();
        for (NodeStateChangeRecord node : nodeStateChangeList) {
            int result;
            if (!CollectionUtils.isEmpty(node.errorMessage())) {
                result = orchestrationPlanModificationService.updateNodeStateAndAddErrorMessageById(node);
            } else {
                result = orchestrationPlanModificationService.updateNodeStateById(node);
            }
            if (result > 0) {
                Map<String, Object> headers = session.getOutboxHeaders().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT, orchestrationPlanRepository.findOrchestrationPlanNodeById(node.nodeId()).orElseThrow(
                        () -> new CoodRecoverableAndNonRetryableException(new OrchestrationPlanNodeNotFoundException(ExceptionCode.NODE_NOT_FOUND_BY_ID, node.nodeId()))
                ), headers);
            }
        }
    }

    public void persistPlan(KafkaConsumerSession session) {
        if (session.getPlanToBePersisted() != null) {
            mongoTemplateWrapperService.save(session.getPlanToBePersisted());
            eventPublisher.publishEvent(CDCEvent.ORCHESTRATION_PLAN_STATE_CHANGE_EVENT, session.getPlanToBePersisted());
        }
    }
}
