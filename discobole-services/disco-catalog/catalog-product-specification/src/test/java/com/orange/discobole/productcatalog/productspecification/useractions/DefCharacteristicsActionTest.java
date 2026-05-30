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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.OperationSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecRelationship;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.PickCharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductSpecCharRelationship;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductSpecCharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductSpecUsageSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.ServiceSpecificationCharRelationship;
import com.orange.discobole.productcatalog.productspecification.pojo.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;

class DefCharacteristicsActionTest extends ProductSpecificationApplicationTests {

	@InjectMocks
	private DefCharacteristicsAction defCharacteristicsAction;

	@Mock
	private ProductSpecService productSpecService;

	@Mock
	private StateMachine stateMachine;

	@Mock
	private ExtendedState extendedState;

	@Mock
	private ConfigurableProperties configurableProperties;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
	private final String TASK_ID = UUID.randomUUID().toString();
	private static String PRODUCTSPECID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
	QueryService queryService = Mockito.mock(QueryService.class);
	private static String accessToken = "Bearer 123";
	@Mock
	private AccessTokenInterceptor accessTokenInterceptor;

	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(defCharacteristicsAction, "objectMapper", objectMapper);
		ReflectionTestUtils.setField(defCharacteristicsAction, "accessTokenInterceptor", accessTokenInterceptor);
		Mockito.when(accessTokenInterceptor.getToken()).thenReturn(accessToken);
		defCharacteristicsAction.init();
	}

	@Test
    void performTest() throws JsonProcessingException {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		variablesFromUserActions.put(ProductSpecConstants.SERVICE_SPEC_ID, "123");
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("defineCharacteristicsAction");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
		ServiceSpecification serviceSpecification = new ServiceSpecification().id("123").usageSpecification(List.of());
		ServiceSpecRelationship specRelationship = new ServiceSpecRelationship().id("123").type("reliesOn");
		serviceSpecification.addServiceSpecRelationshipItem(specRelationship);
		when(queryService.getServiceSpecById("123", accessToken)).thenReturn(serviceSpecification);
		when(configurableProperties.getProductSpecQuery())
				.thenReturn("http://localhost:8087/productCatalogManagement/v1/productSpecification");

		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);

		TimePeriod validFor = new TimePeriod();
		validFor.setStartDateTime(OffsetDateTime.parse("1985-04-12T23:20:50.52Z"));
		validFor.setEndDateTime(OffsetDateTime.parse("1985-04-12T23:20:50.52Z"));

		ProductCharValue productSpecCharValue = new ProductCharValue();
		productSpecCharValue.setIsDefault(true);
		productSpecCharValue.setValidFor(validFor);
		productSpecCharValue.setServiceSpecCharacteristicReferenceValue("abc");
		List<ProductCharValue> productSpecCharValues = new ArrayList<>();
		productSpecCharValues.add(productSpecCharValue);

		List<ProductSpecCharRelationship> productSpecCharRelationships = new ArrayList<>();
		ProductSpecCharRelationship productSpecCharRelationship = new ProductSpecCharRelationship();
		productSpecCharRelationship.setId("pscr1");
		productSpecCharRelationship.setRelationshipType("relieson");
		productSpecCharRelationships.add(productSpecCharRelationship);

		List<ServiceSpecificationCharRelationship> serviceSpecificationCharRelationships = new ArrayList<>();
		ServiceSpecificationCharRelationship charRelationship = new ServiceSpecificationCharRelationship();
		charRelationship.setId("cR_1");
		charRelationship.setRelationshipType("relieson");
		serviceSpecificationCharRelationships.add(charRelationship);

		ProductSpecCharacteristicSpecification pickCharacteristicSpecification = new ProductSpecCharacteristicSpecification();
		pickCharacteristicSpecification.setId("spec_1");
		pickCharacteristicSpecification.setName("Call forwarding number");
		pickCharacteristicSpecification.setCharacteristicValueSpecification(productSpecCharValues);
		pickCharacteristicSpecification.setExternalCharSpecRelationship(productSpecCharRelationships);
		pickCharacteristicSpecification.setInternalCharSpecRelationship(serviceSpecificationCharRelationships);
		pickCharacteristicSpecification.setMinCardinality(0);
		pickCharacteristicSpecification.setMaxCardinality(2);
		
		List<ProductSpecCharacteristicSpecification> pickCharacteristicSpecifications = new ArrayList<>();
		pickCharacteristicSpecifications.add(pickCharacteristicSpecification);

		PickCharacteristicSpecification request=new PickCharacteristicSpecification();
		request.setCharacteristicSpecification(pickCharacteristicSpecifications);
		
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic characteristic = new ObjectCharacteristic();
		characteristic.setName(ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTICS);
		characteristic.setValue(request);
		charact.add(characteristic);
		taskFlowUpdate.setCharacteristic(charact);
		Map<String, Object> performVariables = defCharacteristicsAction.perform(stateMachineTransition, taskFlowUpdate);
		assertThat(performVariables).hasSize(1);

	}

	@Test
    void performCharacteristicValueWithNullTest() throws JsonProcessingException {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("defineCharacteristicsAction");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

		Map<Object, Object> variables = new HashMap<>();
		variables.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
		when(stateMachine.getExtendedState()).thenReturn(extendedState);
		when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);

		TimePeriod validFor = new TimePeriod();
		validFor.setStartDateTime(OffsetDateTime.parse("1985-04-12T23:20:50.52Z"));
		validFor.setEndDateTime(OffsetDateTime.parse("1985-04-12T23:20:50.52Z"));

		ProductCharValue productSpecCharValue = new ProductCharValue();
		productSpecCharValue.setIsDefault(true);
		productSpecCharValue.setValidFor(validFor);
		List<ProductCharValue> productSpecCharValues = new ArrayList<>();
		productSpecCharValues.add(productSpecCharValue);

		List<ProductSpecCharRelationship> productSpecCharRelationships = new ArrayList<>();
		ProductSpecCharRelationship productSpecCharRelationship = new ProductSpecCharRelationship();
		productSpecCharRelationship.setId("pscr1");
		productSpecCharRelationship.setRelationshipType("relieson");
		productSpecCharRelationships.add(productSpecCharRelationship);

		List<ServiceSpecificationCharRelationship> serviceSpecificationCharRelationships = new ArrayList<>();
		ServiceSpecificationCharRelationship charRelationship = new ServiceSpecificationCharRelationship();
		charRelationship.setId("cR_1");
		charRelationship.setRelationshipType("relieson");
		serviceSpecificationCharRelationships.add(charRelationship);

		ProductSpecCharacteristicSpecification pickCharacteristicSpecification = new ProductSpecCharacteristicSpecification();
		// not setting Id of PickCharacteristicSpecification
		pickCharacteristicSpecification.setName("Call forwarding number");
		pickCharacteristicSpecification.setCharacteristicValueSpecification(productSpecCharValues);
		pickCharacteristicSpecification.setExternalCharSpecRelationship(productSpecCharRelationships);
		pickCharacteristicSpecification.setInternalCharSpecRelationship(serviceSpecificationCharRelationships);

		List<ProductSpecCharacteristicSpecification> pickCharacteristicSpecifications = new ArrayList<>();
		pickCharacteristicSpecifications.add(pickCharacteristicSpecification);

		List<ProductSpecUsageSpecification> usageSpecifications=new ArrayList<ProductSpecUsageSpecification>();
		ProductSpecUsageSpecification usage=new ProductSpecUsageSpecification();
		usage.setId("1");
		usage.setName("add");
		usageSpecifications.add(usage);
		
		PickCharacteristicSpecification pickSpecifications=new PickCharacteristicSpecification();
		pickSpecifications.setCharacteristicSpecification(pickCharacteristicSpecifications);
		pickSpecifications.setUsageSpecification(new ArrayList<ProductSpecUsageSpecification>());
		
		
		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic characteristic = new ObjectCharacteristic();
		characteristic.setName(ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTICS);
		characteristic.setValue(pickSpecifications);
		taskFlowUpdate.setCharacteristic(charact);
		charact.add(characteristic);
		Assertions.assertThrows(DiscoManagedClientException.class,
				() -> defCharacteristicsAction.perform(stateMachineTransition, taskFlowUpdate));

	}

	@Test
   void performCharateristicGetTest() {
		String serviceSpecId = "service_spec_id";
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductSpecConstants.SERVICE_SPEC_ID, serviceSpecId);
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
		List<OperationSpecification> operationSpecifications = new ArrayList<>();
		when(queryService.getServiceSpecById(serviceSpecId, accessToken)).thenReturn(new ServiceSpecification().id("service_spec_id")
				.lifecycleStatus(ServiceSpecLifeCycleEnum.ACTIVE.toString())
				.operationSpecification(operationSpecifications));
		List<CharacteristicSpecification> charact = defCharacteristicsAction
				.requiredCharacteristics(stateMachineTransition, null);
		assertThat(charact).hasSize(1);

	}

}
