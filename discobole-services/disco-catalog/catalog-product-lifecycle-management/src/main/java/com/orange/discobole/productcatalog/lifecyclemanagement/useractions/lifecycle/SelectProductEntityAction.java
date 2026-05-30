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

import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.LifeCycleConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.exception.DiscoClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.SelectLifecycleEntity;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.LifeCycleService;
import com.orange.discobole.productcatalog.lifecyclemanagement.util.CharacteristicUtil;
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
 * User action to select the entity id and type during lifecycle process.
 * 
 * @author Vivek Singh
 *
 */
@Component("ManageEntityLifecycle.selectProductEntity")
public class SelectProductEntityAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(SelectProductEntityAction.class);

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private LifeCycleService lifeCycleService;

	private List<CharacteristicSpecification> characteristicList;
	private static final String DISO_LS_INVALID_ENTITY_TYPE = "DISO_LS_INVALID_ENTITY_TYPE";

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + SelectLifecycleEntity.class.getSimpleName() + ".json");
			Object supportEntityData = null;
			supportEntityData = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(LifeCycleConstants.PRODUCT_ENTITY)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(supportEntityData)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In perform method of SelectProductEntityAction");
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				LifeCycleConstants.PRODUCT_ENTITY, characteristicList);

		SelectLifecycleEntity lifeCycleEntityData;
		try {
			lifeCycleEntityData = objectMapper.readValue(objectMapper.writeValueAsString(characteristics.getValue()),
					SelectLifecycleEntity.class);
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
		if (null == lifeCycleEntityData.getEntityType()) {
			throw new DiscoManagedClientException(DISO_LS_INVALID_ENTITY_TYPE);	}


		String aggregateId = lifeCycleService.selectEntity(lifeCycleEntityData.getId(),
				lifeCycleEntityData.getEntityType().toString());

		final List<Characteristic> characteristic = new ArrayList<>();
		characteristic
				.add(new StringCharacteristic().value(lifeCycleEntityData.getId()).name(LifeCycleConstants.ENTITY_ID)
						.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
		characteristic.add(new StringCharacteristic().value(lifeCycleEntityData.getEntityType().toString())
				.name(LifeCycleConstants.ENTITY_TYPE).valueType(String.class.getSimpleName())
				.type(StringCharacteristic.class.getSimpleName()));
		final Map<String, Object> variables = new HashMap<>();
		variables.put(LifeCycleConstants.ENTITY_ID, lifeCycleEntityData.getId());
		variables.put(LifeCycleConstants.ENTITY_TYPE, lifeCycleEntityData.getEntityType());
		variables.put(LifeCycleConstants.ENTITY_AGG, aggregateId);
		variables.put(TaskConstants.CHARACTERISTIC, characteristic);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ LifeCycleConstants.PRODUCT_ENTITY + "-" + characteristicIndex);
		return characteristicList;
	}

}
