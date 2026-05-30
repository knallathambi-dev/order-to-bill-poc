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

package com.orange.discobole.processflow;



import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;
import com.orange.discobole.processflow.event.ProcessFlowUpdatedEvent;
import com.orange.discobole.processflow.event.TaskFlowUpdatedEvent;
import com.orange.discobole.processflow.handler.ProcessFlowEventHandler;
import com.orange.discobole.processflow.infra.Publisher;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The Class EventHandlerInitializer registers the events with their
 * corresponding event handler.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@ConditionalOnExpression("${cqrs.query-enabled:true} and ${cqrs.command-enabled:true}")
@Component
public class EventHandlerInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private ProcessFlowEventHandler eventHandler;

	@PostConstruct
	private void init() {
		publisher.register(ProcessFlowCreatedEvent.class, eventHandler::handle);
		publisher.register(TaskFlowUpdatedEvent.class, eventHandler::handle);
		publisher.register(ProcessFlowUpdatedEvent.class,eventHandler::handle);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
