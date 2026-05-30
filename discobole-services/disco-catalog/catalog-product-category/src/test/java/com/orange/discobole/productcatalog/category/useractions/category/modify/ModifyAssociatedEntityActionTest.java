// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.modify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

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
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryAssociateEntity;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.pojo.category.DefineEntity;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;

public class ModifyAssociatedEntityActionTest extends CategoryApplicationTests {

@InjectMocks
private ModifyAssociatedEntityAction modifyAssociatedEntity;

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
	variablesFromUserActions.put(CategoryConstants.CATEGORY_TYPE, CategoryEntityType.PRODUCTOFFERINGCATEGORY.toString());
	variablesFromUserActions.put(CategoryConstants.CATEGORY_ID,CATEGORY_ID);
	stateMachineTransition = new StateMachineTransition();
	stateMachineTransition.setProcessDefinitionKey("CategoryCreation");
	stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
	stateMachineTransition.setTaskDefinitionId(TASK_ID);
	stateMachineTransition.setTaskDefinitionKey("categoryIdentityData");
	stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
	ReflectionTestUtils.setField(modifyAssociatedEntity, "objectMapper", objectMapper);
	modifyAssociatedEntity.init();
}
@Test
public void performTest() {
	Map<Object, Object> variables = new HashMap<>();
	variables.put(CategoryConstants.CATEGORY_ID, CATEGORY_ID);
	when(stateMachine.getExtendedState()).thenReturn(extendedState);
	when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
	DefineEntity entity=new DefineEntity();
	entity.setEntityId("po-1");
	entity.setEntityType(CategoryAssociateEntity.PRODUCTOFFERING);
	List<DefineEntity> entities =new ArrayList<DefineEntity>();
    entities.add(entity);
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
	List<Characteristic> charact = new ArrayList<>();
	ObjectCharacteristic operationCharac = new ObjectCharacteristic();
	operationCharac.setName(CategoryConstants.DEFINE_ENTITY);
	operationCharac.setValue(entities);
	charact.add(operationCharac);

	taskFlowUpdate.setCharacteristic(charact);

	Map<String, Object> performVariables =modifyAssociatedEntity.perform(stateMachineTransition, taskFlowUpdate); 
	assertThat(performVariables.size()).isEqualTo(0);
	Mockito.verify(modifyCategoryService, Mockito.times(1)).modifyAssociatedEntity(CATEGORY_ID, List.of("po-1"));
}
@Test
public void performTestWithNoValue() {
	Map<Object, Object> variables = new HashMap<>();
	variables.put(CategoryConstants.CATEGORY_ID, CATEGORY_ID);
	when(stateMachine.getExtendedState()).thenReturn(extendedState);
	when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
	
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
	List<Characteristic> charact = new ArrayList<>();
	
	  ObjectCharacteristic operationCharac = new ObjectCharacteristic();
	  operationCharac.setName(CategoryConstants.DEFINE_ENTITY);
	  operationCharac.setValue(null); 
	  charact.add(operationCharac);
	taskFlowUpdate.setCharacteristic(charact);

	Map<String, Object> performVariables =modifyAssociatedEntity.perform(stateMachineTransition, taskFlowUpdate); 
	assertThat(performVariables.size()).isEqualTo(0);
}
@Test
public void performTestWithnullfields() {
	Map<Object, Object> variables = new HashMap<>();
	variables.put(CategoryConstants.CATEGORY_ID, CATEGORY_ID);
	when(stateMachine.getExtendedState()).thenReturn(extendedState);
	when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
	
		List<DefineEntity> entities=new ArrayList<DefineEntity>();
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
	List<Characteristic> charact = new ArrayList<>();
	
	  ObjectCharacteristic operationCharac = new ObjectCharacteristic();
	  operationCharac.setName(CategoryConstants.DEFINE_ENTITY);
	  operationCharac.setValue(entities); 
	  charact.add(operationCharac);
	taskFlowUpdate.setCharacteristic(charact);

	Map<String, Object> performVariables =modifyAssociatedEntity.perform(stateMachineTransition, taskFlowUpdate); 
	assertThat(performVariables.size()).isEqualTo(0);
}

@Test
void requiredCharacteristicsTest() {
	modifyAssociatedEntity.requiredCharacteristics(stateMachineTransition, null);
	List<CharacteristicSpecification> list= modifyAssociatedEntity.requiredCharacteristics(stateMachineTransition, null); 
	 assertNotNull(list);
}
}
