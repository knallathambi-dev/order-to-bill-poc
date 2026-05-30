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

import com.orange.discobole.processflow.exception.DiscoManagedClientException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.SelectPOP;
import com.orange.discobole.productcatalog.productofferingprice.service.ModifyProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;
import com.orange.discobole.productcatalog.productofferingprice.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.FileUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Component("ProductOfferingPriceModification.selectPOP")
public class SelectPOPAction implements UserAction {
	
	private static final Logger LOGGER = LogManager.getLogger(SelectPOPAction.class);

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ModifyProductOfferingPriceService popService;

	private List<CharacteristicSpecification> characteristicList;
	
	@Resource
	QueryService queryService;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productofferingprice/" + SelectPOP.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			Object selectPOPData = null;
			selectPOPData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOfferingPriceConstants.SELECT_PRODUCT_OFFERING_PRICE)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(selectPOPData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In perform method of SelectPOPAction");
		SelectPOP selectPOPData = new SelectPOP();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOfferingPriceConstants.SELECT_PRODUCT_OFFERING_PRICE, characteristicList);
		if (null != characteristics) {
			selectPOPData = (SelectPOP) ValidationUtil.validatePojo(characteristics.getValue(),
					"productOfferingPrice", "selectPOP");
		}
		final Map<String, Object> variables = new HashMap<>();
		String popId = selectPOPData.getId();
		ProductOfferingPrice pop=queryService.getProductOfferingPrice(popId);
		if(pop.getLifecycleStatus().getValue().equals(ProductOfferingPriceLifecycle.LAUNCHED.getValue())){
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_INVALID_POP_LIFECYCLE, "you can not modify entity with launched status", "");
		}
		popService.initiatePOPModification(popId);
		final List<Characteristic> characteristic = new ArrayList<>();
		characteristic.add(new StringCharacteristic().value(popId).name(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID)
				.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));

		variables.put(ProductOfferingPriceConstants.SELECT_POP_LIFECYCLE_STATUS,pop.getLifecycleStatus().toString());
		variables.put(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID, popId);
		variables.put(ProductOfferingPriceConstants.VERSION_TYPE,"NoVersion");
		variables.put(TaskConstants.CHARACTERISTIC, characteristic);
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
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductOfferingPriceConstants.SELECT_PRODUCT_OFFERING_PRICE + "-" + characteristicIndex);
		return characteristicList;

	}


}
