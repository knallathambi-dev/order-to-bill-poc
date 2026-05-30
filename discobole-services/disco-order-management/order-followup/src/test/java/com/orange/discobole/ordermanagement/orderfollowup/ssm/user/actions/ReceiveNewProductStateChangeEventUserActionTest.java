// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.ssm.user.actions;

import com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants;
import com.orange.discobole.ordermanagement.orderfollowup.pojo.spec.characteristic.operation.ProductDeliveredEvent;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItemStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.PRODUCT_DELIVERED_EVENT_CLASS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReceiveNewProductStateChangeEventUserActionTest {
    public static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String EVENT_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String TASK_ID = RandomStringUtils.randomAlphabetic(8);

    @InjectMocks
    private ReceiveNewProductStateChangeEventUserAction newProductStateChangeEventUserAction;

    @BeforeEach
    void setUp() {
        newProductStateChangeEventUserAction.init();
    }

    @Test
    @DisplayName("Given a valid characteristic, " +
            "when performing the new product state change event user action, " +
            "then return a variable list")
    void shouldReturnVariableListWhenValidCharacteristicIsProvided() {
        // Given
        TaskFlowUpdate taskFlowUpdate = buildTaskFlowUpdate();

        // When
        Map<String, Object> responseResult = newProductStateChangeEventUserAction.perform(new StateMachineTransition(), taskFlowUpdate);

        // Then
        Assertions.assertEquals(PRODUCT_ORDER_ID, responseResult.get(FollowUpConstants.PRODUCT_ORDER_ID));
        Assertions.assertEquals(PRODUCT_ORDER_ITEM_ID, responseResult.get(FollowUpConstants.PRODUCT_ORDER_ITEM_ID));
        Assertions.assertEquals(ProductOrderStateType.INPROGRESS, responseResult.get(FollowUpConstants.PRODUCT_ORDER_STATE));
        Assertions.assertEquals(EVENT_ID, responseResult.get(FollowUpConstants.PRODUCT_ORDER_ITEM_EVENT_ID));
    }

    @Test
    @DisplayName("Given a valid task definition ID, " +
            "when retrieving required characteristics, " +
            "then return the required characteristic list")
    void shouldReturnRequiredCharacteristicsWhenTaskDefinitionIdIsValid() {
        // Given
        StateMachineTransition stateMachineTransition = new StateMachineTransition();
        stateMachineTransition.setTaskDefinitionId(TASK_ID);
        Map<Object, Object> contextVariables = new HashMap<>();

        // When
        List<CharacteristicSpecification> requiredCharacteristics =
                newProductStateChangeEventUserAction.requiredCharacteristics(stateMachineTransition, contextVariables);

        // Then
        assertThat(requiredCharacteristics).isNotEmpty();
        Assertions.assertEquals(PRODUCT_DELIVERED_EVENT_CLASS, requiredCharacteristics.get(0).getName());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMaxCardinality());
        Assertions.assertEquals(1, requiredCharacteristics.get(0).getMinCardinality());
    }

    private TaskFlowUpdate buildTaskFlowUpdate() {
        ProductDeliveredEvent productDeliveredEvent = ProductDeliveredEvent.builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .productOrderItemId(PRODUCT_ORDER_ITEM_ID)
                .productOrderItemEventId(EVENT_ID)
                .productState(ProductOrderItemStateType.COMPLETED)
                .relatedProductOrderState(ProductOrderStateType.INPROGRESS)
                .build();

        ObjectCharacteristic objectCharacteristic = new ObjectCharacteristic();
        objectCharacteristic.setName(PRODUCT_DELIVERED_EVENT_CLASS);
        objectCharacteristic.setValue(productDeliveredEvent);

        List<Characteristic> characteristic = Collections.singletonList(objectCharacteristic);
        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(characteristic);
        return taskFlowUpdate;
    }
}