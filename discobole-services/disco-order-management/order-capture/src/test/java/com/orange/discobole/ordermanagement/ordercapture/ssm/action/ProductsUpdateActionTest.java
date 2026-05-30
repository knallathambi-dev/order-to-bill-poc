// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecificationRelationship;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductSpecificationService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOfferingRef;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
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
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.*;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE;
import static java.lang.Boolean.FALSE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsUpdateActionTest {

    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_5 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_6 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPEC_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPEC_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPEC_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPEC_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID_4 = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductInventoryService productInventoryService;

    @InjectMocks
    private ProductsUpdateAction productsUpdateAction;

    @Mock
    private ProductOrderService productOrderService;

    @Mock
    private ProductSpecificationService productSpecificationService;

    @Test
    @DisplayName("Given re-executed order update action, " +
            "when updating products in product inventory, " +
            "then the areProductsUpdated variable should be set to false")
    void shouldNotUpdateProductsWhenReExecuted() {
        // Given
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD, ADD);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);

        Assertions.assertFalse(areProductsUpdated);
    }

    @Test
    @DisplayName("Given an exception in getProductsByProductOrderId, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be false and product order should be Held")
    void shouldNotUpdateProductsWhenExceptionInGetProductsIdsByProductOrder() {
        // Given
        when(productInventoryService.getProductsByProductOrderId(any())).thenThrow(RuntimeException.class);
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD, ADD);
        ProductOrder productOrder =
                StateMachineUtil.getObjectValue(context, CREATED_PRODUCT_ORDER, ProductOrder.class);

        // When
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productOrderService).updateProductOrderInventoryState(productOrder, ProductOrderStateType.HELD);

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);

        Assertions.assertFalse(areProductsUpdated);
    }

    @Test
    @DisplayName("Given an exception in confirmProducts, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be false and productUpdateError set")
    void shouldNotUpdateProductsWhenExceptionInConfirmingProducts() {
        // Given
        Product product = Product.builder().id("1").status(ProductStatusType.SOLD).build();
        List<String> productIds = Arrays.asList(PRODUCT_ID_1, PRODUCT_ID_2, PRODUCT_ID_3);
        when(productInventoryService.getProductsByProductOrderId(product.getId()))
                .thenReturn(List.of(product));
        doThrow(RuntimeException.class).when(productInventoryService).confirmProducts(productIds);

        // When
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD, ADD);
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertFalse(areProductsUpdated);
        Assertions.assertEquals(DescriptionConstants.PRODUCT_UPDATE_ERROR, description);
    }

    @Test
    @DisplayName("Given valid products with ADD action, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be true")
    void shouldUpdateProductsWhenValidProductsForAdd() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem =
                RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                        .productOrderId(PRODUCT_ORDER_ID)
                        .orderItemAction("ADD")
                        .build();
        Product product =
                Product.builder()
                        .id(PRODUCT_ORDER_ID)
                        .status(ProductStatusType.SOLD)
                        .productOrderItem(Collections.singletonList(relatedProductOrderItem))
                        .build();
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(List.of(product));

        // When
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD, ADD);
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productInventoryService).confirmProducts(any());

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertTrue(areProductsUpdated);
        Assertions.assertNull(description);
    }

    @Test
    @DisplayName("Given valid products with migrate action, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be true")
    void shouldUpdateProductsWhenValidProductsForMigration() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem =
                RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                        .productOrderId(PRODUCT_ORDER_ID)
                        .orderItemAction("ADD")
                        .build();
        Product product =
                Product.builder()
                        .id(PRODUCT_ORDER_ID)
                        .status(ProductStatusType.SOLD)
                        .productOrderItem(Collections.singletonList(relatedProductOrderItem))
                        .build();
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(List.of(product));

        // When
        StateContext<String, String> context = mockStateContext(ItemActionType.MIGRATE, MIGRATE);
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productInventoryService).confirmProducts(any());

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertTrue(areProductsUpdated);
        Assertions.assertNull(description);
    }

    @Test
    @DisplayName("Given valid products with MODIFY action, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be true")
    void shouldUpdateProductsWhenValidProductsForModify() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem =
                RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                        .productOrderId(PRODUCT_ORDER_ID)
                        .orderItemAction("ADD")
                        .build();
        Product product =
                Product.builder()
                        .id(PRODUCT_ORDER_ID)
                        .status(ProductStatusType.SOLD)
                        .productOrderItem(Collections.singletonList(relatedProductOrderItem))
                        .build();
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(List.of(product));

        // When
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD, MODIFICATION);
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productInventoryService).confirmProducts(any());

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertTrue(areProductsUpdated);
        Assertions.assertNull(description);
    }

    @Test
    @DisplayName("Given valid products with DELETE action, " +
            "when terminating products in product inventory, " +
            "then areProductsUpdated should be true")
    void shouldTerminateProductsWhenValidProductsForDelete() {
        // Given
        RelatedProductOrderItem relatedProductOrderItem =
                RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                        .productOrderId(PRODUCT_ORDER_ID)
                        .orderItemAction("DELETE")
                        .build();
        Product product =
                Product.builder()
                        .id(PRODUCT_ORDER_ID)
                        .status(ProductStatusType.SOLD)
                        .productOrderItem(Collections.singletonList(relatedProductOrderItem))
                        .build();
        when(productInventoryService.getProductsByProductOrderId(any())).thenReturn(List.of(product));

        // When
        StateContext<String, String> context = mockStateContext(ItemActionType.DELETE, TERMINATION);
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productInventoryService).terminateProducts(any(), any(), anyString());

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertTrue(areProductsUpdated);
        Assertions.assertNull(description);
    }

    @Test
    @DisplayName("Given valid products with noChange action, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be true")
    void shouldUpdateProductsWhenValidProductsForMigrationForNoChangeItem() {
        // Given
        ProductSpecification productSpecification =
                ProductSpecification.builder()
                        .id("123")
                        .productSpecificationRelationship(
                                List.of(
                                        ProductSpecificationRelationship.builder()
                                                .id("456")
                                                .relationshipType("reliesOn")
                                                .build()
                                )
                        )
                        .build();
        StateContext<String, String> context = mockStateContext(ItemActionType.NOCHANGE, MIGRATE);

        // When
        when(productInventoryService.getProductsByProductOrderId(any()))
                .thenReturn(createProductsForMigration());
        when(productSpecificationService.fetchProductSpecifications(any()))
                .thenReturn(List.of(productSpecification));
        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productInventoryService).confirmProducts(any());
        verify(productInventoryService, times(1)).updateProducts(any());

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertTrue(areProductsUpdated);
        Assertions.assertNull(description);
    }

    @Test
    @DisplayName("Given valid products with add action, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be true")
    void shouldUpdateProductsWhenValidProductsForMigrationForAddItem() {
        // Given
        StateContext<String, String> context = mockStateContext(ItemActionType.ADD, MIGRATE);

        // When
        when(productInventoryService.getProductsByProductOrderId(any()))
                .thenReturn(createProductsForMigration());

        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productInventoryService).confirmProducts(any());

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertTrue(areProductsUpdated);
        Assertions.assertNull(description);
    }

    @Test
    @DisplayName("Given valid products with modify action, " +
            "when updating products in product inventory, " +
            "then areProductsUpdated should be true")
    void shouldUpdateProductsWhenValidProductsForMigrationForModifyItem() {
        // Given
        StateContext<String, String> context = mockStateContext(ItemActionType.MODIFY, MIGRATE);

        // When
        when(productInventoryService.getProductsByProductOrderId(any()))
                .thenReturn(createProductsForMigration());

        Mono<Void> result = productsUpdateAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();

        verify(productInventoryService).confirmProducts(any());
        verify(productInventoryService, times(1)).updateProducts(any());

        boolean areProductsUpdated =
                StateMachineUtil.getBooleanValue(context, ARE_PRODUCTS_UPDATED, FALSE);
        String description =
                StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);

        Assertions.assertTrue(areProductsUpdated);
        Assertions.assertNull(description);
    }

    private List<Product> createProductsForMigration() {
        RelatedProductOrderItem noChangeRPOI =
                RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_3)
                        .productOrderId(PRODUCT_ORDER_ID)
                        .orderItemAction(ItemActionType.NOCHANGE.getValue())
                        .build();
        RelatedProductOrderItem migrateRPOI =
                RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                        .productOrderId(PRODUCT_ORDER_ID)
                        .orderItemAction(ItemActionType.MIGRATE.getValue())
                        .build();

        Product noChangeProduct = Product.builder()
                .id(PRODUCT_ID_3)
                .status(ProductStatusType.CREATED)
                .productOrderItem(Collections.singletonList(noChangeRPOI))
                .productOffering(createProductOffering(PRODUCT_OFFERING_ID_3))
                .productSpecification(createProductSpec(PRODUCT_SPEC_ID_3))
                .build();

        Product migrateToProduct1 = Product.builder()
                .id(PRODUCT_ID_1)
                .status(ProductStatusType.CREATED)
                .productOrderItem(Collections.singletonList(migrateRPOI))
                .productOffering(createProductOffering(PRODUCT_OFFERING_ID_1))
                .productSpecification(createProductSpec(PRODUCT_SPEC_ID_1))
                .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship
                        .builder().relationshipType(MIGRATE_FROM)
                        .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder().id(PRODUCT_ID_2)
                                .build()).build()))
                .build();


        Product migrateFromProduct1 =
                Product.builder()
                        .id(PRODUCT_ID_2)
                        .status(ProductStatusType.ACTIVE)
                        .productOrderItem(Collections.singletonList(migrateRPOI))
                        .productOffering(createProductOffering(PRODUCT_OFFERING_ID_2))
                        .productSpecification(createProductSpec(PRODUCT_SPEC_ID_2))
                        .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                                .relationshipType(MIGRATE_FROM)
                                .product(com.orange.discobole.productinventory.dto.v1.ProductRef.builder()
                                        .id(PRODUCT_OFFERING_ID_1)
                                        .build())
                                .build()))
                        .build();

        Product migrateToProduct2 =
                Product.builder()
                        .id(PRODUCT_ID_5)
                        .status(ProductStatusType.CREATED)
                        .productOrderItem(Collections.singletonList(migrateRPOI))
                        .productOffering(createProductOffering(PRODUCT_OFFERING_ID_4))
                        .productSpecification(createProductSpec(PRODUCT_SPEC_ID_4))
                        .productRelationship(List.of(com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                                .relationshipType(MIGRATE_FROM)
                                .product(Product.builder()
                                        .id(PRODUCT_ID_3)
                                        .build())
                                .build()))
                        .build();


        Product migrateFromProduct2 =
                Product.builder()
                        .id(PRODUCT_ID_6)
                        .status(ProductStatusType.ACTIVE)
                        .productOrderItem(Collections.singletonList(migrateRPOI))
                        .productOffering(createProductOffering(PRODUCT_OFFERING_ID_4))
                        .productSpecification(createProductSpec(PRODUCT_SPEC_ID_4))
                        .build();

        return List.of(noChangeProduct, migrateFromProduct1, migrateToProduct1, migrateToProduct2, migrateFromProduct2);
    }

    private com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef createProductSpec(String productSpecId) {
        return com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef.builder()
                .id(productSpecId)
                .build();
    }

    private ProductOfferingRef createProductOffering(String productOfferingId1) {
        return ProductOfferingRef.builder()
                .id(productOfferingId1)
                .atType(ATOMIC_PRODUCT_OFFERING_TYPE)
                .build();
    }

    private DefaultStateContext<String, String> mockStateContext(ItemActionType itemActionType,
                                                                 String requestedConfigurationAction) {
        ExtendedState extendedState = new DefaultExtendedState();
        ProductOrder productOrder = createProductOrder(itemActionType);


        extendedState.getVariables().put(CREATED_PRODUCT_ORDER, productOrder);
        extendedState.getVariables().put(REQUESTED_CONFIGURATION_ACTION, requestedConfigurationAction);

        ObjectStateMachine<String, String> stateMachine =
                new ObjectStateMachine<>(null, null, null, null, null, extendedState, null);

        return new DefaultStateContext<>(
                StateContext.Stage.TRANSITION,
                null,
                null,
                extendedState,
                null,
                stateMachine,
                null,
                null,
                null
        );
    }

    private ProductOrder createProductOrder(ItemActionType itemActionType) {
        if (ItemActionType.NOCHANGE.equals(itemActionType)) {
            return ProductOrder.builder()
                    .id(PRODUCT_ORDER_ID)
                    .productOrderItem(
                            List.of(
                                    ProductOrderItem.builder()
                                            .id(PRODUCT_ORDER_ITEM_ID_1)
                                            .action(itemActionType)
                                            .productOrderItemRelationship(
                                                    List.of(
                                                            OrderItemRelationship.builder()
                                                                    .relationshipType(RelationshipType.ISCHILD)
                                                                    .id(PRODUCT_ORDER_ITEM_ID_1)
                                                                    .build()
                                                    )
                                            )
                                            .product(
                                                    com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                                                            .id("456")
                                                            .productSpecification(
                                                                    creatProductSpecification(itemActionType)
                                                            )
                                                            .build()
                                            )
                                            .build(),
                                    ProductOrderItem.builder()
                                            .id(PRODUCT_ORDER_ITEM_ID_1)
                                            .productOffering(
                                                    com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOfferingRef
                                                            .builder()
                                                            .id("234")
                                                            .atType(ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE)
                                                            .build()
                                            )
                                            .action(ItemActionType.ADD)
                                            .build(),
                                    ProductOrderItem.builder()
                                            .id(PRODUCT_ORDER_ITEM_ID_1)
                                            .action(ItemActionType.ADD)
                                            .build()
                            )
                    )
                    .build();

        }
        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .productOrderItem(
                        List.of(
                                ProductOrderItem.builder()
                                        .id(PRODUCT_ORDER_ITEM_ID_1)
                                        .action(itemActionType)
                                        .productOrderItemRelationship(
                                                List.of(
                                                        OrderItemRelationship.builder()
                                                                .relationshipType(RelationshipType.ISCHILD)
                                                                .id(PRODUCT_ORDER_ITEM_ID_1)
                                                                .build()
                                                )
                                        )
                                        .product(
                                                com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                                                        .id("456")
                                                        .productSpecification(
                                                                creatProductSpecification(itemActionType)
                                                        )
                                                        .build()
                                        )
                                        .build(),
                                ProductOrderItem.builder()
                                        .id(PRODUCT_ORDER_ITEM_ID_1)
                                        .productOffering(
                                                com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOfferingRef
                                                        .builder()
                                                        .id("234")
                                                        .atType(ATOMIC_PRODUCT_OFFERING_TYPE)
                                                        .build()
                                        )
                                        .productOrderItemRelationship(
                                                List.of(
                                                        OrderItemRelationship.builder()
                                                                .relationshipType(RelationshipType.MIGRATEFROM)
                                                                .id("546")
                                                                .build()
                                                )
                                        )
                                        .action(ItemActionType.MIGRATE)
                                        .build(),
                                ProductOrderItem.builder()
                                        .id(PRODUCT_ORDER_ITEM_ID_1)
                                        .action(ItemActionType.ADD)
                                        .build()
                        )
                )
                .build();

    }

    private ProductSpecificationRef creatProductSpecification(ItemActionType itemActionType) {
        if (ItemActionType.MODIFY.equals(itemActionType)) {
            return ProductSpecificationRef.builder()
                    .build();
        }
        return ProductSpecificationRef.builder()
                .id("123")
                .build();
    }
}
