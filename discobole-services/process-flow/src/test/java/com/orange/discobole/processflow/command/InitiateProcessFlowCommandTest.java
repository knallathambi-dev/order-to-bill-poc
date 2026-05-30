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

package com.orange.discobole.processflow.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.orange.discobole.processflow.ProcessFlowAxonApplicationTests;
import com.orange.discobole.processflow.command.InitiateProcessFlowCommand;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;

public class InitiateProcessFlowCommandTest extends ProcessFlowAxonApplicationTests {

    @Test
    public void initiateProcessFlowCommandTest() {
        List<Characteristic> characteristics = new ArrayList<>();
        characteristics
                .add(new StringCharacteristic().value("value1").name("test_1").valueType(String.class.getSimpleName()));
        ProcessFlowCreate processFlowCreate = new ProcessFlowCreate().processFlowSpecification("DummyProcess")
                .characteristic(characteristics);
        InitiateProcessFlowCommand initiateProcessFlowCommand = new InitiateProcessFlowCommand(null,processFlowCreate);

        String initiateProcessFlowCommandJson = "InitiateProcessFlowCommand [processFlowId=null, processFlowCreate=class ProcessFlowCreate {\n" +
                "    processFlowSpecification: DummyProcess\n" +
                "    correlationId: null\n" +
                "    channel: null\n" +
                "    characteristic: [class StringCharacteristic {\n" +
                "        class Characteristic {\n" +
                "            name: test_1\n" +
                "            id: null\n" +
                "            valueType: String\n" +
                "            baseType: null\n" +
                "            schemaLocation: null\n" +
                "            type: null\n" +
                "        }\n" +
                "        value: value1\n" +
                "    }]\n" +
                "    relatedEntity: null\n" +
                "    relatedParty: null\n" +
                "    baseType: null\n" +
                "    schemaLocation: null\n" +
                "    type: null\n" +
                "}]";

        assertEquals(initiateProcessFlowCommandJson, initiateProcessFlowCommand.toString());
        Assertions.assertAll("initiateProcessFlowCommand",
                () -> assertEquals(initiateProcessFlowCommand.getProcessFlowCreate().getProcessFlowSpecification(),
                        "DummyProcess"),
                () -> assertEquals(
                        initiateProcessFlowCommand.getProcessFlowCreate().getCharacteristic().get(0).getName(),
                        "test_1"));
    }

}
