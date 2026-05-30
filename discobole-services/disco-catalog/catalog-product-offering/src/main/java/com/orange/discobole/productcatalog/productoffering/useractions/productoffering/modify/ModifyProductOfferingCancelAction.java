// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class ProductOfferingCancelAction provides the user input to cancel
 * creation of product offering at any step.
 * 
 * @author Vishal Vachaspati
 * @since 1.0
 *
 */
@Component("ProductOfferingModification.productOfferingModificationCancel")
public class ModifyProductOfferingCancelAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingCancelAction.class);

	@Resource
	private ModifyProductOfferingService productOffService;

	@Resource
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + CancelEntityOperation.class.getSimpleName() + ".json");
			Object cancelData = null;
			cancelData = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.PRODUCTOFF_CANCEL)
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
		LOGGER.info("ModifyProductOfferingCancelAction perform method");
		CancelEntityOperation cancelData = null;
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.PRODUCTOFF_CANCEL, characteristicList);
		if (null != characteristic) {
			cancelData = (CancelEntityOperation) ValidationUtil.validatePojo(characteristic.getValue(),
					"productOffering", "cancelEntityOperation");
			if (cancelData.isIsCancelled().equals(Boolean.TRUE)) {
				String productOffId = (String) stateMachine.getVariablesFromUserActions()
						.get(ProductOffConstants.PRODUCT_OFF_ID);
				if (productOffId != null) {
					productOffService.cancelProductOfferingModification(productOffId);
				}
			}
		}

		return new HashMap<>();
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.PRODUCTOFF_CANCEL + "-" + characteristicIndex);
		return characteristicList;
	}
}
