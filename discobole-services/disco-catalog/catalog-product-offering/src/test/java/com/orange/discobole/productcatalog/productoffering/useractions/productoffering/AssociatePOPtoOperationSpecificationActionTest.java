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
import org.mockito.Mockito;
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
import com.orange.discobole.productcatalog.productoffering.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.AssociatePOPtoOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.useractions.productoffering.AssociatePOPtoOperationSpecificationAction;

class AssociatePOPtoOperationSpecificationActionTest extends ProductOfferingApplicationTests {
    @InjectMocks
    private AssociatePOPtoOperationSpecificationAction associatePOPtoOperationSpecificationAction;

    @Mock
    private ProductOfferingService productOfferingService;

    @Mock
    private StateMachine<String,String> stateMachine;

    @Mock
    private ExtendedState extendedState;
    private static String PRODUCTOFFID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    private static final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
    private final String TASK_ID = UUID.randomUUID().toString();
    private static String PRODUCTSPECID = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());
    QueryService queryService = Mockito.mock(QueryService.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(associatePOPtoOperationSpecificationAction, "objectMapper", objectMapper);
        associatePOPtoOperationSpecificationAction.init();
    }

    @Test
    void perform() {
        Map<String, Object> variablesFromUserActions = new HashMap<>();
        variablesFromUserActions.put(ProductSpecConstants.PRODUCT_SPEC_ID, PRODUCTSPECID);
        variablesFromUserActions.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setProcessDefinitionKey("AssociatePOPtoOperationSpecification");
        stateMachineTransition.setProcessInstanceId(PROCESS_FLOW_ID);
        stateMachineTransition.setTaskDefinitionId(TASK_ID);
        stateMachineTransition.setTaskDefinitionKey("associatePOPtoOperationSpecification");
        stateMachineTransition.setVariablesFromUserActions(variablesFromUserActions);

        Map<Object, Object> variables = new HashMap<>();
        variables.put(ProductOffConstants.PRODUCT_OFF_ID, PRODUCTOFFID);
        when(stateMachine.getExtendedState()).thenReturn(extendedState);
        when(stateMachine.getExtendedState().getVariables()).thenReturn(variables);

        List<AssociatePOPtoOperationSpecification> associatePOPtoOperationSpecList = new ArrayList<>();
        AssociatePOPtoOperationSpecification associatePOPtoOperationSpecification = new AssociatePOPtoOperationSpecification();
        associatePOPtoOperationSpecification.setOperationspecid("operation_spec_id");
        associatePOPtoOperationSpecification.setProductofferingpriceid("product_offering_price_id");
        associatePOPtoOperationSpecList.add(associatePOPtoOperationSpecification);

        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        List<Characteristic> charact = new ArrayList<>();
        ObjectCharacteristic operationCharac = new ObjectCharacteristic();
        operationCharac.setName(ProductOffConstants.LINKPOPTOATOMICPRODOFFERING);
        operationCharac.setValue(associatePOPtoOperationSpecList);
        charact.add(operationCharac);

        taskFlowUpdate.setCharacteristic(charact);

        Map<String, Object> performVariables = associatePOPtoOperationSpecificationAction.perform(stateMachineTransition, taskFlowUpdate);
        assertThat(performVariables).isEmpty();
    }


}
