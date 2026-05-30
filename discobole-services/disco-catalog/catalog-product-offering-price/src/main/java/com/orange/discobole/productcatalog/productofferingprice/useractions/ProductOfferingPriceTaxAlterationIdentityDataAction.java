// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions;

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
import com.orange.discobole.productcatalog.productofferingprice.mapper.ProductOfferingPriceTaxAlterationIdentityDataMapper;
import com.orange.discobole.productcatalog.productofferingprice.mapper.ValidProductOfferingPriceMapper;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineTaxProductOfferingPriceAlterationIdentity;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Money;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.FileUtil;
import com.orange.discobole.productcatalog.productofferingprice.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * The Class ProductOfferingPriceAlterationIdentityDataAction provides the user
 * input to define alteration in price for product offering price.
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Component("ProductOfferingPriceCreation.defineProductOfferingPriceTaxAlterationIdentityData")
public class ProductOfferingPriceTaxAlterationIdentityDataAction implements UserAction {

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductOfferingPriceService productOfferingPriceService;

	private List<CharacteristicSpecification> characteristicSpecifications;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicSpecifications = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productofferingprice/"
					+ DefineTaxProductOfferingPriceAlterationIdentity.class.getSimpleName() + ".json");
			Object identityData = null;
			identityData = objectMapper.readValue(file, Object.class);
			characteristicSpecifications.add(new CharacteristicSpecification()
					.name(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_TAX_ALTERATION_IDENTITY)
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
		DefineTaxProductOfferingPriceAlterationIdentity priceTaxAlterationIdentityData = new DefineTaxProductOfferingPriceAlterationIdentity();
		String productOfferingPriceId = stateMachine.getVariablesFromUserActions()
				.get(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID).toString();

		Characteristic identityData = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_TAX_ALTERATION_IDENTITY,
				characteristicSpecifications);
		if (null != identityData) {
			LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) identityData.getValue();

			priceTaxAlterationIdentityData = (DefineTaxProductOfferingPriceAlterationIdentity) ValidationUtil
					.validatePojo(value, "productOfferingPrice", "defineProductOfferingPriceTaxAlterationIdentityData");


			// ----- Extract fields -----
			Money priceCheck = priceTaxAlterationIdentityData.getDefinePOPTaxAlterationIdentityData().getPrice();
			Float percentageCheck = priceTaxAlterationIdentityData.getDefinePOPTaxAlterationIdentityData().getPercentage();
			Object priceType = priceTaxAlterationIdentityData.getDefinePOPTaxAlterationIdentityData().getPriceAlterationType();



			boolean hasPrice = isPriceValid(priceCheck);
			boolean hasPercentage = (percentageCheck != null);






			validatePriceAndPercentage(hasPrice, hasPercentage, priceType);

			// ----- Cleanup -----
			if (PriceAlterationType.PRICE.toString().equals(priceType.toString())) {
				value.remove("percentage");
			} else {
				value.remove("price");
				Object percentage = null;
				percentage = value.get("percentage");

				if (!(priceTaxAlterationIdentityData.getDefinePOPTaxAlterationIdentityData().getPercentage() instanceof Number)) {
					throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_PERCENTAGE_VALUE, percentage.toString(), null);
				}

			}


		}
		productOfferingPriceService.defineProductOfferingPriceTaxAlterationIdentityData(productOfferingPriceId,
				ProductOfferingPriceTaxAlterationIdentityDataMapper
						.toGenerated(priceTaxAlterationIdentityData.getDefinePOPTaxAlterationIdentityData()),
				ValidProductOfferingPriceMapper
						.toGenerate(priceTaxAlterationIdentityData.getValidity()));
		return new HashMap<>();
	}

	private void validatePriceType(DefineTaxProductOfferingPriceAlterationIdentity priceTaxAlterationIdentityData)
	{
		Object priceType = priceTaxAlterationIdentityData.getDefinePOPTaxAlterationIdentityData().getPriceAlterationType();
		if (null == PriceAlterationType.fromValue(priceType.toString())) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPATYPE_VALUE, priceType.toString(), null);
		}
	}

	private boolean isPriceValid(Money priceCheck) {
		if (priceCheck == null) {
			return false;
		}
		String unit = priceCheck.getUnit();
		Float valueObj = priceCheck.getValue();
		return (unit != null && !unit.trim().isEmpty()) && (valueObj != null);
	}
	private void validatePriceAndPercentage(boolean hasPrice, boolean hasPercentage, Object priceType)
	{
		// ----- Validation rules -----

		if (!hasPrice && !hasPercentage) {
			//  Exception 2: Neither price nor percentage defined
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_NEITHER_PRICE_PERCENTAGE_DEFINED);
		}
		// Case 3: Type = PRICE but price missing
		if (PriceAlterationType.PRICE.getValue().equals(priceType.toString()) && (!hasPrice || hasPercentage)) {
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISCO_POP_BOTH_PRICE_PERCENTAGE_DEFINED);

		}
//			 Case 4: Type = PERCENTAGE but percentage missing
		if (PriceAlterationType.PERCENTAGE.getValue().equals(priceType.toString()) && (!hasPercentage||hasPrice)) {
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISCO_POP_BOTH_PRICE_PERCENTAGE_DEFINED
			);
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
						+ ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_TAX_ALTERATION_IDENTITY + "-"
						+ characteristicIndex);
		return characteristicSpecifications;
	}
}
