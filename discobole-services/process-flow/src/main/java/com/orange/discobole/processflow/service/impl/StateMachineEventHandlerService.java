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

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;
import com.orange.discobole.processflow.event.ProcessFlowUpdatedEvent;
import com.orange.discobole.processflow.event.TaskFlowUpdatedEvent;
import com.orange.discobole.processflow.infra.Publisher;

@Component
@ConditionalOnProperty(value = "cqrs.command-enabled", havingValue = "true", matchIfMissing = true)
public class StateMachineEventHandlerService {
	private static final Logger LOGGER = LogManager.getLogger(StateMachineEventHandlerService.class);
	@Resource
	private Publisher publisher;

	@EventHandler
	public void handle(ProcessFlowCreatedEvent event) {
		LOGGER.info("Handling ProcessFlowCreated Event : {} ", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(TaskFlowUpdatedEvent event) {
		LOGGER.info("Handling TaskFlowUpdated Event : {} ", event);
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProcessFlowUpdatedEvent event) {
		LOGGER.info("Handling ProcessFlowUpdated Event : {} ", event);
		publisher.project(List.of(event));
	}
}
