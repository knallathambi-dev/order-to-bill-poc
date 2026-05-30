// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions.productOfferingPrice;

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
import com.orange.discobole.productcatalog.productofferingprice.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.useractions.ProductOfferingPriceCancelAction;

class ProductOfferingPriceCancelActionTest extends ProductOfferingPriceApplicationTests {

	@InjectMocks
	private ProductOfferingPriceCancelAction productOfferingPriceCancelAction;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private ProductOfferingPriceService productOfferingPriceService;

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private ObjectMapper objectMapper;

	String productOfferingPriceId;

	List<CharacteristicSpecification> characteristicList;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(productOfferingPriceCancelAction, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(productOfferingPriceCancelAction, "productOfferingPriceService",
				productOfferingPriceService);
		productOfferingPriceCancelAction.init();
	}

	@Test
	void performTest() {
		productOfferingPriceId = UUID.randomUUID().toString();
		Map<String, Object> userVariablesAction = new HashMap<>();
		userVariablesAction.put(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID, productOfferingPriceId);
		Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn(userVariablesAction);

		List<Characteristic> generatedCharacteristicList = new ArrayList<>();
		CancelEntityOperation cancelEntityOperation = new CancelEntityOperation();
		cancelEntityOperation.setIsCancelled(Boolean.TRUE);
		Characteristic characteristic = new ObjectCharacteristic().value(cancelEntityOperation)
				.name(ProductOfferingPriceConstants.CANCEL_PRODUCT_OFFERING_PRICE).type("ObjectCharacteristic");
		generatedCharacteristicList.add(characteristic);
		Mockito.when(taskFlowUpdate.getCharacteristic()).thenReturn(generatedCharacteristicList);
		Map<String, Object> perform = productOfferingPriceCancelAction.perform(stateMachineTransition, taskFlowUpdate);
		Assertions.assertNotNull(perform);
		Assertions.assertEquals(0, perform.size());
	}

	@Test
	void requiredCharacteristicsTest() {
		characteristicList = productOfferingPriceCancelAction.requiredCharacteristics(stateMachineTransition, null);
		Assertions.assertNotNull(characteristicList);
		Assertions.assertEquals(1, characteristicList.size());
	}
}