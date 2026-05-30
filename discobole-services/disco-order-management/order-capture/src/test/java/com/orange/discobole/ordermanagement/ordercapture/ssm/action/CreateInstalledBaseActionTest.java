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
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductRef;
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

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CreateInstalledBaseActionTest {

    private static final String PRODUCT_HREF_1 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId1";
    private static final String PRODUCT_HREF_2 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId2";
    private static final String PRODUCT_HREF_3 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId3";
    private static final String BUNDLES = "bundles";
    private static final String ROOT_PRODUCT = "rootProduct";
    private static final String ORDER_ITEM_ACTION = "add";
    private static final String ACQUISITION = "acquisition";
    private static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final Instant DEFAULT_CREATION_DATE = Instant.now();
    private static final String PRODUCT_ORDER_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String CONTRACT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String BUNDLE_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String ATOMIC_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_TYPE = "ProductSpecificationRef";
    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_REF_TYPE = "ProductRef";
    private static final String CONTRACT_PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderService productOrderService;

    @InjectMocks
    private CreateInstalledBaseAction createInstalledBaseAction;

    @Test
    @DisplayName("Given a re-executed create installed base action, " +
            "when applying the action, " +
            "then the areProductsCreated variable is set to false")
    void shouldSetAreProductsCreatedFalseWhenReExecution() {
        // Given
        StateContext<String, String> context = mockStateContext(null, null, ACQUISITION);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, TRUE);

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PRODUCTS_CREATED, FALSE));
    }

    @Test
    @DisplayName("Given an exception in create products service, " +
            "when creating products, " +
            "then the areProductsCreated variable is set to false and the description is updated")
    void shouldSetAreProductsCreatedFalseAndUpdateDescriptionWhenExceptionInCreateProductsService() {
        // Given
        ProductOrder productOrder = createProductOrder(ItemActionType.ADD);
        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, ACQUISITION);

        doThrow(RuntimeException.class).when(productOrderService).createProducts(productOrder, null, null, null);

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PRODUCTS_CREATED, FALSE));
        Assertions.assertEquals(DescriptionConstants.PRODUCT_INSTANTIATION_ERROR, StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given a valid product order, " +
            "when creating products for acquisition use case , " +
            "then the areProductsCreated variable is set to true and product refs are added")
    void shouldSetAreProductsCreatedTrueWhenProductOrderIsValidAndAddProductRefsForAcquisition() {
        // Given
        ProductOrder productOrder = createProductOrder(ItemActionType.ADD);
        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, ACQUISITION);

        doReturn(createdProducts()).when(productOrderService).createProducts(any(), any(), any(), any());

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder upadedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(upadedProductOrder);
        Assertions.assertTrue(areProductRefsAddedToOrderItems(upadedProductOrder));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PRODUCTS_CREATED, TRUE));
    }

    @Test
    @DisplayName("Given a valid product order, " +
            "when creating products for modification use case , " +
            "then the areProductsCreated variable is set to true and product refs are added")
    void shouldSetAreProductsCreatedTrueWhenProductOrderIsValidAndAddProductRefsForModification() {
        // Given
        ProductOrder productOrder = createProductOrder(ItemActionType.ADD);
        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, OrderCaptureConstants.MODIFICATION);

        doReturn(createdProducts()).when(productOrderService).createProducts(any(), any(), any(), any());

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder upadedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(upadedProductOrder);
        Assertions.assertTrue(areProductRefsAddedToOrderItems(upadedProductOrder));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PRODUCTS_CREATED, TRUE));
    }

    @Test
    @DisplayName("Given a ProductOrderItem with null Product, " +
            "when apply is called, " +
            "then a new ProductRef is created and set with product values")
    void shouldCreateProductRefWhenOrderItemHasNullProduct() {
        // Given
        ProductOrderItem orderItem = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_1)
                .action(ItemActionType.ADD)
                .build();

        ProductOrder productOrder = ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .productOrderItem(List.of(orderItem))
                .build();

        Product product = Product.builder()
                .id(PRODUCT_ID_1)
                .href(PRODUCT_HREF_1)
                .productOrderItem(List.of(
                        RelatedProductOrderItem.builder()
                                .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                                .productOrderId(DEFAULT_PRODUCT_ORDER_ID)
                                .orderItemAction(ItemActionType.ADD.getValue())
                                .build()
                ))
                .build();

        doReturn(List.of(product)).when(productOrderService).createProducts(any(), any(), any(), any());

        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, ACQUISITION);

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        List<ProductOrderItem> productOrderItems = updatedProductOrder.getProductOrderItem();
        ProductOrderItem updatedOrderItem = productOrderItems.get(0);
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef createdProductRef =
                (com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef) updatedOrderItem.getProduct();
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertNotNull(productOrderItems);
        Assertions.assertFalse(productOrderItems.isEmpty());
        Assertions.assertNotNull(updatedOrderItem.getProduct());
        Assertions.assertInstanceOf(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef.class, updatedOrderItem.getProduct());
        Assertions.assertEquals(PRODUCT_ID_1, createdProductRef.getId());
        Assertions.assertEquals(PRODUCT_HREF_1, createdProductRef.getHref());
    }

    @Test
    @DisplayName("Given a ProductOrderItem with ProductRef and null Product, " +
            "when apply is called, " +
            "then ProductRef is not updated")
    void shouldNotUpdateProductRefWhenProductIsNull() {
        // Given
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef productRef = com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef.builder()
                .id(null)
                .href(null)
                .build();

        ProductOrderItem orderItem = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_1)
                .action(ItemActionType.ADD)
                .product(productRef)
                .build();

        ProductOrder productOrder = ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .productOrderItem(List.of(orderItem))
                .build();

        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, ACQUISITION);

        doReturn(List.of()).when(productOrderService).createProducts(any(), any(), any(), any());

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        Assertions.assertNull(productRef.getId());
        Assertions.assertNull(productRef.getHref());
    }

    @Test
    @DisplayName("Given a ProductOrderItem with ProductRef, " +
            "when apply is called, " +
            "then ProductRef's ID and href are updated with the created Product values")
    void shouldUpdateProductRefWithCreatedProductValues() {
        // Given
        Product product = Product.builder()
                .id(PRODUCT_ID_1)
                .href(PRODUCT_HREF_1)
                .productOrderItem(List.of(
                        RelatedProductOrderItem.builder()
                                .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                                .productOrderId(DEFAULT_PRODUCT_ORDER_ID)
                                .orderItemAction(ItemActionType.ADD.getValue())
                                .build()
                ))
                .build();

        com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef productRef = com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef.builder().build();

        ProductOrderItem orderItem = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_1)
                .action(ItemActionType.ADD)
                .product(productRef)
                .build();

        ProductOrder productOrder = ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .productOrderItem(List.of(orderItem))
                .build();

        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, ACQUISITION);

        doReturn(List.of(product)).when(productOrderService).createProducts(any(), any(), any(), any());

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder updatedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef updatedProductRef = (com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef) updatedProductOrder.getProductOrderItem().get(0).getProduct();
        Assertions.assertNotNull(updatedProductOrder);
        Assertions.assertEquals(PRODUCT_ID_1, updatedProductRef.getId());
        Assertions.assertEquals(PRODUCT_HREF_1, updatedProductRef.getHref());
    }

    @Test
    @DisplayName("Given a valid product order, " +
            "when deleting products for termination use case , " +
            "then the the product order shouldn't be null ")
    void shouldSetAreProductsUpdatesTrueWhenProductOrderIsValidAndAddProductRefsForTermination() {
        // Given
        ProductOrder productOrder = createProductOrder(ItemActionType.DELETE);
        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, OrderCaptureConstants.TERMINATION);

        doReturn(Collections.EMPTY_LIST).when(productOrderService).createProducts(any(), any(), any(), any());

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder upadedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);

        Assertions.assertNotNull(upadedProductOrder);
    }

    @Test
    @DisplayName("Given a valid product order, " +
            "when creating products for Migration use case , " +
            "then the areProductsCreated variable is set to true and product refs are added")
    void shouldSetAreProductsCreatedTrueWhenProductOrderIsValidAndAddProductRefsForAMigration() {
        // Given
        ProductOrder productOrder = createProductOrder(ItemActionType.MIGRATE);
        StateContext<String, String> context = mockStateContext(productOrder, CONTRACT_PRODUCT_ID, ACQUISITION);

        doReturn(createdProducts()).when(productOrderService).createProducts(any(), any(), any(), any());

        // When
        Mono<Void> result = createInstalledBaseAction.apply(context);

        // Then
        StepVerifier.create(result).expectComplete().verify();
        ProductOrder upadedProductOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);
        Assertions.assertNotNull(upadedProductOrder);
        Assertions.assertTrue(areProductRefsAddedToOrderItems(upadedProductOrder));
        Assertions.assertTrue(StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_PRODUCTS_CREATED, TRUE));
    }


    private List<Product> createdProducts() {
        return List.of(createProduct(PRODUCT_ID_1, PRODUCT_HREF_1, CONTRACT_PRODUCT_OFFERING_ID, PRODUCT_ORDER_ITEM_ID_1, "Mobile Package Max", "Contract", List.of(
                        com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                                .product(createProductRef(PRODUCT_ID_2, PRODUCT_HREF_2))
                                .relationshipType(BUNDLES)
                                .build())),
                createProduct(PRODUCT_ID_2, PRODUCT_HREF_2, CONTRACT_PRODUCT_OFFERING_ID, PRODUCT_ORDER_ITEM_ID_2, "Mobile Package Max", "BundleProductOffering", List.of(
                        com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                                .product(createProductRef(PRODUCT_ID_3, PRODUCT_HREF_3))
                                .relationshipType(BUNDLES)
                                .build(),
                        com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                                .product(createProductRef(PRODUCT_ID_1, PRODUCT_HREF_1))
                                .relationshipType(ROOT_PRODUCT)
                                .build())),
                createProduct(PRODUCT_ID_3, PRODUCT_HREF_3, ATOMIC_PRODUCT_OFFERING_ID, PRODUCT_ORDER_ITEM_ID_3, "Mobile Line", "AtomicProductOffering", List.of(
                        com.orange.discobole.productinventory.dto.v1.ProductRelationship.builder()
                                .product(createProductRef(PRODUCT_ID_1, PRODUCT_HREF_1))
                                .relationshipType(ROOT_PRODUCT)
                                .build())));
    }

    private Product createProduct(String productId, String productHref, String productOfferingId, String productOrderItemId, String productOfferingName, String productOfferingType, List<com.orange.discobole.productinventory.dto.v1.ProductRelationship> relationships) {
        return Product.builder()
                .id(productId)
                .href(productHref)
                .productOrderItem(List.of(RelatedProductOrderItem.builder()
                        .orderItemAction(ORDER_ITEM_ACTION)
                        .productOrderId(DEFAULT_PRODUCT_ORDER_ID)
                        .orderItemId(productOrderItemId)
                        .build()))
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id(productOfferingId)
                        .name(productOfferingName)
                        .atType(productOfferingType)
                        .build())
                .productRelationship(relationships)
                .productSpecification(productOfferingType.equals("AtomicProductOffering") ? com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef.builder()
                        .id(PRODUCT_SPECIFICATION_ID)
                        .name("Mobile Line")
                        .atType(PRODUCT_SPECIFICATION_TYPE)
                        .build() : null)
                .build();
    }

    private ProductRef createProductRef(String productId, String productHref) {
        return ProductRef.builder()
                .id(productId)
                .href(productHref)
                .atType(PRODUCT_REF_TYPE)
                .build();
    }

    private ProductOrder createProductOrder(ItemActionType itemActionType) {
        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(List.of(
                        createProductOrderItem(PRODUCT_ORDER_ITEM_ID_1, CONTRACT_PRODUCT_OFFERING_ID, "Mobile Package Max", "Contract", List.of(createRelationship(RelationshipType.BUNDLES, itemActionType, PRODUCT_ORDER_ITEM_ID_2)), itemActionType),
                        createProductOrderItem(PRODUCT_ORDER_ITEM_ID_2, BUNDLE_PRODUCT_OFFERING_ID, "Mobile Package Max", "BundleProductOffering", List.of(createRelationship(RelationshipType.ISCHILD, itemActionType, PRODUCT_ORDER_ITEM_ID_1), createRelationship(RelationshipType.BUNDLES, itemActionType, PRODUCT_ORDER_ITEM_ID_3)), itemActionType),
                        createProductOrderItem(PRODUCT_ORDER_ITEM_ID_3, ATOMIC_PRODUCT_OFFERING_ID, "Mobile Line", "AtomicProductOffering", List.of(createRelationship(RelationshipType.ISCHILD, itemActionType, PRODUCT_ORDER_ITEM_ID_2)), itemActionType)))
                .build();
    }

    private OrderItemRelationship createRelationship(RelationshipType relationshipType, ItemActionType itemActionType, String relatedProductOrderItem) {
        if (ItemActionType.MIGRATE.equals(itemActionType)) {
            return OrderItemRelationship.builder().id("1234").relationshipType(RelationshipType.MIGRATEFROM).build();
        }
        return OrderItemRelationship.builder()
                .id(relatedProductOrderItem)
                .relationshipType(relationshipType)
                .build();
    }

    private ProductOrderItem createProductOrderItem(String productOrderItemId, String productOfferingId, String
            productOfferingName, String productOfferingType, List<OrderItemRelationship> relationships, ItemActionType
                                                            itemActionType) {
        return ProductOrderItem.builder()
                .id(productOrderItemId)
                .state(ProductOrderItemStateType.DRAFT)
                .action(itemActionType)
                .product(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                        .isBundle(!productOfferingType.equals("AtomicProductOffering"))
                        .productSpecification(productOfferingType.equals("AtomicProductOffering") ? ProductSpecificationRef.builder()
                                .id(PRODUCT_SPECIFICATION_ID)
                                .name("Mobile Line")
                                .atType(PRODUCT_SPECIFICATION_TYPE)
                                .build() : null)
                        .build())
                .productOffering(ProductOfferingRef.builder()
                        .id(productOfferingId)
                        .name(productOfferingName)
                        .atType(productOfferingType)
                        .build())
                .productOrderItemRelationship(relationships)
                .build();
    }

    private boolean areProductRefsAddedToOrderItems(ProductOrder productOrder) {
        boolean contractRefFound = false;
        boolean bundleRefFound = false;
        boolean atomicRefFound = false;

        for (ProductOrderItem item : productOrder.getProductOrderItem()) {
            if (item.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
                String productOfferingType = item.getProductOffering().getAtType();
                String productId = product.getId();
                String productHref = product.getHref();

                switch (productOfferingType) {
                    case "Contract" -> {
                        if (PRODUCT_ID_1.equals(productId) && PRODUCT_HREF_1.equals(productHref)) {
                            contractRefFound = true;
                        }
                    }
                    case "BundleProductOffering" -> {
                        if (PRODUCT_ID_2.equals(productId) && PRODUCT_HREF_2.equals(productHref)) {
                            bundleRefFound = true;
                        }
                    }
                    case "AtomicProductOffering" -> {
                        if (PRODUCT_ID_3.equals(productId) && PRODUCT_HREF_3.equals(productHref)) {
                            atomicRefFound = true;
                        }
                    }
                    default ->
                            throw new IllegalArgumentException("Unexpected product offering type: " + productOfferingType);
                }
            }
        }

        return contractRefFound && bundleRefFound && atomicRefFound;
    }

    private DefaultStateContext<String, String> mockStateContext(ProductOrder productOrder, String
            productId, String requestedConfiguration) {
        ExtendedState extendedState = new DefaultExtendedState();
        if (Objects.nonNull(productOrder)) {
            extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        }
        if (requestedConfiguration.equals(OrderCaptureConstants.MODIFICATION) || requestedConfiguration.equals(OrderCaptureConstants.TERMINATION)) {
            extendedState.getVariables().put(OrderCaptureConstants.REQUESTED_CONFIGURATION_ACTION, requestedConfiguration);

            extendedState.getVariables().put(OrderCaptureConstants.CONTRACT_PRODUCT_ID, productId);
        }

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }
}