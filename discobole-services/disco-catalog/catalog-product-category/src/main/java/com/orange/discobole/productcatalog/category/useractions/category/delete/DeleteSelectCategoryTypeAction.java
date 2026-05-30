// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.delete;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.exception.DiscoClientException;
import com.orange.discobole.productcatalog.category.pojo.category.SelectEntityType;
import com.orange.discobole.productcatalog.category.service.DeleteCategoryService;
import com.orange.discobole.productcatalog.category.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.category.util.FileUtil;
import com.orange.discobole.productcatalog.category.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Varshika Choudhary
 */
@Component("CategoryDeletion.deleteSelectCategoryType")
public class DeleteSelectCategoryTypeAction implements UserAction {

	@Resource
    private ObjectMapper objectMapper;

    @Resource
    private DeleteCategoryService categoryService;

    private List<CharacteristicSpecification> characteristicList;

    @PostConstruct
    public void init() throws JSONException, JsonMappingException, JsonProcessingException,Exception {
        characteristicList = new ArrayList<>();

        String file = FileUtil.read("/schemas/category/" + SelectEntityType.class.getSimpleName() + ".json");
        JSONObject jsonObject = new JSONObject(file);
        JSONArray enumEntityArray = new JSONArray(List.of(CategoryEntityType.PRODUCTSPECIFICATIONCATEGORY.toString(),
                CategoryEntityType.PRODUCTOFFERINGCATEGORY.toString(),CategoryEntityType.PRODUCTOFFERINGPRICECATEGORY.toString()));
        jsonObject.getJSONObject("SelectEntityType").getJSONObject("properties")
                .getJSONObject("categoryType").put("enum", enumEntityArray);
        Object supportEntityData = null;
        supportEntityData = objectMapper.readValue(jsonObject.toString(), Object.class);
        characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.ENTITY_TYPE)
                .valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
                .characteristicValueSpecification(
                        List.of(new ObjectCharacteristicValueSpecification().value(supportEntityData)
                                .type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));

    }

    @Override
    public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
            throws ParameterException {

        SelectEntityType entityTypeData = new SelectEntityType();
        Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
                CategoryConstants.ENTITY_TYPE, characteristicList);
        if (null != characteristics) {
            entityTypeData = (SelectEntityType) ValidationUtil.validatePojo(characteristics.getValue(),
                    "category", "selectEntityType");
            if (null == CategoryEntityType.fromValue(entityTypeData.getCategoryType().toString())) {
                final String message = String.format("invalid characteristic value: %s",
                        characteristics.getValue().toString());
                throw new InvalidParameterException(message);
            }
        }
        final Map<String, Object> variables = new HashMap<>();
        String categoryType = entityTypeData.getCategoryType().getValue();
        final List<Characteristic> characteristic = new ArrayList<>();
        characteristic
                .add(new StringCharacteristic().value(categoryType).name(CategoryConstants.CATEGORY_TYPE)
                        .valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
        variables.put(CategoryConstants.CATEGORY_TYPE, categoryType);
        variables.put(TaskConstants.CHARACTERISTIC, characteristic);
        return variables;
    }

    @Override
    public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
        int characteristicIndex = 0;
        characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
                + CategoryConstants.ENTITY_TYPE + "-" + characteristicIndex);
        return characteristicList;

    }
}