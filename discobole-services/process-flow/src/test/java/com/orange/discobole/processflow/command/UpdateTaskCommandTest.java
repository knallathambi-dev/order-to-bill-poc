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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.orange.discobole.processflow.ProcessFlowAxonApplicationTests;
import com.orange.discobole.processflow.command.UpdateTaskCommand;
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;

public class UpdateTaskCommandTest extends ProcessFlowAxonApplicationTests {

	@Test
	public void updateTaskCommandTest() {
		String processFlowId = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
		String taskFlowId = "task_1";
		ChannelRef channelRef = new ChannelRef();
		channelRef.name("web");
		List<ChannelRef> channelRefList = new ArrayList<>();
		channelRefList.add(channelRef);
		List<Characteristic> characteristics = new ArrayList<>();
		characteristics.add(
				new StringCharacteristic().value("value_1").name("test_1").valueType(String.class.getSimpleName()));

		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate().channel(channelRefList).characteristic(characteristics);
		UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(processFlowId, taskFlowId, taskFlowUpdate);
		String updateTaskCommandJson = "UpdateTaskCommand{processFlowId='2e05d202-18a0-4e7b-bb3f-88c5dd68954d', taskFlowId='task_1', taskFlowUpdate=class TaskFlowUpdate {\n" +
				"    completionMethod: null\n" +
				"    correlationId: null\n" +
				"    isMandatory: null\n" +
				"    priority: null\n" +
				"    channel: [class ChannelRef {\n" +
				"        id: null\n" +
				"        href: null\n" +
				"        name: web\n" +
				"        baseType: null\n" +
				"        schemaLocation: null\n" +
				"        type: null\n" +
				"        referredType: null\n" +
				"    }]\n" +
				"    characteristic: [class StringCharacteristic {\n" +
				"        class Characteristic {\n" +
				"            name: test_1\n" +
				"            id: null\n" +
				"            valueType: String\n" +
				"            baseType: null\n" +
				"            schemaLocation: null\n" +
				"            type: null\n" +
				"        }\n" +
				"        value: value_1\n" +
				"    }]\n" +
				"    relatedEntity: null\n" +
				"    relatedParty: null\n" +
				"    baseType: null\n" +
				"    schemaLocation: null\n" +
				"    type: null\n" +
				"}}";
		assertEquals(updateTaskCommandJson, updateTaskCommand.toString());
		Assertions.assertAll("updateTaskCommand",
				() -> assertEquals(updateTaskCommand.getProcessFlowId(), processFlowId),
				() -> assertEquals(updateTaskCommand.getTaskFlowId(), taskFlowId),
				() -> assertEquals(updateTaskCommand.getTaskFlowUpdate().getChannel().get(0).getName(), "web"),
				() -> assertEquals(updateTaskCommand.getTaskFlowUpdate().getCharacteristic().get(0).getName(),
						"test_1"));

	}

	@Test
	public void updateTaskCommandNullTest(){
		UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand();

		Assertions.assertAll("updateTaskCommand",
				() -> assertNull(updateTaskCommand.getProcessFlowId()),
				() -> assertNull(updateTaskCommand.getTaskFlowId()),
				() -> assertNull(updateTaskCommand.getTaskFlowUpdate()),
				() -> assertNull(updateTaskCommand.getTaskFlowUpdate()));

	}
}
