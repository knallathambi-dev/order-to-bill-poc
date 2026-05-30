// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
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
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.pojo.ValidateEntityOperation;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;


public class ValidateProductSpecActionTest extends ProductSpecificationApplicationTests {

	@InjectMocks
	private ValidateProductSpecAction userAction;

	@Mock
	private StateMachine stateMachine;

	@Mock
	private ProductSpecService productSpecService;

	@Mock
	private ExtendedState extendedState;

	private static String PRODUCTSPECID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";

	private final String TASK_ID = UUID.randomUUID().toString();
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(userAction, "objectMapper", objectMapper);
		userAction.init();
	}

	@Test
	void performProductOfferingValidationWhenUserClicksOnValidate() {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("defineProductOfferingMarketSegment");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);

		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);

		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		ValidateEntityOperation validateOperation = new ValidateEntityOperation();
		validateOperation.setValidated(true);
		Characteristic characteristic = new ObjectCharacteristic().value(validateOperation).valueType("Object")
				.name(ProductSpecConstants.VALIDATE);
		characteristicList.add(characteristic);
		taskFlowUpdate.setCharacteristic(characteristicList);
		Map<String, Object> performVariables = userAction.perform(stateMachineTransition, taskFlowUpdate);
		assertThat(performVariables.size()).isEqualTo(0);
		verify(productSpecService).validateProductSpecification(PRODUCTSPECID);
	}

}
