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

package com.orange.discobole.processflow.ssm.dto;

import com.orange.discobole.processflow.ProcessFlowAxonApplicationTests;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StateMachineTransitionTest extends ProcessFlowAxonApplicationTests {
    private static final Logger LOGGER = LogManager.getLogger(StateMachineTransitionTest.class);

    private StateMachineTransition stateMachineTransition;
    private final String TASK_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        stateMachineTransition = new StateMachineTransition();
        Map<String, Object> variablesFromUserActions = new HashMap<>();
        variablesFromUserActions.put("productSpecId", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
        stateMachineTransition.setProcessInstanceId("2e05d202-18a0-4e7b-bb3f-88c5dd68954d");
        stateMachineTransition.setTaskDefinitionId(TASK_ID);
        stateMachineTransition.setTaskDefinitionKey("selectSupportEntity");
        stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
    }

    @Test
    public void stateMachineTransitionTest() {

        Assertions.assertAll("stateMachineTransition",
                () -> Assertions.assertEquals(stateMachineTransition.getProcessDefinitionKey(), "ProductSpecCreation"),
                () -> Assertions.assertEquals(stateMachineTransition.getProcessInstanceId(), "2e05d202-18a0-4e7b-bb3f-88c5dd68954d"),
                () -> Assertions.assertEquals(stateMachineTransition.getTaskDefinitionId(), TASK_ID),
                () -> Assertions.assertEquals(stateMachineTransition.getVariablesFromUserActions().get("productSpecId"), "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"),
                () -> Assertions.assertEquals(stateMachineTransition.getTaskDefinitionKey(), "selectSupportEntity"));
        LOGGER.info("stateMachineTransition: {}", stateMachineTransition);
    }

}
