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
import static org.mockito.Mockito.verify;

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
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectSupportEntity;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.useractions.productoffering.ProductOffCreationAction;

class ProductOffCreationActionTest extends ProductOfferingApplicationTests {

	@InjectMocks
	private ProductOffCreationAction productOffCreationAction;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private ProductOfferingService productOfferingService;

	private static String PRODUCTSPECID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	void init() {
		ReflectionTestUtils.setField(productOffCreationAction, "objectMapper", objectMapper);
		productOffCreationAction.init();
	}

	@Test
	void performProductOfferingCreationWhenUserEntersValues() {
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		SelectSupportEntity supportEntityData = new SelectSupportEntity();
		supportEntityData.setId(PRODUCTSPECID);
		supportEntityData.setSupportEntityType(SupportEntity.PRODUCTSPEC);
		ObjectCharacteristic charac = new ObjectCharacteristic();
		charac.value(supportEntityData).name(ProductOffConstants.SUPPORT_ENTITY)
				.type(ObjectCharacteristic.class.getSimpleName());
		characteristicList.add(charac);
		taskFlowUpdate.setCharacteristic(characteristicList);
		Map<String, Object> performVariables = productOffCreationAction.perform(stateMachineTransition, taskFlowUpdate);
		assertThat(performVariables).hasSize(2);
		verify(productOfferingService).createProductOffering(PRODUCTSPECID, null);
	}

}
