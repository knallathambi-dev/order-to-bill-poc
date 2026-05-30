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

package com.orange.discobole.processflow.dto;

import com.orange.discobole.processflow.ProcessFlowAxonApplicationTests;
import com.orange.discobole.processflow.dto.DiscoTaskFlow;
import com.orange.discobole.processflow.dto.generated.Links;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskLink;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscoTaskFlowTest extends ProcessFlowAxonApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(DiscoTaskFlowTest.class);

    private DiscoTaskFlow discoTaskFlow;
    private final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
    private final String TASK_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        String processFlowId = PROCESS_FLOW_ID;

        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setId(TASK_ID);
        List<TaskLink> nextTaskList1 = new ArrayList<>();
        TaskLink nextTask1 = new TaskLink();
        nextTask1.setTaskFlowSpecificationId("task_2");
        nextTask1.title("task_2");
        nextTaskList1.add(nextTask1);
        taskFlow.setLinks(new Links().nextTaskstoBePerformed(nextTaskList1));

        discoTaskFlow = new DiscoTaskFlow(processFlowId, taskFlow);
    }

    @Test
    public void discoTaskFLowTest() {
        Assertions.assertAll("discoTaskFlow",
                () -> Assertions.assertEquals(discoTaskFlow.getProcessFLowId(), PROCESS_FLOW_ID),
                () -> Assertions.assertEquals(discoTaskFlow.getTaskFlow().getId(), TASK_ID));
        LOGGER.info("stateMachineTransition: {}", discoTaskFlow);
    }

    @Test
    public void discoTaskFLowSetterTest() {
        discoTaskFlow.setProcessFLowId("2e05d202");

        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setId("25sfdaf");
        List<TaskLink> nextTaskList1 = new ArrayList<>();
        TaskLink nextTask1 = new TaskLink();
        nextTask1.setTaskFlowSpecificationId("Task123");
        nextTask1.title("Test");
        nextTaskList1.add(nextTask1);
        taskFlow.setLinks(new Links().nextTaskstoBePerformed(nextTaskList1));

        discoTaskFlow.setTaskFlow(taskFlow);
        Assertions.assertAll("discoTaskFlow",
                () -> Assertions.assertEquals(discoTaskFlow.getProcessFLowId(), "2e05d202"),
                () -> Assertions.assertEquals(discoTaskFlow.getTaskFlow().getId(), "25sfdaf"));
        LOGGER.info("stateMachineTransition: {}", discoTaskFlow);
    }

}
