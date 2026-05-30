// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.delete;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import com.orange.discobole.productcatalog.category.pojo.category.ValidateEntityOperation;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;

 class DeleteCategoryValidateActionTest {

    @InjectMocks
    private DeleteCategoryValidateAction deleteCategoryValidateAction;

    @Mock
    private DeleteCategoryService deleteCategoryService;

    private static final String CATEGORY_ID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    private StateMachineTransition stateMachineTransition;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);

        Map<String, Object> variablesFromUserActions = new HashMap<>();
        variablesFromUserActions.put(CategoryConstants.CATEGORY_ID, CATEGORY_ID);

        stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

        ReflectionTestUtils.setField(deleteCategoryValidateAction, "objectMapper", objectMapper);

        deleteCategoryValidateAction.init();
    }

    @Test
    void performTest_multipleIds() {
        List<String> categoryIds = Arrays.asList("cat1", "cat2", "cat3");
        Map<String, Object> variablesFromUserActions = new HashMap<>();
        variablesFromUserActions.put(CategoryConstants.CATEGORY_ID, categoryIds);

        StateMachineTransition multiIdTransition = new StateMachineTransition();
        multiIdTransition.setVariablesFromUserActions(variablesFromUserActions);

        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        List<Characteristic> characteristicList = new ArrayList<>();
        ValidateEntityOperation validateEntity = new ValidateEntityOperation();
        validateEntity.setValidated(true);
        Characteristic characteristic = new ObjectCharacteristic()
                .value(validateEntity)
                .valueType("Object")
                .name(CategoryConstants.VALIDATE);
        characteristicList.add(characteristic);
        taskFlowUpdate.setCharacteristic(characteristicList);

        deleteCategoryValidateAction.perform(multiIdTransition, taskFlowUpdate);

        for (String id : categoryIds) {
            Mockito.verify(deleteCategoryService, Mockito.times(1)).validateDeleteCategory(id);
        }
    }

    @Test
    void requiredCharacteristicsTest() {
        List<CharacteristicSpecification> list = deleteCategoryValidateAction.requiredCharacteristics(stateMachineTransition, null);
        assertNotNull(list);
    }
}
