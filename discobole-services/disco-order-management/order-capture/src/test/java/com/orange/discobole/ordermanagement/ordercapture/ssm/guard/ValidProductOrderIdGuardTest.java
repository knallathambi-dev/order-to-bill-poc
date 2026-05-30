// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItemStateType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.processflow.dto.generated.RelatedParty;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ValidProductOrderIdGuardTest {
    private static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final Instant DEFAULT_CREATION_DATE = Instant.now();
    private static final String DEFAULT_PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED = RandomStringUtils.randomAlphabetic(5);
    private static final String INVALID_RELATED_PARTY_ID = "226-mfXX";
    private static final String VALID_RELATED_PARTY_ID = "226-mf30";

    @InjectMocks
    private ValidProductOrderIdGuard validProductOrderIdGuard;

    @Test
    @DisplayName("Given re-executed validProductOrderId guard, " +
            "when checking if the product order Id is valid, " +
            "then return true")
    void shouldReturnTrueWhenReExecuted() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_PRODUCT_ORDER_ID, createRelatedParty(VALID_RELATED_PARTY_ID));
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = validProductOrderIdGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Given invalid product order id and valid related party id, " +
            "when checking if the product order Id is valid, " +
            "then the result of the guard in context is set to false " +
            "and description is set to VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED and return false")
    void shouldReturnFalseWhenInvalidProductOrderIdAndValidRelatedPartyId() {
        // Given
        StateContext<String, String> context = mockStateContext(VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED, createRelatedParty(VALID_RELATED_PARTY_ID));

        // When
        Mono<Boolean> result = validProductOrderIdGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given valid product order id and valid related party id, " +
            "when checking if the product order Id is valid, " +
            "then the result of the guard in context is set to true " +
            "and return true")
    void shouldReturnTrueWhenValidProductOrderIdAndValidRelatedPartyId() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_PRODUCT_ORDER_ID, createRelatedParty(VALID_RELATED_PARTY_ID));

        // When
        Mono<Boolean> result = validProductOrderIdGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Given invalid product order id and invalid related party id, " +
            "when checking if the product order id is valid, " +
            "then the result of the guard in context is set to false " +
            "and description is set to VALID_PARTY_IDENTIFIER_REQUIRED and return false")
    void shouldReturnFalseWhenInvalidProductOrderIdAndInvalidRelatedPartyId() {
        // Given
        StateContext<String, String> context = mockStateContext(VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED, createRelatedParty(INVALID_RELATED_PARTY_ID));

        // When
        Mono<Boolean> result = validProductOrderIdGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given valid product order id and invalid related party id, " +
            "when checking if the product order Id is valid, " +
            "then the result of the guard in context is set to true " +
            "and description is set to VALID_PARTY_IDENTIFIER_REQUIRED and return false")
    void shouldReturnFalseWhenValidProductOrderIdAndInvalidRelatedPartyId() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_PRODUCT_ORDER_ID, createRelatedParty(INVALID_RELATED_PARTY_ID));

        // When
        Mono<Boolean> result = validProductOrderIdGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALID_PRODUCT_ORDER_ID_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();
        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();
        Assertions.assertEquals(DescriptionConstants.VALID_PARTY_IDENTIFIER_REQUIRED, context.getExtendedState()
                .getVariables().get(StateMachineUtil.DESCRIPTION));
    }

    private RelatedParty createRelatedParty(String relatedPartyId) {
        return new RelatedParty()
                .id(relatedPartyId)
                .name("Lisa")
                .role("customer")
                .referredType("individual");
    }

    private ProductOrder createProductOrder() {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .id(DEFAULT_PRODUCT_ORDER_ITEM_ID)
                .state(ProductOrderItemStateType.DRAFT)
                .build();
        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(Collections.singletonList(productOrderItem))
                .build();
    }

    private DefaultStateContext<String, String> mockStateContext(String productOrderId, RelatedParty providedRelatedParty) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.PRODUCT_ORDER_ID, productOrderId);
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createProductOrder());
        extendedState.getVariables().put(OrderCaptureConstants.RELATED_PARTY, createRelatedParty(VALID_RELATED_PARTY_ID));
        extendedState.getVariables().put(OrderCaptureConstants.TASK_RELATED_PARTY, List.of(providedRelatedParty));
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}