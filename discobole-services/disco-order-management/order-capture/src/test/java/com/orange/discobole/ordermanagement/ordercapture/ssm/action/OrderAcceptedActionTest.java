// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.producer.ProductOrderCommandProducer;
import com.orange.discobole.ordermanagement.ordercapture.service.OrderValidityService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType.ACCEPTED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class OrderAcceptedActionTest {

    private static final String HREF = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final Instant CREATION_DATE = Instant.now();
    private static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderService productOrderService;

    @Mock
    private ProductOrderCommandProducer productOrderCommandProducer;

    @InjectMocks
    private OrderAcceptedAction orderAcceptedAction;
    @Mock
    private OrderValidityService validityCharacteristicHelper;

    @Test
    @DisplayName("Given re-executed order update action, " +
            "when applying, " +
            "then 'isProductOrderUpdated' is set to false")
    void shouldSetProductOrderUpdatedFalseOnReExecution() {
        // Given
        StateContext<String, String> context = createMockStateContext(null);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, TRUE);

        // When
        Mono<Void> result = orderAcceptedAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean isUpdated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, FALSE);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    @DisplayName("Given exception during get product order, " +
            "when applying, " +
            "then 'isProductOrderUpdated' is false and description is set")
    void shouldHandleExceptionInGetProductOrder() {
        // Given
        StateContext<String, String> context = createMockStateContext(null);

        // When
        Mono<Void> result = orderAcceptedAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Mockito.verify(productOrderCommandProducer, Mockito.times(0)).publishCommand(any(), any());
        boolean isUpdated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, FALSE);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(isUpdated);
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, description);
    }

    @Test
    @DisplayName("Given exception during update product order, " +
            "when applying, " +
            "then 'isProductOrderUpdated' is false")
    void shouldSetProductOrderUpdatedFalseOnUpdateException() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = createMockStateContext(productOrder);

        doNothing().when(validityCharacteristicHelper).adjustOrderRequestedCompletionDate(productOrder, RELATED_PARTY_ID);
        doNothing().when(validityCharacteristicHelper).handleInvalidValidityCharacteristics(productOrder, RELATED_PARTY_ID, true);
        doThrow(RuntimeException.class).when(productOrderService).updateProductOrderInventoryState(productOrder, ACCEPTED);

        // When
        Mono<Void> result = orderAcceptedAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        boolean isUpdated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, FALSE);
        Assertions.assertFalse(isUpdated);
    }

    @Test
    @DisplayName("Given valid product order, " +
            "when applying acceptance, " +
            "then 'isProductOrderUpdated' is true and event is published")
    void shouldUpdateProductOrderAndPublishEvent() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = createMockStateContext(productOrder);

        doNothing().when(validityCharacteristicHelper).adjustOrderRequestedCompletionDate(productOrder, RELATED_PARTY_ID);
        doNothing().when(validityCharacteristicHelper).handleInvalidValidityCharacteristics(productOrder, RELATED_PARTY_ID, true);

        // When
        Mono<Void> result = orderAcceptedAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Mockito.verify(productOrderService, Mockito.times(1)).updateProductOrderInventoryState(productOrder, ACCEPTED);
        boolean isUpdated = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_PRODUCT_ORDER_UPDATED, TRUE);
        Assertions.assertTrue(isUpdated);
    }

    private ProductOrder createProductOrder() {
        ProductOrderItem item = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID)
                .state(ProductOrderItemStateType.DRAFT)
                .build();

        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .href(HREF)
                .creationDate(CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(Collections.singletonList(item))
                .build();
    }

    private DefaultStateContext<String, String> createMockStateContext(ProductOrder productOrder) {
        ExtendedState extendedState = new DefaultExtendedState();
        com.orange.discobole.processflow.dto.generated.RelatedParty relatedParty = new com.orange.discobole.processflow.dto.generated.RelatedParty();
        relatedParty.setId(RELATED_PARTY_ID);
        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, relatedParty);
        if (Objects.nonNull(productOrder)) {
            extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        }

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null, extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null, null, null);
    }
}