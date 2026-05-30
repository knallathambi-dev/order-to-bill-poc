// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.mapper.ProductOfferingPriceChargeIdentityDataMapper;
import com.orange.discobole.productcatalog.productofferingprice.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productofferingprice.mapper.ValidProductOfferingPriceMapper;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentity;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineRelationship;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.FileUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The Class ProductOfferingPriceChargeIdentityDataAction provides the user
 * input to define price charge for product offering price.
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Component("ProductOfferingPriceCreation.defineProductOfferingPriceChargeIdentityData")
public class ProductOfferingPriceChargeIdentityDataAction implements UserAction {

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductOfferingPriceService productOfferingPriceService;

	List<CharacteristicSpecification> characteristicSpecifications;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicSpecifications = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productofferingprice/"
					+ DefineProductOfferingPriceChargeIdentity.class.getSimpleName() + ".json");
			Object identityData = null;
			identityData = objectMapper.readValue(file, Object.class);
			characteristicSpecifications.add(new CharacteristicSpecification()
					.name(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_CHARGE_IDENTITY)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(identityData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		DefineProductOfferingPriceChargeIdentity priceChargeIdentityData = new DefineProductOfferingPriceChargeIdentity();
		String productOfferingPriceId = stateMachine.getVariablesFromUserActions()
				.get(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID).toString();
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_CHARGE_IDENTITY,
				characteristicSpecifications);
		List<ProductOfferingPriceRelationship> relationships = new ArrayList<>();
		if (null != characteristic) {
			priceChargeIdentityData = (DefineProductOfferingPriceChargeIdentity) ValidationUtil.validatePojo(
					characteristic.getValue(), "productOfferingPrice", "defineProductOfferingPriceChargeIdentity");
		

		for (DefineRelationship rel : priceChargeIdentityData.getRelationships()) {
			ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship();
			productOfferingPriceRelationship.id(rel.getId()).relationshipType(rel.getRelationshipType())
					.validFor(TimePeriodMapper.toGenerated(rel.getValidFor()));
			relationships.add(productOfferingPriceRelationship);
		}
		
	}
	productOfferingPriceService.defineProductOfferingPriceChargeIdentityData(productOfferingPriceId,
			ProductOfferingPriceChargeIdentityDataMapper
					.toGenerated(priceChargeIdentityData.getDefinePOPChargeIdentityData()),
			relationships,
			ValidProductOfferingPriceMapper.toGenerate(priceChargeIdentityData.getDefinePOPStatusValidityPeriod()));
	return new HashMap<>();
}

	/**
	 * Required characteristics.
	 *
	 * @param stateMachine the stateMachine
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicSpecifications.get(characteristicIndex)
				.id(stateMachineTransition.getTaskDefinitionId() + "-"
						+ ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_CHARGE_IDENTITY + "-"
						+ characteristicIndex);
		return characteristicSpecifications;
	}
}
