// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.ValidateEntityOperation;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class ValidatePOModificationAction validates product offering when user
 * clicks on validate.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingModification.validate")
public class ValidatePOModificationAction implements UserAction {

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

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
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.VALIDATE)
					.valueType(Object.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(validate).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method processes the user input to modify validation for product
	 * offering.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) {
		ValidateEntityOperation validate = null;
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.VALIDATE, characteristicList);
		if (null != characteristic) {
			validate = (ValidateEntityOperation) ValidationUtil.validatePojo(characteristic.getValue(),
					"productOffering", "validateEntityOperation");
			if (validate.isIsValidated().equals(Boolean.TRUE)) {
				String productOfferingId = (String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductOffConstants.PRODUCT_OFF_ID);
				String versionType = (String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductOffConstants.VERSION_TYPE);
				
				if (productOfferingId != null) {
					productOfferingService.validateProductOfferingModification(productOfferingId,versionType);
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
				stateMachine.getTaskDefinitionId() + "-" + ProductOffConstants.VALIDATE + "-" + characteristicIndex);
		return characteristicList;
	}

}
