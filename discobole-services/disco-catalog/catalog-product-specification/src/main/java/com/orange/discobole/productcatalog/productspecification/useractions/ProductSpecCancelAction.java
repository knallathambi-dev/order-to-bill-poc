// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import com.orange.discobole.productcatalog.productspecification.util.ValidationUtil;

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
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Component("ProductSpecCreation.cancel")
public class ProductSpecCancelAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecCancelAction.class);

	@Resource
	private ProductSpecService productSpecService;

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
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.PRODUCTSPEC_CANCEL)
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
		CancelEntityOperation cancelData = null;
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductSpecConstants.PRODUCTSPEC_CANCEL, characteristicList);
		if (null != characteristic) {
			cancelData = (CancelEntityOperation) ValidationUtil.validatePojo(characteristic.getValue(),
					"productSpecification",		"cancelEntityOperation");
			if (cancelData.isIsCancelled().equals(Boolean.TRUE)) {
				String productSpecId = (String) stateMachine.getVariablesFromUserActions()
						.get(ProductSpecConstants.PRODUCT_SPEC_ID);
				if (productSpecId != null) {
					productSpecService.cancelProductSpec(productSpecId);
				}
			} else {

			}
		}

		final Map<String, Object> variables = new HashMap<>();
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductSpecConstants.PRODUCTSPEC_CANCEL + "-" + characteristicIndex);
		return characteristicList;
	}
}
