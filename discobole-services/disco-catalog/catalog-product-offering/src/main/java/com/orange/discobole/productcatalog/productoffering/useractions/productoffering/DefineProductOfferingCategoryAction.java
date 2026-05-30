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
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineProductOfferingCategory;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class ProductOffDescAction provides the user input to define categories
 * for product offering.
 *
 * @author Piyush Goel
 * @since 1.0
 */
@Component("ProductOfferingCreation.defCategory")
public class DefineProductOfferingCategoryAction implements UserAction {

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil
					.read("/schemas/productoffering/" + DefineProductOfferingCategory.class.getSimpleName() + ".json");
			Object defCategory = null;
			defCategory = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.CATEGORY)
					.valueType(List.class.getSimpleName()).minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(defCategory).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method processes the user input to define categories for product
	 * offering.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) {

		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		Characteristic category = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.CATEGORY, characteristicList);
		List<String> categoryIds = new ArrayList<>();
		if (null != category) {
			List<DefineProductOfferingCategory> categoryObject = (List<DefineProductOfferingCategory>) (Object) ValidationUtil
					.validateArrayOfPojo(category.getValue(), "productOffering", "defineCategory");
			for (DefineProductOfferingCategory value : categoryObject) {
				categoryIds.add(value.getProductCategoryId());
			}
		}
		productOfferingService.defineProductOfferingCategory(productOffId, categoryIds);
		return new HashMap<>();
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
				stateMachine.getTaskDefinitionId() + "-" + ProductOffConstants.CATEGORY + "-" + characteristicIndex);
		return characteristicList;
	}

}
