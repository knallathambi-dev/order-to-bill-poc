// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.useractions.lifecycle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.LifeCycleConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.exception.DiscoClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.lifecyclemanagement.util.FileUtil;

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
 * User action to cancel the lifecycle process.
 * 
 * @author Ankur Singh
 *
 */
@Component("ManageEntityLifecycle.lifecycleCancel")
public class LifeCycleCancelAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(LifeCycleCancelAction.class);

	/**
	 * 
	 */
	@Resource
	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		// To define static characteristics
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In perform method of LifeCycleCancelAction");
		return new HashMap<>();

	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		List<CharacteristicSpecification> characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + CancelEntityOperation.class.getSimpleName() + ".json");
			Object cancelData = null;
			cancelData = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification()
					.id(stateMachineTransition.getTaskDefinitionId() + "-" + LifeCycleConstants.LIFECYCLE_CANCEL + "-"
							+ characteristicIndex)
					.name(LifeCycleConstants.LIFECYCLE_CANCEL).valueType(Object.class.getSimpleName()).minCardinality(1)
					.maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(cancelData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
		return characteristicList;
	}

}
