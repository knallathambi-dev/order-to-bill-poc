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

import jakarta.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;
import com.orange.discobole.processflow.event.ProcessFlowUpdatedEvent;
import com.orange.discobole.processflow.event.TaskFlowUpdatedEvent;
import com.orange.discobole.processflow.repository.ProcessFlowRepo;
import com.orange.discobole.processflow.repository.TaskFlowRepo;

/**
 * The Class ProcessFlowEventHandler handles the corresponding events received from
 * event bus.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */

//@ConditionalOnExpression("${cqrs.command-enabled:true} and '${cqrs.query-enabled:true}'.equals('false')")
@ConditionalOnProperty(value = "cqrs.query-enabled", havingValue = "true", matchIfMissing = true)
@Component
public class ProcessFlowEventHandler {

	/*
	 * @Resource private ProcessFlowRepo processFlowRepo;
	 * 
	 * @Resource private TaskFlowRepo taskFlowRepo;
	 */
	private static final Logger LOGGER = LogManager.getLogger(ProcessFlowEventHandler.class);
	 @Resource
	    private ProcessFlowRepo processFlowRepo;

	    @Resource
	    private TaskFlowRepo taskFlowRepo;

	    /**
	     * Saves the process and tasks received from the event.
	     *
	     * @param event the event
	     */
	    public void handle(ProcessFlowCreatedEvent event) {
			LOGGER.info("handling ProcessFlowCreatedEvent event: {}", event.getProcessFlowId());
			processFlowRepo.save(event.getProcessFlow());
	        taskFlowRepo.saveAll(event.getDiscoTaskFlows());
	    }

	    /**
	     * Saves the task received from the event.
	     *
	     * @param event the event
	     */
	    public void handle(TaskFlowUpdatedEvent event) {
			LOGGER.info("handling TaskFlowUpdatedEvent event with processFlow: {}", event.getProcessFlowId());
			taskFlowRepo.save(event.getDiscoTaskFlow());
	    }

	    /**
	     * update the PrcessFlow
	     *
	     * @param event
	     */
	    public void handle(ProcessFlowUpdatedEvent event) {
	    	LOGGER.info("handling ProcessFlowUpdatedEvent event: {}", event.getProcessFlowId());
	        processFlowRepo.save(event.getProcessFlow());
	    }

}
