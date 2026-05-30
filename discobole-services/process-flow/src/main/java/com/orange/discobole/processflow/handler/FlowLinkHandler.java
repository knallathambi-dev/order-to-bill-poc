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

package com.orange.discobole.processflow.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.aggregate.StateMachineUtil;
import com.orange.discobole.processflow.config.CQRSProperties;
import com.orange.discobole.processflow.constant.ProcessConstants;
import com.orange.discobole.processflow.controller.ProcessFlowQueryController;
import com.orange.discobole.processflow.controller.TaskFlowCommandController;
import com.orange.discobole.processflow.controller.TaskFlowQueryController;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Handler to add HATEOAS capabilities in CQRS approach
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Component
public class FlowLinkHandler {

    private static final Logger LOGGER = LogManager.getLogger(FlowLinkHandler.class);

    @Resource
    private CQRSProperties properties;

    @Resource
    private ApplicationContext appCtx;

    /**
     * Add the links for self and next task flow.
     *
     * @param processFlow the current process flow
     */
    public void addLinks(final ProcessFlow processFlow) {
        final WebMvcLinkBuilder selfLinkBuilder = linkTo(
                methodOn(ProcessFlowQueryController.class).fetchProcessFlowById(processFlow.getId(),null));
        final String self = properties.isQueryEnabled() ? selfLinkBuilder.toString()
                : properties.getQueryBaseUrl() + selfLinkBuilder.toUri().getPath();
        processFlow.setHref(self);
        processFlow.getLinks().setSelf(new Link().href(self));

        final WebMvcLinkBuilder nextTaskListBuilder = linkTo(
                methodOn(TaskFlowQueryController.class).fetchTaskFlows(processFlow.getId(),null));
        final String nextTaskList = properties.isQueryEnabled() ? nextTaskListBuilder.toString()
                : properties.getQueryBaseUrl() + nextTaskListBuilder.toUri().getPath();
        processFlow.getLinks().setTaskFlowList(new Link().href(nextTaskList));
        LOGGER.info("nextTaskList: {}", nextTaskList);
        processFlow.getLinks().getNextTaskstoBePerformed().forEach(nextTaskConsumer(processFlow.getId()));
    }

    /**
     * Add the links for self and next task.
     *
     * @param processFlowId the current process flow id
     * @param taskFlow      the current task flow
     */
    public void addLinks(final String processFlowId, final TaskFlow taskFlow) {
        final WebMvcLinkBuilder selfLinkBuilder = linkTo(
                methodOn(TaskFlowQueryController.class).fetchTaskFlowById(processFlowId, taskFlow.getId(),null));
        final String self = properties.isQueryEnabled() ? selfLinkBuilder.toString()
                : properties.getQueryBaseUrl() + selfLinkBuilder.toUri().getPath();
        taskFlow.setHref(self);
        taskFlow.getLinks().setSelf(new Link().href(self));

        final WebMvcLinkBuilder nextTaskListBuilder = linkTo(
                methodOn(TaskFlowQueryController.class).fetchTaskFlows(processFlowId,null));
        final String nextTaskList = properties.isQueryEnabled() ? nextTaskListBuilder.toString()
                : properties.getQueryBaseUrl() + nextTaskListBuilder.toUri().getPath();
        taskFlow.getLinks().setTaskFlowList(new Link().href(nextTaskList));

        taskFlow.getLinks().getNextTaskstoBePerformed().forEach(nextTaskConsumer(processFlowId));

        if (taskFlow.getLinks().getExistingTaskEditable() != null)
            taskFlow.getLinks().getExistingTaskEditable().forEach(nextTaskConsumer(processFlowId));

        List<TaskLink> nextTasksToBePerformed = taskFlow.getLinks().getNextTaskstoBePerformed();
        if (nextTasksToBePerformed != null && !nextTasksToBePerformed.isEmpty()) {
            nextTasksToBePerformed = nextTasksToBePerformed.stream()
                    .filter(taskLink -> !isHidden(taskLink.getTitle(), processFlowId))
                    .collect(Collectors.toList());
            taskFlow.getLinks().setNextTaskstoBePerformed(nextTasksToBePerformed);
        }
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

    private Consumer<TaskLink> nextTaskConsumer(String processFlowId) {
        return nt -> {
            final WebMvcLinkBuilder taskLinkBuilder = linkTo(methodOn(TaskFlowCommandController.class)
                    .updateTaskFlow(processFlowId, nt.getTaskFlowSpecificationId(), null,null));
            final String nextTask = properties.isCommandEnabled() ? taskLinkBuilder.toString()
                    : properties.getCommandBaseUrl() + taskLinkBuilder.toUri().getPath();
            if (null == nt.getHref()) {
                nt.setHref(nextTask);
            }
            if (null == nt.getMethod()) {
                nt.setMethod(Method.PATCH);
            }
            if (null == nt.getAccepts()) {
                nt.setAccepts("merge-json-patch");
            }
        };
    }

}
