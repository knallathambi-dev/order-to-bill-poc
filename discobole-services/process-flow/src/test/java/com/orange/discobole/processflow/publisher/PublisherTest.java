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

package com.orange.discobole.processflow.publisher;
/*
 * package com.orange.bos.envelope.publisher;
 * 
 * import static org.assertj.core.api.Assertions.assertThat;
 * 
 * import java.util.ArrayList; import java.util.HashMap; import java.util.List;
 * import java.util.Map;
 * 
 * import org.junit.jupiter.api.Test;
 * 
 * import com.orange.bos.envelope.EnvelopeCqrsApplicationTests; import
 * com.orange.bos.envelope.dto.BosTaskFlow; import
 * com.orange.bos.envelope.dto.generated.Characteristic; import
 * com.orange.bos.envelope.dto.generated.Links; import
 * com.orange.bos.envelope.dto.generated.ProcessFlow; import
 * com.orange.bos.envelope.dto.generated.StringCharacteristic; import
 * com.orange.bos.envelope.dto.generated.TaskFlow; import
 * com.orange.bos.envelope.dto.generated.TaskLink; import
 * com.orange.bos.envelope.event.Event; import
 * com.orange.bos.envelope.event.ProcessFlowCreatedEvent; import
 * com.orange.bos.envelope.event.TaskFlowUpdatedEvent; import
 * com.orange.bos.envelope.infra.EnvelopeEventStore; import
 * com.orange.bos.envelope.infra.Publisher;
 * 
 * public class PublisherTest extends EnvelopeCqrsApplicationTests {
 * 
 * private Publisher publisher;
 * 
 * @Test public void storeEventsTest() { String processFlowId =
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
 * Map<String, Object> variablesFromUserActions = new HashMap<>();
 * variablesFromUserActions.put("test", "test");
 * 
 * List<Event> eventList = new ArrayList<>();
 * 
 * Map<String, Object> taskIds = new HashMap<>(); taskIds.put("taskId",
 * "Task_1");
 * 
 * eventList.add(new ProcessFlowCreatedEvent(processFlowId, processFlow,
 * bosTaskFlows, taskIds)); eventList.add(new TaskFlowUpdatedEvent(bosTaskFlow,
 * variablesFromUserActions, characteristics, taskIds));
 * 
 * Map<String, List<Event>> eventStore = new HashMap<>();
 * 
 * publisher = new Publisher(new EnvelopeEventStore() {
 * 
 * @Override public void add(String aggregateId, List<Event> eventList) {
 * eventStore.put(aggregateId, eventList); }
 * 
 * @Override public List<Event> fetch(String aggregateId) { return null; }
 * 
 * @Override public List<Event> fetchBackward(String productSpecId) { // TODO
 * Auto-generated method stub return null; }
 * 
 * @Override public void deleteStream(String aggregateId) { // TODO
 * Auto-generated method stub
 * 
 * } }); publisher.publish("1", eventList);
 * assertThat(eventStore.get("1")).containsExactlyElementsOf(eventList); }
 * 
 * }
 */