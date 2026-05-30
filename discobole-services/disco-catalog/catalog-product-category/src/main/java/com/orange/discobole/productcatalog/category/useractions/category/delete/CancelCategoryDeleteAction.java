// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.delete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.exception.DiscoClientException;
import com.orange.discobole.productcatalog.category.pojo.category.CancelEntityOperation;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;
import com.orange.discobole.productcatalog.category.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.category.util.FileUtil;
import com.orange.discobole.productcatalog.category.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the cancellation of a category deletion operation.
 * Author: Varshika Choudhary
 */
@Component("DeleteCategory.cancelCategoryDeletion")
public class CancelCategoryDeleteAction implements UserAction {

    @Resource
    private DeleteCategoryService deleteCategoryService;

    @Resource
    private ObjectMapper objectMapper;

    private List<CharacteristicSpecification> characteristicList;

    @PostConstruct
    public void init() {
        characteristicList = new ArrayList<>();
        try {
            String file = FileUtil.read("/schemas/category/" + CancelEntityOperation.class.getSimpleName() + ".json");
            Object cancelData = objectMapper.readValue(file, Object.class);
            characteristicList.add(
                    new CharacteristicSpecification()
                            .name(CategoryConstants.CATEGORY_CANCEL)
                            .valueType(Object.class.getSimpleName())
                            .minCardinality(1)
                            .maxCardinality(1)
                            .characteristicValueSpecification(
                                    List.of(
                                            new ObjectCharacteristicValueSpecification()
                                                    .value(cancelData)
                                                    .type(ObjectCharacteristicValueSpecification.class.getSimpleName())
                                    )
                            )
            );
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
            throws ParameterException {

        if (!shouldCancel(taskFlowUpdate)) {
            return new HashMap<>();
        }

        Object categoryIdObj = stateMachine.getVariablesFromUserActions().get(CategoryConstants.CATEGORY_ID);
        cancelCategories(categoryIdObj);

        return new HashMap<>();
    }

    private boolean shouldCancel(TaskFlowUpdate taskFlowUpdate) throws ParameterException {
        Characteristic characteristic = CharacteristicUtil.getCharacteristic(
                taskFlowUpdate.getCharacteristic(),
                CategoryConstants.CATEGORY_CANCEL,
                characteristicList
        );

        if (characteristic == null) {
            return false;
        }

        CancelEntityOperation cancelData = (CancelEntityOperation) ValidationUtil.validatePojo(
                characteristic.getValue(),
                "category",
                "cancelEntityOperation"
        );

        return Boolean.TRUE.equals(cancelData.isIsCancelled());
    }

    private void cancelCategories(Object categoryIdObj) {
        if (categoryIdObj instanceof String) {
            deleteCategoryService.cancelCategoryDeletion((String) categoryIdObj);
        } else if (categoryIdObj instanceof List<?>) {
            cancelMultipleCategories((List<?>) categoryIdObj);
        }
    }

    private void cancelMultipleCategories(List<?> categoryIds) {
        for (Object id : categoryIds) {
            if (id instanceof String) {
                deleteCategoryService.cancelCategoryDeletion((String) id);
            }
        }
    }


    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(
            StateMachineTransition stateMachineTransition,
            Map<Object, Object> contextVariables) {
        if (!characteristicList.isEmpty()) {
            int characteristicIndex = 0;
            characteristicList.get(characteristicIndex).id(
                    stateMachineTransition.getTaskDefinitionId() + "-"
                            + CategoryConstants.CATEGORY_CANCEL + "-"
                            + characteristicIndex
            );
        }
        return characteristicList;
    }
}
