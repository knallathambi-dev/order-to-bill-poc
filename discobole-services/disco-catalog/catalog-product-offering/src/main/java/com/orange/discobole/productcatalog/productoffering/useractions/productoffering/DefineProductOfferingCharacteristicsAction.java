// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.dto.generated.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class DefineProductOfferingCharacteristicsAction defines product offering
 * characteristics
 *
 * @author Shreya Sharma
 * @since 1.0
 */
@Component("ProductOfferingCreation.defCharacteristics")
public class DefineProductOfferingCharacteristicsAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(DefineProductOfferingCharacteristicsAction.class);

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ConfigurableProperties configurableProperties;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productoffering/"
					+ PickAtomicProductOfferingCharacteristic.class.getSimpleName() + ".json");
			Object charOffering = null;
			charOffering = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification()
					.name(ProductOffConstants.PRODUCT_OFF_CHARACTERISTICS).valueType(List.class.getSimpleName())
					.minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(charOffering).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In perform method of DefineProductOfferingCharacteristicsAction");
		String productOfferingId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.PRODUCT_OFF_CHARACTERISTICS, characteristicList);
		if (null != characteristic) {
			List<PickAtomicProductOfferingCharacteristic> pickProductOfferingCharacteristic = (List<PickAtomicProductOfferingCharacteristic>) (Object) ValidationUtil
					.validateArrayOfPojo(characteristic.getValue(), "productOffering",
							"pickAtomicProductOfferingCharacteristic");
			productOfferingService.updateProductOfferingCharacteristics(productOfferingId,
					pickProductOfferingCharacteristic);
			final Map<String, Object> variables = new HashMap<>();
			final List<Characteristic> characteristics = new ArrayList<>();
			characteristics.add(new ObjectCharacteristic().value(pickProductOfferingCharacteristic).name(characteristic.getName())
					.valueType(List.class.getSimpleName()).type(ObjectCharacteristic.class.getSimpleName()));
			variables.put(TaskConstants.CHARACTERISTIC,characteristics);
			return variables;
		}

		return new HashMap<>();
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.PRODUCT_OFF_CHARACTERISTICS + "-" + characteristicIndex);
		return characteristicList;
	}

}
