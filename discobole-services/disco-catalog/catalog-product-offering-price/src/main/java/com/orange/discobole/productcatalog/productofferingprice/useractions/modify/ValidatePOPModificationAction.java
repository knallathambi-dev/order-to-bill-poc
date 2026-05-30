// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.pojo.ValidateEntityOperation;
import com.orange.discobole.productcatalog.productofferingprice.service.ModifyProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.FileUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The Class ValidateProductSpecAction validates product specification when user
 * clicks on validate.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
@Component("ProductOfferingPriceModification.validate")
public class ValidatePOPModificationAction implements UserAction {

	@Resource
	private ModifyProductOfferingPriceService productOfferingPriceService;

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
			characteristicList.add(new CharacteristicSpecification().name(ProductOfferingPriceConstants.VALIDATE)
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
				ProductOfferingPriceConstants.VALIDATE, characteristicList);
		if (null != characteristic) {
			validate = (ValidateEntityOperation) ValidationUtil.validatePojo(characteristic.getValue(),
					"productOfferingPrice",			"validateEntityOperation");
			if (validate.isIsValidated().equals(Boolean.TRUE)) {
				String productOfferingPriceId = (String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID);
				String versionType=(String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductOfferingPriceConstants.VERSION_TYPE);
				if (productOfferingPriceId != null) {
					productOfferingPriceService.validatePOPModification(productOfferingPriceId, versionType);
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
				stateMachine.getTaskDefinitionId() + "-" + ProductOfferingPriceConstants.VALIDITY + "-" + characteristicIndex);
		return characteristicList;
	}

}
