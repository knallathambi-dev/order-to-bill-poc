// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Assertions;
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
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.pojo.*;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineProductOfferingIdentityData;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify.ModifyPODescriptionAction;

class ModifyPODescriptionActionTest extends ProductOfferingApplicationTests {

	@InjectMocks
	private ModifyPODescriptionAction productOffDescAction;

	@Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private StateMachine<String, String> stateMachine;

	@Mock
	private ModifyProductOfferingService productOfferingService;

	List<CharacteristicSpecification> characteristicList;

	@Mock
	private QueryService queryService;

	@Mock
	private ExtendedState extendedState;

	private static String PRODUCTOFFID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";

	private final String TASK_ID = UUID.randomUUID().toString();

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	void init() {
		ReflectionTestUtils.setField(productOffDescAction, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(productOffDescAction, "queryService", queryService);
//		productOffDescAction.init();
		Mockito.when(queryService.fetchProductOfferingById(PRODUCTOFFID, null)).thenReturn(new ProductOffering().id(PRODUCTOFFID).lifecycleStatus(ProductOfferingLifecycle.ACTIVE).type(ProductOfferingType.ATOMICPRODUCTOFFERING));
		when(queryService.fetchBundleProductOfferingByAtomicProductOfferingId(PRODUCTOFFID)).thenReturn(new ArrayList<>());
		
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
		stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductOfferingCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("productDescriptionAction");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

	}

	@Test
	void performProductOfferingDescriptionWhenUserEntersValues() {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("defineProductOfferingDescription");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);

		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
		
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> characteristicList = new ArrayList<>();
		DefineProductOfferingIdentityData identityData = new DefineProductOfferingIdentityData();
		DefineIdentityData defineIdentityData = new DefineIdentityData();
		defineIdentityData.setBrand("brand");
		defineIdentityData.setDescription("description");
		defineIdentityData.setIsInstallable(true);
		defineIdentityData.setIsVisible(true);
		defineIdentityData.setName("name");
		identityData.setDefineData(defineIdentityData);
		identityData.setChannels(new ArrayList<DefineProductOfferingSaleChannel>());
		identityData.setMarketSegments(new ArrayList<DefineProductOfferingMarketSegment>());
		identityData.setPoTerms(new ArrayList<ManageProductOfferingTerm>());
		identityData.setRelatedParties(new ArrayList<SelectRelatedParty>());
		identityData.setValidityPeriod(new DefineEntityValidityPeriod());
		ObjectCharacteristic charac = new ObjectCharacteristic();
		charac.value(identityData).name(ProductOffConstants.IDENTITY_DATA);
		characteristicList.add(charac);
		taskFlowUpdate.setCharacteristic(characteristicList);
		Map<String, Object> performVariables = productOffDescAction.perform(stateMachineTransition, taskFlowUpdate);
		assertThat(performVariables).isEmpty();
		verify(productOfferingService).modifyProductOffDesc(PRODUCTOFFID, defineIdentityData, new HashSet<String>(),
				new HashSet<String>(), new HashSet<RelatedParty>(), new HashSet<ProductOfferingTerm>(), null, null,
				"Product Offering initial creation", null);
	}

	@Test
	void requiredCharacteristicsTest() {
		Map<Object, Object> variables1 = new HashMap<>();	
		Map<String, Object> variables = new HashMap<>();
		variables.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
		variables1.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables1);
		
		characteristicList = productOffDescAction.requiredCharacteristics(stateMachineTransition, null);
		Assertions.assertNotNull(characteristicList);
		Assertions.assertEquals(1, characteristicList.size());
	}

}
