// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions;

import java.util.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.FileUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.ValidationUtil;

/**
 * The Class ProductOfferingPriceCancelAction provides the user
 * input to cancel creation ofproduct offering price at any step.
 * 
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Component("ProductOfferingPriceCreation.productOfferingPriceCancel")
public class ProductOfferingPriceCancelAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingPriceCancelAction.class);

	@Resource
	private ProductOfferingPriceService productOfferingPriceService;

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
			String file = FileUtil
					.read("/schemas/" + CancelEntityOperation.class.getSimpleName() + ".json");
			Object cancelData = null;
			cancelData = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification()
					.name(ProductOfferingPriceConstants.CANCEL_PRODUCT_OFFERING_PRICE)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(cancelData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}

	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In process method of ProductOfferingPriceCancelAction");
		CancelEntityOperation cancelData = null;
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOfferingPriceConstants.CANCEL_PRODUCT_OFFERING_PRICE, characteristicList);
		if (null != characteristic) {
			cancelData = (CancelEntityOperation) ValidationUtil.validatePojo(characteristic.getValue(),
					"productOfferingPrice", "cancelEntityOperation");
			if (cancelData.isIsCancelled().equals(Boolean.TRUE)) {
				String productOfferingPriceId = (String) stateMachine.getVariablesFromUserActions()
						.get(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID);
				if (productOfferingPriceId != null) {
					productOfferingPriceService.cancelProductOfferingPrice(productOfferingPriceId);
				}
			} 
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
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOfferingPriceConstants.CANCEL_PRODUCT_OFFERING_PRICE + "-" + characteristicIndex);
		return characteristicList;
	}
}
