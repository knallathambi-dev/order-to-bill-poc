// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions.productOfferingPrice.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productofferingprice.ProductOfferingPriceApplicationTests;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.pojo.ValidateEntityOperation;
import com.orange.discobole.productcatalog.productofferingprice.service.ModifyProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.useractions.modify.ValidatePOPModificationAction;

class ValidatePOPModificationActionTest extends ProductOfferingPriceApplicationTests {

	@InjectMocks
	private ValidatePOPModificationAction validatePOPModificationAction;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private ModifyProductOfferingPriceService productOfferingPriceService;

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private ObjectMapper objectMapper;

	List<CharacteristicSpecification> characteristicList;
	String productOfferingPriceId;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(validatePOPModificationAction, "productOfferingPriceService", productOfferingPriceService);
		ReflectionTestUtils.setField(validatePOPModificationAction, "objectMapper", objectMapper);
		validatePOPModificationAction.init();
	}

	@Test
	void performTest() {
		productOfferingPriceId = UUID.randomUUID().toString();
		Map<String, Object> userVariablesAction = new HashMap<>();
		userVariablesAction.put(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID, productOfferingPriceId);
		Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn(userVariablesAction);

		List<Characteristic> generatedCharacteristicList = new ArrayList<>();
		ValidateEntityOperation validateEntityOperation = new ValidateEntityOperation();
		validateEntityOperation.setValidated(true);

		Characteristic characteristic = new ObjectCharacteristic().value(validateEntityOperation)
				.name(ProductOfferingPriceConstants.VALIDATE).type("ObjectCharacteristic");
		generatedCharacteristicList.add(characteristic);
		Mockito.when(taskFlowUpdate.getCharacteristic()).thenReturn(generatedCharacteristicList);
		Map<String, Object> perform = validatePOPModificationAction.perform(stateMachineTransition, taskFlowUpdate);
		Assertions.assertNotNull(perform);
		Assertions.assertEquals(1, perform.size());
	}

	@Test
	void requiredCharacteristicsTest() {
		characteristicList = validatePOPModificationAction.requiredCharacteristics(stateMachineTransition, null);
		Assertions.assertNotNull(characteristicList);
		Assertions.assertEquals(1, characteristicList.size());
	}
}
