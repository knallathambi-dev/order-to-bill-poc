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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
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
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.BundledProductOfferingOption;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.BundledProductOfferings;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.ManageProductOfferingBundling;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.useractions.productoffering.DefineProductOfferingBundlingAction;

class DefineProductOfferingBundlingActionTest extends ProductOfferingApplicationTests {

	@InjectMocks
	private DefineProductOfferingBundlingAction defineProductOfferingBundlingAction;

	@Mock
	private StateMachine stateMachine;

	@Mock
	private ProductOfferingService productOfferingService;

	@Mock
	private ExtendedState extendedState;

	private static String PRODUCTOFFID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";

	private final String TASK_ID = UUID.randomUUID().toString();

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	void init() {
		ReflectionTestUtils.setField(defineProductOfferingBundlingAction, "objectMapper", objectMapper);
		defineProductOfferingBundlingAction.init();
	}

	@Test
	void performBundleProductOfferingWhenUserEntersValues() {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductOffCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("defineProductOfferingBundlingAction");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);

		ManageProductOfferingBundling manageProductOfferingBundling = new ManageProductOfferingBundling();
		manageProductOfferingBundling.setGlobalMinCardinality(0);
		manageProductOfferingBundling.setGlobalMaxCardinality(1);
		List<BundledProductOfferings> productOfferBundlings = new ArrayList<>();
		BundledProductOfferingOption bundledProductOfferingOption = new BundledProductOfferingOption();
		bundledProductOfferingOption.setNumberRelOfferDefault(0);
		bundledProductOfferingOption.setNumberRelOfferLowerLimit(0);
		bundledProductOfferingOption.setNumberRelOfferUpperLimit(1);
		BundledProductOfferings bundlePo = new BundledProductOfferings();
		bundlePo.setId("1");
		bundlePo.setBundledProductOfferingOption(bundledProductOfferingOption);
		productOfferBundlings.add(bundlePo);
		manageProductOfferingBundling.setBundledProductOffering(productOfferBundlings);

		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic operationCharac = new ObjectCharacteristic();
		operationCharac.setName(ProductOffConstants.PRODUCT_OFFERING_BUNDLING);
		operationCharac.setValue(manageProductOfferingBundling);
		charact.add(operationCharac);

		taskFlowUpdate.setCharacteristic(charact);
		Map<String, Object> performVariables = defineProductOfferingBundlingAction.perform(stateMachineTransition,
				taskFlowUpdate);
		assertThat(performVariables).isEmpty();

	}

}
