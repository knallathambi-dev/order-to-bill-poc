// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering;

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
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperationSpecificationIds;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperationSpecificationName;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineBundledOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
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
 * The Class DefineBundledOperationSpecificationAction provides the user input
 * to define bundled Operation Specification for product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingCreation.bundleddefOperation")
public class DefineBundledOperationSpecificationAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(DefineBundledOperationSpecificationAction.class);

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read(
					"/schemas/productoffering/" + DefineBundledOperationSpecification.class.getSimpleName() + ".json");
			Object operationSpec = null;
			operationSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.BUNDLED_OPERATION_SPEC)
					.valueType(List.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(operationSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In perform method of DefineBundledOperationSpecificationAction");
		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		Characteristic prodOffOpChar = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.BUNDLED_OPERATION_SPEC, characteristicList);
		List<CommercialOperation> operationSpecifications = new ArrayList<>();
		List<DefineBundledOperationSpecification> operationSpec = null;
		if (null != prodOffOpChar) {
			operationSpec = (List<DefineBundledOperationSpecification>) (Object) ValidationUtil.validateArrayOfPojo(
					prodOffOpChar.getValue(), "productOffering", "defineBundledPOOperationSpecification");
			for (DefineBundledOperationSpecification value : operationSpec) {
				CommercialOperationSpecificationIds idIndex = CommercialOperationSpecificationIds.fromValue(value.getId().toString());
				if(idIndex==null){
					final String message = value.getId().toString();
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_OPERATION_ID, message, message);
				}
				CommercialOperationSpecificationName nameIndex = CommercialOperationSpecificationName.fromValue(value.getName().toString());
				if (nameIndex==null||idIndex.ordinal() != nameIndex.ordinal()) {
					final String message = value.getName().toString();
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_OPERATION_NAME, message, message);
				}
				CommercialOperation operationSpecValue = new CommercialOperation();
				operationSpecValue.id(value.getId().toString()).name(value.getName().toString())
						.description(value.getDescription())
						.validFor(TimePeriodMapper.toGenerated(value.getValidFor()));
				operationSpecifications.add(operationSpecValue);
			}
		}
		productOfferingService.defineBundledProductOfferingOperation(productOffId, operationSpecifications);
		return new HashMap<>();

	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.BUNDLED_OPERATION_SPEC + "-" + characteristicIndex);
		return characteristicList;
	}

}
