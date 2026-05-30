// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.mapper.ProductOfferingPriceAlterationIdentityDataMapper;
import com.orange.discobole.productcatalog.productofferingprice.mapper.ValidProductOfferingPriceMapper;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentity;
import com.orange.discobole.productcatalog.productofferingprice.service.ModifyProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.FileUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * The Class ProductOfferingPriceAlterationIdentityDataAction provides the user
 * input to define alteration in price for product offering price.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
@Component("ProductOfferingPriceModification.modifyPOPAlterationIdentityData")
public class ModifyPOPAlterationIdentityDataAction implements UserAction {

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ModifyProductOfferingPriceService productOfferingPriceService;

	List<CharacteristicSpecification> characteristicSpecifications;

	@PostConstruct
	public void init() {
		characteristicSpecifications = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productofferingprice/"
					+ DefineProductOfferingPriceAlterationIdentity.class.getSimpleName() + ".json");
			Object identityData = null;
			identityData = objectMapper.readValue(file, Object.class);
			characteristicSpecifications.add(new CharacteristicSpecification()
					.name(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ALTERATION_IDENTITY)
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
	    DefineProductOfferingPriceAlterationIdentity priceAlterationIdentityData = new DefineProductOfferingPriceAlterationIdentity();
	    String productOfferingPriceId = stateMachine.getVariablesFromUserActions()
	            .get(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID).toString();

	    Characteristic identityData = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
	            ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ALTERATION_IDENTITY,
	            characteristicSpecifications);

	    if (identityData != null) {
	        LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) identityData.getValue();
	        priceAlterationIdentityData = (DefineProductOfferingPriceAlterationIdentity) ValidationUtil
	                .validatePojo(value, "productOfferingPrice", "defineProductOfferingPriceAlterationIdentityData");

	        Object priceType = priceAlterationIdentityData.getDefinePOPAlterationIdentityData().getPriceAlterationType();
	        validatePriceAlterationType(priceType);

	        Object priceCheck = priceAlterationIdentityData.getDefinePOPAlterationIdentityData().getPrice();
	        Object percentageCheck = priceAlterationIdentityData.getDefinePOPAlterationIdentityData().getPercentage();

	        validatePriceAndPercentage(priceCheck, percentageCheck);

	        if (PriceAlterationType.PRICE.toString().equals(priceType.toString())) {
	            value.remove("percentage");
	        } else {
	            value.remove("price");
	            validatePercentage(priceAlterationIdentityData);
	        }
	    }

	    productOfferingPriceService.modifyPOPAlterationIdentityData(
	            productOfferingPriceId,
	            ProductOfferingPriceAlterationIdentityDataMapper
	                    .toGenerated(priceAlterationIdentityData.getDefinePOPAlterationIdentityData()),
	            ValidProductOfferingPriceMapper
	                    .toGenerate(priceAlterationIdentityData.getValidity()));
	    return new HashMap<>();
	}

	private void validatePriceAlterationType(Object priceType) {
	    if (null == PriceAlterationType.fromValue(priceType.toString())) {
	        final String message = priceType.toString();
	        throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPATYPE_VALUE, message, null);
	    }
	}

	private void validatePriceAndPercentage(Object priceCheck, Object percentageCheck) {
	    boolean hasPrice = priceCheck != null;
	    boolean hasPercentage = percentageCheck != null;

	    if (hasPrice && hasPercentage) {
	        throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POPA_BOTH_PRICE_PERCENTAGE_DEFINED);
	    }
	    if (!hasPrice && !hasPercentage) {
	        throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POPA_NEITHER_PRICE_PERCENTAGE_DEFINED);
	    }
	}

	private void validatePercentage(DefineProductOfferingPriceAlterationIdentity priceAlterationIdentityData) {
	    Object percentageObj = priceAlterationIdentityData.getDefinePOPAlterationIdentityData().getPercentage();
	    if (!(percentageObj instanceof Number)) {
	        throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_PERCENTAGE_VALUE, null, null);
	    }
	    double percent = ((Number) percentageObj).doubleValue();
	    if (percent < 0 || percent > 100) {
	        throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_PERCENTAGE_DEFINED_IN_RANGE);
	    }
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
						+ ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ALTERATION_IDENTITY + "-"
						+ characteristicIndex);
		return characteristicSpecifications;
	}
}
