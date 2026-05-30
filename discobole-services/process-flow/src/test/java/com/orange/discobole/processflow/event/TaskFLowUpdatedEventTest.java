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
/*
 * package com.orange.bos.envelope.event;
 * 
 * import static org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.junit.jupiter.api.Assertions.assertNull;
 * 
 * import java.util.ArrayList; import java.util.HashMap; import java.util.List;
 * import java.util.Map;
 * 
 * import org.junit.jupiter.api.Assertions; import org.junit.jupiter.api.Test;
 * 
 * import com.orange.bos.envelope.EnvelopeCqrsApplicationTests; import
 * com.orange.bos.envelope.dto.BosTaskFlow; import
 * com.orange.bos.envelope.dto.generated.Characteristic; import
 * com.orange.bos.envelope.dto.generated.Links; import
 * com.orange.bos.envelope.dto.generated.StringCharacteristic; import
 * com.orange.bos.envelope.dto.generated.TaskFlow; import
 * com.orange.bos.envelope.dto.generated.TaskLink;
 * 
 * public class TaskFLowUpdatedEventTest extends EnvelopeCqrsApplicationTests {
 * 
 * @Test public void taskFlowUpdatedEventTest() {
 * 
 * String processFlowId = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d"; TaskFlow
 * taskFlow = new TaskFlow(); taskFlow.setId("Task_1"); List<TaskLink>
 * nextTaskList1 = new ArrayList<>(); TaskLink nextTask1 = new TaskLink();
 * nextTask1.setTaskFlowSpecificationId("task_2"); nextTask1.title("task_2");
 * nextTaskList1.add(nextTask1); taskFlow.setLinks(new
 * Links().nextTaskstoBePerformed(nextTaskList1)); BosTaskFlow bosTaskFlow = new
 * BosTaskFlow(processFlowId, taskFlow); List<BosTaskFlow> bosTaskFlows = new
 * ArrayList<>(); bosTaskFlows.add(bosTaskFlow);
 * 
 * List<Characteristic> characteristics = new ArrayList<>();
 * characteristics.add( new
 * StringCharacteristic().value("value_1").name("test_1").valueType(String.class
 * .getSimpleName()));
 * 
 * Map<String, Object> variablesFromUserActions = new HashMap<>();
 * variablesFromUserActions.put("test", "test"); Map<String, Object>
 * processTaskIds = new HashMap<>(); TaskFlowUpdatedEvent taskFlowUpdatedEvent =
 * new TaskFlowUpdatedEvent(bosTaskFlow, variablesFromUserActions,
 * characteristics, processTaskIds);
 * 
 * String json =
 * "TaskFlowUpdatedEvent{bosTaskFlow=BosTaskFlow{processFLowId='2e05d202-18a0-4e7b-bb3f-88c5dd68954d', taskFlow=class TaskFlow {\n"
 * + "    id: Task_1\n" + "    href: null\n" + "    correlationId: null\n" +
 * "    completionMethod: null\n" + "    isMandatory: null\n" +
 * "    priority: null\n" + "    taskFlowSpecification: null\n" +
 * "    channel: null\n" + "    characteristic: null\n" +
 * "    relatedEntity: null\n" + "    relatedParty: null\n" +
 * "    state: null\n" + "    taskFlowRelationship: null\n" +
 * "    baseType: null\n" + "    schemaLocation: null\n" + "    type: null\n" +
 * "    links: class Links {\n" + "        self: null\n" +
 * "        taskFlowList: null\n" +
 * "        nextTaskstoBePerformed: [class TaskLink {\n" +
 * "            title: task_2\n" + "            href: null\n" +
 * "            state: null\n" + "            taskFlowSpecificationId: task_2\n"
 * + "            taskFlowSpecificationCharacteristic: null\n" +
 * "            method: null\n" + "            accepts: null\n" + "        }]\n"
 * + "        existingTaskEditable: null\n" + "    }\n" +
 * "}}, variablesFromUserActions={test=test}, taskCharacteristicList=[class StringCharacteristic {\n"
 * + "    class Characteristic {\n" + "        name: test_1\n" +
 * "        id: null\n" + "        valueType: String\n" +
 * "        baseType: null\n" + "        schemaLocation: null\n" +
 * "        type: null\n" + "    }\n" + "    value: value_1\n" +
 * "}], processTaskIds={}}";
 * 
 * Assertions.assertAll("processFlowCreatedEvent", () ->
 * assertEquals(taskFlowUpdatedEvent.getBosTaskFlow().getProcessFLowId(),
 * processFlowId), () ->
 * assertEquals(taskFlowUpdatedEvent.getBosTaskFlow().getTaskFlow().getId(),
 * "Task_1"), () ->
 * assertEquals(taskFlowUpdatedEvent.getVariablesFromUserActions().get("test"),
 * "test"), () ->
 * assertEquals(taskFlowUpdatedEvent.getTaskCharacteristicList().get(0).getName(
 * ), "test_1"), () -> assertEquals(taskFlowUpdatedEvent.toString(), json), ()
 * ->
 * assertEquals(taskFlowUpdatedEvent.getBosTaskFlow().getTaskFlow().getLinks()
 * .getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId(), "task_2"));
 * }
 * 
 * @Test public void defaultConsTest() { TaskFlowUpdatedEvent
 * taskFlowUpdatedEvent = new TaskFlowUpdatedEvent();
 * 
 * Assertions.assertAll("processFlowCreatedEvent", () ->
 * assertNull(taskFlowUpdatedEvent.getBosTaskFlow()));
 * 
 * }
 * 
 * }
 */