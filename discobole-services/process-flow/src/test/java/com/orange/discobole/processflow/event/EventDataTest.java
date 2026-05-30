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

package com.orange.discobole.processflow.event;

import com.orange.discobole.processflow.ProcessFlowAxonApplicationTests;
import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventDataTest extends ProcessFlowAxonApplicationTests {

    private EventData eventData;

    @Test
    public void eventDataTest() {
        Event event = new ProcessFlowCreatedEvent();

        eventData = EventData.from(event);

        String json = "EventData{event=ProcessFlowCreatedEvent{processFlowId='null', processFlow=null, discoTaskFlows=null, processTaskIds=null, variables=null}, type='com.orange.discobole.processflow.event.ProcessFlowCreatedEvent'}";

        Assertions.assertEquals(eventData.getEvent().aggregateName(), "ProcessFlowStateMachine");
        Assertions.assertEquals(eventData.getType(), "com.orange.discobole.processflow.event.ProcessFlowCreatedEvent");
        Assertions.assertEquals(eventData.toString(), json);

    }
}
