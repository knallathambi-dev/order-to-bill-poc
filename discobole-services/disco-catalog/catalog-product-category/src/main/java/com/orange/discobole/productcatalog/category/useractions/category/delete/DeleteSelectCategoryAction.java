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
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.exception.DiscoClientException;
import com.orange.discobole.productcatalog.category.pojo.category.SelectEntities;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;
import com.orange.discobole.productcatalog.category.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.category.util.FileUtil;
import com.orange.discobole.productcatalog.category.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Varshika Choudhary
 */
@Component("DeleteCategory.deleteSelectCategory")
public class DeleteSelectCategoryAction implements UserAction {

    @Resource
    private ObjectMapper objectMapper;

    private List<CharacteristicSpecification> characteristicList;

    @Resource
    private DeleteCategoryService categoryService;

    @PostConstruct
    public void init() {

        characteristicList = new ArrayList<>();
        try {
            String file = FileUtil.read("/schemas/category/" + SelectEntities.class.getSimpleName() + ".json");
            JSONObject jsonObject = new JSONObject(file);
            Object selectCategoryData = null;
            selectCategoryData = objectMapper.readValue(jsonObject.toString(), Object.class);
            characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.SELECT_ENTITIES)
                    .valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
                    .characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
                            .value(selectCategoryData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
        } catch (Exception e) {
            throw new DiscoClientException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
            throws ParameterException {
        SelectEntities selectCategoryDelete = new SelectEntities();
        Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
                CategoryConstants.SELECT_ENTITIES, characteristicList);

        if (null != characteristics) { selectCategoryDelete = (SelectEntities)
                ValidationUtil.validatePojo(characteristics.getValue(), "category",
                        "selectEntities"); }

        final Map<String, Object> variables = new HashMap<>();
        List<String> categoryIds = selectCategoryDelete.getIds();
        String categoryType = "ProductOfferingCategory";
        for (String categoryId : categoryIds) {
            categoryService.selectCategoryDeletion(categoryId, categoryType);
        }
        final List<Characteristic> characteristic = new ArrayList<>();
        for (String categoryId : categoryIds) {
            characteristic.add(
                    new StringCharacteristic()
                            .value(categoryId)
                            .name(CategoryConstants.CATEGORY_ID)
                            .valueType(String.class.getSimpleName())
                            .type(StringCharacteristic.class.getSimpleName())
            );
        }

        variables.put(TaskConstants.CHARACTERISTIC, characteristic);
        variables.put(CategoryConstants.CATEGORY_ID, categoryIds);

        return variables;
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
        int characteristicIndex = 0;
        characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
                + CategoryConstants.SELECT_ENTITIES + "-" + characteristicIndex);
        return characteristicList;
    }

}
