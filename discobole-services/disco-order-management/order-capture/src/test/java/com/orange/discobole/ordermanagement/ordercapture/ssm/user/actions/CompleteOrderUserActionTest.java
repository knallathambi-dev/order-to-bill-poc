// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.user.actions;

import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.BillingAccountRef;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.PaymentRef;
import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.PaymentRefIdentifier;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CompleteOrderUserActionTest {

    public static final String OBJECT_TYPE = "object";
    private static final String ORDER_ITEM_ID_1 = "item1";
    private static final String ORDER_ITEM_ID_2 = "item2";
    private static final String ORDER_ITEM_ID_3 = "item3";
    private static final String PAYMENT_REF_ID_1 = "payment1";
    private static final String PAYMENT_REF_ID_2 = "payment2";
    private static final String PAYMENT_REF_ID_3 = "payment3";
    private static final String BILLING_ACCOUNT_REF_ID = "billing1";

    @InjectMocks
    private CompleteOrderUserAction completeOrderUserAction;
    private DefaultStateContext<String, String> stateContext;

    @BeforeEach
    void setUp() {
        completeOrderUserAction.init();
        stateContext = mockStateContext();
    }

    @Test
    @DisplayName("Given unpaid order items and items requiring BA ref " +
            "when requiredCharacteristics is called " +
            "then return characteristic list with 6 characteristics")
    void shouldReturnSixCharacteristicsWhenUnpaidItemsAndItemsRequiringBARefExist() {
        // Given && When
        List<CharacteristicSpecification> result = completeOrderUserAction.requiredCharacteristics(mock(StateMachineTransition.class), stateContext.getExtendedState().getVariables());

        // Then
        assertEquals(6, result.size());
    }

    @Test
    @DisplayName("Given no unpaid items and no items requiring BA ref " +
            "when requiredCharacteristics is called " +
            "then return empty characteristic list")
    void shouldReturnEmptyCharacteristicListWhenNoUnpaidItemsAndNoItemsRequiringBARef() {
        // Given
        stateContext.getExtendedState().getVariables().put(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, Collections.emptyList());
        stateContext.getExtendedState().getVariables().put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, Collections.emptyList());

        // When
        List<CharacteristicSpecification> result = completeOrderUserAction.requiredCharacteristics(mock(StateMachineTransition.class), stateContext.getExtendedState().getVariables());

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given unpaid order items only " +
            "when requiredCharacteristics is called " +
            "then return characteristic list with 4 characteristics")
    void shouldReturnFourCharacteristicsWhenUnpaidItemsOnly() {
        // Given
        stateContext.getExtendedState().getVariables().put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, Collections.emptyList());

        // When
        List<CharacteristicSpecification> result = completeOrderUserAction.requiredCharacteristics(mock(StateMachineTransition.class), stateContext.getExtendedState().getVariables());

        // Then
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("Given items requiring BA ref only " +
            "when requiredCharacteristics is called " +
            "then return characteristic list with 2 characteristics")
    void shouldReturnTwoCharacteristicsWhenItemsRequiringBARefOnly() {
        // Given
        stateContext.getExtendedState().getVariables().put(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, Collections.emptyList());

        // When
        List<CharacteristicSpecification> result = completeOrderUserAction.requiredCharacteristics(mock(StateMachineTransition.class), stateContext.getExtendedState().getVariables());

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Given order item that requires BA ref and payment ref at the same time " +
            "when requiredCharacteristics is called " +
            "then return characteristic list with 3 characteristics")
    void shouldReturnThreeCharacteristicsWhenCommonUnpaidItemsAndItemsRequiringBARef() {
        // Given
        stateContext.getExtendedState().getVariables().put(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, List.of(ORDER_ITEM_ID_3));
        stateContext.getExtendedState().getVariables().put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, List.of(ORDER_ITEM_ID_3));

        // When
        List<CharacteristicSpecification> result = completeOrderUserAction.requiredCharacteristics(mock(StateMachineTransition.class), stateContext.getExtendedState().getVariables());

        // Then
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Given valid task flow update with payment refs and billing account refs " +
            "when perform is called " +
            "then it should return a map with payment refs and billing account refs")
    void shouldReturnPaymentAndBillingRefsWhenPerformIsCalled() throws ParameterException {
        // Given
        TaskFlowUpdate taskFlowUpdate = createTaskFlowUpdate();

        // When
        Map<String, Object> result = completeOrderUserAction.perform(mock(StateMachineTransition.class), taskFlowUpdate);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        Map<String, List<String>> paymentRefCharacteristics = (Map<String, List<String>>) result.get(OrderCaptureConstants.ORDER_ITEM_PAYMENT_REF_MAP);
        assertNotNull(paymentRefCharacteristics);
        assertEquals(2, paymentRefCharacteristics.size());
        assertEquals(List.of(PAYMENT_REF_ID_1, PAYMENT_REF_ID_2), paymentRefCharacteristics.get(ORDER_ITEM_ID_1));
        assertEquals(List.of(PAYMENT_REF_ID_3), paymentRefCharacteristics.get(ORDER_ITEM_ID_2));

        Map<String, String> baRefCharacteristics = (Map<String, String>) result.get(OrderCaptureConstants.ORDER_ITEM_BILLING_ACCOUNT_REF_MAP);
        assertNotNull(baRefCharacteristics);
        assertEquals(1, baRefCharacteristics.size());
        assertEquals(BILLING_ACCOUNT_REF_ID, baRefCharacteristics.get(ORDER_ITEM_ID_3));
    }

    @Test
    @DisplayName("Given items requiring appointment ref only, " +
            "when requiredCharacteristics is called, " +
            "then return characteristic list with 2 characteristics")
    void shouldReturnTwoCharacteristicsWhenItemsRequiringAppointmentOnly() {
        // Given
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, Collections.emptyList());
        extendedState.getVariables().put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, Collections.emptyList());
        extendedState.getVariables().put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF,
                List.of(ORDER_ITEM_ID_3));

        StateContext<String, String> ctx = new DefaultStateContext<>(
                StateContext.Stage.TRANSITION, null, null, extendedState,
                null, null, null, null, null);

        // When
        List<CharacteristicSpecification> result =
                completeOrderUserAction.requiredCharacteristics(mock(StateMachineTransition.class),
                        ctx.getExtendedState().getVariables());

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Given unpaid order items and the same item requiring appointment ref " +
            "when requiredCharacteristics is called " +
            "then return characteristic list with 5 characteristics")
    void shouldReturnFiveCharacteristicsWhenUnpaidItemsAndAppointmentRefOverlap() {
        // Given
        StateContext<String, String> context = mockStateContext();
        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF,
                        List.of(ORDER_ITEM_ID_1));

        // When
        List<CharacteristicSpecification> result =
                completeOrderUserAction.requiredCharacteristics(mock(StateMachineTransition.class),
                        context.getExtendedState().getVariables());

        // Then
        assertEquals(7, result.size());
    }

    private DefaultStateContext<String, String> mockStateContext() {
        ExtendedState extendedState = new DefaultExtendedState();

        List<String> unpaidOrderItemIds = Arrays.asList(ORDER_ITEM_ID_1, ORDER_ITEM_ID_2);
        List<String> orderItemIdsRequiringBARef = List.of(ORDER_ITEM_ID_3);

        extendedState.getVariables().put(OrderCaptureConstants.UNPAID_ORDER_ITEM_IDS, unpaidOrderItemIds);
        extendedState.getVariables().put(OrderCaptureConstants.ORDER_ITEM_IDS_REQUIRING_BA_REF, orderItemIdsRequiringBARef);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, null, null, null, null);
    }

    private TaskFlowUpdate createTaskFlowUpdate() {
        List<Characteristic> characteristics = new ArrayList<>();

        String paymentCharacteristicId1 = UUID.randomUUID().toString();
        String paymentCharacteristicId2 = UUID.randomUUID().toString();
        characteristics.add(createPaymentRefCharacteristic(paymentCharacteristicId1, PAYMENT_REF_ID_1, PAYMENT_REF_ID_2));
        characteristics.add(createPaymentRefCharacteristic(paymentCharacteristicId2, PAYMENT_REF_ID_3, null));

        String billingCharacteristicId = UUID.randomUUID().toString();
        characteristics.add(createBillingAccountRefCharacteristic(billingCharacteristicId));

        characteristics.add(createOrderItemCharacteristic(ORDER_ITEM_ID_1, paymentCharacteristicId1));
        characteristics.add(createOrderItemCharacteristic(ORDER_ITEM_ID_2, paymentCharacteristicId2));
        characteristics.add(createOrderItemCharacteristic(ORDER_ITEM_ID_3, billingCharacteristicId));

        TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate();
        taskFlowUpdate.setCharacteristic(characteristics);
        return taskFlowUpdate;
    }

    private ObjectCharacteristic createPaymentRefCharacteristic(String characteristicId, String refId1, String refId2) {
        PaymentRef paymentRef = new PaymentRef();
        paymentRef.setPaymentRefIdentifier(createPaymentRefIdentifiers(refId1, refId2));

        return createObjectCharacteristic(characteristicId, OrderCaptureConstants.PAYMENT_REF, paymentRef);
    }

    private List<PaymentRefIdentifier> createPaymentRefIdentifiers(String... refIds) {
        List<PaymentRefIdentifier> identifiers = new ArrayList<>();
        for (String refId : refIds) {
            if (refId != null) {
                identifiers.add(createPaymentRefIdentifier(refId));
            }
        }
        return identifiers;
    }

    private PaymentRefIdentifier createPaymentRefIdentifier(String id) {
        PaymentRefIdentifier identifier = new PaymentRefIdentifier();
        identifier.setId(id);
        return identifier;
    }

    private ObjectCharacteristic createBillingAccountRefCharacteristic(String characteristicId) {
        BillingAccountRef billingAccountRef = new BillingAccountRef();
        billingAccountRef.setBillingAccountRefIdentifier(CompleteOrderUserActionTest.BILLING_ACCOUNT_REF_ID);

        return createObjectCharacteristic(characteristicId, OrderCaptureConstants.BILLING_ACCOUNT_REF, billingAccountRef);
    }

    private ObjectCharacteristic createOrderItemCharacteristic(String orderItemId, String relationshipId) {
        ObjectCharacteristic characteristic = createObjectCharacteristic(UUID.randomUUID().toString(), OrderCaptureConstants.ORDER_ITEM, orderItemId);

        if (relationshipId != null) {
            CharacteristicRelationship relationship = new CharacteristicRelationship();
            relationship.setId(relationshipId);
            relationship.setRelationshipType(OrderCaptureConstants.REQUIRES);
            characteristic.setCharacteristicRelationship(List.of(relationship));
        } else {
            characteristic.setCharacteristicRelationship(Collections.emptyList());
        }

        return characteristic;
    }

    private ObjectCharacteristic createObjectCharacteristic(String characteristicId, String name, Object value) {
        ObjectCharacteristic characteristic = new ObjectCharacteristic();
        characteristic.setId(characteristicId);
        characteristic.setName(name);
        characteristic.setValueType(OBJECT_TYPE);
        characteristic.setValue(value);
        return characteristic;
    }
}