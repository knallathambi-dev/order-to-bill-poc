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
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectPO;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

@Component("ProductOfferingModification.selectPO")
public class SelectPOAction implements UserAction {
	private static final Logger LOGGER = LogManager.getLogger(SelectPOAction.class);

	@Resource
	private ModifyProductOfferingService productOffService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private QueryService queryService;
	
	private List<CharacteristicSpecification> characteristicList;

	/**
	 * Inits the characteristics with initial values.
	 */
	@PostConstruct
	public void init() {

		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productoffering/" + SelectPO.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			Object selectPOData = null;
			selectPOData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.SELECT_PRODUCT_OFFERING)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(selectPOData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("SelectPOAction perform method");
		SelectPO selectPoData = new SelectPO();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.SELECT_PRODUCT_OFFERING, characteristicList);
		if (null != characteristics) {
			selectPoData = (SelectPO) ValidationUtil.validatePojo(characteristics.getValue(), "productOffering",
					"selectPO");
		}
		final Map<String, Object> variables = new HashMap<>();
		String poId = selectPoData.getId();
		ProductOffering productOffering=queryService.fetchProductOfferingById(poId, null);
		// only lifecycle status can be modified in the launched state

		productOffService.initiatePOModification(poId);
		final List<Characteristic> characteristic = new ArrayList<>();
		characteristic.add(new StringCharacteristic().value(poId).name(ProductOffConstants.PRODUCT_OFF_ID)
				.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
		variables.put(ProductOffConstants.SELECT_PRODOFF_LIFECYCLE_STATUS,productOffering.getLifecycleStatus().toString());
		variables.put(ProductOffConstants.VERSION_TYPE,"NoVersion");
		variables.put(ProductOffConstants.PRODUCT_OFF_ID, poId);
		variables.put(TaskConstants.CHARACTERISTIC, characteristic);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.SELECT_PRODUCT_OFFERING + "-" + characteristicIndex);
		return characteristicList;
	}

}
