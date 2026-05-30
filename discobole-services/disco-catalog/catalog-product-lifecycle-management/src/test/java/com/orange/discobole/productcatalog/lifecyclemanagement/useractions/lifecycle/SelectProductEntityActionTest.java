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
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.LifeCycleConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.SelectLifecycleEntity;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.LifeCycleService;

class SelectProductEntityActionTest extends ManageLifeCycleApplicationTests 
{
   	     
	@InjectMocks
	private SelectProductEntityAction selectProductEntityAction;

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
		Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn(variablesFromUserActions);
		ReflectionTestUtils.setField(selectProductEntityAction, "objectMapper", objectMapper);
		selectProductEntityAction.init();
	}

	@Test
	void performTest() {
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		SelectLifecycleEntity lifeCycleEntityData = new SelectLifecycleEntity();
		lifeCycleEntityData.setEntityType(EntityType.PRODUCTSPECIFICATION);
		lifeCycleEntityData.setId(PRODUCTSPECID);
		
		Characteristic characteristic = new ObjectCharacteristic().value(lifeCycleEntityData).valueType("Object")
				.name(LifeCycleConstants.PRODUCT_ENTITY);
		characteristicList.add(characteristic);
		taskFlowUpdate.setCharacteristic(characteristicList);
		selectProductEntityAction.perform(stateMachineTransition, taskFlowUpdate);
		Mockito.verify(lifeCycleService, Mockito.times(1)).selectEntity(lifeCycleEntityData.getId(),
				lifeCycleEntityData.getEntityType().toString());
	}
	@Test
	void performTestWithNullEntityType() {
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		SelectLifecycleEntity lifeCycleEntityData = new SelectLifecycleEntity();
		lifeCycleEntityData.setEntityType(null);
		lifeCycleEntityData.setId(PRODUCTSPECID);
		
		Characteristic characteristic = new ObjectCharacteristic().value(lifeCycleEntityData).valueType("Object")
				.name(LifeCycleConstants.PRODUCT_ENTITY);
		characteristicList.add(characteristic);
		taskFlowUpdate.setCharacteristic(characteristicList);
		try {
		selectProductEntityAction.perform(stateMachineTransition, taskFlowUpdate);
		}
		catch(DiscoManagedClientException ex) {
			assertNotNull(ex.getReason());
		}
	}

	@Test
	void requiredCharacteristicsTest() {
		selectProductEntityAction.requiredCharacteristics(stateMachineTransition, null);
		assertNotNull(stateMachineTransition);
	}


}