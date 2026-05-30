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
import com.orange.discobole.productcatalog.category.pojo.category.ValidateEntityOperation;
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

@Component("DeleteCategory.validate")
public class DeleteCategoryValidateAction implements UserAction {

	@Resource
	private DeleteCategoryService deleteCategoryService;

	@Resource
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/category/" + ValidateEntityOperation.class.getSimpleName() + ".json");
			Object validate = null;
			validate = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.VALIDATE)
					.valueType(Object.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(validate).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		ValidateEntityOperation validate = null;
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(
				taskFlowUpdate.getCharacteristic(),
				CategoryConstants.VALIDATE, characteristicList);
		if (null != characteristic) {
			validate = (ValidateEntityOperation) ValidationUtil.validatePojo(
					characteristic.getValue(), "category", "validateEntityOperation");
			if (Boolean.TRUE.equals(validate.isIsValidated())) {
				// Support for multiple IDs 
				Object categoryIdObj = stateMachine.getVariablesFromUserActions().get(CategoryConstants.CATEGORY_ID);
				 if (categoryIdObj instanceof List) {
					// Multiple IDs
					List<?> list = (List<?>) categoryIdObj;
					for (Object id : list) {
						if (id instanceof String) {
							deleteCategoryService.validateDeleteCategory((String) id);
						}
					}
				}

			}
		}

		final Map<String, Object> variables = new HashMap<>();
		variables.put("StopStateMachine", true);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		return characteristicList;
	}

}
