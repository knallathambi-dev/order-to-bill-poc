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

//package com.orange.disco.processflow.service;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import com.orange.disco.processflow.dto.DiscoTaskFlow;
//import com.orange.disco.processflow.dto.generated.Characteristic;
//import com.orange.disco.processflow.dto.generated.StringCharacteristic;
//import com.orange.disco.processflow.dto.generated.TaskFlow;
//import com.orange.disco.processflow.repository.TaskFlowRepo;
//import com.orange.disco.processflow.service.impl.TaskFlowQueryServiceImpl;
//
//public class TaskFlowQueryServiceImplTest {
//
//	private final TaskFlowQueryServiceImpl taskFlowQueryService = Mockito.spy(new TaskFlowQueryServiceImpl());
//
//	private final TaskFlowRepo taskFlowRepo = Mockito.mock(TaskFlowRepo.class);
//
//	private final String processFlowId = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
//
//	@BeforeEach
//	public void setup() {
//		ReflectionTestUtils.setField(taskFlowQueryService, "taskFlowRepo", taskFlowRepo);
//	}
//
//	@Test
//	public void findTaskFlowsByProcessFlowIdTest() {
//		List<DiscoTaskFlow> discoTaskFlows = new ArrayList<>();
//
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(
//				new StringCharacteristic().value("value_1").name("test_1").valueType(String.class.getSimpleName()));
//		TaskFlow taskFlow = new TaskFlow().id("Task_1").characteristic(characteristics);
//
//		DiscoTaskFlow discoTaskFlow = new DiscoTaskFlow(processFlowId, taskFlow);
//		discoTaskFlows.add(discoTaskFlow);
//		when(taskFlowRepo.findAll()).thenReturn(discoTaskFlows);
//
//		List<TaskFlow> taskFlows = taskFlowQueryService.findTaskFlowsByProcessFlowId(processFlowId);
//		assertNotNull(taskFlows);
//		assertTrue(taskFlows.size() > 0);
//		assertEquals("Task_1", taskFlows.get(0).getId());
//	}
//
//	@Test
//	public void findTaskFlowByIdTest() {
//		List<Characteristic> characteristics = new ArrayList<>();
//		characteristics.add(
//				new StringCharacteristic().value("value_1").name("test_1").valueType(String.class.getSimpleName()));
//		TaskFlow taskFlow = new TaskFlow().id("Task_1").characteristic(characteristics);
//
//		DiscoTaskFlow discoTaskFlow = new DiscoTaskFlow(processFlowId, taskFlow);
//		when(taskFlowRepo.findById(anyString())).thenReturn(java.util.Optional.ofNullable(discoTaskFlow));
//		TaskFlow expectedTaskFlow = taskFlowQueryService.findTaskFlowById(processFlowId, "Task_1");
//		assertNotNull(expectedTaskFlow);
//		assertEquals("Task_1", expectedTaskFlow.getId());
//	}
//}


