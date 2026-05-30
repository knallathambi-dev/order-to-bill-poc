// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.modify;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.pojo.category.CancelEntityOperation;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;

public class CategoryModificationCancelActionTest extends CategoryApplicationTests {
@InjectMocks
private CategoryModificationCancelAction categoryCancelAction;
@Mock
private ModifyCategoryService modifyCategoryService;
@Mock
private StateMachine stateMachine;

@Mock
private ConfigurableProperties configurableProperties;

@Mock
private ExtendedState extendedState;
private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
private final String TASK_ID = UUID.randomUUID().toString();
private static String CATEGORY_ID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
private StateMachineTransition stateMachineTransition;
private final ObjectMapper objectMapper = new ObjectMapper()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

@BeforeEach
public void init() throws JsonMappingException, JsonProcessingException, JSONException, Exception {
	Map<String, Object> variablesFromUserActions = new HashMap<>();
	variablesFromUserActions.put(CategoryConstants.CATEGORY_ID,CATEGORY_ID);
	stateMachineTransition = new StateMachineTransition();
	stateMachineTransition.setProcessDefinitionKey("CategoryCreation");
	stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
	stateMachineTransition.setTaskDefinitionId(TASK_ID);
	stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
	ReflectionTestUtils.setField(categoryCancelAction, "objectMapper", objectMapper);
	categoryCancelAction.init();
}
@Test
void performTest() {
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
	List<Characteristic> characteristicList = new ArrayList<>();
	CancelEntityOperation cancelData = new CancelEntityOperation();
	cancelData.setIsCancelled(true);
	Characteristic characteristic = new ObjectCharacteristic().value(cancelData).valueType("Object")
			.name(CategoryConstants.CATEGORY_CANCEL);
	characteristicList.add(characteristic);
	taskFlowUpdate.setCharacteristic(characteristicList);
	categoryCancelAction.perform(stateMachineTransition, taskFlowUpdate);
	Mockito.verify(modifyCategoryService, Mockito.times(1)).cancelCategoryModification(CATEGORY_ID);

}

@Test
void requiredCharacteristicsTest() {
	categoryCancelAction.requiredCharacteristics(stateMachineTransition, null);
	List<CharacteristicSpecification> list= categoryCancelAction.requiredCharacteristics(stateMachineTransition, null); 
	 assertNotNull(list);
}
}
