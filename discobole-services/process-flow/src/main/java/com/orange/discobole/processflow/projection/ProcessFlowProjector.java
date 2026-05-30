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

package com.orange.discobole.processflow.projection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.constant.ProcessFlowConstants;
import com.orange.discobole.processflow.event.*;

/**
 * The Class ProcessFlowProjector sends the generated events on EventBus.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@ConditionalOnExpression("${cqrs.command-enabled:true} and '${cqrs.query-enabled:true}'.equals('false')")
//@EnableBinding(ProcessFlowChannel.class)
@Component
public class ProcessFlowProjector {

    private static final Logger LOGGER = LogManager.getLogger(ProcessFlowProjector.class);

    @Autowired
    private StreamBridge bridge;

//    @Resource
//    private MessageChannel producerProcessFlow;
//    

    /**
     * 
     * Sends the generated ProcessFlowCreatedEvent to event bus.
     *
     * @param event the event
     */
    public void handle(ProcessFlowCreatedEvent event) {
        LOGGER.info("Producing message on topic processflow containing event {}", event.getProcessFlowId());
//        producerProcessFlow.send(MessageBuilder.withPayload(EventData.from(event))
//                .setHeader(ProcessFlowConstants.PARTITION_KEY, event.getProcessTaskIds()).build());
        bridge.send("handle-out-0", MessageBuilder.withPayload(EventData.from(event)).setHeader(ProcessFlowConstants.PARTITION_KEY,  event.getProcessFlowId()).build());
    }
    
  /*  public void processFlow(Event event){
//        producerCatalogProductSpec.send(MessageBuilder.withPayload(EventData.from(event))
//                .setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
//        ProcessFlowCreatedEvent processFlowCreatedEvent = (ProcessFlowCreatedEvent) event;
       // bridge.send("processFlow-out-0", MessageBuilder.withPayload(event).setHeader(ProcessFlowConstants.PARTITION_KEY,  event.getProcessFlowId()).build());
    }*/

    /**
     * Sends the generated TaskFlowUpdatedEvent to event bus.
     *
     * @param event the event
     */
    public void handle(TaskFlowUpdatedEvent event) {
//        producerProcessFlow.send(MessageBuilder.withPayload(EventData.from(event))
//                .setHeader(ProcessFlowConstants.PARTITION_KEY, event.getProcessTaskIds()).build());
        LOGGER.info("handling taskFlowUpdatedEvent event with processFlow: {}", event.getProcessFlowId());
        bridge.send("handle-out-0", MessageBuilder.withPayload(EventData.from(event)).setHeader(ProcessFlowConstants.PARTITION_KEY,  event.getProcessTaskIds()).build());
    }

	/**
	 * Sends the generated ProcessFlowUpdatedEvent to event bus.
	 *
	 * @param event the event
	 */
    public void handle(ProcessFlowUpdatedEvent event) {
//        producerProcessFlow.send(MessageBuilder.withPayload(EventData.from(event))
//                .setHeader(ProcessFlowConstants.PARTITION_KEY, event.getProcessFlow().getId()).build());
        LOGGER.info("handling processFlowUpdatedEvent event with processFlow: {}", event.getProcessFlowId());
        bridge.send("handle-out-0", MessageBuilder.withPayload(EventData.from(event)).setHeader(ProcessFlowConstants.PARTITION_KEY,  event.getProcessFlow().getId()).build());
    }

}
