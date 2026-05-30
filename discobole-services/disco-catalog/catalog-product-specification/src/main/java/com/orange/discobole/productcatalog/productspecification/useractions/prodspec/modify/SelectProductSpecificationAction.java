// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions.prodspec.modify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectProductSpecification;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import com.orange.discobole.productcatalog.productspecification.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("ProductSpecModification.selectProductSpecification")
public class SelectProductSpecificationAction implements UserAction {



	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductSpecService productSpecService;

	@Resource
	private QueryService queryService;
	
	private List<CharacteristicSpecification> characteristicList;
	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + SelectProductSpecification.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			Object selectPSData = null;
			selectPSData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.SELECT_PRODSPEC)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(selectPSData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		SelectProductSpecification selectPSData = new SelectProductSpecification();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductSpecConstants.SELECT_PRODSPEC, characteristicList);
		if (null != characteristics) {
			selectPSData = (SelectProductSpecification) ValidationUtil.validatePojo(characteristics.getValue(),
					"productSpecification", "selectProductSpecification");
		}
		final Map<String, Object> variables = new HashMap<>();
		String productSpecId = selectPSData.getId();
		ProductSpecification productSpec=queryService.fetchProductSpecById(productSpecId, accessTokenInterceptor.getToken());
		productSpecService.initiateProductSpecModification(productSpecId);
		final List<Characteristic> characteristic = new ArrayList<>();
		characteristic.add(new StringCharacteristic().value(productSpecId).name(ProductSpecConstants.PRODUCT_SPEC_ID)
				.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()));
		if(productSpec.getSupportEntity().equals(SupportEntity.STOCKITEMTYPE)){
			variables.put(ProductSpecConstants.STOCK_ITEM_ID, productSpec.getStockItemType().getId());
		}
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, productSpecId);
		variables.put(ProductSpecConstants.SELECT_PRODSPEC_LIFECYCLE_STATUS,productSpec.getLifecycleStatus().toString());
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, productSpecId);
		variables.put(TaskConstants.CHARACTERISTIC, characteristic);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductSpecConstants.SELECT_PRODSPEC + "-" + characteristicIndex);
		return characteristicList;

	}

}
