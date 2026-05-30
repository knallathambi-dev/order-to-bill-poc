// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.helper.builders.OrchestrationPlanBuilders;
import com.helper.builders.ProductOrderBuilders;
import com.helper.builders.ProductOrderItemDTOBuilders;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.MaintainOrchestrationPlanNodeRelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanInitService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanNodeRelationshipService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl.MaintainOrchestrationPlanNodeRelatedProductImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl.OrchestrationPlanInitServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl.OrchestrationPlanNodeRelationshipServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.ProductCatalogCustomMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MaintainOrchestrationPlanNodeRelatedProductImplTest {

    public static final String PRODUCT_SPECIFICATION_NO_REQUIRES_LIST = "/product-specification/productSpecificationWithNoRequireList.json";
    public static final String PRODUCT_SPECIFICATION_ONE_REQUIRES_LIST = "/product-specification/productSpecificationWithOneRequireList.json";
    public static final String PRODUCT_SPECIFICATION_MULTIPLE_REQUIRES_LIST = "/product-specification/productSpecificationWithMultipleRequireList.json";
    @Mock
    OrchestrationPlanRepository orchestrationPlanRepository;
    OrchestrationPlanInitService orchestrationPlanInitServiceImpl;
    OrchestrationPlanNodeRelationshipService orchestrationPlanNodeRelationshipService;
    @Mock
    ProductCatalogCustomMapper productCatalogCustomMapper;
    @Mock
    OrchestrationPlanMapper orchestrationPlanMapper;
    private MaintainOrchestrationPlanNodeRelatedProduct maintainOrchestrationPlanNodeRelatedProduct;
    private CharacteristicMapper characteristicMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        maintainOrchestrationPlanNodeRelatedProduct = new MaintainOrchestrationPlanNodeRelatedProductImpl();
        characteristicMapper = new CharacteristicMapperImpl();
        orchestrationPlanInitServiceImpl = new OrchestrationPlanInitServiceImpl(orchestrationPlanMapper, orchestrationPlanRepository, productCatalogCustomMapper, characteristicMapper);
        orchestrationPlanNodeRelationshipService = new OrchestrationPlanNodeRelationshipServiceImpl(maintainOrchestrationPlanNodeRelatedProduct);
    }

    @Test
    void givenOrchestrationPlanAndProductSpecification_whenCountOfReliesOnOrRequiresProductSpecificationRelationshipsEqualZero_thenRelatedProductShouldNotHaveReliesOnProducts() {

        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilders.orchestrationPlanBuilder()
                .build();

        ProductOrder productOrder = ProductOrderBuilders.productOrderBuilder()
                .productOrderItem(List.of(ProductOrderItemDTOBuilders.productOrderItemDTOBuilder().build())).build();

        Map<String, ProductSpecification> productSpecificationMap = JsonUtil.readObjectFromResource(PRODUCT_SPECIFICATION_NO_REQUIRES_LIST, new TypeReference<ArrayList<ProductSpecification>>() {
        }).stream().collect(Collectors.toMap(ProductSpecification::getId, Function.identity()));

        orchestrationPlanNodeRelationshipService.maintainRelationshipsBetweenNodes(productOrder.getProductOrderItem(), orchestrationPlan, productSpecificationMap);

        orchestrationPlan.getOrchestrationPlanNodes().forEach(orchestrationPlanNode -> Assertions.assertNull(orchestrationPlanNode.getRelatedProduct()));
    }


    @Test
    void givenOrchestrationPlanAndProductSpecification_whenCountOfReliesOnOrRequiresProductSpecificationRelationshipsEqualOne_thenSaveItWithReliesOn() {

        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilders.orchestrationPlanBuilder()
                .build();

        ProductOrder productOrder = ProductOrderBuilders.productOrderBuilder()
                .productOrderItem(List.of(ProductOrderItemDTOBuilders.productOrderItemDTOBuilder().build())).build();

        Map<String, ProductSpecification> productSpecificationMap = JsonUtil.readObjectFromResource(PRODUCT_SPECIFICATION_ONE_REQUIRES_LIST, new TypeReference<ArrayList<ProductSpecification>>() {
        }).stream().collect(Collectors.toMap(ProductSpecification::getId, Function.identity()));

        orchestrationPlanNodeRelationshipService.maintainRelationshipsBetweenNodes(productOrder.getProductOrderItem(), orchestrationPlan, productSpecificationMap);

        orchestrationPlan.getOrchestrationPlanNodes().forEach(orchestrationPlanNode -> {
            Assertions.assertNotNull(orchestrationPlanNode.getRelatedProduct());

            int actualRelatedProductReliesOnSize = orchestrationPlanNode.getRelatedProduct()
                    .stream().filter(relatedProduct -> relatedProduct.getRelationshipType().compareTo(RelatedProductRelationType.RELIES_ON) == 0)
                    .toList()
                    .size();

            final int RELATED_PRODUCT_WITH_RELIES_ON_COUNT = 1;
            Assertions.assertEquals(RELATED_PRODUCT_WITH_RELIES_ON_COUNT, actualRelatedProductReliesOnSize);

            orchestrationPlanNode.getRelatedProduct().forEach(relatedProduct ->
                    Assertions.assertEquals(RelatedProductRelationType.RELIES_ON, relatedProduct.getRelationshipType())
            );
        });
    }

    @Test
    void givenOrchestrationPlanAndProductSpecification_whenMultipleReliesOnOrRequiresProductSpecificationRelationships_thenSaveItWithReliesOn() {

        OrchestrationPlan orchestrationPlan = OrchestrationPlanBuilders.orchestrationPlanBuilder()
                .build();

        ProductOrder productOrder = ProductOrderBuilders.productOrderBuilder()
                .productOrderItem(List.of(ProductOrderItemDTOBuilders.productOrderItemDTOBuilder().build(),
                        ProductOrderItemDTOBuilders.productOrderItemDTOBuilder()
                                .product(Product.builder()
                                        .productSpecification(ProductSpecificationRef.builder()
                                                .id("5e18402d-c964-4d52-b362-22ef79c27c01")
                                                .name("Connectivity")
                                                .atType("ProductSpecificationRef")
                                                .build())
                                        .build()
                                ).build())).build();

        Map<String, ProductSpecification> productSpecificationMap = JsonUtil.readObjectFromResource(PRODUCT_SPECIFICATION_MULTIPLE_REQUIRES_LIST, new TypeReference<ArrayList<ProductSpecification>>() {
        }).stream().collect(Collectors.toMap(ProductSpecification::getId, Function.identity()));

        orchestrationPlanNodeRelationshipService.maintainRelationshipsBetweenNodes(productOrder.getProductOrderItem(), orchestrationPlan, productSpecificationMap);

        orchestrationPlan.getOrchestrationPlanNodes().forEach(orchestrationPlanNode -> {
            Assertions.assertNotNull(orchestrationPlanNode.getRelatedProduct());
            int actualRelatedProductReliesOnSize = orchestrationPlanNode.getRelatedProduct()
                    .stream().filter(relatedProduct -> relatedProduct.getRelationshipType().compareTo(RelatedProductRelationType.RELIES_ON) == 0)
                    .toList()
                    .size();
            final int RELATED_PRODUCT_WITH_RELIES_ON_COUNT = 2;
            Assertions.assertEquals(RELATED_PRODUCT_WITH_RELIES_ON_COUNT, actualRelatedProductReliesOnSize);

            orchestrationPlanNode.getRelatedProduct().forEach(relatedProduct ->
                    Assertions.assertEquals(RelatedProductRelationType.RELIES_ON, relatedProduct.getRelationshipType())
            );
        });
    }
}