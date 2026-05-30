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
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class ModifySelectProductOfferingTypeAction provides the user input to
 * define the type of product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingModification.selectPOTypeForModification")
public class ModifySelectProductOfferingTypeAction implements UserAction {
	private List<CharacteristicSpecification> characteristicList;

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
				final String message = characteristics.getValue().toString();
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_TYPE,null,message);
			}
		}
		final Map<String, Object> variables = new HashMap<>();
		offeringType.setIsInstallable(true);
		variables.put(ProductOffConstants.PRODUCTOFFERINGTYPE, offeringType.getProductOfferingType().toString());
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
