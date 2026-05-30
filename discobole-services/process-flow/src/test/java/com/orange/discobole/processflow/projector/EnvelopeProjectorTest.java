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

package com.orange.discobole.processflow.projector;
/*
 * package com.orange.bos.envelope.projector;
 * 
 * import static org.mockito.ArgumentMatchers.any; import static
 * org.mockito.Mockito.verify;
 * 
 * import java.util.ArrayList; import java.util.HashMap; import java.util.List;
 * import java.util.Map;
 * 
 * import org.junit.jupiter.api.Test; import org.mockito.Mockito; import
 * org.springframework.messaging.Message; import
 * org.springframework.messaging.MessageChannel; import
 * org.springframework.test.util.ReflectionTestUtils;
 * 
 * import com.orange.bos.envelope.dto.BosTaskFlow; import
 * com.orange.bos.envelope.dto.generated.Characteristic; import
 * com.orange.bos.envelope.dto.generated.Links; import
 * com.orange.bos.envelope.dto.generated.ProcessFlow; import
 * com.orange.bos.envelope.dto.generated.StringCharacteristic; import
 * com.orange.bos.envelope.dto.generated.TaskFlow; import
 * com.orange.bos.envelope.dto.generated.TaskLink; import
 * com.orange.bos.envelope.event.ProcessFlowCreatedEvent; import
 * com.orange.bos.envelope.event.TaskFlowUpdatedEvent; import
 * com.orange.bos.envelope.projection.EnvelopeProjector;
 * 
 * public class EnvelopeProjectorTest {
 * 
 * private final EnvelopeProjector projector; private final MessageChannel
 * producerEnvelope;
 * 
 * public EnvelopeProjectorTest() { projector = new EnvelopeProjector();
 * producerEnvelope = Mockito.mock(MessageChannel.class);
 * ReflectionTestUtils.setField(projector, "producerEnvelope",
 * producerEnvelope); }
 * 
 * @Test public void handleProcessFlowCreatedEvent() { String processFlowId =
 * "2e05d202-18a0-4e7b-bb3f-88c5dd68954d"; List<Characteristic> characteristics
 * = new ArrayList<>(); characteristics.add( new
 * StringCharacteristic().value("value_1").name("test_1").valueType(String.class
 * .getSimpleName())); List<TaskLink> nextTaskList = new ArrayList<>(); TaskLink
 * nextTask = new TaskLink(); nextTask.setTaskFlowSpecificationId("task_1");
 * nextTask.title("task_1"); nextTaskList.add(nextTask); ProcessFlow processFlow
 * = new ProcessFlow().processFlowSpecification("DummyProcess")
 * .characteristic(characteristics); processFlow.setLinks(new
 * Links().nextTaskstoBePerformed(nextTaskList));
 * 
 * TaskFlow taskFlow = new TaskFlow(); taskFlow.setId("Task_1"); List<TaskLink>
 * nextTaskList1 = new ArrayList<>(); TaskLink nextTask1 = new TaskLink();
 * nextTask1.setTaskFlowSpecificationId("task_2"); nextTask1.title("task_2");
 * nextTaskList1.add(nextTask1); taskFlow.setLinks(new
 * Links().nextTaskstoBePerformed(nextTaskList1)); BosTaskFlow bosTaskFlow = new
 * BosTaskFlow(processFlowId, taskFlow); List<BosTaskFlow> bosTaskFlows = new
 * ArrayList<>(); bosTaskFlows.add(bosTaskFlow);
 * 
 * Map<String, Object> taskIds = new HashMap<>(); taskIds.put("taskId",
 * "Task_1"); ProcessFlowCreatedEvent event = new
 * ProcessFlowCreatedEvent(processFlowId, processFlow, bosTaskFlows, taskIds);
 * projector.handle(event); verify(producerEnvelope).send(any(Message.class)); }
 * 
 * @Test public void handleTaskFlowUpdatedEvent() { String processFlowId =
 * "2e05d202-18a0-4e7b-bb3f-88c5dd68954d"; TaskFlow taskFlow = new TaskFlow();
 * taskFlow.setId("Task_1"); List<TaskLink> nextTaskList1 = new ArrayList<>();
 * TaskLink nextTask1 = new TaskLink();
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
 * processTaskIds = new HashMap<>(); TaskFlowUpdatedEvent event = new
 * TaskFlowUpdatedEvent(bosTaskFlow, variablesFromUserActions, characteristics,
 * processTaskIds);
 * 
 * projector.handle(event); verify(producerEnvelope).send(any(Message.class)); }
 * 
 * }
 */