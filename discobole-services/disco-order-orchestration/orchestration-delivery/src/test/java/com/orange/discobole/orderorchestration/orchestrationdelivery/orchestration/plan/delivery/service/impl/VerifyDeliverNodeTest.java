// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.builder.OrchestrationPlanNodeBuilder;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.OrchestrationDeliveryFalloutManagement;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.ProductUnExpectedStateException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductSpecificationNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.VerifyNodeService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.productinventory.dto.v1.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.builder.RelatedProductBuilder.getRelatedProductBuilder;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType.MIGRATE;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemRelationshipType.RELIES_ON;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemRelationshipType.SELLS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {VerifyNodeServiceImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "config.enableOperationalStatusValidationForDelivery = false",
        "config.enablePrerequisiteValidation = true"
})
class VerifyDeliverNodeTest {
    @Autowired
    @SuppressWarnings("PMD.UnusedPrivateField")
    VerifyNodeService verifyNodeService;

    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    OrchestrationPlanService orchestrationPlanService;

    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    OrchestrationPlanRepository orchestrationPlanRepository;

    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    OrchestrationDeliveryFalloutManagement orchestrationDeliveryFalloutManagement;

    @MockBean
    private ProductManagementService productManagementService;

    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    private OrchestrationPlanModificationService orchestrationPlanModificationService;

    @Test
    void testDeliverSelectedNodeWithTwoInvalidProductSpecificationPrerequisites() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder().type(RelatedProductType.CFS).isInstallable(true).id("id1").build(),
                        getRelatedProductBuilder().id(null).build()))).build();

        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.ABORTED)
                .operationalStatus(ProductOperationalStatusType.PENDINGACTIVE)
                .build();

        CoodRecoverableAndNonRetryableException exception = Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB));

        ProductUnExpectedStateException cause = (ProductUnExpectedStateException) exception.getCause();
        assertEquals("PRODUCT_UNEXPECTED_STATE_EXCEPTION", cause.getCoodError().code());
        assertEquals("Action required is {add} and the Product to be delivered id {id} and state {Aborted} should be {Created}", cause.getCoodError().reason());
        assertEquals("PRODUCT_STATE_NOT_EXPECTED", cause.getCoodError().message());

    }

    @Test
    void testDeliverSelectedNodeWithActionModifyWithInvalidProductState() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "modify", 1)))
                .build();


        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.ABORTED)
                .operationalStatus(ProductOperationalStatusType.PENDINGACTIVE)
                .build();

        CoodRecoverableAndNonRetryableException exception = Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB));

        ProductUnExpectedStateException cause = (ProductUnExpectedStateException) exception.getCause();

        assertEquals("PRODUCT_UNEXPECTED_STATE_EXCEPTION", cause.getCoodError().code());
        assertEquals("Action required is {modify} and the Product to be delivered id {id} and state {Aborted} should be {Active}", cause.getCoodError().reason());
        assertEquals("PRODUCT_STATE_NOT_EXPECTED", cause.getCoodError().message());
    }

    @Test
    void testDeliverSelectedNodeWithActionAddWithInvalidProductState() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .build();

        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.ACTIVE)
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .build();


        // Call the method being tested
        CoodRecoverableAndNonRetryableException exception = Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB));

        ProductUnExpectedStateException cause = (ProductUnExpectedStateException) exception.getCause();

        assertEquals("PRODUCT_UNEXPECTED_STATE_EXCEPTION", cause.getCoodError().code());
        assertEquals("Action required is {add} and the Product to be delivered id {id} and state {Active} should be {Created}", cause.getCoodError().reason());
        assertEquals("PRODUCT_STATE_NOT_EXPECTED", cause.getCoodError().message());
    }

    @Test
    void testDeliverSelectedNodeWithInvalidProductSpecificationPrerequisites() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder().id(null).isInstallable(true).build())))
                .build();


        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.CREATED)
                .operationalStatus(ProductOperationalStatusType.CREATED)
                .productRelationship(List.of(ProductRelationship.builder()
                        .product(ProductRef.builder().id("productId1").build())
                        .relationshipType(RELIES_ON.getValue())
                        .build(), ProductRelationship.builder()
                        .product(ProductRef.builder().id("productId2").build())
                        .relationshipType(SELLS.getValue())
                        .build()))
                .build();

        mockGetProductByFields(ProductStatusType.ABORTED);

        // Call the method being tested
        CoodRecoverableAndNonRetryableException exception = Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB));

        ProductValidationException cause = (ProductValidationException) exception.getCause();

        assertEquals("PREREQUISITES_PRODUCT_SPECIFICATION_NOT_SATISFIED", cause.getCoodError().code());
        assertEquals("the required relationship for product Specification with id = {111} isn't realized by an active product", cause.getCoodError().message());
        assertEquals("All ProductSpecificationRelationships with relationship Type = reliesOn defined in the Product Catalog should be realized by a ProductRelationship on the installed product with relationship Type = reliesOn and the related product should be active", cause.getCoodError().reason());

    }

    private void mockGetProductByFields(ProductStatusType active) {
        Product prerequisiteProduct = Product.builder().id("productId1")
                .status(active).build();
        when(productManagementService.getProductsByFields(any(), any())).thenReturn(List.of(prerequisiteProduct));
    }

    @Test
    void testDeliverSelectedNodeWithUnInstallableProductSpecificationPrerequisites() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder().id(null).isInstallable(false).build())))
                .build();


        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.CREATED)
                .operationalStatus(ProductOperationalStatusType.CREATED)
                .build();

        // Call the method being tested
        verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB);

        assertNull(orchestrationPlanNode.getErrorMessage());
        assertEquals(OrchestrationPlanNodeState.IN_PROGRESS, orchestrationPlanNode.getState());
    }

    @Test
    void testDeliverSelectedNodeWithSoldProductStatusPrerequisites() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "add", 1)))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder().id(null).isInstallable(false).build())))
                .build();


        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.CREATED)
                .operationalStatus(ProductOperationalStatusType.CREATED)
                .productRelationship(List.of(ProductRelationship.builder()
                        .relationshipType(RELIES_ON.getValue())
                        .product(ProductRef.builder()
                                .id("productId1")
                                .build())
                        .build()))
                .build();

        mockGetProductByFields(ProductStatusType.SOLD);

        // Call the method being tested
        verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB);

        assertNull(orchestrationPlanNode.getErrorMessage());
        assertEquals(OrchestrationPlanNodeState.IN_PROGRESS, orchestrationPlanNode.getState());
    }


    @Test
    void givenOrchestrationPlanNodeWithInstallableProductWithIdNullAndProductSpecificationIsNull_whenVerifyOrchestrationPlanNodeDelivery_thenExceptionThrown() {
        // Mocking orchestrationPlan, nodes, productOrderItemDTOS, resultCPIB
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("id1", "modify", 1)))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder()
                        .id(null)
                        .productSpecification(null)
                        .productOrderItemId("id1").type(RelatedProductType.CFS).isInstallable(true).build())))
                .build();


        // Mocking resultCPIB
        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.ACTIVE)
                .operationalStatus(ProductOperationalStatusType.PENDINGACTIVE)
                .build();

        ProductSpecificationNotFoundException specificationNotFoundException = (ProductSpecificationNotFoundException) Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB)).getCause();

        assertEquals("PRODUCT_SPECIFICATION_NOT_ADDED_FOR_RELATED_PRODUCT", specificationNotFoundException.getCoodError().code());
    }

    @Test
    void givenOrchestrationPlanNodeWithMigrateActionAndNoInstalledProductForTheMigrateFromProduct_whenVerifyOrchestrationPlanNodeDelivery_thenExceptionThrown() {
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilder.getCFSOrchestrationPlanNodeBuilder()
                .relatedProductOrder(RelatedProductOrder.builder()
                        .id("productId1")
                        .build())
                .relatedProductOrderItem(List.of(new RelatedProductOrderItem("item1", MIGRATE.getValue(), 1)))
                .relatedProduct(new ArrayList<>(List.of(getRelatedProductBuilder()
                                .relationshipType(RelatedProductRelationType.DELIVERS)
                                .productOrderItemId("item1")
                                .build(),
                        RelatedProduct.builder()
                                .relationshipType(RelatedProductRelationType.MIGRATED_FROM)
                                .productOrderItemId("item2")
                                .build()
                )))
                .build();

        Product resultCPIB = Product.builder().id("id")
                .status(ProductStatusType.CREATED)
                .operationalStatus(ProductOperationalStatusType.CONFIRMED)
                .build();

        when(productManagementService.getProductsByOrderIdAndItemIds(anyList(), any())).thenReturn(List.of());

        ProductNotFoundException productNotFoundException = (ProductNotFoundException) Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                verifyNodeService.verifyOrchestrationPlanNodeDelivery(orchestrationPlanNode, resultCPIB)).getCause();

        assertEquals("INSTALLED_PRODUCT_NOT_FOUND_EXCEPTION", productNotFoundException.getCoodError().code());

    }
}
