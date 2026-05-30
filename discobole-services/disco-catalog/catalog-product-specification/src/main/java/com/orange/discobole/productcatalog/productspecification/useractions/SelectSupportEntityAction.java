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
import com.orange.discobole.processflow.constant.ProcessConstants;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectSupportEntity;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import com.orange.discobole.productcatalog.productspecification.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
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
@Component("ProductSpecCreation.selectSupportEntity")
public class SelectSupportEntityAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(SelectSupportEntityAction.class);

	private static final String DISCO_PS_INVALID_SUPPORT_ENTITY_TYPE = "DISCO_PS_INVALID_SUPPORT_ENTITY_TYPE";

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductSpecService productSpecService;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + SelectSupportEntity.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			JSONArray enumEntityArray = new JSONArray(List.of(SupportEntity.CFSSPEC.toString(),
					SupportEntity.STOCKITEMTYPE.toString()));
			jsonObject.getJSONObject("SelectSupportEntity").getJSONObject("properties")
					.getJSONObject("supportEntityType").put("enum", enumEntityArray);
			Object supportEntityData = null;
			supportEntityData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.SUPPORT_ENTITY)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(supportEntityData)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		SelectSupportEntity supportEntityData = new SelectSupportEntity();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductSpecConstants.SUPPORT_ENTITY, characteristicList);
		if (null != characteristics) {
			supportEntityData = (SelectSupportEntity) ValidationUtil.validatePojo(characteristics.getValue(),
					"productSpecification", "selectSupportEntity");
			if (null == SupportEntity.fromValue(supportEntityData.getSupportEntityType().toString())) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_SUPPORT_ENTITY_TYPE, characteristics.getValue().toString(), null);
			}
		}
		final Map<String, Object> variables = new HashMap<>();
		if (supportEntityData.getSupportEntityType().equals(SupportEntity.CFSSPEC)) {
			String serviceSpecId = supportEntityData.getId();
			String productSpecId = productSpecService.initiateProductSpecCreation(serviceSpecId);
			final List<Characteristic> characteristic = new ArrayList<>();
			characteristic
					.add(new StringCharacteristic().value(serviceSpecId).name(ProductSpecConstants.SERVICE_SPEC_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
			characteristic
					.add(new StringCharacteristic().value(productSpecId).name(ProductSpecConstants.PRODUCT_SPEC_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
			variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, productSpecId);
			variables.put(ProductSpecConstants.SERVICE_SPEC_ID, serviceSpecId);
			variables.put(TaskConstants.CHARACTERISTIC, characteristic);
			final List<RelatedEntity> relatedEntityList = new ArrayList<>();
			relatedEntityList.add(new RelatedEntity().name(ProductSpecConstants.PRODUCT_SPEC_ID).id(productSpecId));
			variables.put(ProcessConstants.RELATED_ENTITY, relatedEntityList);
		}
		else if(supportEntityData.getSupportEntityType().equals(SupportEntity.STOCKITEMTYPE)){
			String stockItemId = supportEntityData.getId();
			String productSpecId = productSpecService.initiateStockItemProductSpecCreation(stockItemId);
			final List<Characteristic> characteristic = new ArrayList<>();
			characteristic
					.add(new StringCharacteristic().value(stockItemId).name(ProductSpecConstants.STOCK_ITEM_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
			characteristic
					.add(new StringCharacteristic().value(productSpecId).name(ProductSpecConstants.PRODUCT_SPEC_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
			variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, productSpecId);
			variables.put(ProductSpecConstants.STOCK_ITEM_ID, stockItemId);
			variables.put(TaskConstants.CHARACTERISTIC, characteristic);
			final List<RelatedEntity> relatedEntityList = new ArrayList<>();
			relatedEntityList.add(new RelatedEntity().name(ProductSpecConstants.PRODUCT_SPEC_ID).id(productSpecId));
			variables.put(ProcessConstants.RELATED_ENTITY, relatedEntityList);
		}
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductSpecConstants.SUPPORT_ENTITY + "-" + characteristicIndex);
		return characteristicList;
	}

}
