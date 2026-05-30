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
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPriceRelationship;
import com.orange.discobole.productcatalog.productofferingprice.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productofferingprice.mapper.ProductOfferingPriceInstallmentChargeIdentityDataMapper;
import com.orange.discobole.productcatalog.productofferingprice.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productofferingprice.mapper.ValidProductOfferingPriceMapper;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.*;
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
@Component("ProductOfferingPriceCreation.defineProductOfferingPriceInstallmentChargeIdentityData")
public class ProductOfferingPriceInstallmentChargeIdentityDataAction implements UserAction {

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
					+ DefineInstallmentChargeProductOfferingPriceIdentity.class.getSimpleName() + ".json");
			Object identityData = null;
			identityData = objectMapper.readValue(file, Object.class);
			characteristicSpecifications.add(new CharacteristicSpecification()
					.name(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_INSTALLMENT_CHARGE_IDENTITY)
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
		DefineInstallmentChargeProductOfferingPriceIdentity installmentChargeIdentityData = new DefineInstallmentChargeProductOfferingPriceIdentity();
		String productOfferingPriceId = stateMachine.getVariablesFromUserActions()
				.get(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID).toString();

		Characteristic identityData = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_INSTALLMENT_CHARGE_IDENTITY,
				characteristicSpecifications);
		List<ProductOfferingPriceRelationship> relationships = new ArrayList<>();
		if (null != identityData) {
			LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) identityData.getValue();

			installmentChargeIdentityData = (DefineInstallmentChargeProductOfferingPriceIdentity) ValidationUtil
					.validatePojo(value, "productOfferingPrice", "defineProductOfferingPriceInstallmentChargeIdentityData");

			for (DefineRelationship rel : installmentChargeIdentityData.getRelationships()) {
				ProductOfferingPriceRelationship productOfferingPriceRelationship = new ProductOfferingPriceRelationship();
				productOfferingPriceRelationship.id(rel.getId()).relationshipType(rel.getRelationshipType())
						.validFor(TimePeriodMapper.toGenerated(rel.getValidFor()));
				relationships.add(productOfferingPriceRelationship);
			}

			// ----- Extract fields -----
 Money priceCheck = installmentChargeIdentityData.getDefineProductOfferingPriceInstallmentChargeIdentityData().getPrice();

            Float interestRate =installmentChargeIdentityData.getDefineProductOfferingPriceInstallmentChargeIdentityData().getInterestRate();
			Float downPayment =installmentChargeIdentityData.getDefineProductOfferingPriceInstallmentChargeIdentityData().getDownPayment();






			validateRequiredFields(priceCheck,interestRate,downPayment);

		}
		productOfferingPriceService.defineProductOfferingPriceInstallmentPlanIdentityData(productOfferingPriceId,
				ProductOfferingPriceInstallmentChargeIdentityDataMapper
						.toGenerated(installmentChargeIdentityData.getDefineProductOfferingPriceInstallmentChargeIdentityData()),
				ValidProductOfferingPriceMapper
						.toGenerate(installmentChargeIdentityData.getValidity()),relationships);
		return new HashMap<>();
	}




	private void validateRequiredFields(Money price,Float interestRate,Float downPayment) {

		// PRICE validation
		if (price == null) {

			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_INSTALLMENTPLAN_POP_PRICE_NOT_DEFINED);
		}
		if (price.getUnit() == null || price.getUnit().trim().isEmpty()) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_INSTALLMENTPLAN_POP_PRICE_UNIT_NOT_DEFINED);
		}
		if (price.getValue() == null) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_INSTALLMENTPLAN_POP_PRICE_VALUE_NOT_DEFINED);
		}



		if (interestRate != null && 	(interestRate < 0 || interestRate > 100)) {



				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISCO_INSTALLMENTPLAN_INTEREST_RATE_INVALID

				);

		}

		// DOWNPAYMENT validation
		if (downPayment != null) {
			if (price == null || price.getValue() == null) {
				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISCO_INSTALLMENTPLAN_POP_PRICE_VALUE_NOT_DEFINED
				);
			}

			if (downPayment < 0) {
				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISCO_INSTALLMENTPLAN_DOWNPAYMENT_NEGATIVE
				);
			}

			if (downPayment >= price.getValue()) {
				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISCO_DOWNPAYMENT_SHOULD_LESS_THAN_PRICE_VALUE
				);
			}
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
						+ ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_INSTALLMENT_CHARGE_IDENTITY + "-"
						+ characteristicIndex);
		return characteristicSpecifications;
	}
}
