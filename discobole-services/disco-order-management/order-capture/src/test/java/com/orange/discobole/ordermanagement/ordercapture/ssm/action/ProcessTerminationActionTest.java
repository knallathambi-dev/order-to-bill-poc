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
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.service.ResourceInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessTerminationActionTest {

    public static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(10);
    public static final String CONTRACT_PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);
    public static final Instant DEFAULT_CREATION_DATE = Instant.now();
    public static final String DEFAULT_RESOURCE_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String STATE_1 = "STATE1";
    public static final String STATE_2 = "STATE2";
    public static final String EVENT_1 = "EVENT1";
    public static final String PRODUCT_ORDER_TYPE = "ProductOrder";
    public static final String PRODUCT_TYPE = "Product";
    public static final String PRODUCT_REF_TYPE = "ProductRef";

    @InjectMocks
    private ProcessTerminationAction processTerminationAction;

    @Mock
    private ProductOrderService productOrderService;
    @Mock
    private ProductInventoryService productInventoryService;
    @Mock
    private ResourceInventoryService resourceInventoryService;

    @DisplayName("Given re-executed process termination, " +
            "when applying termination, " +
            "then process will not terminate")
    @Test
    void shouldNotTerminateAlreadyTerminatedProcess() {
        // Given
        StateContext<String, String> context = mockStateContext(null, false, null, "migrate");
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(context.getStateMachine().getTransitions().isEmpty());
        Assertions.assertFalse(context.getStateMachine().isComplete());
    }

    @DisplayName("Given empty products, " +
            "when terminating process, " +
            "then process will terminate successfully")
    @Test
    void shouldTerminateProcessWithNoProducts() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, null, "migrate");
        doNothing().when(productOrderService).updateProductOrderInventoryState(any(), any());
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(Collections.emptyList());

        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        verify(productInventoryService, times(0)).cancelProducts(any());
        verify(productInventoryService, times(0)).updateProducts(any());
        Assertions.assertTrue(context.getStateMachine().getTransitions().isEmpty());
    }

    @DisplayName("Given exception on rolling back reserved resource, " +
            "when terminating process, " +
            "then process will be terminated with error description")
    @Test
    void shouldHandleExceptionOnRollingBackReservedResource() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, false, null, "migrate");
        doThrow(new DiscoException(ExceptionMessage.ERROR_WHILE_ROLLING_BACK_RESERVED_RESOURCE))
                .when(resourceInventoryService).rollBackReservedResource(anyList());

        // When
        processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(0)).cancelProducts(any());
        verify(productInventoryService, times(0)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(isEmptyTransitions);
        Assertions.assertEquals(DescriptionConstants.RESOURCES_CANCELLATION_ERROR, description);
    }

    @DisplayName("Given exception on cancel product, " +
            "when terminating process, " +
            "then process will be terminated with error description")
    @Test
    void shouldHandleExceptionOnCancelingProducts() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, null, "migrate");
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProducts(com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED);
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(products);
        doThrow(new DiscoException(ExceptionMessage.ERROR_CANCELLING_PRODUCTS))
                .when(productInventoryService).cancelProducts(List.of(PRODUCT_ID_1, PRODUCT_ID_2));

        // When
        processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(1)).cancelProducts(any());
        verify(productInventoryService, times(0)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(isEmptyTransitions);
        Assertions.assertEquals(DescriptionConstants.PRODUCT_UPDATE_ERROR, description);
    }

    @DisplayName("Given exception on get product, " +
            "when terminating process, " +
            "then process will be terminated with error description")
    @Test
    void shouldHandleExceptionOnGettingProducts() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, null, "migrate");
        doThrow(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE))
                .when(productInventoryService).getProductsByProductOrderId(any());

        //when
        processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(0)).cancelProducts(any());
        verify(productInventoryService, times(0)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(isEmptyTransitions);
        Assertions.assertEquals(DescriptionConstants.PRODUCT_UPDATE_ERROR, description);
    }

    @DisplayName("Given exception on updating order status, " +
            "when terminating process, " +
            "then process will be terminated with internal server error")
    @Test
    void shouldHandleExceptionOnUpdatingProductOrderStatus() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, false, null, "terminate");
        doAnswer(invocation -> {
            throw new RuntimeException();
        }).when(productOrderService).updateProductOrderInventoryState(any(ProductOrder.class), eq(ProductOrderStateType.CANCELLED));

        // When
        processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(0)).cancelProducts(any());
        verify(productInventoryService, times(0)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(isEmptyTransitions);
        Assertions.assertEquals(DescriptionConstants.INTERNAL_SERVER_ERROR, description);
    }

    @DisplayName("Given acquisition order to be terminated, " +
            "when terminating process, " +
            "then process will terminate successfully")
    @Test
    void shouldTerminateProcessSuccessfullyWhenContractProductIdIsNull() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, null, "add");
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProducts(com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED);
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(products);

        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(1)).cancelProducts(any());
        verify(productInventoryService, times(0)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(isEmptyTransitions);
    }

    @DisplayName("Given exception on updating products, " +
            "when terminating process, " +
            "then process will be terminated with error description")
    @Test
    void shouldHandleExceptionOnUpdatingContractProductStatus() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, CONTRACT_PRODUCT_ID, "add");
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProducts(com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE);
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(products);
        doThrow(DiscoException.class).when(productInventoryService).updateProducts(any());

        // When
        processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(0)).cancelProducts(any());
        verify(productInventoryService, times(1)).updateProducts(any());

        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        Assertions.assertFalse(isEmptyTransitions);
        Assertions.assertEquals(DescriptionConstants.PRODUCT_UPDATE_ERROR, description);

    }

    @DisplayName("Given not acquisition order to be terminated with active products, " +
            "when terminating process, " +
            "then process will terminate successfully")
    @Test
    void shouldTerminateProcessSuccessfullyWhenNotAcquisitionOrderAndProductsAreActive() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, CONTRACT_PRODUCT_ID, "terminate");
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProducts(com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE);
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(products);

        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(0)).cancelProducts(any());
        verify(productInventoryService, times(1)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(isEmptyTransitions);
    }

    @DisplayName("Given not acquisition order to be terminated with sold products, " +
            "when terminating process, " +
            "then process will terminate successfully")
    @Test
    void shouldTerminateProcessSuccessfullyWhenNotAcquisitionOrderAndProductsAreSold() {
        // Given
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, CONTRACT_PRODUCT_ID, "terminate");
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProducts(com.orange.discobole.productinventory.dto.v1.ProductStatusType.SOLD);
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(products);

        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(0)).cancelProducts(any());
        verify(productInventoryService, times(1)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(isEmptyTransitions);
    }

    @DisplayName("Given not acquisition order to be terminated with sold products, " +
            "when terminating process, " +
            "then process will terminate successfully")
    @Test
    void shouldTerminateProcessSuccessfullyWhenOrderAndProductsAreActiveForMigration() {
        // Given
        RelatedProductOrderItem addRelatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.ADD)
                .build();
        com.orange.discobole.productinventory.dto.v1.Product reliesFromProduct = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE)
                        .build())
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_1)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .status(com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE)
                .build();
        ProductOrder productOrder = createProductOrder();
        StateContext<String, String> context = mockStateContext(productOrder, true, CONTRACT_PRODUCT_ID, "migrate");
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProductsForMigration(com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED);
        when(productInventoryService.getProductsByProductOrderId(DEFAULT_PRODUCT_ORDER_ID)).thenReturn(products);
        doNothing()
                .when(productInventoryService).cancelProducts(List.of(PRODUCT_ID_1, PRODUCT_ID_2));
        when(productInventoryService.getProductById(any())).thenReturn(reliesFromProduct);

        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(1)).cancelProducts(any());
        verify(productInventoryService, times(2)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(isEmptyTransitions);
    }

    @Test
    @DisplayName("Given a valid products , " +
            "when revertRelationships is called, " +
            "then reliesOn relationships are reverted ")
    void shouldRevertReliesOnRelationshipsSuccessfullyForValidProductsForMigration() {
        // Given
        RelatedProductOrderItem addRelatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.ADD)
                .build();
        com.orange.discobole.productinventory.dto.v1.Product reliesFromProduct = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE)
                        .build())
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_1)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .status(com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE)
                .build();
        ProductOrder productOrder = createProductOrder();
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProductHierarchy();
        StateContext<String, String> context = mockStateContext(productOrder, true, CONTRACT_PRODUCT_ID, "migrate");
        when(productInventoryService.getProductsByProductOrderId(DEFAULT_PRODUCT_ORDER_ID)).thenReturn(products);
        when(productInventoryService.getProductById(any())).thenReturn(reliesFromProduct);


        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(1)).cancelProducts(any());
        verify(productInventoryService, times(2)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(isEmptyTransitions);
    }

    @Test
    @DisplayName("Given a valid products , " +
            "when revertRelationships is called, " +
            "then bundles relationships are reverted ")
    void shouldRevertReliesOnRelationshipsSuccessfullyForValidProductsForModification() {
        // Given
        RelatedProductOrderItem addRelatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.ADD)
                .build();
        com.orange.discobole.productinventory.dto.v1.Product reliesFromProduct = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_1)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .status(com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE)
                .build();

        com.orange.discobole.productinventory.dto.v1.Product parentLevelProduct = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_1)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();
        ProductOrder productOrder = createProductOrder();
        List<com.orange.discobole.productinventory.dto.v1.Product> products = createProductHierarchy();
        StateContext<String, String> context = mockStateContext(productOrder, true, CONTRACT_PRODUCT_ID, "modify");
        when(productInventoryService.getProductsByProductOrderId(DEFAULT_PRODUCT_ORDER_ID)).thenReturn(products);
        when(productInventoryService.getProductById(any())).thenReturn(reliesFromProduct);
        when(productInventoryService.getProductByIds(any())).thenReturn(List.of(parentLevelProduct));


        // When
        Mono<Void> result = processTerminationAction.apply(context);

        // Then
        verify(productInventoryService, times(1)).cancelProducts(any());
        verify(productInventoryService, times(2)).updateProducts(any());
        boolean isEmptyTransitions = context.getStateMachine().getTransitions().isEmpty();
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertTrue(isEmptyTransitions);
    }

    private List<com.orange.discobole.productinventory.dto.v1.Product> createProductHierarchy() {
        RelatedProductOrderItem addRelatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(DEFAULT_PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.ADD)
                .build();

        com.orange.discobole.productinventory.dto.v1.Product reliesFromProduct = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id("123")
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE)
                        .build())
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_1)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .status(com.orange.discobole.productinventory.dto.v1.ProductStatusType.ACTIVE)
                .build();


        com.orange.discobole.productinventory.dto.v1.Product product1 = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_1)
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE)
                        .build())
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_ON)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id("123")
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .status(com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED)
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .build();

        RelatedProductOrderItem migrateRelatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(DEFAULT_PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.MIGRATE)
                .build();

        com.orange.discobole.productinventory.dto.v1.Product product2 = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE)
                        .build())
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.RELIES_ON)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id("456")
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.MIGRATE_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id("123")
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .status(com.orange.discobole.productinventory.dto.v1.ProductStatusType.CREATED)
                .productOrderItem(List.of(migrateRelatedProductOrderItem))
                .build();

        com.orange.discobole.productinventory.dto.v1.Product parentLevelProduct = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE)
                        .build())
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_1)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id(PRODUCT_ID_2)
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build(), com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                .id("123")
                                .atType(PRODUCT_REF_TYPE)
                                .build())
                        .build()))
                .build();
        return List.of(parentLevelProduct, product1, product2, reliesFromProduct);

    }

    private ProductOrder createProductOrder() {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID)
                .state(ProductOrderItemStateType.DRAFT)
                .product(Product.builder()
                        .id(PRODUCT_ID_1)
                        .realizingResource(Collections.singletonList(ResourceRef.builder()
                                .id(DEFAULT_RESOURCE_ID)
                                .atType(OrderCaptureConstants.LOGICAL_RESOURCE)
                                .build()))
                        .productRelationship(List.of(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRelationship.builder()
                                .relationshipType("hasParent")
                                .product(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef.builder()
                                        .id(PRODUCT_ID_2)
                                        .build())
                                .build()))
                        .atType(PRODUCT_TYPE)
                        .build())
                .build();
        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(Collections.singletonList(productOrderItem))
                .atType(PRODUCT_ORDER_TYPE)
                .build();
    }

    private StateContext<String, String> mockStateContext(ProductOrder productOrder, boolean areProductsCreated, String contractProductId, String requestedConfigurationAction) {
        ExtendedState extendedState = new DefaultExtendedState();
        if (Objects.nonNull(productOrder)) {
            extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
            extendedState.getVariables().put(OrderCaptureConstants.ARE_PRODUCTS_CREATED, areProductsCreated);
            extendedState.getVariables().put(OrderCaptureConstants.REQUESTED_CONFIGURATION_ACTION, requestedConfigurationAction);

        }
        if (Objects.nonNull(contractProductId)) {
            extendedState.getVariables().put(OrderCaptureConstants.CONTRACT_PRODUCT_ID, contractProductId);
        }

        return new DefaultStateContext<>(
                StateContext.Stage.TRANSITION,
                null,
                null,
                extendedState,
                null,
                createInitializedStateMachine(),
                null,
                null,
                null
        );
    }

    private StateMachine<String, String> createStateMachine() throws Exception {
        var builder = StateMachineBuilder.<String, String>builder();
        builder.configureConfiguration()
                .withConfiguration()
                .autoStartup(true);
        builder.configureStates()
                .withStates()
                .initial(STATE_1)
                .state(STATE_2);
        builder.configureTransitions()
                .withExternal()
                .source(STATE_1).target(STATE_2).event(EVENT_1);
        return builder.build();
    }

    private StateMachine<String, String> createInitializedStateMachine() {
        try {
            return createStateMachine();
        } catch (Exception e) {
            throw new DiscoException();
        }
    }

    private List<com.orange.discobole.productinventory.dto.v1.Product> createProducts(com.orange.discobole.productinventory.dto.v1.ProductStatusType productStatusType) {
        com.orange.discobole.productinventory.dto.v1.Product product1 = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_1)
                .status(productStatusType)
                .build();

        com.orange.discobole.productinventory.dto.v1.Product product2 = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .status(productStatusType)
                .build();
        return List.of(product1, product2);
    }

    private List<com.orange.discobole.productinventory.dto.v1.Product> createProductsForMigration(com.orange.discobole.productinventory.dto.v1.ProductStatusType productStatusType) {
        com.orange.discobole.productinventory.dto.v1.ProductRelationship productRelationship = com.orange.discobole.productinventory.dto.v1.ProductRelationship
                .builder()
                .relationshipType(OrderCaptureConstants.RELIES_ON)
                .product(com.orange.discobole.productinventory.dto.v1.ProductRef
                        .builder().id("123")
                        .build())
                .build();


        RelatedProductOrderItem addRelatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(DEFAULT_PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.ADD)
                .build();
        com.orange.discobole.productinventory.dto.v1.Product product1 = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_1)
                .status(productStatusType)
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE)
                        .build())
                .productRelationship(List.of(productRelationship))
                .productOrderItem(List.of(addRelatedProductOrderItem))
                .build();
        RelatedProductOrderItem migrateRelatedProductOrderItem = RelatedProductOrderItem
                .builder()
                .productOrderId(DEFAULT_PRODUCT_ORDER_ID)
                .orderItemAction(OrderCaptureConstants.MIGRATE)
                .build();

        com.orange.discobole.productinventory.dto.v1.Product product2 = com.orange.discobole.productinventory.dto.v1.Product.builder()
                .id(PRODUCT_ID_2)
                .status(productStatusType)
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id("123")
                        .atType(ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE)
                        .build())
                .productRelationship(List.of(productRelationship))
                .productOrderItem(List.of(migrateRelatedProductOrderItem))
                .build();
        return List.of(product1, product2);
    }
}