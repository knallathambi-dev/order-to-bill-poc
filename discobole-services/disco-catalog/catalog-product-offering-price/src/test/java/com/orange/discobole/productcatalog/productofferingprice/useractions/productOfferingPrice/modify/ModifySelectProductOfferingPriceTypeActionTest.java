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
import java.util.List;
import java.util.Map;

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
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.SelectPOPType;
import com.orange.discobole.productcatalog.productofferingprice.service.ModifyProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.useractions.modify.ModifySelectProductOfferingPriceTypeAction;

class ModifySelectProductOfferingPriceTypeActionTest extends ProductOfferingPriceApplicationTests {

	@InjectMocks
	private ModifySelectProductOfferingPriceTypeAction productOfferingPriceTypeAction;

	@Mock
	private ModifyProductOfferingPriceService productOfferingPriceService;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private ObjectMapper objectMapper;

	List<CharacteristicSpecification> characteristicSpecifications;

	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(productOfferingPriceTypeAction, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(productOfferingPriceTypeAction, "productOfferingPriceService", productOfferingPriceService);
		productOfferingPriceTypeAction.init();
	}

	@Test
	void performTest() {
		SelectPOPType selectPOPType=new SelectPOPType();
		selectPOPType.setpOPType(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE);
		List<Characteristic> characteristicList = new ArrayList<>();
		characteristicList
				.add(new ObjectCharacteristic().value(selectPOPType)
						.name(ProductOfferingPriceConstants.SELECT_POP_TYPE).type("ObjectCharacteristic"));

		Mockito.when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristicList);
		Map<String, Object> perform = productOfferingPriceTypeAction.perform(stateMachineTransition, taskFlowUpdate);
		Assertions.assertNotNull(perform);
		Assertions.assertEquals(1, perform.size());
		Assertions.assertEquals(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.toString(),
				perform.get(ProductOfferingPriceConstants.SELECT_POP_TYPE));
	}

	@Test
	void requiredCharacteristicsTest() {
		characteristicSpecifications = productOfferingPriceTypeAction.requiredCharacteristics(stateMachineTransition, null);
		Assertions.assertNotNull(characteristicSpecifications);
		Assertions.assertEquals(1, characteristicSpecifications.size());
	}
}