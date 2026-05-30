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
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify.ModifyPOOperationSpecificationAction;

class ModifyPOOperationSpecificationActionTest extends ProductOfferingApplicationTests {

    @InjectMocks
    private ModifyPOOperationSpecificationAction defineOperationSpecificationAction;

    @Mock
	private StateMachineTransition stateMachineTransition;

	@Mock
	private ModifyProductOfferingService productOfferingService;
	
	List<CharacteristicSpecification> characteristicList;

    @Mock
    private StateMachine stateMachine;

    @Mock
    private ExtendedState extendedState;
    private static String PRODUCTOFFID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
    private final String TASK_ID = UUID.randomUUID().toString();
    private static String PRODUCTSPECID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());
    QueryService queryService = Mockito.mock(QueryService.class);
    ProductOffering productOffering = new ProductOffering();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(defineOperationSpecificationAction, "objectMapper", objectMapper);
        productOffering.setId(PRODUCTOFFID);
        productOffering.setLifecycleStatus(ProductOfferingLifecycle.ACTIVE);
        defineOperationSpecificationAction.init();
    }

    @Test
    void performTest() throws JsonProcessingException {
        Map<String, Object> variablesFromUserActions = new HashMap<>();
        variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
        variablesFromUserActions.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setProcessDefinitionKey("ProductOffCreation");
        stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
        stateMachineTransition.setTaskDefinitionId(TASK_ID);
        stateMachineTransition.setTaskDefinitionKey("defineProductOfferingOperations");
        stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

        Map<Object, Object> variables = new HashMap<>();
        variables.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
        when(stateMachine.getExtendedState()).thenReturn(extendedState);
        when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);
        when(queryService.fetchProductOfferingById(PRODUCTOFFID, null)).thenReturn(productOffering);

        List<DefineOperationSpecification> operationSpecificationList = new ArrayList<>();
        DefineOperationSpecification operationSpecification = new DefineOperationSpecification();
        operationSpecification.setId("1");
        operationSpecification.setName("Create");
        operationSpecification.setValidFor(new TimePeriod().startDateTime(OffsetDateTime.now()));
        operationSpecificationList.add(operationSpecification);

        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        List<Characteristic> charact = new ArrayList<>();
        ObjectCharacteristic operationCharac = new ObjectCharacteristic();
        operationCharac.setName(ProductOffConstants.OPERATION_SPEC);
        operationCharac.setValue(operationSpecificationList);
        charact.add(operationCharac);

        taskFlowUpdate.setCharacteristic(charact);

        Map<String, Object> performVariables = defineOperationSpecificationAction.perform(stateMachineTransition, taskFlowUpdate);
        assertThat(performVariables).isEmpty();
    }

    @Test
    void performCharateristicGetTest() {
        String prodOffId = "product_off_id";
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        Map<String, Object> variablesFromUserActions = new HashMap<>();
        variablesFromUserActions.put(ProductOffConstants.PRODUCT_OFF_ID, prodOffId);
        stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);
        List<CommercialOperation> operation = new ArrayList<>();
        CommercialOperation commercialOperation = new CommercialOperation();
        commercialOperation.id("1").name("Create");
        operation.add(commercialOperation);
        when(queryService.fetchProductOfferingById(prodOffId, null)).thenReturn(new ProductOffering().id("product_off_id")
                .lifecycleStatus(ProductOfferingLifecycle.ACTIVE).commercialOperation(operation));
        List<CharacteristicSpecification> charact = defineOperationSpecificationAction.requiredCharacteristics(stateMachineTransition, null);
        assertThat(charact).hasSize(1);
    }
}
