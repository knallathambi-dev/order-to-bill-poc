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


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.json.JSONArray;
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
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class ProductOfferingTypeAction is to define Offering Type.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingCreation.offeringType")
public class ProductOfferingTypeAction implements UserAction {

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	/**
	 * Inits the characteristics with initial values.
	 */
	@PostConstruct
	public void init() {

		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil
					.read("/schemas/productoffering/" + SelectProductOfferingType.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			JSONArray enumEntityArray = new JSONArray(List.of(ProductOfferingType.ATOMICPRODUCTOFFERING.toString(), ProductOfferingType.BUNDLEPRODUCTOFFERING.toString(), ProductOfferingType.CONTRACT.toString()));
			jsonObject.getJSONObject("SelectProductOfferingType").getJSONObject("properties").getJSONObject("productOfferingType")
					.put("enum", enumEntityArray);
			Object offeringType = null;
			offeringType = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.OFFERING_TYPE)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(offeringType).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		SelectProductOfferingType offeringType = new SelectProductOfferingType();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.OFFERING_TYPE, characteristicList);
		if (null != characteristics) {
			offeringType = (SelectProductOfferingType) ValidationUtil.validatePojo(characteristics.getValue(),
					"productOffering", "SelectProductOfferingType");
			if (null == ProductOfferingType.fromValue(offeringType.getProductOfferingType().toString())) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_TYPE);
			}
		}
		final Map<String, Object> variables = new HashMap<>();
		offeringType.setIsInstallable(true);
		if (offeringType.getProductOfferingType().equals(ProductOfferingType.BUNDLEPRODUCTOFFERING)) {
			offeringType.setIsBundle(true);
			offeringType.setIsSellable(true);
		}
		String productOffId = productOfferingService.createProductOfferingType(offeringType);
		final List<Characteristic> characteristic = new ArrayList<>();
		characteristic.add(new StringCharacteristic().value(productOffId).name(ProductOffConstants.PRODUCT_OFF_ID)
				.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
		variables.put(ProductOffConstants.PRODUCTOFFERINGTYPE, offeringType.getProductOfferingType().toString());
		variables.put(ProductOffConstants.PRODUCT_OFF_ID, productOffId);
		variables.put(TaskConstants.CHARACTERISTIC, characteristic);
		return variables;
	}

	/**
	 * Required characteristics.
	 *
	 * @param stateMachineTransition
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.OFFERING_TYPE + "-" + characteristicIndex);
		return characteristicList;
	}

}
