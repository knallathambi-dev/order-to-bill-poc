// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.RelatedProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.productinventory.dto.v1.*;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
class MaintainDeliveryNodeDeliveryNodeRelatedProductImplTest {

    public static final String PREREQ_PRODUCT_ID = "prereq-product-id";
    public static final String DEPENDENT_ID = "dependent-id";

    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private ObjectMapper objectMapper;

    @Mock
    private OrchestrationPlanService orchestrationPlanService;

    @Mock
    private ProductManagementService productManagementService;

    @InjectMocks
    private MaintainDeliveryNodeDeliveryNodeRelatedProductImpl maintainDeliveryNodeRelatedProduct;

    private OrchestrationPlanNode orchestrationPlanNode;
    private Product productDTO;

    @BeforeEach
    void setUp() {
        orchestrationPlanNode = Instancio.create(OrchestrationPlanNode.class);
        orchestrationPlanNode.setId("node-id");

        productDTO = Instancio.create(Product.class);
        productDTO.setId("product-id");
        ProductRef productRef = new ProductRef();
        productRef.setId(PREREQ_PRODUCT_ID);
        productDTO.getProductRelationship().add(ProductRelationship.builder().product(productRef).relationshipType(RelatedProductRelationType.RELIES_ON.getValue()).build());

        ProductRef productRefDependent = new ProductRef();
        productRef.setId(DEPENDENT_ID);
        productDTO.getProductRelationship().add(ProductRelationship.builder().product(productRefDependent).relationshipType(RelatedProductRelationType.RELIES_FROM.getValue()).build());
    }

    @Test
    void givenNullRelatedProduct_whenMaintainOrchestrationPlanNodeRelatedProduct_thenANotFoundExceptionThrown() {

        productDTO.setRealizingService(Collections.singletonList(Instancio.create(ServiceRef.class)));
        orchestrationPlanNode.setRelatedProduct(null);

        CoodRecoverableAndNonRetryableException exception = Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () ->
                maintainDeliveryNodeRelatedProduct.maintainOrchestrationPlanNodeRelatedProduct(orchestrationPlanNode, productDTO));

        RelatedProductNotFoundException cause = (RelatedProductNotFoundException) exception.getCause();

        assertEquals("NODE_RELATED_PRODUCT_NOT_FOUND", cause.getCoodError().code());
        assertEquals("Related product not found for node id :{node-id}", cause.getCoodError().message());
        assertEquals("Couldn't find related product for orchestration plan node", cause.getCoodError().reason());
    }

    @Test
    void givenRelatedProductAndService_whenMaintainOrchestrationPlanNodeRelatedProduct_thenRelatedProductsUpdated() {
        // Given
        RelatedProduct relatedProduct = Instancio.create(RelatedProduct.class);
        relatedProduct.setRelationshipType(RelatedProductRelationType.DELIVERS);
        relatedProduct.setType(RelatedProductType.CFS);

        List<RelatedProduct> relatedProductList = new ArrayList<>();
        relatedProductList.add(relatedProduct);
        orchestrationPlanNode.setRelatedProduct(relatedProductList);
        productDTO.setRealizingService(Collections.singletonList(Instancio.create(ServiceRef.class)));

        // When
        maintainDeliveryNodeRelatedProduct.maintainOrchestrationPlanNodeRelatedProduct(orchestrationPlanNode, productDTO);

        // Then
        verify(orchestrationPlanService).updateNodeRelatedProducts(orchestrationPlanNode);
        assertEquals(productDTO.getId(), relatedProduct.getId());
        assertFalse(relatedProduct.getRealisingService().isEmpty());
    }

    @Test
    void givenProductWithRelationshipReliesOn_whenMaintainRelatedProductsWithRelationshipReliesOn_thenRelatedProductIdIsPrereqProductId() {
        // Given
        RelatedProduct relatedProduct = Instancio.create(RelatedProduct.class);
        relatedProduct.setRelationshipType(RelatedProductRelationType.RELIES_ON);
        ProductSpecification spec = Instancio.create(ProductSpecification.class);
        spec.setId("spec-id");
        relatedProduct.setProductSpecification(spec);
        orchestrationPlanNode.setRelatedProduct(Collections.singletonList(relatedProduct));

        Product product = Instancio.create(Product.class);
        product.setProductSpecification(ProductSpecificationRef.builder().id(spec.getId()).name(spec.getName()).build());
        product.setId(PREREQ_PRODUCT_ID);
        List<Product> prerequisiteProducts = List.of(product);
        when(productManagementService.getProductsByFields(anyList(), anyList())).thenReturn(prerequisiteProducts);

        // When
        ReflectionTestUtils.invokeMethod(
                maintainDeliveryNodeRelatedProduct,
                "maintainRelatedProductsWithRelationshipReliesOn",
                orchestrationPlanNode,
                productDTO
        );

        // Then
        assertEquals(PREREQ_PRODUCT_ID, relatedProduct.getId());
    }

    @Test
    void givenProductWithRelationshipReliesFrom_whenMaintainRelatedProductsWithRelationshipReliesFrom_thenRelatedProductIsAdded() {
        // Given
        ProductRelationship relationship = Instancio.create(ProductRelationship.class);
        relationship.setRelationshipType(RelatedProductRelationType.RELIES_FROM.getValue());
        relationship.setProduct(ProductRef.builder().id(DEPENDENT_ID).build());
        productDTO.setProductRelationship(Collections.singletonList(relationship));

        // When
        ReflectionTestUtils.invokeMethod(
                maintainDeliveryNodeRelatedProduct,
                "maintainRelatedProductsWithRelationshipReliesFrom",
                orchestrationPlanNode,
                productDTO
        );

        // Then
        assertFalse(orchestrationPlanNode.getRelatedProduct().isEmpty());
        assertTrue(orchestrationPlanNode.getRelatedProduct().stream().filter(relatedProduct -> relatedProduct.getRelationshipType().equals(RelatedProductRelationType.RELIES_FROM)).findAny().isPresent());
    }

    @Test
    void givenProductWithRelationship_whenGetRelatedProductIdsWithRelationship_thenReturnRelatedProductIdList() {
        // Given
        ProductRelationship relationship = new ProductRelationship();
        relationship.setRelationshipType(RelatedProductRelationType.RELIES_ON.getValue());
        relationship.setProduct(ProductRef.builder().id("related-product-id").build());
        productDTO.setProductRelationship(Collections.singletonList(relationship));

        // When
        List<String> result = ReflectionTestUtils.invokeMethod(
                maintainDeliveryNodeRelatedProduct,
                "getRelatedProductIdsWithRelationship",
                productDTO,
                RelatedProductRelationType.RELIES_ON.getValue()
        );

        // Then
        assertEquals(1, result.size());
        assertEquals("related-product-id", result.get(0));
    }

    @Test
    void givenInvalidProductSpecificationId_whenDoesRelatedProductIdsContainProductSpecificationId_thenReturnFalse() {
        // Given
        Set<String> relatedProductIds = Set.of("spec-id-1", "spec-id-2");
        String productSpecificationId = "spec-id-3"; // Not present in relatedProductIds

        // When
        Boolean result = ReflectionTestUtils.invokeMethod(
                maintainDeliveryNodeRelatedProduct,
                "doesRelatedProductIdsContainProductSpecificationId",
                relatedProductIds,
                productSpecificationId
        );

        // Then
        assertFalse(result);
    }

    @Test
    void givenRelatedProductsWithRelationshipTypeReliesOn_whenGetRelatedProductSpecificationIdsEqualsRelationshipTypeReliesOn_thenReturnSetContainingSpecId1() {
        // Given
        RelatedProduct relatedProduct = new RelatedProduct();
        relatedProduct.setRelationshipType(RelatedProductRelationType.RELIES_ON);
        ProductSpecification productSpecification = new ProductSpecification();
        productSpecification.setId("spec-id-1");
        relatedProduct.setProductSpecification(productSpecification);
        List<RelatedProduct> relatedProducts = List.of(relatedProduct);

        // When
        Set<String> result = ReflectionTestUtils.invokeMethod(
                maintainDeliveryNodeRelatedProduct,
                "getRelatedProductSpecificationIdsEqualsRelationshipTypeReliesOn",
                relatedProducts
        );

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains("spec-id-1"));
    }

    @Test
    void givenOrchestrationPlanNode_whenAddRelatedProductWithReliesFromRelation_thenProductWithIdIsAdded() {
        // Given
        int oldSize = orchestrationPlanNode.getRelatedProduct().size();
        Consumer<String> consumer = ReflectionTestUtils.invokeMethod(
                maintainDeliveryNodeRelatedProduct,
                "addRelatedProductWithReliesFromRelation",
                orchestrationPlanNode
        );

        // When
        consumer.accept("new-product-id");

        // Then
        assertEquals(oldSize + 1, orchestrationPlanNode.getRelatedProduct().size());
        assertEquals("new-product-id", orchestrationPlanNode.getRelatedProduct().get(oldSize).getId());
    }
}

