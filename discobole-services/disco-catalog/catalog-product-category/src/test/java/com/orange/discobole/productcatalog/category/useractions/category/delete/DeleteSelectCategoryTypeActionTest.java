// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.delete;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.pojo.category.SelectEntityType;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;
/**
 * @author Varshika Choudhary
 */
 class DeleteSelectCategoryTypeActionTest extends CategoryApplicationTests {

    @InjectMocks
    private DeleteSelectCategoryTypeAction deleteSelectCategoryTypeAction;
    @Mock
    private DeleteCategoryService deleteCategoryService;
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
        ReflectionTestUtils.setField(deleteSelectCategoryTypeAction, "objectMapper", objectMapper);
        deleteSelectCategoryTypeAction.init();
    }

    @Test
    public void performTest() {
        Map<String, Object> variablesFromUserActions = new HashMap<>();
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setProcessDefinitionKey("CategoryDeletion");
        stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
        stateMachineTransition.setTaskDefinitionId(TASK_ID);
        stateMachineTransition.setTaskDefinitionKey("selectCategoryType");
        stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        List<Characteristic> charact = new ArrayList<>();
        ObjectCharacteristic selectEntityCharac = new ObjectCharacteristic();
        selectEntityCharac.setName(CategoryConstants.ENTITY_TYPE);
        SelectEntityType selectEntityType = new SelectEntityType();
        selectEntityType.setCategoryType(CategoryEntityType.PRODUCTOFFERINGCATEGORY);
        selectEntityCharac.setValue(selectEntityType);
        charact.add(selectEntityCharac);
        taskFlowUpdate.setCharacteristic(charact);
        if(selectEntityType.getCategoryType().equals(CategoryEntityType.PRODUCTOFFERINGCATEGORY)) {
            Map<String, Object> performVariables = deleteSelectCategoryTypeAction.perform(stateMachineTransition,
                    taskFlowUpdate);
            assertThat(performVariables.get(CategoryConstants.CATEGORY_TYPE)).isEqualTo(CategoryEntityType.PRODUCTOFFERINGCATEGORY.getValue());
            
        }
        
    }

    @Test
    public void performCharateristicGetTest() {
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        List<CharacteristicSpecification> charact = deleteSelectCategoryTypeAction
                .requiredCharacteristics(stateMachineTransition, null);
        assertThat(charact.size()).isEqualTo(1);

    }
}
