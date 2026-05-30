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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectSupportEntity;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class ProductOffCreationAction to initiate creation of product offering.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@Component("ProductOfferingCreation.creation")
public class ProductOffCreationAction implements UserAction {

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ConfigurableProperties configurableProperties;

	/**
	 * Inits the characteristics with initial values.
	 */
	@PostConstruct
	public void init() {

		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + SelectSupportEntity.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			JSONArray enumEntityArray = new JSONArray(List.of(SupportEntity.PRODUCTSPEC.toString()));
			jsonObject.getJSONObject("SelectSupportEntity").getJSONObject("properties")
					.getJSONObject("supportEntityType").put("enum", enumEntityArray);
			Object supportEntityData = null;
			supportEntityData = objectMapper.readValue(jsonObject.toString(), Object.class);			
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.SUPPORT_ENTITY)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(supportEntityData)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method initiates the creation of product offering when user selects a
	 * product specification.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map
	 */
	@Override
	public Map<String, Object> perform(final StateMachineTransition stateMachineTransition,
			final TaskFlowUpdate taskFlowUpdate) {
		SelectSupportEntity supportEntityData = new SelectSupportEntity();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.SUPPORT_ENTITY, characteristicList);
		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		if (null != characteristics) {
			supportEntityData = (SelectSupportEntity) ValidationUtil.validatePojo(characteristics.getValue(),
					"productOffering", "selectSupportEntity");
			if (null == SupportEntity.fromValue(supportEntityData.getSupportEntityType().toString())) {
				final String message = characteristics.getValue().toString();
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_SUPPORT_ENTITY_TYPE,message,null);
			}
		}
		final Map<String, Object> variables = new HashMap<>();
		if (SupportEntity.PRODUCTSPEC == supportEntityData.getSupportEntityType()) {
			String productSpecId = supportEntityData.getId();
			productOfferingService.createProductOffering(productSpecId, productOffId);
			final List<Characteristic> characteristic = new ArrayList<>();
			characteristic
					.add(new StringCharacteristic().value(productSpecId).name(ProductSpecConstants.PRODUCT_SPEC_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
			variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, productSpecId);
			variables.put(TaskConstants.CHARACTERISTIC, characteristic);
		}
		return variables;
	}

	/**
	 * Required characteristics.
	 *
	 * @param stateMachine the stateMachine
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(final StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductOffConstants.SUPPORT_ENTITY + "-" + characteristicIndex);
		return characteristicList;
	}
}
