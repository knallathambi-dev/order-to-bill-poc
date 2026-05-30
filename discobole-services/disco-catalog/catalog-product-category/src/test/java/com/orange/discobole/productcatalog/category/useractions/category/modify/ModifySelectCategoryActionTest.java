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
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.pojo.category.SelectCategory;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;

class ModifySelectCategoryActionTest extends CategoryApplicationTests {
	
	@InjectMocks
	private ModifySelectCategoryAction modifySelectCategoryAction;
	@Mock
	private ModifyCategoryService modifycategoryService;
	@Mock
	private StateMachine stateMachine;
	
	@Mock
	private ConfigurableProperties configurableProperties;
	
	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
	private final String TASK_ID = UUID.randomUUID().toString();
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	public void init() throws JsonMappingException, JsonProcessingException, JSONException, Exception {
		ReflectionTestUtils.setField(modifySelectCategoryAction, "objectMapper", objectMapper);
		modifySelectCategoryAction.init();
	}
	
	@Test
	public void performTest() {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(CategoryConstants.CATEGORY_TYPE, "PRODUCTOFFERINGCATEGORY");
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("CategoryModification");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("selectCategory");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic selectEntityCharac = new ObjectCharacteristic();
		selectEntityCharac.setName(CategoryConstants.SELECT_CATEGORY);
		SelectCategory selectCategory=new SelectCategory();
		selectCategory.setId("83706d6c-fa8c-489b-ad71-01dd556d99b5");
		selectEntityCharac.setValue(selectCategory);
		charact.add(selectEntityCharac);
		taskFlowUpdate.setCharacteristic(charact);
		if(selectCategory.getId() == "83706d6c-fa8c-489b-ad71-01dd556d99b5") {
			modifycategoryService.initiateCategoryModification("83706d6c-fa8c-489b-ad71-01dd556d99b5");

			Map<String, Object> performVariables = modifySelectCategoryAction.perform(stateMachineTransition,
					taskFlowUpdate);
			assertThat(performVariables.get(CategoryConstants.CATEGORY_ID)).isEqualTo("83706d6c-fa8c-489b-ad71-01dd556d99b5");
			assertEquals(List.of(new StringCharacteristic().value("83706d6c-fa8c-489b-ad71-01dd556d99b5").name(CategoryConstants.CATEGORY_ID)
				.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName())),
					performVariables.get(TaskConstants.CHARACTERISTIC));
			
			
		}
		
	}
	
	@Test
	public void performCharateristicGetTest() {
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		List<CharacteristicSpecification> charact = modifySelectCategoryAction
				.requiredCharacteristics(stateMachineTransition, null);
		assertThat(charact.size()).isEqualTo(1);

	}


}
