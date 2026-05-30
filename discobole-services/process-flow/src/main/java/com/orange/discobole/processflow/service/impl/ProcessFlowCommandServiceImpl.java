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

import java.util.UUID;



import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.aggregate.StateMachineUtil;
import com.orange.discobole.processflow.command.InitiateProcessFlowCommand;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.processflow.resolver.StateMachineNextTransitionResolver;
import com.orange.discobole.processflow.service.ProcessFlowCommandService;

import jakarta.annotation.Resource;

@ConditionalOnProperty(value = "cqrs.command-enabled", havingValue = "true", matchIfMissing = true)
@Service
public class ProcessFlowCommandServiceImpl implements ProcessFlowCommandService {

	@Resource
	private Publisher publisher;

	@Resource
	private ApplicationContext appCtx;

	@Resource
	private StateMachineNextTransitionResolver<String, String> stateMachineNextTransitionResolver;
	@Resource
	private StateMachineUtil stateMachineUtil;
	
	private final CommandGateway commandGateway;
	 
	@Autowired
	public ProcessFlowCommandServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
		
	}
	@Override
	public ProcessFlow createProcessFlow(ProcessFlowCreate processFlowCreate) {
		String processDefinitionKey = processFlowCreate.getProcessFlowSpecification();
		String processFlowId=UUID.randomUUID().toString();
		String processFlow= commandGateway.sendAndWait(new InitiateProcessFlowCommand(processFlowId,processFlowCreate));
		MongoEventStorageEngine engine=(MongoEventStorageEngine) appCtx.getBean("eventStorageEngine");
		DomainEventMessage<com.orange.discobole.processflow.event.ProcessFlowCreatedEvent> message=(DomainEventMessage<ProcessFlowCreatedEvent>) engine.readEvents(processFlow, 0).peek();
		return message.getPayload().getProcessFlow();
	
	}
	@Override
	public void deleteProcessFlow(String processFlowId) {
		// TODO Auto-generated method stub
		
	}
	
}
