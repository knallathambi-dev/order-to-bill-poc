// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.useractions.productOfferingPrice;

import java.time.OffsetDateTime;
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
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.pojo.TimePeriod;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.ApplicationDuration;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefinePOPStatusValidityPeriod;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentity;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Quantity;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.useractions.ProductOfferingPriceAlterationIdentityDataAction;

class ProductOfferingPriceAlterationIdentityDataActionTest extends ProductOfferingPriceApplicationTests {

	@InjectMocks
	private ProductOfferingPriceAlterationIdentityDataAction productOfferingPriceAlterationIdentityDataAction;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private ProductOfferingPriceService productOfferingPriceService;

	@Mock
	private TaskFlowUpdate taskFlowUpdate;

	@Mock
	private ObjectMapper objectMapper;

	String productOfferingPriceId;

	List<CharacteristicSpecification> characteristicSpecifications;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(productOfferingPriceAlterationIdentityDataAction, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(productOfferingPriceAlterationIdentityDataAction, "productOfferingPriceService",
				productOfferingPriceService);
		productOfferingPriceAlterationIdentityDataAction.init();
	}

	@Test
	void performTest() {
		productOfferingPriceId = UUID.randomUUID().toString();
		Map<String, Object> userVariablesAction = new HashMap<>();
		userVariablesAction.put(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ID, productOfferingPriceId);
		Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn(userVariablesAction);

		List<Characteristic> characteristicList = new ArrayList<>();
		DefineProductOfferingPriceAlterationIdentity alteration = new DefineProductOfferingPriceAlterationIdentity();
		DefineProductOfferingPriceAlterationIdentityData identityData = new DefineProductOfferingPriceAlterationIdentityData();
		identityData.setName("POPA");
		identityData.setDescription("POP alteration");
		identityData.setPriceType(PriceType.RC);
		identityData.setPriceAlterationType(PriceAlterationType.PERCENTAGE);
		identityData.setApplicationDuration(new ApplicationDuration().amount(10).units("hour"));
		identityData.setPriority(1);
		identityData.setPercentage(51.0f);
		//identityData.setPrice(new Money().value(100.0f).unit("inr"));
		identityData.setUnitOfMeasure(new Quantity().amount(1.0f).units("inr"));
		
		
		TimePeriod validFor = new TimePeriod().startDateTime(OffsetDateTime.now());
		DefinePOPStatusValidityPeriod popStatusValidityPeriod = new DefinePOPStatusValidityPeriod();
		popStatusValidityPeriod.setLifecycleStatus(ProductOfferingPriceLifecycle.LAUNCHED);
		popStatusValidityPeriod.setValidFor(validFor);
		
		alteration.setDefinePOPAlterationIdentityData(identityData);
		alteration.setValidity(popStatusValidityPeriod);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.convertValue(alteration, Map.class);
		Characteristic characteristic = new ObjectCharacteristic().value(map)
				.name(ProductOfferingPriceConstants.PRODUCT_OFFERING_PRICE_ALTERATION_IDENTITY)
				.type("ObjectCharacteristic");
		characteristicList.add(characteristic);
		Mockito.when(taskFlowUpdate.getCharacteristic()).thenReturn(characteristicList);
		Map<String, Object> perform = productOfferingPriceAlterationIdentityDataAction.perform(stateMachineTransition,
				taskFlowUpdate);
		Assertions.assertNotNull(perform);
		Assertions.assertEquals(0, perform.size());

	}

	@Test
	void requiredCharacteristicsTest() {
		characteristicSpecifications = productOfferingPriceAlterationIdentityDataAction
				.requiredCharacteristics(stateMachineTransition, null);
		Assertions.assertNotNull(characteristicSpecifications);
		Assertions.assertEquals(1, characteristicSpecifications.size());
	}
}
