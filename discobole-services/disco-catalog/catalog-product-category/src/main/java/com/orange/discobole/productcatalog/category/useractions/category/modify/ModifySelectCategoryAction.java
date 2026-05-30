// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.exception.DiscoClientException;
import com.orange.discobole.productcatalog.category.pojo.category.SelectCategory;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
import com.orange.discobole.productcatalog.category.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.category.util.FileUtil;
import com.orange.discobole.productcatalog.category.util.ValidationUtil;

@Component("CategoryModification.selectCategory")
public class ModifySelectCategoryAction implements UserAction {
	
	@Resource
	private ObjectMapper objectMapper;
	
	private List<CharacteristicSpecification> characteristicList;
	@Resource
	private ModifyCategoryService categoryService;
	
	@PostConstruct
	public void init() {

		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/category/" + SelectCategory.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			Object selectCategoryData = null;
			selectCategoryData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.SELECT_CATEGORY)
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
		SelectCategory selectCategory= new SelectCategory();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				CategoryConstants.SELECT_CATEGORY, characteristicList);
		
		  if (null != characteristics) { selectCategory = (SelectCategory)
		  ValidationUtil.validatePojo(characteristics.getValue(), "category",
		  "selectCategory"); }
		
		final Map<String, Object> variables = new HashMap<>();
		String categoryId = selectCategory.getId();
		categoryService.initiateCategoryModification(categoryId);
		final List<Characteristic> characteristic = new ArrayList<>();
		characteristic.add(new StringCharacteristic().value(categoryId).name(CategoryConstants.CATEGORY_ID)
				.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
		variables.put(CategoryConstants.CATEGORY_ID, categoryId);
		variables.put(TaskConstants.CHARACTERISTIC, characteristic);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ CategoryConstants.SELECT_CATEGORY + "-" + characteristicIndex);
		return characteristicList;
	}

}
