// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.SelectPOPType;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.FileUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The Class SelectProductOfferingPriceTypeAction provides the user input to
 * define price type of product offering price.
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Component("ProductOfferingPriceCreation.selectPOPType")
public class SelectProductOfferingPriceTypeAction implements UserAction {

	@Resource
	private ObjectMapper objectMapper;

	List<CharacteristicSpecification> characteristicSpecifications;

	@Resource
	private ProductOfferingPriceService productOfferingPriceService;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicSpecifications = new ArrayList<>();
		try {
			String file = FileUtil
					.read("/schemas/productofferingprice/" + SelectPOPType.class.getSimpleName() + ".json");
			Object selectPopType = null;
			selectPopType = objectMapper.readValue(file, Object.class);
			characteristicSpecifications.add(new CharacteristicSpecification()
					.name(ProductOfferingPriceConstants.SELECT_POP_TYPE).valueType(Object.class.getSimpleName())
					.minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(selectPopType).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}

	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		SelectPOPType selectPOPType = new SelectPOPType();
		Characteristic popType = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOfferingPriceConstants.SELECT_POP_TYPE, characteristicSpecifications);
		if (null != popType) {
			selectPOPType = (SelectPOPType) ValidationUtil.validatePojo(popType.getValue(), "productOfferingPrice",
					"selectPOPType");
			if (null == ProductOfferingPriceType.fromValue(selectPOPType.getpOPType().toString())) {
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_CHARACERISTICS_VALUE, popType.getValue().toString(),null);
			}
		}
		String productOfferingPriceId = productOfferingPriceService
				.createProductOfferingPrice(selectPOPType.getpOPType());
		Map<String, Object> variablesMap = new HashMap<>();
		final List<Characteristic> characteristic = new ArrayList<>();
		characteristic
				.add(new StringCharacteristic().value(productOfferingPriceId).name(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID)
						.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
		variablesMap.put(TaskConstants.CHARACTERISTIC, characteristic);
		variablesMap.put(ProductOfferingPriceConstants.SELECT_POP_TYPE, selectPOPType.getpOPType().toString());
		variablesMap.put(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID, productOfferingPriceId);
		return variablesMap;
	}

	/**
	 * Required characteristics.
	 *
	 * @param stateMachine the stateMachine
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicSpecifications.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOfferingPriceConstants.SELECT_POP_TYPE + "-" + characteristicIndex);
		return characteristicSpecifications;
	}
}
