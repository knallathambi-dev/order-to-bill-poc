// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;

public class ProductSpecCancelActionTest extends ProductSpecificationApplicationTests {

	@InjectMocks
	private ProductSpecCancelAction productSpecCancelAction;

	@Mock
	private ProductSpecService productSpecService;

	@Mock
	private StateMachineTransition stateMachineTransition;

	private final Map<String, Object> variablesFromUserActions = new HashMap<>();

	private static final String PRODUCT_SPC_ID = UUID.randomUUID().toString();
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	void setUp() {
		variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCT_SPC_ID);
		Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn(variablesFromUserActions);
		ReflectionTestUtils.setField(productSpecCancelAction, "objectMapper", objectMapper);
		productSpecCancelAction.init();
	}

	@Test
	void performTest() {
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		CancelEntityOperation cancelData = new CancelEntityOperation();
		cancelData.setIsCancelled(true);
		Characteristic characteristic = new ObjectCharacteristic().value(cancelData).valueType("Object")
				.name(ProductSpecConstants.PRODUCTSPEC_CANCEL);
		characteristicList.add(characteristic);
		taskFlowUpdate.setCharacteristic(characteristicList);
		productSpecCancelAction.perform(stateMachineTransition, taskFlowUpdate);
		Mockito.verify(productSpecService, Mockito.times(1)).cancelProductSpec(PRODUCT_SPC_ID);

	}

	@Test
	void requiredCharacteristicsTest() {
		productSpecCancelAction.requiredCharacteristics(stateMachineTransition, null);
		Assertions.assertNotNull(stateMachineTransition.getVariablesFromUserActions());
	}

}