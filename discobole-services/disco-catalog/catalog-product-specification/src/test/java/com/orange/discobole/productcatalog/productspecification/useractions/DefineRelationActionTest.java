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
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecRelationshipType;
import com.orange.discobole.productcatalog.productspecification.pojo.AssociatePolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.pojo.DefineRelationship;
import com.orange.discobole.productcatalog.productspecification.pojo.EntityRelationships;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;


 class DefineRelationActionTest extends ProductSpecificationApplicationTests {

	@InjectMocks
	private DefineRelationAction defineRelationAction;

	@Mock
	private ProductSpecService productSpecService;

	@Mock
	private StateMachine stateMachine;

	@Mock
	private ExtendedState extendedState;

	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
	private final String TASK_ID = UUID.randomUUID().toString();
	private static String PRODUCTSPECID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(defineRelationAction, "objectMapper", objectMapper);
		defineRelationAction.init();
	}

	@Test
	 void performTest() {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("defineRelationAction");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);

		List<DefineRelationship> defineRelationships = new ArrayList<>();
		DefineRelationship defineRelationship = new DefineRelationship();
		defineRelationship.setId("relation_1");
		defineRelationship.setRelationshipType(ProductSpecRelationshipType.RELIESON);
		defineRelationships.add(defineRelationship);
		List<AssociatePolicyRuleRef> associatePolicyRuleRef = new ArrayList<>();
		AssociatePolicyRuleRef associatePolicy = new AssociatePolicyRuleRef();
		associatePolicy.setId("1");
		associatePolicy.setName("policy");
		associatePolicyRuleRef.add(associatePolicy);
		EntityRelationships entityRelationship = new EntityRelationships();
		entityRelationship.setDefineRelationship(defineRelationships);
		entityRelationship.setAssociatePolicyRuleRef(associatePolicyRuleRef);
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic characteristic = new ObjectCharacteristic();
		characteristic.setName(ProductSpecConstants.RELATION_SPEC);
		characteristic.setValue(entityRelationship);
		charact.add(characteristic);
		taskFlowUpdate.setCharacteristic(charact);
		Map<String, Object> performVariables = defineRelationAction.perform(stateMachineTransition, taskFlowUpdate);
		assertThat(performVariables).hasSize(1);
	}

	@Test
	 void performCharateristicGetTest() {
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		List<CharacteristicSpecification> charact = defineRelationAction
				.requiredCharacteristics(stateMachineTransition, null);
		assertThat(charact).hasSize(1);

	}

}
