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

package com.orange.discobole.processflow.handler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.processflow.ProcessFlowAxonApplicationTests;
import com.orange.discobole.processflow.config.CQRSProperties;
import com.orange.discobole.processflow.controller.ProcessFlowQueryController;
import com.orange.discobole.processflow.controller.TaskFlowQueryController;
import com.orange.discobole.processflow.dto.generated.Links;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlowStateType;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import com.orange.discobole.processflow.handler.FlowLinkHandler;

class FlowLinkHandlerTest extends ProcessFlowAxonApplicationTests {

	private FlowLinkHandler flowLinkHandler;
	private WebMvcLinkBuilderFactory webMvcLinkBuilderFactory = null;
	private ProcessFlow processFlow;
	private TaskFlow taskFlow;
	private CQRSProperties cqrsProperties;
	private ApplicationContext appCtx;
	String processFlowId;
	String supportEntityTaskId;
	String cancelTaskId;

	@BeforeEach
	void setUp() {
		processFlowId = UUID.randomUUID().toString();
		supportEntityTaskId = UUID.randomUUID().toString();
		cancelTaskId = UUID.randomUUID().toString();
		flowLinkHandler = new FlowLinkHandler();
		webMvcLinkBuilderFactory = new WebMvcLinkBuilderFactory();
		cqrsProperties = Mockito.mock(CQRSProperties.class);
		appCtx = Mockito.mock(ApplicationContext.class);
		ReflectionTestUtils.setField(flowLinkHandler, "properties", cqrsProperties);
		ReflectionTestUtils.setField(flowLinkHandler, "appCtx", appCtx);
	}

	@Order(1)
	@Test
	void addLinks() {
		List<TaskLink> nextTaskToBePerformed = new ArrayList<>();
		TaskLink supportEntityTask = new TaskLink();
		supportEntityTask.title("ProductSpecCreation.selectSupportEntity").state(TaskFlowStateType.ACTIVE)
				.taskFlowSpecificationId(supportEntityTaskId);
		TaskLink cancelTask = new TaskLink();
		cancelTask.title("ProductSpecCreation.cancel").state(TaskFlowStateType.ACTIVE)
				.taskFlowSpecificationId(cancelTaskId);
		nextTaskToBePerformed.add(supportEntityTask);
		nextTaskToBePerformed.add(cancelTask);
		processFlow = new ProcessFlow().id(processFlowId)
				.links(new Links().nextTaskstoBePerformed(nextTaskToBePerformed));
		Link selfLink = webMvcLinkBuilderFactory
				.linkTo(methodOn(ProcessFlowQueryController.class).fetchProcessFlowById(processFlowId,null)).withSelfRel();
		Mockito.when(cqrsProperties.isQueryEnabled()).thenReturn(Boolean.TRUE);
		Link nextTaskList = webMvcLinkBuilderFactory
				.linkTo(methodOn(TaskFlowQueryController.class).fetchTaskFlows(processFlowId,null)).withSelfRel();
		flowLinkHandler.addLinks(processFlow);
	}

	@Order(2)
	@Test
	void testAddLinks() {
		String descriptionTaskId = UUID.randomUUID().toString();
		List<TaskLink> nextTaskToBePerformed = new ArrayList<>();
		TaskLink supportEntityTask = new TaskLink();
		supportEntityTask.title("ProductSpecCreation.description").state(TaskFlowStateType.ACTIVE)
				.taskFlowSpecificationId(descriptionTaskId);
		TaskLink cancelTask = new TaskLink();
		cancelTask.title("ProductSpecCreation.cancel").state(TaskFlowStateType.ACTIVE)
				.taskFlowSpecificationId(cancelTaskId);
		nextTaskToBePerformed.add(supportEntityTask);
		nextTaskToBePerformed.add(cancelTask);
		TaskFlow taskFlow = new TaskFlow().id(supportEntityTaskId)
				.links(new Links().nextTaskstoBePerformed(nextTaskToBePerformed));

		Link selfTask = webMvcLinkBuilderFactory
				.linkTo(methodOn(TaskFlowQueryController.class).fetchTaskFlowById(processFlowId, taskFlow.getId(),null)).withSelfRel();
		Mockito.when(cqrsProperties.isQueryEnabled()).thenReturn(Boolean.TRUE);
		Link nextTaskList = webMvcLinkBuilderFactory
				.linkTo(methodOn(TaskFlowQueryController.class).fetchTaskFlows(processFlowId,null)).withSelfRel();
		Mockito.when(cqrsProperties.isQueryEnabled()).thenReturn(Boolean.TRUE);

		flowLinkHandler.addLinks(processFlowId, taskFlow);
	}
}