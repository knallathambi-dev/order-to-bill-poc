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
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.AllowedProductAction;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectAllowedActionData;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("ProductOfferingModification.modifySelectAllowedAction")
public class ModifyPOSelectAllowedAction implements UserAction{
	
	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private QueryService queryService;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil
					.read("/schemas/productoffering/" + SelectAllowedActionData.class.getSimpleName() + ".json");
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

	public Map<String, Object> perform(final StateMachineTransition stateMachineTransition,
									   final TaskFlowUpdate taskFlowUpdate) throws ParameterException {


		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		ProductOffering productOffering=queryService.fetchProductOfferingById(productOffId, null);
		// only lifecycle status can be modified in the launched state
		if(productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.LAUNCHED.getValue()) ||
				productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.RETIRED.getValue() )||
						productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.OBSOLETE.getValue())){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE, "you can not modify entity with launched,retired or obsolete status", "");
		}
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
			productOfferingService.modifySelectAllowedAction(
					productOffId,
					dtoList
			);


		}

		return new HashMap<>();
	}



	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.ALLOWED_ACTION + "-" + characteristicIndex);
		return characteristicList;
	}
}

