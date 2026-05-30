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

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.command.UpdateTaskCommand;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.event.TaskFlowUpdatedEvent;
import com.orange.discobole.processflow.exception.TaskFlowNotFoundException;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.processflow.resolver.StateMachineNextTransitionResolver;
import com.orange.discobole.processflow.service.TaskFLowCommandService;

@ConditionalOnProperty(value = "cqrs.command-enabled", havingValue = "true", matchIfMissing = true)
@Service
public class TaskFlowCommandServiceImpl implements TaskFLowCommandService {

	@Resource
	private Publisher publisher;

	@Resource
	private ApplicationContext appCtx;

	@Resource
	private StateMachineNextTransitionResolver<String, String> stateMachineNextTransitionResolver;

	private final CommandGateway commandGateway;

	@Autowired
	public TaskFlowCommandServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;

	}

	@Override
	public TaskFlow updateTaskFlow(String processFlowId, String taskFlowId, TaskFlowUpdate taskFlowUpdate)
			throws TaskFlowNotFoundException {
		String processFlowId2 = commandGateway
				.sendAndWait(new UpdateTaskCommand(processFlowId, taskFlowId, taskFlowUpdate));

		MongoEventStorageEngine engine = (MongoEventStorageEngine) appCtx.getBean("eventStorageEngine");
		Long last = engine.lastSequenceNumberFor(processFlowId).get() - 1;
		DomainEventMessage<com.orange.discobole.processflow.event.TaskFlowUpdatedEvent> message = (DomainEventMessage<TaskFlowUpdatedEvent>) engine
				.readEvents(processFlowId, last).peek();

		return message.getPayload().getDiscoTaskFlow().getTaskFlow();
	}
}
