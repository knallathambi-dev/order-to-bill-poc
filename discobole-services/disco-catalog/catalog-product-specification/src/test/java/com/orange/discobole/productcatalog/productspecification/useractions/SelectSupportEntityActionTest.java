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
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.constant.ProcessConstants;
import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.RelatedEntity;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectSupportEntity;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;


public class SelectSupportEntityActionTest extends ProductSpecificationApplicationTests {

	@InjectMocks
	private SelectSupportEntityAction selectSupportEntityAction;

	@Mock
	private ProductSpecService productSpecService;

	@Mock
	private StateMachine stateMachine;

	@Mock
	private ConfigurableProperties configurableProperties;

	private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
	private final String TASK_ID = UUID.randomUUID().toString();
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());

	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(selectSupportEntityAction, "objectMapper", objectMapper);
		selectSupportEntityAction.init();
	}

	@Test
	public void performTest() {
		Map<String, Object> variablesFromUserActions = new HashMap<>();
		variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, "prod_1");
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		stateMachineTransition.setProcessDefinitionKey("ProductSpecCreation");
		stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
		stateMachineTransition.setTaskDefinitionId(TASK_ID);
		stateMachineTransition.setTaskDefinitionKey("defineSupportEntityAction");
		stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

		TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
		List<Characteristic> charact = new ArrayList<>();
		ObjectCharacteristic supportEntityCharac = new ObjectCharacteristic();
		supportEntityCharac.setName(ProductSpecConstants.SUPPORT_ENTITY);
		SelectSupportEntity supportEntityData = new SelectSupportEntity();
		supportEntityData.setId("802");
		supportEntityData.setSupportEntityType(SupportEntity.CFSSPEC);
		supportEntityCharac.setValue(supportEntityData);
		charact.add(supportEntityCharac);
		taskFlowUpdate.setCharacteristic(charact);
		if(supportEntityData.getSupportEntityType() == SupportEntity.CFSSPEC) {
			when(productSpecService.initiateProductSpecCreation("802")).thenReturn("pid_1");

			Map<String, Object> performVariables = selectSupportEntityAction.perform(stateMachineTransition,
					taskFlowUpdate);
			assertThat(performVariables.get(ProductSpecConstants.PRODUCT_SPEC_ID)).isEqualTo("pid_1");
			assertEquals(List.of(
					new StringCharacteristic().value("802").name(ProductSpecConstants.SERVICE_SPEC_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()),
					new StringCharacteristic().value("pid_1").name(ProductSpecConstants.PRODUCT_SPEC_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName())),
					performVariables.get(TaskConstants.CHARACTERISTIC));
			assertEquals(List.of(new RelatedEntity().name(ProductSpecConstants.PRODUCT_SPEC_ID).id("pid_1")),
					performVariables.get(ProcessConstants.RELATED_ENTITY));
		}
		TaskFlowUpdate taskFlowUpdate1 = new TaskFlowUpdate();
		List<Characteristic> charact1 = new ArrayList<>();
		ObjectCharacteristic supportEntityCharac1 = new ObjectCharacteristic();
		supportEntityCharac1.setName(ProductSpecConstants.SUPPORT_ENTITY);
		SelectSupportEntity supportEntityData1 = new SelectSupportEntity();
		supportEntityData1.setId("802");
		supportEntityData1.setSupportEntityType(SupportEntity.STOCKITEMTYPE);
		supportEntityCharac1.setValue(supportEntityData1);
		charact1.add(supportEntityCharac1);
		taskFlowUpdate1.setCharacteristic(charact1);
		if(supportEntityData1.getSupportEntityType().equals(SupportEntity.STOCKITEMTYPE)){
			when(productSpecService.initiateStockItemProductSpecCreation("802")).thenReturn("pid_1");

			Map<String, Object> performVariables = selectSupportEntityAction.perform(stateMachineTransition,
					taskFlowUpdate1);
			assertThat(performVariables.get(ProductSpecConstants.PRODUCT_SPEC_ID)).isEqualTo("pid_1");
			assertEquals(List.of(
					new StringCharacteristic().value("802").name(ProductSpecConstants.STOCK_ITEM_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName()),
					new StringCharacteristic().value("pid_1").name(ProductSpecConstants.PRODUCT_SPEC_ID)
							.valueType(String.class.getSimpleName()).type(StringCharacteristic.class.getSimpleName())),
					performVariables.get(TaskConstants.CHARACTERISTIC));
			assertEquals(List.of(new RelatedEntity().name(ProductSpecConstants.PRODUCT_SPEC_ID).id("pid_1")),
					performVariables.get(ProcessConstants.RELATED_ENTITY));
		}
	}

	@Test
	public void performCharateristicGetTest() {
		StateMachineTransition stateMachineTransition = new StateMachineTransition();
		List<CharacteristicSpecification> charact = selectSupportEntityAction
				.requiredCharacteristics(stateMachineTransition, null);
		assertThat(charact.size()).isEqualTo(1);

	}

}
