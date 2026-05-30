// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions.prodspec.modify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productspecification.pojo.DefineEntityValidityPeriod;
import com.orange.discobole.productcatalog.productspecification.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;
import com.orange.discobole.productcatalog.productspecification.pojo.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;


class ModifyIDefineIdentityActionTest extends ProductSpecificationApplicationTests {

	@InjectMocks
	private ModifyDefineIdentityDataAction modifyDescriptionAction;

	@Mock
	private ProductSpecService productSpecService;

	@Mock
	private StateMachine stateMachine;

	@Mock
	private ExtendedState extendedState;
	
	@Mock
	private QueryService queryService;

	@Mock
	private AccessTokenInterceptor accessTokenInterceptor;

	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
	private final String TASK_ID = UUID.randomUUID().toString();
	private static String PRODUCTSPECID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
	private static String accessToken = "Bearer 123";

	@BeforeEach
	 void init() {
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		
	}

	private StateMachineTransition stateMachineTransition;
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	public void before() {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("productDescriptionAction");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
		ReflectionTestUtils.setField(modifyDescriptionAction, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(modifyDescriptionAction, "queryService", queryService);
	}

	@Test
	void performTest() {

		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
		when(queryService.fetchProductSpecById(PRODUCTSPECID, accessToken)).thenReturn(new ProductSpecification().id(PRODUCTSPECID).lifecycleStatus(ProductSpecificationLifecycle.INTEST));
		when(queryService.fetchProductOfferingsByProductSpecId(PRODUCTSPECID,accessToken)).thenReturn(new ArrayList<>());
		
		
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic identityData = new ObjectCharacteristic();
		identityData.setName(ProductSpecConstants.IDENTITY_DATA);
		
		IdentityData desc = new IdentityData();
		desc.setBrand("brand");
		desc.setDescription("description");
		desc.setName("name");
		desc.setProductNumber("productnum");

		DefineEntityValidityPeriod validFor=new DefineEntityValidityPeriod();
		validFor.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		
		DefineIdentityData def=new DefineIdentityData();
		def.setIdentityData(desc);
		def.setDefineEntityValidityPeriod(validFor);
		
		identityData.setValue(def);
		charact.add(identityData);
		taskFlowUpdate.setCharacteristic(charact);
		
		Map<String, Object> variables2 = new HashMap<>();
		variables2.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		variables2.put("lifecycleCharacteristicList", modifyDescriptionAction.requiredCharacteristics(stateMachineTransition, null));
		stateMachineTransition.setVariablesFromUserActions(variables2);
		
		Map<String, Object> performVariables = modifyDescriptionAction.perform(stateMachineTransition, taskFlowUpdate);
		assertThat(performVariables).isEmpty();
	}

	 void requiredCharacteristicNullTest() {
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic characteristic = new ObjectCharacteristic();
		IdentityData identityData = new IdentityData();
		identityData.setName("Cisco");
		characteristic.setValue(identityData);
		charact.add(characteristic);
		taskFlowUpdate.setCharacteristic(charact);
		modifyDescriptionAction.perform(stateMachineTransition, taskFlowUpdate);
	}

	@Test
	void requiredCharacteristicBlankTest() {
		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
		when(queryService.fetchProductSpecById(PRODUCTSPECID, accessToken)).thenReturn(new ProductSpecification().id(PRODUCTSPECID).lifecycleStatus(ProductSpecificationLifecycle.INTEST));
		when(queryService.fetchProductOfferingsByProductSpecId(PRODUCTSPECID,accessToken)).thenReturn(new ArrayList<>());
		
		
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic identityData = new ObjectCharacteristic();
		identityData.setName(ProductSpecConstants.IDENTITY_DATA);
		
		IdentityData desc = new IdentityData();
		desc.setBrand("brand");
		desc.setDescription("description");
		desc.setName("name");
		desc.setProductNumber("productnum");

		DefineEntityValidityPeriod validFor=new DefineEntityValidityPeriod();
		validFor.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
		
		DefineIdentityData def=new DefineIdentityData();
		def.setIdentityData(desc);
		def.setDefineEntityValidityPeriod(validFor);
		
		identityData.setValue(def);
		charact.add(identityData);
		taskFlowUpdate.setCharacteristic(null);
		
		Map<String, Object> variables2 = new HashMap<>();
		variables2.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		List<CharacteristicSpecification>  res=modifyDescriptionAction.requiredCharacteristics(stateMachineTransition, null);
		res.get(0).setMinCardinality(2);
		variables2.put("lifecycleCharacteristicList",res);
		stateMachineTransition.setVariablesFromUserActions(variables2);
		
		
		taskFlowUpdate.setCharacteristic(null);
		assertThrows(InvalidParameterException.class,
				() -> modifyDescriptionAction.perform(stateMachineTransition, taskFlowUpdate));
	}

}
