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

package com.orange.discobole.processflow.service;
/*
 * package com.orange.bos.envelope.service;
 * 
 * import static org.junit.Assert.assertNotNull; import static
 * org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.mockito.ArgumentMatchers.any; import static
 * org.mockito.ArgumentMatchers.anyList; import static
 * org.mockito.ArgumentMatchers.eq; import static org.mockito.Mockito.when;
 * 
 * import java.util.ArrayList; import java.util.HashMap; import java.util.List;
 * import java.util.Map;
 * 
 * import com.orange.bos.envelope.event.ProcessFlowUpdatedEvent; import
 * org.junit.jupiter.api.Assertions; import org.junit.jupiter.api.BeforeEach;
 * import org.junit.jupiter.api.Test; import org.mockito.Mockito; import
 * org.springframework.context.ApplicationContext; import
 * org.springframework.test.util.ReflectionTestUtils;
 * 
 * import com.orange.bos.envelope.aggregate.StateMachineAggregate; import
 * com.orange.bos.envelope.command.UpdateTaskCommand; import
 * com.orange.bos.envelope.dto.BosTaskFlow; import
 * com.orange.bos.envelope.dto.generated.Characteristic; import
 * com.orange.bos.envelope.dto.generated.ProcessFlow; import
 * com.orange.bos.envelope.dto.generated.StringCharacteristic; import
 * com.orange.bos.envelope.dto.generated.TaskFlow; import
 * com.orange.bos.envelope.dto.generated.TaskFlowUpdate; import
 * com.orange.bos.envelope.event.Event; import
 * com.orange.bos.envelope.event.ProcessFlowCreatedEvent; import
 * com.orange.bos.envelope.event.TaskFlowUpdatedEvent; import
 * com.orange.bos.envelope.infra.EnvelopeEventStore; import
 * com.orange.bos.envelope.infra.Publisher; import
 * com.orange.bos.envelope.resolver.StateMachineNextTransitionResolver; import
 * com.orange.bos.envelope.service.impl.TaskFlowCommandServiceImpl;
 * 
 * public class TaskFlowCommandServiceImplTest {
 * 
 * private final TaskFlowCommandServiceImpl taskFlowCommandService =
 * Mockito.spy(TaskFlowCommandServiceImpl.class);
 * 
 * private final EnvelopeEventStore eventStore =
 * Mockito.mock(EnvelopeEventStore.class);
 * 
 * private final Publisher publisher = Mockito.mock(Publisher.class);
 * 
 * private final ApplicationContext context =
 * Mockito.mock(ApplicationContext.class);
 * 
 * private final StateMachineNextTransitionResolver<String, String>
 * transitionResolver = Mockito .mock(StateMachineNextTransitionResolver.class);
 * 
 * String processFlowId = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
 * 
 * private StateMachineAggregate stateMachineAggregate;
 * 
 * @BeforeEach public void setup() {
 * ReflectionTestUtils.setField(taskFlowCommandService, "eventStore",
 * eventStore); ReflectionTestUtils.setField(taskFlowCommandService,
 * "publisher", publisher); ReflectionTestUtils.setField(taskFlowCommandService,
 * "appCtx", context); ReflectionTestUtils.setField(taskFlowCommandService,
 * "stateMachineNextTransitionResolver", transitionResolver);
 * 
 * ProcessFlow processFlow = new
 * ProcessFlow().processFlowSpecification("DummyProcess").id(processFlowId);
 * List<BosTaskFlow> bosTaskFlows = new ArrayList<>(); List<Characteristic>
 * characteristics = new ArrayList<>(); characteristics.add( new
 * StringCharacteristic().value("value_1").name("test_1").valueType(String.class
 * .getSimpleName())); TaskFlow taskFlow = new
 * TaskFlow().id("Task_1").characteristic(characteristics); BosTaskFlow
 * bosTaskFlow = new BosTaskFlow(processFlowId, taskFlow);
 * bosTaskFlows.add(bosTaskFlow);
 * 
 * Map<String, Object> taskIds = new HashMap<>(); taskIds.put("taskId",
 * "Task_1");
 * 
 * ProcessFlowCreatedEvent processFlowCreatedEvent = new
 * ProcessFlowCreatedEvent(processFlowId, processFlow, bosTaskFlows, taskIds);
 * List<Event> events = new ArrayList<>(); events.add(processFlowCreatedEvent);
 * when(eventStore.fetch(processFlowId)).thenReturn(events);
 * 
 * stateMachineAggregate = Mockito.mock(StateMachineAggregate.class);
 * when(taskFlowCommandService.aggregate(anyList(), eq(context),
 * eq(transitionResolver))) .thenReturn(stateMachineAggregate);
 * 
 * Map<String, Object> variablesFromUserActions = new HashMap<>();
 * List<Characteristic> taskCharacteristicList = new ArrayList<>();
 * taskCharacteristicList.add(new
 * StringCharacteristic().value("task_char_value_1").name("task_char_1")
 * .valueType(String.class.getSimpleName())); Map<String, Object> processTaskIds
 * = new HashMap<>(); TaskFlowUpdatedEvent taskFlowUpdatedEvent = new
 * TaskFlowUpdatedEvent(bosTaskFlow, variablesFromUserActions,
 * taskCharacteristicList, processTaskIds);
 * 
 * ProcessFlowUpdatedEvent processFlowUpdatedEvent = new
 * ProcessFlowUpdatedEvent(processFlow);
 * 
 * List<Event> eventList = new ArrayList<>();
 * eventList.add(taskFlowUpdatedEvent); eventList.add(processFlowUpdatedEvent);
 * 
 * 
 * when(stateMachineAggregate.process(any(UpdateTaskCommand.class))).thenReturn(
 * eventList);
 * 
 * }
 * 
 * @Test public void triggerUpdateTaskCommand() {
 * 
 * TaskFlow returnTaskFLow =
 * taskFlowCommandService.updateTaskFlow(processFlowId, "Task_1", new
 * TaskFlowUpdate()); assertNotNull(returnTaskFLow);
 * Assertions.assertAll("updateTaskCommand", () ->
 * assertEquals(returnTaskFLow.getId(), "Task_1"), () ->
 * assertEquals(returnTaskFLow.getCharacteristic().get(0).getName(), "test_1"));
 * }
 * 
 * }
 */