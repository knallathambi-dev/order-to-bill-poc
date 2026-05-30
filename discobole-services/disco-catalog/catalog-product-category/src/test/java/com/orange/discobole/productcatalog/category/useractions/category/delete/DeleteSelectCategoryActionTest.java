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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.pojo.category.SelectEntities;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;

 class DeleteSelectCategoryActionTest {

    @InjectMocks
    private DeleteSelectCategoryAction deleteSelectCategoryAction;

    @Mock
    private DeleteCategoryService deleteCategoryService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private StateMachineTransition stateMachineTransition;
    private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
    private final String TASK_ID = UUID.randomUUID().toString();

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up the ObjectMapper in the action
        ReflectionTestUtils.setField(deleteSelectCategoryAction, "objectMapper", objectMapper);

        doNothing().when(deleteCategoryService).selectCategoryDeletion(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );

        // Prepare state machine transition
        Map<String, Object> variablesFromUserActions = new HashMap<>();
        stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setProcessDefinitionKey("CategoryDeletion");
        stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
        stateMachineTransition.setTaskDefinitionId(TASK_ID);
        stateMachineTransition.setTaskDefinitionKey("selectCategory");
        stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

        // Initialize the action (loads characteristicList)
        deleteSelectCategoryAction.init();
    }

    @Test
    public void performTest() {
        // Prepare TaskFlowUpdate with correct characteristic
        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        List<Characteristic> charact = new ArrayList<>();
        SelectEntities selectEntities = new SelectEntities();
        List<String> ids = new ArrayList<>();
        ids.add("category1");
        selectEntities.setIds(ids);

        Characteristic characteristic = new ObjectCharacteristic()
                .value(selectEntities)
                .valueType("Object")
                .name(CategoryConstants.SELECT_ENTITIES);

        charact.add(characteristic);
        taskFlowUpdate.setCharacteristic(charact);

        // Call perform and assert
        Map<String, Object> performVariables = deleteSelectCategoryAction.perform(stateMachineTransition, taskFlowUpdate);
        assertThat(performVariables).containsEntry(CategoryConstants.CATEGORY_ID, ids);
    }

    @Test
     void requiredCharacteristicsTest() {
        List<CharacteristicSpecification> list = deleteSelectCategoryAction.requiredCharacteristics(stateMachineTransition, null);
        assertNotNull(list);
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getName()).isEqualTo(CategoryConstants.SELECT_ENTITIES);
    }
}
