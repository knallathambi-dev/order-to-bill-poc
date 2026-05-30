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

package com.orange.discobole.processflow.aggregate;
/*
 * package com.orange.bos.envelope.aggregate;
 * 
 * import com.orange.bos.envelope.EnvelopeCqrsApplicationTests; import
 * com.orange.bos.envelope.command.InitiateProcessFlowCommand; import
 * com.orange.bos.envelope.command.UpdateTaskCommand; import
 * com.orange.bos.envelope.delegate.UserAction; import
 * com.orange.bos.envelope.dto.generated.*; import
 * com.orange.bos.envelope.event.Event; import
 * com.orange.bos.envelope.event.ProcessFlowCreatedEvent; import
 * com.orange.bos.envelope.event.TaskFlowUpdatedEvent; import
 * com.orange.bos.envelope.exception.TaskFlowNotFoundException; import
 * com.orange.bos.envelope.resolver.StateMachineNextTransitionResolver; import
 * com.orange.bos.envelope.ssm.builder.StateMachineModelBuilder; import
 * com.orange.bos.envelope.ssm.dto.StateMachineTransition; import
 * org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import
 * org.mockito.Mock; import org.mockito.Mockito; import
 * org.springframework.context.ApplicationContext; import
 * org.springframework.http.HttpStatus; import
 * org.springframework.statemachine.ExtendedState; import
 * org.springframework.statemachine.StateMachine; import
 * org.springframework.statemachine.config.ObjectStateMachineFactory; import
 * org.springframework.statemachine.config.model.StateMachineModel; import
 * org.springframework.statemachine.state.State; import
 * org.springframework.statemachine.transition.Transition; import
 * org.springframework.statemachine.trigger.Trigger; import
 * org.springframework.test.util.ReflectionTestUtils;
 * 
 * import java.util.*;
 * 
 * import static org.assertj.core.api.Assertions.assertThat; import static
 * org.junit.Assert.assertTrue; import static
 * org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.junit.jupiter.api.Assertions.assertNotNull; import static
 * org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.when;
 * 
 * public class StateMachineAggregateTest extends EnvelopeCqrsApplicationTests {
 * 
 * @Mock private ApplicationContext context;
 * 
 * @Mock private StateMachine<String, String> stateMachine;
 * 
 * @Mock private StateMachineModelBuilder stateMachineModelBuilder;
 * 
 * @Mock private StateMachineModel<String, String> stateMachineModel;
 * 
 * @Mock private ObjectStateMachineFactory<String, String> factory;
 * 
 * @Mock private ExtendedState extendedState;
 * 
 * @Mock private StateMachineNextTransitionResolver<String, String>
 * stateMachineNextTransitionResolver;
 * 
 * @Mock private Transition<String, String> transition;
 * 
 * @Mock private State<String, String> state;
 * 
 * @Mock private Trigger<String, String> trigger;
 * 
 * @Mock private UserAction userAction;
 * 
 * private static final String PROCESS_FLOW_ID =
 * "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
 * 
 * private static final String TASK_FLOW_ID =
 * "c49c9be9-3f16-48e8-ae81-9dbfdbf6aecd";
 * 
 * 
 * @BeforeEach public void setup() { Map<Object, Object> variables = new
 * HashMap<>();
 * variables.put("2e05d202-18a0-4e7b-bb3f-88c5dd68954d.selectSupportEntity",
 * TASK_FLOW_ID);
 * when(stateMachine.getExtendedState()).thenReturn(extendedState);
 * when(stateMachine.getId()).thenReturn("ProductSpecCreation");
 * when(stateMachine.getUuid()).thenReturn(UUID.fromString(PROCESS_FLOW_ID));
 * when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
 * when(stateMachineNextTransitionResolver.getAvailableTask(stateMachine)).
 * thenReturn(List.of(transition));
 * when(transition.getSource()).thenReturn(state);
 * when(transition.getSource().getId()).thenReturn("selectSupportEntity");
 * when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
 * when(transition.getTrigger()).thenReturn(trigger);
 * when(transition.getTrigger().getEvent()).thenReturn("supportEntitySelected");
 * }
 * 
 * @Test public void raiseProcessFlowCreatedEventTest() {
 * 
 * ProcessFlowCreate processFlowCreate = new
 * ProcessFlowCreate().processFlowSpecification("DummyProcess");
 * StateMachineAggregate stateMachineAggregate = Mockito.spy(new
 * StateMachineAggregate(List.of(), context,
 * stateMachineNextTransitionResolver));
 * ReflectionTestUtils.setField(stateMachineAggregate, "appCtx", context);
 * ReflectionTestUtils.setField(stateMachineAggregate,
 * "stateMachineNextTransitionResolver", stateMachineNextTransitionResolver);
 * when(context.getBean(StateMachineModelBuilder.class)).thenReturn(
 * stateMachineModelBuilder);
 * when(context.getBean(StateMachineModelBuilder.class).build("DummyProcess")).
 * thenReturn(stateMachineModel);
 * when(stateMachineAggregate.getFactory(stateMachineModel)).thenReturn(factory)
 * ; when(factory.getStateMachine(any(UUID.class))).thenReturn(stateMachine);
 * when(stateMachine.getExtendedState()).thenReturn(extendedState);
 * when(stateMachine.getId()).thenReturn("DummyProcess");
 * when(stateMachine.getUuid()).thenReturn(UUID.fromString(PROCESS_FLOW_ID));
 * 
 * when(stateMachineNextTransitionResolver.getAvailableTask(stateMachine)).
 * thenReturn(List.of(transition));
 * when(transition.getSource()).thenReturn(state);
 * when(transition.getSource().getId()).thenReturn("dummy-state");
 * 
 * List<Event> eventList = stateMachineAggregate.process(new
 * InitiateProcessFlowCommand(processFlowCreate));
 * 
 * assertNotNull(eventList); assertEquals(1, eventList.size());
 * assertTrue(eventList.get(0) instanceof ProcessFlowCreatedEvent);
 * assertEquals(PROCESS_FLOW_ID, ((ProcessFlowCreatedEvent)
 * eventList.get(0)).getProcessFlowId());
 * 
 * }
 * 
 * @Test public void raiseTaskFlowUpdatedEventTest() { Map<String, String>
 * values = new LinkedHashMap<>(); values.put("supportEntitySpecification.id",
 * "CS40001"); values.put("supportEntityType", "CFSSpec"); final
 * List<Characteristic> characteristics = new ArrayList<>(); characteristics
 * .add(new ObjectCharacteristic().value(values).name("selectSupportEntity")
 * .valueType(Object.class.getSimpleName()).type(ObjectCharacteristic.class.
 * getSimpleName())); TaskFlowUpdate taskFlowUpdate = new
 * TaskFlowUpdate().characteristic(characteristics);
 * 
 * UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(PROCESS_FLOW_ID,
 * TASK_FLOW_ID, taskFlowUpdate);
 * 
 * StateMachineAggregate stateMachineAggregate = Mockito.spy(new
 * StateMachineAggregate(List.of(), context,
 * stateMachineNextTransitionResolver));
 * 
 * ReflectionTestUtils.setField(stateMachineAggregate, "appCtx", context);
 * ReflectionTestUtils.setField(stateMachineAggregate,
 * "stateMachineNextTransitionResolver", stateMachineNextTransitionResolver);
 * ReflectionTestUtils.setField(stateMachineAggregate, "stateMachine",
 * stateMachine);
 * 
 * Map<String, Boolean> editableStates = new HashMap<>();
 * editableStates.put("selectSupportEntity", true);
 * ReflectionTestUtils.setField(stateMachineAggregate, "editableStates",
 * editableStates);
 * 
 * when(context.containsBean("ProductSpecCreation.selectSupportEntity")).
 * thenReturn(true);
 * when(context.getBean("ProductSpecCreation.selectSupportEntity",
 * UserAction.class)).thenReturn(userAction);
 * when(userAction.perform(any(StateMachineTransition.class),
 * any(TaskFlowUpdate.class))).thenReturn(new HashMap<>());
 * 
 * 
 * List<Event> eventList = stateMachineAggregate.process(updateTaskCommand);
 * assertNotNull(eventList); assertTrue(eventList.size() >= 1);
 * 
 * assertTrue(eventList.get(0) instanceof TaskFlowUpdatedEvent);
 * TaskFlowUpdatedEvent taskFlowUpdatedEvent = (TaskFlowUpdatedEvent)
 * eventList.get(0);
 * 
 * assertEquals(taskFlowUpdatedEvent.getBosTaskFlow().getProcessFLowId(),
 * PROCESS_FLOW_ID);
 * assertEquals(taskFlowUpdatedEvent.getBosTaskFlow().getTaskFlow().getId(),
 * TASK_FLOW_ID); }
 * 
 * 
 * @Test public void raiseTaskFlowUpdatedEventForExistingTaskTest() {
 * 
 * Map<String, String> values = new LinkedHashMap<>();
 * values.put("supportEntitySpecification.id", "CS40001");
 * values.put("supportEntityType", "CFSSpec"); final List<Characteristic>
 * characteristics = new ArrayList<>(); characteristics .add(new
 * ObjectCharacteristic().value(values).name("selectSupportEntity")
 * .valueType(Object.class.getSimpleName()).type(ObjectCharacteristic.class.
 * getSimpleName())); TaskFlowUpdate taskFlowUpdate = new
 * TaskFlowUpdate().characteristic(characteristics);
 * 
 * UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(PROCESS_FLOW_ID,
 * TASK_FLOW_ID, taskFlowUpdate);
 * 
 * 
 * Set<TaskLink> editableTaskLinks = new LinkedHashSet<>(); final TaskLink
 * nextTask = new TaskLink(); nextTask.setTitle("ProductSpecCreation" + '.' +
 * "selectSupportEntity"); nextTask.state(TaskFlowStateType.ACTIVE);
 * nextTask.setTaskFlowSpecificationId(TASK_FLOW_ID);
 * editableTaskLinks.add(nextTask);
 * 
 * 
 * List<StateMachineTransition> stateMachineTransitionsList = new ArrayList<>();
 * StateMachineTransition stateMachineTransition = new StateMachineTransition();
 * stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
 * stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
 * stateMachineTransition.setTaskDefinitionKey("selectSupportEntity");
 * stateMachineTransition.setTaskDefinitionId(TASK_FLOW_ID);
 * stateMachineTransition.setTransition(transition);
 * stateMachineTransition.setVariablesFromUserActions(new HashMap<>());
 * 
 * stateMachineTransitionsList.add(stateMachineTransition);
 * 
 * StateMachineAggregate stateMachineAggregate = Mockito.spy(new
 * StateMachineAggregate(List.of(), context,
 * stateMachineNextTransitionResolver));
 * ReflectionTestUtils.setField(stateMachineAggregate, "appCtx", context);
 * ReflectionTestUtils.setField(stateMachineAggregate,
 * "stateMachineTransitionsList", stateMachineTransitionsList);
 * ReflectionTestUtils.setField(stateMachineAggregate, "editableTaskLinks",
 * editableTaskLinks); ReflectionTestUtils.setField(stateMachineAggregate,
 * "stateMachine", stateMachine);
 * 
 * when(context.containsBean("ProductSpecCreation.selectSupportEntity")).
 * thenReturn(true);
 * 
 * List<CharacteristicSpecification> characteristicList = new ArrayList<>();
 * characteristicList.add(new
 * CharacteristicSpecification().name("selectSupportEntity")
 * .valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
 * .characteristicValueSpecification( List.of(new
 * ObjectCharacteristicValueSpecification().value("supportEntityData")
 * .type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
 * when(context.getBean("ProductSpecCreation.selectSupportEntity",
 * UserAction.class)).thenReturn(userAction);
 * when(context.getBean("ProductSpecCreation.selectSupportEntity",
 * UserAction.class).
 * requiredCharacteristics(stateMachineTransition)).thenReturn(
 * characteristicList);
 * 
 * when(userAction.perform(any(StateMachineTransition.class),
 * any(TaskFlowUpdate.class))).thenReturn(new HashMap<>());
 * 
 * List<Event> eventList = stateMachineAggregate.process(updateTaskCommand);
 * assertNotNull(eventList);
 * 
 * assertTrue(eventList.size() >= 1);
 * 
 * assertTrue(eventList.get(0) instanceof TaskFlowUpdatedEvent);
 * TaskFlowUpdatedEvent taskFlowUpdatedEvent = (TaskFlowUpdatedEvent)
 * eventList.get(0);
 * 
 * assertEquals(taskFlowUpdatedEvent.getBosTaskFlow().getProcessFLowId(),
 * PROCESS_FLOW_ID);
 * assertEquals(taskFlowUpdatedEvent.getBosTaskFlow().getTaskFlow().getId(),
 * TASK_FLOW_ID); }
 * 
 * 
 * @Test public void taskIdNotFoundExceptionTest() { try { Map<String, String>
 * values = new LinkedHashMap<>(); values.put("supportEntitySpecification.id",
 * "CS40001"); values.put("supportEntityType", "CFSSpec"); final
 * List<Characteristic> characteristics = new ArrayList<>(); characteristics
 * .add(new ObjectCharacteristic().value(values).name("selectSupportEntity")
 * .valueType(Object.class.getSimpleName()).type(ObjectCharacteristic.class.
 * getSimpleName())); TaskFlowUpdate taskFlowUpdate = new
 * TaskFlowUpdate().characteristic(characteristics);
 * 
 * UpdateTaskCommand updateTaskCommand = new UpdateTaskCommand(PROCESS_FLOW_ID,
 * "task123", taskFlowUpdate);
 * 
 * StateMachineAggregate stateMachineAggregate = Mockito.spy(new
 * StateMachineAggregate(List.of(), context,
 * stateMachineNextTransitionResolver));
 * 
 * ReflectionTestUtils.setField(stateMachineAggregate, "appCtx", context);
 * ReflectionTestUtils.setField(stateMachineAggregate,
 * "stateMachineNextTransitionResolver", stateMachineNextTransitionResolver);
 * ReflectionTestUtils.setField(stateMachineAggregate, "stateMachine",
 * stateMachine);
 * 
 * List<Event> eventList = stateMachineAggregate.process(updateTaskCommand); }
 * catch (TaskFlowNotFoundException e) { assertThat(e.getCode()).isEqualTo(60);
 * assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND); } } }
 */