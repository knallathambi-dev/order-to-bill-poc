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

import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.util.QueryParamUtil;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.constant.ProcessConstants;
import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;
import com.orange.discobole.processflow.exception.TaskFlowNotFoundException;
import com.orange.discobole.processflow.repository.TaskFlowRepo;
import com.orange.discobole.processflow.service.TaskFlowQueryService;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ConditionalOnProperty(value = "cqrs.query-enabled", havingValue = "true", matchIfMissing = true)
@Service
public class TaskFlowQueryServiceImpl implements TaskFlowQueryService {

    @Resource
    private TaskFlowRepo taskFlowRepo;
    @Resource
    private ApplicationContext appCtx;
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<TaskFlow> findTaskFlowsByProcessFlowId(Map<String, Object> requestParams) {
        Query query = buildQuery(requestParams);
        List<DiscoTaskFlow> discoTaskFlows = mongoTemplate.find(query, DiscoTaskFlow.class);
        return discoTaskFlows.stream().map(DiscoTaskFlow::getTaskFlow).collect(Collectors.toList());
    }

    @Override
    public TaskFlow findTaskFlowById(Map<String, Object> requestParams) throws TaskFlowNotFoundException {
        Query query = buildQuery(requestParams);
        List<DiscoTaskFlow> discoTaskFlows = mongoTemplate.find(query, DiscoTaskFlow.class);
        if(discoTaskFlows.isEmpty()){
            throw new TaskFlowNotFoundException((String) requestParams.get("taskFlow._id"));
        }

        TaskFlow taskFlow = discoTaskFlows.get(0).getTaskFlow();
        List<TaskLink> nextTasksToBePerformed = new ArrayList<>();
        if (taskFlow.getLinks() != null)
            nextTasksToBePerformed = taskFlow.getLinks().getNextTaskstoBePerformed();
        if (nextTasksToBePerformed != null && !nextTasksToBePerformed.isEmpty()) {
            nextTasksToBePerformed = nextTasksToBePerformed.stream()
                    .filter(taskLink -> !isHidden(taskLink.getTitle(), (String) requestParams.get("_id")))
                    .collect(Collectors.toList());
            taskFlow.getLinks().setNextTaskstoBePerformed(nextTasksToBePerformed);
        }

        return taskFlow;
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
