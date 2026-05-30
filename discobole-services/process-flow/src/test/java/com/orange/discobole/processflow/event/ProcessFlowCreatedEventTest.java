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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.orange.discobole.processflow.ProcessFlowAxonApplicationTests;
import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.Links;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import com.orange.discobole.processflow.event.ProcessFlowCreatedEvent;

public class ProcessFlowCreatedEventTest extends ProcessFlowAxonApplicationTests {

    @Test
    public void processFlowCreatedEventTest() {
        String processFlowId = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
        List<Characteristic> characteristics = new ArrayList<>();
        characteristics.add(
                new StringCharacteristic().value("value_1").name("test_1").valueType(String.class.getSimpleName()));
        List<TaskLink> nextTaskList = new ArrayList<>();
        TaskLink nextTask = new TaskLink();
        nextTask.setTaskFlowSpecificationId("task_1");
        nextTask.title("task_1");
        nextTaskList.add(nextTask);
        ProcessFlow processFlow = new ProcessFlow().processFlowSpecification("DummyProcess")
                .characteristic(characteristics);
        processFlow.setLinks(new Links().nextTaskstoBePerformed(nextTaskList));

        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setId("Task_1");
        List<TaskLink> nextTaskList1 = new ArrayList<>();
        TaskLink nextTask1 = new TaskLink();
        nextTask1.setTaskFlowSpecificationId("task_2");
        nextTask1.title("task_2");
        nextTaskList1.add(nextTask1);
        taskFlow.setLinks(new Links().nextTaskstoBePerformed(nextTaskList1));
        DiscoTaskFlow discoTaskFlow = new DiscoTaskFlow(processFlowId, taskFlow);
        List<DiscoTaskFlow> discoTaskFlows = new ArrayList<>();
        discoTaskFlows.add(discoTaskFlow);

        Map<String, Object> taskIds = new HashMap<>();
        taskIds.put("taskId", "Task_1");
        ProcessFlowCreatedEvent processFlowCreatedEvent = new ProcessFlowCreatedEvent(processFlowId, processFlow,
                discoTaskFlows, taskIds,new HashMap<String, Object>());

        Assertions.assertAll("processFlowCreatedEvent",
                () -> assertEquals(processFlowCreatedEvent.getProcessFlowId(), processFlowId),
                () -> assertEquals(processFlowCreatedEvent.getProcessFlow().getCharacteristic().get(0).getName(),
                        "test_1"),
                () -> assertEquals(processFlowCreatedEvent.getProcessFlow().getProcessFlowSpecification(),
                        "DummyProcess"),
                () -> assertEquals(processFlowCreatedEvent.getDiscoTaskFlows().get(0).getProcessFLowId(), processFlowId),
                () -> assertEquals(processFlowCreatedEvent.getDiscoTaskFlows().get(0).getTaskFlow().getId(), "Task_1"),
                () -> assertEquals(processFlowCreatedEvent.getDiscoTaskFlows().get(0).getTaskFlow().getLinks()
                        .getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId(), "task_2"));
    }

    @Test
    public void defaultConsTest() {
        ProcessFlowCreatedEvent processFlowCreatedEvent = new ProcessFlowCreatedEvent();

        Assertions.assertAll("processFlowCreatedEvent",
                () -> assertNull(processFlowCreatedEvent.getProcessFlowId()),
                () -> assertNull(processFlowCreatedEvent.getProcessFlow()),
                () -> assertNull(processFlowCreatedEvent.getDiscoTaskFlows()));

    }
}
