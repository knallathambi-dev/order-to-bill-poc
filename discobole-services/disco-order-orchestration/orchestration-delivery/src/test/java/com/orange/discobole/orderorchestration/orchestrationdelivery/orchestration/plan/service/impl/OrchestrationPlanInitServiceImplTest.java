// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.MaintainOrchestrationPlanNodeRelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanInitService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanNodeRelationshipService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl.MaintainOrchestrationPlanNodeRelatedProductImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl.OrchestrationPlanInitServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.impl.OrchestrationPlanNodeRelationshipServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.ProductCatalogCustomMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.ValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrchestrationPlanInitServiceImplTest {

    public static final String PRODUCT_ORDER_DTO_JSON_FILE = "/ProductOrderDTO.json";
    public static final String VALID_PRODUCT_ORDER_DTO_JSON_FILE = "/validProductOrderItem.json";
    public static final String INVALID_PRODUCT_ORDER_DTO_JSON_FILE = "/invalidProductOrder.json";
    public static final String INVALID_PRODUCT_ORDER_ITEM_DTO_JSON_FILE = "/invalidProductOrderItem.json";
    @Mock
    OrchestrationPlanRepository orchestrationPlanRepository;
    OrchestrationPlanInitService orchestrationPlanInitServiceImpl;
    OrchestrationPlanNodeRelationshipService orchestrationPlanNodeRelationshipService;
    @Mock
    ProductCatalogCustomMapper productCatalogCustomMapper;
    @Mock
    OrchestrationPlanMapper orchestrationPlanMapper;
    private MaintainOrchestrationPlanNodeRelatedProduct maintainOrchestrationPlanNodeRelatedProduct;
    @Mock
    private CharacteristicMapper characteristicMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        maintainOrchestrationPlanNodeRelatedProduct = new MaintainOrchestrationPlanNodeRelatedProductImpl();
        orchestrationPlanNodeRelationshipService = new OrchestrationPlanNodeRelationshipServiceImpl(maintainOrchestrationPlanNodeRelatedProduct);
        orchestrationPlanInitServiceImpl = new OrchestrationPlanInitServiceImpl(orchestrationPlanMapper, orchestrationPlanRepository, productCatalogCustomMapper, characteristicMapper);
    }

    @Test
    void testFilterOrderItems() {

        ProductOrder productOrder = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });

        try (MockedStatic<ValidationUtil> mockedStatic = Mockito.mockStatic(ValidationUtil.class)) {

            mockedStatic.when(() -> ValidationUtil.isValid(any())).thenReturn(true);

            //given expected value
            List<ProductOrderItem> result = orchestrationPlanInitServiceImpl.filterOrderItems(productOrder);

            ProductOrder validProductOrder = JsonUtil.readObjectFromResource(VALID_PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
            });
            Assertions.assertEquals(validProductOrder.getProductOrderItem(), result);
        }
    }

    @Test
    void testFilterOrderItemsListIsEmpty() {

        ProductOrder invalidProductOrder = JsonUtil.readObjectFromResource(INVALID_PRODUCT_ORDER_ITEM_DTO_JSON_FILE, new TypeReference<>() {
        });

        try (MockedStatic<ValidationUtil> mockedStatic = Mockito.mockStatic(ValidationUtil.class)) {
            mockedStatic.when(() -> ValidationUtil.isValid(any())).thenReturn(false);
            List<ProductOrderItem> invalidList = orchestrationPlanInitServiceImpl.filterOrderItems(invalidProductOrder);
            Assertions.assertTrue(invalidList.isEmpty());
        }
    }

    @Test
    void testFilterOrderItemWithNullProduct() {

        ProductOrder invalidProductOrder = JsonUtil.readObjectFromResource(INVALID_PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });

        try (MockedStatic<ValidationUtil> mockedStatic = Mockito.mockStatic(ValidationUtil.class)) {
            mockedStatic.when(() -> ValidationUtil.isValid(any())).thenReturn(false);
            List<ProductOrderItem> invalidList = orchestrationPlanInitServiceImpl.filterOrderItems(invalidProductOrder);
            Assertions.assertTrue(invalidList.isEmpty());
        }
    }

    @Test
    void givenProductOrderItemWithProductRefType_whenGetProductOrderItemCharacteristics_thenEmptySetReturned() {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .product(ProductRef.builder().build())
                .build();

        Set<Characteristic> characteristics = orchestrationPlanInitServiceImpl.getProductOrderItemCharacteristics(productOrderItem, "id1");

        Assertions.assertTrue(characteristics.isEmpty());
    }

    @Test
    void givenProductOrderItemWithProductTypeAndThrowExcpetionOnMappeing_whenGetProductOrderItemCharacteristics_thenCoodExceptionThrown() {
        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .product(Product.builder()
                        .productCharacteristic(List.of())
                        .build())
                .build();

        when(characteristicMapper.from(anyList())).thenThrow(new RuntimeException("Test Exception"));

        Assertions.assertThrows(CoodRecoverableAndNonRetryableException.class, () -> {
            orchestrationPlanInitServiceImpl.getProductOrderItemCharacteristics(productOrderItem, "id1");
        });
    }


}