// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions.prodspec.modify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.pojo.ValidateEntityOperation;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import com.orange.discobole.productcatalog.productspecification.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class ValidateProductSpecAction validates product specification when user
 * clicks on validate.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Component("ProductSpecModification.validate")
public class ValidateProductSpecModificationAction implements UserAction {

	@Resource
	private ProductSpecService productSpecService;

	@Resource
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + ValidateEntityOperation.class.getSimpleName() + ".json");
			Object validate = null;
			validate = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.VALIDATE)
					.valueType(Object.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(validate).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method processes the user input to validate for product
	 * specification.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) {
		ValidateEntityOperation validate = null;
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductSpecConstants.VALIDATE, characteristicList);
		if (null != characteristic) {
			validate = (ValidateEntityOperation) ValidationUtil.validatePojo(characteristic.getValue(),
					"productSpecification",			"validateEntityOperation");
			if (validate.isIsValidated().equals(Boolean.TRUE)) {
				String productSpecId = (String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductSpecConstants.PRODUCT_SPEC_ID);
				if (productSpecId != null) {
					productSpecService.validateProductSpecificationModification(productSpecId);
				}
			}
		}

		final Map<String, Object> variables = new HashMap<>();
		variables.put("StopStateMachine", true);
		return variables;
	}

	/**
	 * Required characteristics.
	 *
	 * @param stateMachine the stateMachine
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(
				stateMachine.getTaskDefinitionId() + "-" + ProductSpecConstants.VALIDATE + "-" + characteristicIndex);
		return characteristicList;
	}

}
