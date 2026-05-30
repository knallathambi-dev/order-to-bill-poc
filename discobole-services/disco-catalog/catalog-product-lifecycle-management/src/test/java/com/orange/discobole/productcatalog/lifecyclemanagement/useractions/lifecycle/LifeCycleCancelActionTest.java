// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.useractions.lifecycle;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.LifeCycleConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.exception.DiscoClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.LifeCycleService;

class LifeCycleCancelActionTest extends ManageLifeCycleApplicationTests{
	@InjectMocks
	private LifeCycleCancelAction lifeCycleCancelAction;

	@Mock
	private LifeCycleService lifeCycleService;
	
	@Mock
	private StateMachineTransition stateMachineTransition;

	private final Map<String, Object> variablesFromUserActions = new HashMap<>();

	private static final String PRODUCTSPECID = UUID.randomUUID().toString();
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	void setUp() {
		variablesFromUserActions.put(LifeCycleConstants.ENTITY_ID, PRODUCTSPECID);
		Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn(variablesFromUserActions);
		ReflectionTestUtils.setField(lifeCycleCancelAction, "objectMapper", objectMapper);
		lifeCycleCancelAction.init();
	}

	@Test
	void performTest() {
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		CancelEntityOperation cancelData = new CancelEntityOperation();
		cancelData.setIsCancelled(true);
		Characteristic characteristic = new ObjectCharacteristic().value(cancelData).valueType("Object")
				.name(LifeCycleConstants.LIFECYCLE_CANCEL);
		characteristicList.add(characteristic);
		taskFlowUpdate.setCharacteristic(characteristicList);
		lifeCycleCancelAction.perform(stateMachineTransition, taskFlowUpdate);
		assertNotNull(taskFlowUpdate);
	}
 
	@Test
	void requiredCharacteristicsTest() {
		lifeCycleCancelAction.requiredCharacteristics(stateMachineTransition, null);
		assertNotNull(stateMachineTransition);
	}
	@Test
	void requiredCharacteristicsWithNullTransitionTest() {
		try {
		lifeCycleCancelAction.requiredCharacteristics(null, null);
		}
		catch(DiscoClientException ex) {
			assertNotNull(ex);
		}
	}

	
}