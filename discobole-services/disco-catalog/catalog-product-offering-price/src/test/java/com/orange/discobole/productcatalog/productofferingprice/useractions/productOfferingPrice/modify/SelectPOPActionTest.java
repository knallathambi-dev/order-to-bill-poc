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

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
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
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.SelectPOP;
import com.orange.discobole.productcatalog.productofferingprice.service.ModifyProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;
import com.orange.discobole.productcatalog.productofferingprice.useractions.modify.SelectPOPAction;

class SelectPOPActionTest extends ProductOfferingPriceApplicationTests {

	@InjectMocks
	private SelectPOPAction selectPOPAction;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private ModifyProductOfferingPriceService productOfferingPriceService;

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private ObjectMapper objectMapper;
	
	@Mock
	private QueryService queryService;

	List<CharacteristicSpecification> characteristicList;
	String productOfferingPriceId;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(selectPOPAction, "popService", productOfferingPriceService);
		ReflectionTestUtils.setField(selectPOPAction, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(selectPOPAction, "queryService", queryService);
		selectPOPAction.init();
	}

	@Test
	void performTest() {
		productOfferingPriceId = UUID.randomUUID().toString();
		Map<String, Object> userVariablesAction = new HashMap<>();
		userVariablesAction.put(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID, productOfferingPriceId);
		Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn(userVariablesAction);

		List<Characteristic> generatedCharacteristicList = new ArrayList<>();
		SelectPOP selectPOP = new SelectPOP();
		selectPOP.setId(productOfferingPriceId);

		Characteristic characteristic = new ObjectCharacteristic().value(selectPOP)
				.name(ProductOfferingPriceConstants.SELECT_PRODUCT_OFFERING_PRICE).type("ObjectCharacteristic");
		generatedCharacteristicList.add(characteristic);
		Mockito.when(taskFlowUpdate.getCharacteristic()).thenReturn(generatedCharacteristicList);
		Mockito.when(queryService.getProductOfferingPrice(productOfferingPriceId)).
				thenReturn(new ProductOfferingPrice().id(productOfferingPriceId).lifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED));
		try {
			Map<String, Object> perform = selectPOPAction.perform(stateMachineTransition, taskFlowUpdate);
			Assertions.assertNotNull(perform);
			Assertions.assertEquals(4, perform.size());
		}catch (Exception exception){
			Assertions.assertInstanceOf(DiscoManagedClientException.class, exception);
		}
	}

	@Test
	void requiredCharacteristicsTest() {
		characteristicList = selectPOPAction.requiredCharacteristics(stateMachineTransition, null);
		Assertions.assertNotNull(characteristicList);
		Assertions.assertEquals(1, characteristicList.size());
	}
}
