// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.constant.ProcessConstants;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.pojo.category.CategoryIdentityData;
import com.orange.discobole.productcatalog.category.pojo.category.DefineCategoryIdentityData;
import com.orange.discobole.productcatalog.category.service.CategoryService;

public class CategoryIdentityDataActionTest extends CategoryApplicationTests {
@InjectMocks
CategoryIdentityDataAction categoryIdentityDataAction;

@Mock
private CategoryService categoryService;
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
	stateMachineTransition.setTaskDefinitionKey("categoryIdentityData");
	stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
	ReflectionTestUtils.setField(categoryIdentityDataAction, "objectMapper", objectMapper);
	categoryIdentityDataAction.init();
}
@Test
public void performTest() {

	
    
    Map<Object, Object> variables = new HashMap<>();
    variables.put(CategoryConstants.CATEGORY_ID, CATEGORY_ID);
    when(stateMachine.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(variables);

    
    DefineCategoryIdentityData defCategoryIdentityData = new DefineCategoryIdentityData();
    CategoryIdentityData categoryIdentityData = new CategoryIdentityData();
    categoryIdentityData.setName("test");
    categoryIdentityData.setDescription("descptn");
    categoryIdentityData.setIsRoot(true);
    defCategoryIdentityData.setCategoryIdentityData(categoryIdentityData);

   
    TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
    List<Characteristic> charact = new ArrayList<>();
    ObjectCharacteristic operationCharac = new ObjectCharacteristic();
    operationCharac.setName(CategoryConstants.CATEGORY_IDENTITY_DATA);
    operationCharac.setValue(defCategoryIdentityData);
    charact.add(operationCharac);
    taskFlowUpdate.setCharacteristic(charact);

 
    when(categoryService.defineCategoryIdentityData(
            any(String.class),      
            any(String.class),      
            anyBoolean(),           
            any(),                  
            anyList(),              
            anyList()               
    )).thenReturn(CATEGORY_ID);

    // Execute
    Map<String, Object> performVariables =
            categoryIdentityDataAction.perform(stateMachineTransition, taskFlowUpdate);

    // Asserts
    assertThat(performVariables.size()).isEqualTo(4);
    assertThat(performVariables).containsKey(CategoryConstants.CATEGORY_TYPE);
    assertThat(performVariables).containsKey(TaskConstants.CHARACTERISTIC);
    assertThat(performVariables).containsKey(ProcessConstants.RELATED_ENTITY);
    assertThat(performVariables).containsKey(CategoryConstants.CATEGORY_ID);
    assertThat(performVariables.get(CategoryConstants.CATEGORY_ID)).isEqualTo(CATEGORY_ID);

}
@Test
void requiredCharacteristicNullTest() {
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
	List<Characteristic> charact = new ArrayList<>();
	ObjectCharacteristic characteristic = new ObjectCharacteristic();
	CategoryIdentityData identityData = new CategoryIdentityData();
	identityData.setName("Cisco");
	characteristic.setValue(identityData);
	charact.add(characteristic);
	taskFlowUpdate.setCharacteristic(charact);
	assertThrows(InvalidParameterException.class,
			() -> categoryIdentityDataAction.perform(stateMachineTransition, taskFlowUpdate));
}

@Test
void requiredCharacteristicBlankTest() {
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
	List<Characteristic> charact = new ArrayList<>();

	taskFlowUpdate.setCharacteristic(charact);
	assertThrows(InvalidParameterException.class,
			() -> categoryIdentityDataAction.perform(stateMachineTransition, taskFlowUpdate));
}
@Test
void requiredCharacteristicsTest() {
	categoryIdentityDataAction.requiredCharacteristics(stateMachineTransition, null);
	List<CharacteristicSpecification> list= categoryIdentityDataAction.requiredCharacteristics(stateMachineTransition, null); 
  	 assertNotNull(list);
	
}
}
