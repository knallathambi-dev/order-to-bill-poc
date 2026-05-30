// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.orange.discobole.processflow.exception.DiscoManagedClientException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.AssociatePOPtoOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

/**
 * The Class ModifyPOAssociatePOPtoOperationSpecificationAction modifies association of
 * product offering price to operation specification.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Component("ProductOfferingModification.modifyPOAssociatePOPtoOperationSpecData")
public class ModifyPOAssociatePOPtoOperationSpecificationAction implements UserAction {

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private QueryService queryService;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read(
					"/schemas/productoffering/" + AssociatePOPtoOperationSpecification.class.getSimpleName() + ".json");
			Object linkPopToProdOffering = null;
			linkPopToProdOffering = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification()
					.name(ProductOffConstants.LINKPOPTOATOMICPRODOFFERING).valueType(List.class.getSimpleName())
					.minCardinality(0).maxCardinality(1).characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(linkPopToProdOffering)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate) {

		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		ProductOffering productOffering=queryService.fetchProductOfferingById(productOffId, null);
		// only lifecycle status can be modified in the launched state
		if(productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.LAUNCHED.getValue()) ||
				productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.RETIRED.getValue())){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE, "you can not modify entity with launched or retired status", "");
		}
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.LINKPOPTOATOMICPRODOFFERING, characteristicList);
		List<AssociatePOPtoOperationSpecification> operationSpec = null;
		List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList = new ArrayList<>();

		if (null != characteristic) {
			operationSpec = (List<AssociatePOPtoOperationSpecification>) (Object) ValidationUtil.validateArrayOfPojo(
					characteristic.getValue(), "productOffering", "associatePOPtoOperationSpecification");
			for (AssociatePOPtoOperationSpecification value : operationSpec) {
				AssociatePOPtoOperationSpec associatePOPtoOperationSpec = new AssociatePOPtoOperationSpec();
				associatePOPtoOperationSpec.setOperationSpecId(value.getOperationspecid());
				associatePOPtoOperationSpec.setProductOfferingPriceId(value.getProductofferingpriceid());
				associatePOPtoOperationSpec.setProductOfferingTerm(value.getProductOfferingTerm());
				associatePOPtoOperationSpecList.add(associatePOPtoOperationSpec);
			}
		}
		productOfferingService.modifyAssociatePOPtoOperationSpecification(associatePOPtoOperationSpecList,
				productOffId);
		return new HashMap<>();
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.LINKPOPTOATOMICPRODOFFERING + "-" + characteristicIndex);
		return characteristicList;
	}

}
