// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.modify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.pojo.category.CancelEntityOperation;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
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
@Component("CategoryModification.categoryModificationCancel")
public class CategoryModificationCancelAction implements UserAction {

	@Resource
	private ModifyCategoryService modifyCategoryService;

	@Resource
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() throws JsonProcessingException {
		characteristicList = new ArrayList<>();
			String file = FileUtil.read("/schemas/category/" + CancelEntityOperation.class.getSimpleName() + ".json");
			Object cancelData = null;
			cancelData = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.CATEGORY_CANCEL)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(cancelData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
	}
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {

		CancelEntityOperation cancelData = null;
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				CategoryConstants.CATEGORY_CANCEL, characteristicList);
		if (null != characteristic) {
			cancelData = (CancelEntityOperation) ValidationUtil.validatePojo(characteristic.getValue(),
					"category", "cancelEntityOperation");
			if (cancelData.isIsCancelled().equals(Boolean.TRUE)) {
				String categoryId = (String) stateMachine.getVariablesFromUserActions()
						.get(CategoryConstants.CATEGORY_ID);
				if (categoryId != null) {
					modifyCategoryService.cancelCategoryModification(categoryId);
				}
			}
		}
		return new HashMap<>();
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ CategoryConstants.CATEGORY_CANCEL + "-" + characteristicIndex);
		return characteristicList;
	}

}
