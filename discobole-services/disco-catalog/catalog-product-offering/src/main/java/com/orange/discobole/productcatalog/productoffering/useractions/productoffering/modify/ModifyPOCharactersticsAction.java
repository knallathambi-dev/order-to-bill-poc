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


import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class ModifyPOCharactersticsAction modifies product offering
 * characteristics
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingModification.modifyPOCharacteristicsData")
public class ModifyPOCharactersticsAction implements UserAction {

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private QueryService queryService;

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

		String productOfferingId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		ProductOffering productOffering=queryService.fetchProductOfferingById(productOfferingId, null);
		// only lifecycle status can be modified in the launched state
		if(productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.LAUNCHED.getValue()) ||
				productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.RETIRED.getValue())){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE, "you can not modify entity with launched or retired status", "");
		}
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.PRODUCT_OFF_CHARACTERISTICS, characteristicList);
		List<PickAtomicProductOfferingCharacteristic> pickProductOfferingCharacteristic = new ArrayList<>();
		if (null != characteristic) {
			pickProductOfferingCharacteristic = (List<PickAtomicProductOfferingCharacteristic>) (Object) ValidationUtil
					.validateArrayOfPojo(characteristic.getValue(), "productOffering",
							"pickAtomicProductOfferingCharacteristic");
		}
		productOfferingService.modifyProductOfferingCharacteristics(productOfferingId,
				pickProductOfferingCharacteristic);
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
