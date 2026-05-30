// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.guard;

import com.orange.discobole.ordermanagement.ordercapture.constant.GuardNameConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.productinventory.dto.v1.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.ObjectState;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.InitialTransition;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.MIGRATE_FROM;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.RELIES_ON;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationProductIdentifierGuardTest {

    public static final String PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String CONTRACT_PRODUCT_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String START_STATE = "start";
    private static final String EVENT = "event";

    @Mock
    private ProductInventoryService productInventoryService;

    @InjectMocks
    private ValidationProductIdentifierGuard validationProductIdentifierGuard;

    @DisplayName("Given re-executed validationProductIdentifier guard, " +
            "when checking product in product inventory, " +
            "then return true")
    @Test
    void shouldReturnTrueWhenReExecuted() {
        // Given
        StateContext<String, String> context = mockStateContext();
        StateMachineUtil.setGuardContext(context, Boolean.TRUE, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
    }

    @DisplayName("Given an invalid product id, " +
            "when checking product in product inventory, " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void shouldReturnFalseWhenProductIdIsInvalid() {
        // Given
        StateContext<String, String> context = mockStateContext();
        when(productInventoryService.getProductById(anyString())).thenThrow(new RuntimeException("Service exception"));

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);

        // Then
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();

        assertEquals(INTERNAL_SERVER_ERROR, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given a not exist product, " +
            "when checking product in product inventory, " +
            "then handleValidationException is triggered and returns false")
    @Test
    void shouldHandleExceptionDuringProductValidation() {
        // Given
        StateContext<String, String> context = mockStateContext();
        when(productInventoryService.getProductById(anyString())).thenThrow(new DiscoException(SELECTED_PRODUCT_DOES_NOT_EXIST));

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);

        // Then
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();

        assertEquals(SELECTED_PRODUCT_DOES_NOT_EXIST, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given valid contract product id and unreachable product Inventory service, " +
            "when checking product in product inventory, " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void shouldReturnFalseWhenUnreachableProductInventoryService() {
        // Given
        when(productInventoryService.getProductById(anyString())).thenThrow(new DiscoException(PRODUCT_INVENTORY_SERVICE_UNREACHABLE));

        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();

        assertEquals(PRODUCT_INVENTORY_SERVICE_UNREACHABLE, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given not valid contract product, " +
            "when checking product in product inventory, " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @ParameterizedTest
    @MethodSource("invalidContractUseCases")
    void shouldReturnFalseWhenInvalidContractProduct(String productOfferingType, ProductOperationalStatusType operationalStatusType) {
        // Given
        Product product = createProduct(productOfferingType, operationalStatusType, null);

        when(productInventoryService.getProductById(anyString())).thenReturn(product);
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();

        assertEquals(SELECTED_PRODUCT_NOT_VALID, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given valid contract product id without migrate from relationship, " +
            "when checking product in product inventory, " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @ParameterizedTest
    @MethodSource("validContractUseCases")
    void shouldReturnTrueWhenValidContractProductWithNoMigrateFromRelationship(List<ProductRelationship> productRelationshipList) {
        // Given
        Product product = createProduct(CONTRACT_PRODUCT_OFFERING_TYPE, ProductOperationalStatusType.ACTIVE, productRelationshipList);
        when(productInventoryService.getProductById(anyString())).thenReturn(product);
        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

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

    @DisplayName("Given valid contract product id having migrate from relationship and unreachable product service, " +
            "when checking product in product inventory, " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void shouldReturnFalseWhenExceptionOnGetProducts() {
        // Given
        ProductRelationship productRelationship = ProductRelationship.builder()
                .relationshipType(MIGRATE_FROM)
                .product(ProductRef.builder()
                        .id(PRODUCT_ID).build())
                .build();
        Product product = createProduct(CONTRACT_PRODUCT_OFFERING_TYPE, ProductOperationalStatusType.ACTIVE, Collections.singletonList(productRelationship));
        when(productInventoryService.getProductById(anyString())).thenReturn(product);
        when(productInventoryService.getProductByIds(any())).thenThrow(new DiscoException(PRODUCT_INVENTORY_SERVICE_UNREACHABLE));

        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();

        assertEquals(PRODUCT_INVENTORY_SERVICE_UNREACHABLE, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @DisplayName("Given valid contract product id with migrate from Product not terminated, " +
            "when checking product in product inventory, " +
            "then the result of the guard in context is set to false " +
            "and return false")
    @Test
    void shouldReturnFalseWhenMigrateFromProductNotTerminated() {
        // Given
        ProductRelationship productRelationship = ProductRelationship.builder()
                .relationshipType(MIGRATE_FROM)
                .product(ProductRef.builder()
                        .id(PRODUCT_ID).build())
                .build();

        Product product1 = Product.builder()
                .id(PRODUCT_ID)
                .operationalStatus(ProductOperationalStatusType.PENDINGMODIFICATION)
                .build();

        Product product = createProduct(CONTRACT_PRODUCT_OFFERING_TYPE, ProductOperationalStatusType.ACTIVE, Collections.singletonList(productRelationship));

        when(productInventoryService.getProductById(anyString())).thenReturn(product);
        when(productInventoryService.getProductByIds(any())).thenReturn(Collections.singletonList(product1));

        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify();

        StepVerifier.create(guardResultContext)
                .expectNext(false)
                .expectComplete()
                .verify();

        assertEquals(SELECTED_PRODUCT_NOT_VALID, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }


    @DisplayName("Given valid contract product id with migrate from product is terminated, " +
            "when checking product in product inventory, " +
            "then the result of the guard in context is set to true " +
            "and return true")
    @ParameterizedTest
    @MethodSource("validMigrateFromContractUseCases")
    void shouldReturnTrueWhenValidContractProduct(List<Product> products) {
        // Given
        ProductRelationship productRelationship = ProductRelationship.builder()
                .relationshipType(MIGRATE_FROM)
                .product(ProductRef.builder()
                        .id(PRODUCT_ID).build())
                .build();

        Product product = createProduct(CONTRACT_PRODUCT_OFFERING_TYPE, ProductOperationalStatusType.ACTIVE, Collections.singletonList(productRelationship));

        when(productInventoryService.getProductById(anyString())).thenReturn(product);
        when(productInventoryService.getProductByIds(any())).thenReturn(products);

        StateContext<String, String> context = mockStateContext();

        // When
        Mono<Boolean> result = validationProductIdentifierGuard.apply(context);
        Mono<Boolean> guardResultContext = StateMachineUtil.getGuardResult(context, GuardNameConstants.VALIDATION_PRODUCT_IDENTIFIER_GUARD);

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


    private DefaultStateContext<String, String> mockStateContext() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CONTRACT_PRODUCT_ID, CONTRACT_PRODUCT_ID);
        State<String, String> state = new ObjectState<>(START_STATE);
        Transition<String, String> transition = new InitialTransition<>(state);
        Message<String> event = MessageBuilder.withPayload(EVENT).build();
        MessageHeaders header = new MessageHeaders(new HashMap<>());
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(Collections.emptyList(), Collections.emptyList(), state, transition, event,
                extendedState, UUID.randomUUID());
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, event, header, extendedState, transition, stateMachine, state,
                state, null);
    }

    Product createProduct(String productOfferingType, ProductOperationalStatusType operationalStatusType, List<ProductRelationship> productRelationshipList) {
        Product product = Product.builder()
                .id(CONTRACT_PRODUCT_ID)
                .operationalStatus(operationalStatusType)
                .productRelationship(productRelationshipList)
                .build();
        if (productOfferingType != null) {
            product.setProductOffering(ProductOfferingRef.builder()
                    .id(PRODUCT_OFFERING_ID)
                    .atType(productOfferingType)
                    .build());
        }
        return product;
    }

    private static Stream<Arguments> invalidContractUseCases() {
        return Stream.of(
                Arguments.of(CONTRACT_PRODUCT_OFFERING_TYPE, ProductOperationalStatusType.PENDINGMODIFICATION),
                Arguments.of(null, ProductOperationalStatusType.ACTIVE),
                Arguments.of(ATOMIC_PRODUCT_OFFERING_TYPE, ProductOperationalStatusType.ACTIVE)
        );
    }

    private static Stream<Arguments> validContractUseCases() {
        ProductRelationship productRelationship1 = ProductRelationship.builder()
                .relationshipType(RELIES_ON)
                .product(ProductRef.builder()
                        .id(PRODUCT_ID).build())
                .build();
        ProductRelationship productRelationship2 = ProductRelationship.builder()
                .relationshipType(RELIES_ON)
                .product(Product.builder()
                        .id(PRODUCT_ID).build())
                .build();

        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(Collections.emptyList()),
                Arguments.of(Collections.singletonList(productRelationship1)),
                Arguments.of(Collections.singletonList(productRelationship2))

        );
    }

    private static Stream<Arguments> validMigrateFromContractUseCases() {

        Product product = Product.builder()
                .id(PRODUCT_ID)
                .operationalStatus(ProductOperationalStatusType.TERMINATED)
                .build();

        return Stream.of(
                Arguments.of(Collections.emptyList()),
                Arguments.of(Collections.singletonList(product))
        );
    }
}