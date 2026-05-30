// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.service.impl;

import jakarta.annotation.Resource;

import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.constant.ProcessConstants;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;
import com.orange.discobole.processflow.exception.ProcessFlowNotFoundException;
import com.orange.discobole.processflow.repository.ProcessFlowRepo;
import com.orange.discobole.processflow.service.ProcessFlowQueryService;
import com.orange.discobole.processflow.util.QueryParamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ConditionalOnProperty(value = "cqrs.query-enabled", havingValue = "true", matchIfMissing = true)
@Service
public class ProcessFlowQueryServiceImpl implements ProcessFlowQueryService {

    @Resource
    private ProcessFlowRepo processFlowRepo;

    @Resource
    private ApplicationContext appCtx;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<ProcessFlow> findProcessFlows(Map<String, Object> requestParams) {
        Query query = buildQuery(requestParams);
        List<ProcessFlow> processFlows = mongoTemplate.find(query, ProcessFlow.class);

        for (ProcessFlow processFlow : processFlows
        ) {
            List<TaskLink> nextTasksToBePerformed = processFlow.getLinks().getNextTaskstoBePerformed();
            if (nextTasksToBePerformed != null && !nextTasksToBePerformed.isEmpty()) {
                nextTasksToBePerformed = nextTasksToBePerformed.stream()
                        .filter(taskLink -> !isHidden(taskLink.getTitle(), processFlow.getId()))
                        .collect(Collectors.toList());
                processFlow.getLinks().setNextTaskstoBePerformed(nextTasksToBePerformed);
            }
        }

        return processFlows;
    }

    @Override
    public ProcessFlow findProcessFlowById(Map<String, Object> requestParams) throws ProcessFlowNotFoundException {
        String processFlowId = (String) requestParams.get("_id");
        Query query = buildQuery(requestParams);
        List<ProcessFlow> processFlows = mongoTemplate.find(query, ProcessFlow.class);
        if(processFlows.isEmpty()){
            throw new ProcessFlowNotFoundException(processFlowId);
        }

        ProcessFlow processFlow = processFlows.get(0);
        List<TaskLink> nextTasksToBePerformed = new ArrayList<>();
        if (processFlow.getLinks() != null)
            nextTasksToBePerformed = processFlow.getLinks().getNextTaskstoBePerformed();
        if (nextTasksToBePerformed != null && !nextTasksToBePerformed.isEmpty()) {
            nextTasksToBePerformed = nextTasksToBePerformed.stream()
                    .filter(taskLink -> !isHidden(taskLink.getTitle(), processFlowId))
                    .collect(Collectors.toList());
            processFlow.getLinks().setNextTaskstoBePerformed(nextTasksToBePerformed);
        }
        return processFlow;
    }

    public boolean isHidden(String title, String processFlowId) {
        MongoEventStorageEngine engine = (MongoEventStorageEngine) appCtx.getBean("eventStorageEngine");
        if (engine != null) {
            DomainEventMessage<ProcessFlowCreatedEvent> message = (DomainEventMessage<ProcessFlowCreatedEvent>) engine
                    .readEvents(processFlowId, 0).peek();

            Map<String, Boolean> hiddenStates = (Map<String, Boolean>) message.getPayload().getVariables().get(ProcessConstants.HIDDEN_STATES);

            String state = title.split("\\.")[1];
            return hiddenStates.containsKey(state) && hiddenStates.get(state);
        }
        return false;
    }

    private Query buildQuery(Map<String, Object> requestParams) {
        Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);
        Query query = new Query();

        params.forEach((key, values) -> query.addCriteria(Criteria.where(key).in(values)));

        return query;
    }
}
