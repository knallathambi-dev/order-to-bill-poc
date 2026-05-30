// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.exception.DiscoException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferencesAdditionActionTest {

    public static final String PRODUCT_ORDER_TYPE = "ProductOrder";
    public static final String PRODUCT_TYPE = "Product";
    public static final String PRODUCT_REF_TYPE = "ProductRef";
    private static final String ORDER_INVENTORY_HREF = "http://OrderInventoryUrl/productOrderInventory/v1/productOrder/productOrderId";
    private static final String CONTRACT_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String BUNDLE_PRODUCT_OFFERING_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String ATOMIC_PRODUCT_OFFERING_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String ATOMIC_PRODUCT_OFFERING_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String ATOMIC_PRODUCT_OFFERING_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID_5 = RandomStringUtils.randomAlphabetic(10);
    private static final String ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String ITEM_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String ITEM_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String ITEM_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String ITEM_ID_5 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_HREF_1 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId1";
    private static final String PRODUCT_HREF_2 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId2";
    private static final String PRODUCT_HREF_3 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId3";
    private static final String PRODUCT_HREF_4 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId4";
    private static final String PRODUCT_HREF_5 = "http://productInventoryUrl/productInventoryManagement/v1/product/productId5";
    private static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PAYMENT_REF_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String BILLING_ACCOUNT_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_NAME = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderService productOrderService;
    @Mock
    private ProductInventoryService productInventoryService;
    @InjectMocks
    private ReferencesAdditionAction action;
    private StateContext<String, String> context;
    private ProductOrder productOrder;

    @BeforeEach
    void setup() {
        productOrder = createProductOrder();
        context = mockStateContext(productOrder);
    }

    @Test
    @DisplayName("Given a re-executed action, " +
            "when applying the action, " +
            "then it should not update the context")
    void shouldNotUpdateContextWhenReExecutedAction() {
        // Given
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, Boolean.TRUE);

        // When
        Mono<Void> result = action.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verifyNoInteractions(productOrderService, productInventoryService);
        Assertions.assertNull(context.getExtendedState().getVariables().get(OrderCaptureConstants.ARE_REFERENCES_ADDED));
    }

    @Test
    @DisplayName("Given an exception occurs while adding payment refs, " +
            "when applying the action, " +
            "then it should handle the exception and update context variables")
    void shouldHandleExceptionWhenAddingPaymentRefs() {
        // Given
        DiscoException exception = new DiscoException("Error adding payment ref");
        doThrow(exception).when(productOrderService).addPaymentRef(any(), any());

        // When
        Mono<Void> result = action.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(productOrderService, never()).addBillingAccountRef(any(), any());
        verify(productInventoryService, never()).updateProducts(anyString());

        Assertions.assertFalse((Boolean) context.getExtendedState().getVariables().get(OrderCaptureConstants.ARE_REFERENCES_ADDED));
        Assertions.assertEquals(exception.getReason(), StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given an exception occurs while adding billing account refs, " +
            "when applying the action, " +
            "then it should handle the exception and update context variables")
    void shouldHandleExceptionWhenAddingReferences() {
        // Given
        DiscoException exception = new DiscoException("Error adding billing account ref");
        doNothing().when(productOrderService).addPaymentRef(any(), any());
        doThrow(exception).when(productOrderService).addBillingAccountRef(any(), any());

        // When
        Mono<Void> result = action.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(productInventoryService, never()).updateProducts(anyString());

        Assertions.assertFalse((Boolean) context.getExtendedState().getVariables().get(OrderCaptureConstants.ARE_REFERENCES_ADDED));
        Assertions.assertEquals(exception.getReason(), StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given an exception occurs while updating products, " +
            "when applying the action, " +
            "then it should handle the exception and update context variables")
    void shouldHandleExceptionWhenUpdatingProducts() {
        // Given
        DiscoException exception = new DiscoException("Error updating products");
        doNothing().when(productOrderService).addPaymentRef(any(), any());
        doNothing().when(productOrderService).addBillingAccountRef(any(), any());
        doThrow(exception).when(productInventoryService).updateProducts(anyString());

        // When
        Mono<Void> result = action.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        Assertions.assertFalse((Boolean) context.getExtendedState().getVariables().get(OrderCaptureConstants.ARE_REFERENCES_ADDED));
        Assertions.assertEquals(exception.getReason(), StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION));
    }

    @Test
    @DisplayName("Given unpaid order items and order items requiring billing account, " +
            "when applying the action, " +
            "then it should add references and update products")
    void shouldAddPaymentAndBillingRefsAndUpdateProducts() {
        // When
        Mono<Void> result = action.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(productOrderService).addPaymentRef(productOrder, Map.of(ITEM_ID_3, List.of(PAYMENT_REF_ID)));
        verify(productOrderService).addBillingAccountRef(productOrder, Map.of(ITEM_ID_4, BILLING_ACCOUNT_ID));
        verify(productInventoryService).updateProducts(anyString());

        Assertions.assertTrue((Boolean) context.getExtendedState().getVariables().get(OrderCaptureConstants.ARE_REFERENCES_ADDED));
        Assertions.assertEquals(productOrder, context.getExtendedState().getVariables().get(OrderCaptureConstants.CREATED_PRODUCT_ORDER));
    }

    @Test
    @DisplayName("Given order items requiring appointment AND appointment is required, when applying the action, then it should add appointment refs")
    void shouldAddAppointmentRefs() {
        // Given
        String appointmentRefId = RandomStringUtils.randomAlphabetic(10);
        context.getExtendedState().getVariables().put(
                OrderCaptureConstants.ORDER_ITEM_APPOINTMENT_REF_MAP,
                Map.of(ITEM_ID_5, appointmentRefId));

        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, Boolean.TRUE);

        // When
        StepVerifier.create(action.apply(context)).verifyComplete();

        // Then
        verify(productOrderService).addAppointmentRef(productOrder,
                Map.of(ITEM_ID_5, appointmentRefId));
        Assertions.assertTrue((Boolean) context.getExtendedState()
                .getVariables().get(OrderCaptureConstants.ARE_REFERENCES_ADDED));
    }

    @Test
    @DisplayName("Given order items requiring appointment BUT appointment is not required, it should NOT add appointment refs")
    void shouldNotAddAppointmentRefsWhenNotRequired() {
        //Given
        String appointmentRefId = RandomStringUtils.randomAlphabetic(10);
        context.getExtendedState().getVariables().put(
                OrderCaptureConstants.ORDER_ITEM_APPOINTMENT_REF_MAP,
                Map.of(ITEM_ID_5, appointmentRefId));
        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, Boolean.FALSE);

        //When
        StepVerifier.create(action.apply(context)).verifyComplete();

        //Then
        verify(productOrderService, never()).addAppointmentRef(any(), any());
    }
    private ProductOrder createProductOrder() {
        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .href(ORDER_INVENTORY_HREF)
                .creationDate(Instant.now())
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(List.of(
                        createProductOrderItem(ITEM_ID_1, CONTRACT_PRODUCT_OFFERING_ID, "Mobile Package Max", PRODUCT_ID_1, PRODUCT_HREF_1, "Contract", "Product", List.of(createOrderItemRelationship(ITEM_ID_2, RelationshipType.BUNDLES))),
                        createProductOrderItem(ITEM_ID_2, BUNDLE_PRODUCT_OFFERING_ID, "Mobile Package Max", PRODUCT_ID_2, PRODUCT_HREF_2, "BundleProductOffering", "Product", List.of(createOrderItemRelationship(ITEM_ID_1, RelationshipType.ISCHILD), createOrderItemRelationship(ITEM_ID_3, RelationshipType.BUNDLES))),
                        createProductOrderItem(ITEM_ID_3, ATOMIC_PRODUCT_OFFERING_ID_1, "Mobile Line", PRODUCT_ID_3, PRODUCT_HREF_3, "AtomicProductOffering", "Product", List.of(createOrderItemRelationship(ITEM_ID_2, RelationshipType.ISCHILD))),
                        createProductOrderItem(ITEM_ID_4, ATOMIC_PRODUCT_OFFERING_ID_2, "Time Bundle", PRODUCT_ID_4, PRODUCT_HREF_4, "AtomicProductOffering", "Product", List.of(createOrderItemRelationship(ITEM_ID_2, RelationshipType.ISCHILD))),
                        createProductOrderItem(ITEM_ID_5, ATOMIC_PRODUCT_OFFERING_ID_3, "Data Bundle", PRODUCT_ID_5, PRODUCT_HREF_5, "AtomicProductOffering", "ProductRef", List.of(createOrderItemRelationship(ITEM_ID_2, RelationshipType.ISCHILD))))
                )
                .atType(PRODUCT_ORDER_TYPE)
                .build();
    }

    private ProductOrderItem createProductOrderItem(String itemId, String offeringId, String offeringName, String productId, String productHref, String offeringType, String productType, List<OrderItemRelationship> relationships) {
        return ProductOrderItem.builder()
                .id(itemId)
                .state(ProductOrderItemStateType.DRAFT)
                .action(ItemActionType.ADD)
                .product(createProductOrProductRef(productId, productHref, offeringType, productType))
                .productOffering(createProductOfferingRef(offeringId, offeringName, offeringType))
                .productOrderItemRelationship(relationships)
                .build();
    }

    private ProductRefOrValue createProductOrProductRef(String productId, String productHref, String offeringType, String productType) {
        if (PRODUCT_REF_TYPE.equals(productType)) {
            return createProductRef(productId, productHref);
        } else {
            return createProduct(productId, productHref, offeringType);
        }
    }

    private Product createProduct(String productId, String productHref, String offeringType) {
        return Product.builder()
                .id(productId)
                .isBundle(!"AtomicProductOffering".equals(offeringType))
                .href(productHref)
                .productSpecification("AtomicProductOffering".equals(offeringType) ? createProductSpecificationRef() : null)
                .atType(PRODUCT_TYPE)
                .build();
    }

    private ProductRef createProductRef(String productId, String productHref) {
        return ProductRef.builder()
                .id(productId)
                .href(productHref)
                .atType(PRODUCT_REF_TYPE)
                .build();
    }

    private ProductSpecificationRef createProductSpecificationRef() {
        return ProductSpecificationRef.builder()
                .id(PRODUCT_SPECIFICATION_ID)
                .name(PRODUCT_SPECIFICATION_NAME)
                .atType("ProductSpecificationRef")
                .build();
    }

    private ProductOfferingRef createProductOfferingRef(String offeringId, String offeringName, String offeringType) {
        return ProductOfferingRef.builder()
                .id(offeringId)
                .name(offeringName)
                .atType(offeringType)
                .build();
    }

    private OrderItemRelationship createOrderItemRelationship(String id, RelationshipType relationshipType) {
        return OrderItemRelationship.builder()
                .id(id)
                .relationshipType(relationshipType)
                .build();
    }

    private StateContext<String, String> mockStateContext(ProductOrder productOrder) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.ORDER_ITEM_PAYMENT_REF_MAP, Map.of(ITEM_ID_3, List.of(PAYMENT_REF_ID)));
        extendedState.getVariables().put(OrderCaptureConstants.ORDER_ITEM_BILLING_ACCOUNT_REF_MAP, Map.of(ITEM_ID_4, BILLING_ACCOUNT_ID));
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);

        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null, extendedState, null);

        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null, null, null);
    }
}