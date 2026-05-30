// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.useractions.lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
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
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.LifeCycleConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.LifecycleState;
import com.orange.discobole.productcatalog.lifecyclemanagement.exception.DiscoClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.LifeCycleManager;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.DefineLifecycleState;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.LifeCycleService;
import com.orange.discobole.productcatalog.lifecyclemanagement.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.lifecyclemanagement.util.FileUtil;
import com.orange.discobole.productcatalog.lifecyclemanagement.util.ValidationUtil;

/**
 * User action to process the command of update lifecycle status.
 *
 * @author Vivek Singh
 *
 */
@Component("ManageEntityLifecycle.updateStatus")
public class UpdateStatusAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(UpdateStatusAction.class);

	private static final String DISO_LS_INVALID_LIFECYCLE_VALUE = "DISO_LS_INVALID_LIFECYCLE_VALUE";

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private LifeCycleService lifeCycleService;

	@Resource
	private LifeCycleManager lifeCycleManager;

	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;

	private List<CharacteristicSpecification> characteristicList;

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In perform method of UpdateStatusAction");
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				LifeCycleConstants.PRODUCT_UPDATE_STATE, characteristicList);
		String aggregateId = (String) stateMachine.getVariablesFromUserActions().get(LifeCycleConstants.ENTITY_AGG);

		DefineLifecycleState defineStateLifecycleData = null;
		if (null != characteristics) {
			defineStateLifecycleData = (DefineLifecycleState) ValidationUtil
					.validatePojo(characteristics.getValue(), "lifecycle", DefineLifecycleState.class.getSimpleName());
		}

		if (defineStateLifecycleData != null) {
			if (null == LifecycleState.fromValue(defineStateLifecycleData.getState().toString())) {
				throw new DiscoManagedClientException(DISO_LS_INVALID_LIFECYCLE_VALUE);
			}
String version = defineStateLifecycleData.getVersion();
			lifeCycleService.updateStatus(aggregateId, defineStateLifecycleData.getState(),version);
		}
		return new HashMap<>();
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		Map<String, Object> variables = stateMachine.getVariablesFromUserActions();
		String entityId = (String) variables.get(LifeCycleConstants.ENTITY_ID);
		EntityType entityType = (EntityType) variables.get(LifeCycleConstants.ENTITY_TYPE);
		Set<String> nextPossibleStates = lifeCycleManager.getNextPossibleStates(entityId, entityType, accessTokenInterceptor.getToken());
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + DefineLifecycleState.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			JSONArray enumStatesArray = new JSONArray(nextPossibleStates);
			jsonObject.getJSONObject("DefineLifecycleState").getJSONObject("properties").getJSONObject("state")
					.put("enum", enumStatesArray);
			Object supportEntityData = null;
			supportEntityData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(LifeCycleConstants.PRODUCT_UPDATE_STATE)
					.id(stateMachine.getTaskDefinitionId() + "-" + LifeCycleConstants.PRODUCT_UPDATE_STATE + "-"
							+ characteristicIndex)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(supportEntityData)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
		return characteristicList;
	}

}
