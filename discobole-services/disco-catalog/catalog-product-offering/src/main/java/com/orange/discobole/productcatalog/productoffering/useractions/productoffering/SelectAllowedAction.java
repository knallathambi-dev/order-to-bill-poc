// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.AllowedProductAction;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectAllowedActionData;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * The Class ProductOffDescAction provides the user input to describe product
 * offering.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@Component("ProductOfferingCreation.selectAllowedAction")
public class SelectAllowedAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(SelectAllowedAction.class);

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductOfferingService productOfferingService;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read(
					"/schemas/productoffering/" + SelectAllowedActionData.class.getSimpleName() + ".json");
			Object selectAllowedAction = null;
			selectAllowedAction = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.ALLOWED_ACTION)
					.valueType(List.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(selectAllowedAction).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method processes the user input to describe product offering.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	public Map<String, Object> perform(final StateMachineTransition stateMachineTransition,
									   final TaskFlowUpdate taskFlowUpdate) throws ParameterException {
		LOGGER.info("SelectAllowedAction perform method");

		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.ALLOWED_ACTION ,characteristicList);
		List<AllowedProductAction> dtoList = new ArrayList<>();
		List<SelectAllowedActionData> pojoList = null;
		if (null != characteristic) {
			pojoList = (List<SelectAllowedActionData>) (Object) ValidationUtil
					.validateArrayOfPojo(characteristic.getValue(), "productOffering", "selectAllowedAction");
			for (SelectAllowedActionData value : pojoList) {
				AllowedProductAction dto = new AllowedProductAction();
				dto.setAction(value.getActionData());
				dto.setChannelRef(value.getChannels());
				dtoList.add(dto);
			}
			productOfferingService.defineSelectAllowedAction(
					productOffId,
					dtoList
			);


		}

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
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductOffConstants.ALLOWED_ACTION + "-" + characteristicIndex);
		return characteristicList;

	}
}
