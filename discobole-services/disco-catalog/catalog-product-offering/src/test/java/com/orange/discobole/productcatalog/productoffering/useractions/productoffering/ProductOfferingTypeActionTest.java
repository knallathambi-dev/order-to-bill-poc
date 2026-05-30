// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.useractions.productoffering.ProductOfferingTypeAction;

class ProductOfferingTypeActionTest extends ProductOfferingApplicationTests {

	@InjectMocks
	private ProductOfferingTypeAction productOfferingTypeAction;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private ProductOfferingService productOfferingService;
	
	private static String PRODUCTOFFID = "product_off_id";
	
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	void init() {
		ReflectionTestUtils.setField(productOfferingTypeAction, "objectMapper", objectMapper);
		productOfferingTypeAction.init();
	}
	
	@Test
	void performProductOfferingTypeWhenUserEntersValues() {
		SelectProductOfferingType offeringType = new SelectProductOfferingType();
		offeringType.setIsBundle(true);
		offeringType.setIsInstallable(true);
		offeringType.setIsSellable(true);
		offeringType.setProductOfferingType(ProductOfferingType.BUNDLEPRODUCTOFFERING);
		when(productOfferingService.createProductOfferingType(offeringType)).thenReturn("product_off_id");
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		ObjectCharacteristic charac = new ObjectCharacteristic();
		charac.value(offeringType).name(ProductOffConstants.OFFERING_TYPE)
				.type(ObjectCharacteristic.class.getSimpleName());
		characteristicList.add(charac);
		taskFlowUpdate.setCharacteristic(characteristicList);
		Map<String, Object> performVariables = productOfferingTypeAction.perform(stateMachineTransition, taskFlowUpdate);
		performVariables.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
		assertThat(performVariables).hasSize(3);
		assertEquals("product_off_id", performVariables.get(ProductOffConstants.PRODUCT_OFF_ID));
	}

}
