// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.*;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecificationRef;
import com.orange.discobole.ordermanagement.commons.dto.product.stock.*;
import com.orange.discobole.ordermanagement.commons.dto.resource.inventory.Resource;
import com.orange.discobole.ordermanagement.commons.dto.resource.inventory.ResourceCharacteristic;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.domain.SettingsEntity;
import com.orange.discobole.ordermanagement.ordercapture.service.*;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.exception.DiscoException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourcesReservationActionTest {

    public static final String DEFAULT_PRODUCT_STOCK_RESERVED = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RESOURCE_ID_1 = RandomStringUtils.randomAlphabetic(3);
    public static final String DEFAULT_RESOURCE_ID_2 = RandomStringUtils.randomAlphabetic(3);
    public static final String PRODUCT_ORDER_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ORDER_ITEM_ID_2 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ORDER_ITEM_ID_3 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ORDER_ITEM_ID_4 = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final Instant DEFAULT_CREATION_DATE = Instant.now();
    public static final String DEFAULT_CONFIGURATION_ID = "ACKBasic_In store_20-olympic-city_2024-02-14";
    public static final String CONFIGURATION_ID_ACK_MAX_PLUS = "ACKMaxPlus_In store_20-olympic-city_2024-02-14";
    public static final String DEFAULT_CONFIGURATION_ID_MOD_ADD_HANDSET = "MODAddHandset_contractId_bundledId_InShop_20-olympic-city_2024-02-14";
    public static final String DEFAULT_SERIAL_NUMBER = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_RESOURCE_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_OFFERING_ID_1 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_OFFERING_ID_2 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String VALUE = RandomStringUtils.randomAlphabetic(5);
    public static final String STOCK_ITEM_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DELIVERY_DATE = "2024-05-31";

    @Mock
    private ProductStockManagementService productStockManagementService;
    @Mock
    private ProductSpecificationService productSpecificationService;
    @Mock
    private ResourceInventoryService resourceInventoryService;
    @Mock
    private ProductOrderService productOrderService;
    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private ResourcesReservationAction resourcesReservationAction;

    @Test
    @DisplayName("Given re-executed reserve resources action, " +
            "when reserving resources, " +
            "then the areResourcesReserved variable should be set to false")
    void shouldNotReserveResourcesWhenReExecuted() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        context.getExtendedState().getVariables().put(StateMachineUtil.ON_METHOD, TRUE);

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
    }

    @Test
    @DisplayName("Given an exception in reserve product stock management service, " +
            "when reserving resources, " +
            "then the areResourcesReserved variable should be set to false")
    void shouldNotReserveResourcesWhenExceptionInReservePhysicalResourceService() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doThrow(DiscoException.class).when(productStockManagementService).reserveProductStocks(any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
    }

    @Test
    @DisplayName("Given an exception in get reserved product stock management service, " +
            "when reserving resources, " +
            "then the areResourcesReserved variable should be set to false")
    void shouldNotReserveResourcesWhenExceptionInGetReservedPhysicalResourceService() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doThrow(DiscoException.class).when(productStockManagementService).getReservedProductStocks(any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
    }

    @Test
    @DisplayName("Given not available product stock, " +
            "when reserving resources, " +
            "then the areResourcesReserved variable should be set to false")
    void shouldNotReserveResourcesWhenNotAvailablePhysicalResource() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doReturn(Collections.emptyMap()).when(productStockManagementService).getReservedProductStocks(any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
    }

    @Test
    @DisplayName("Given an exception in resource inventory service, " +
            "when reserving resources, " +
            "then the areResourcesReserved variable should be set to false")
    void shouldNotReserveResourcesWhenExceptionInResourceInventoryService() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createProductSpecifications()).when(productSpecificationService).fetchProductSpecifications(any());
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doReturn(createGetReservedProductOrderItemProductStocks()).when(productStockManagementService).getReservedProductStocks(any());
        doThrow(RuntimeException.class).when(resourceInventoryService).checkAndReserveLogicalResources(any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
    }

    @Test
    @DisplayName("Given not available logical resource in inventory, " +
            "when reserving resources, " +
            "then the areResourcesReserved variable should be set to false")
    void shouldNotReserveResourcesWhenNotAvailableLogicalResource() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createProductSpecifications()).when(productSpecificationService).fetchProductSpecifications(any());
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doReturn(createGetReservedProductOrderItemProductStocks()).when(productStockManagementService).getReservedProductStocks(any());


        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        String description = StateMachineUtil.getStringValue(context, StateMachineUtil.DESCRIPTION);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
        Assertions.assertEquals(DescriptionConstants.REQUIRED_RESOURCES_NOT_AVAILABLE, description);
    }

    @Test
    @DisplayName("Given an exception in add resource service, " +
            "when reserving resources, " +
            "then the areResourcesReserved variable should be set to false")
    void shouldNotReserveResourcesWhenExceptionInAddResourceService() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createProductSpecifications()).when(productSpecificationService).fetchProductSpecifications(any());
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doReturn(createGetReservedProductOrderItemProductStocks()).when(productStockManagementService).getReservedProductStocks(any());
        doReturn(createReservedResourceList()).when(resourceInventoryService).checkAndReserveLogicalResources(any());
        doThrow(RuntimeException.class).when(productOrderService).addResourceRef(any(), any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
    }

    @Test
    @DisplayName("Given available resources, " +
            "when reserving resources with ACK configuration Id, " +
            "then the areResourcesReserved variable should be set to true")
    void shouldReserveResourcesWhenAvailableResources() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createProductSpecifications()).when(productSpecificationService).fetchProductSpecifications(any());
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doReturn(createGetReservedProductOrderItemProductStocks()).when(productStockManagementService).getReservedProductStocks(any());
        doReturn(createReservedResourceList()).when(resourceInventoryService).checkAndReserveLogicalResources(any());
        doNothing().when(productOrderService).addResourceRef(any(), any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        Map<String, String> physicalProductOrderItemSerialNumberMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ORDER_ITEM_SERIAL_NUMBER_MAP);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertTrue(areResourcesReserved);
        Assertions.assertEquals(DEFAULT_SERIAL_NUMBER, physicalProductOrderItemSerialNumberMap.get(PRODUCT_ORDER_ITEM_ID_1));
    }

    @Test
    @DisplayName("Given available resources, " +
            "when reserving resources with ACKMaxPlus configuration Id, " +
            "then the areResourcesReserved variable should be set to true")
    void shouldReserveResourcesWhenAvailableResourcesForAcquisitionMaxPlus() {
        // Given
        StateContext<String, String> context = mockStateContext(CONFIGURATION_ID_ACK_MAX_PLUS);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createProductSpecifications()).when(productSpecificationService).fetchProductSpecifications(any());
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doReturn(createGetReservedProductOrderItemProductStocks()).when(productStockManagementService).getReservedProductStocks(any());
        doReturn(createReservedResourceList()).when(resourceInventoryService).checkAndReserveLogicalResources(any());
        doNothing().when(productOrderService).addResourceRef(any(), any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        Map<String, String> physicalProductOrderItemSerialNumberMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ORDER_ITEM_SERIAL_NUMBER_MAP);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertTrue(areResourcesReserved);
        Assertions.assertEquals(DEFAULT_SERIAL_NUMBER, physicalProductOrderItemSerialNumberMap.get(PRODUCT_ORDER_ITEM_ID_1));
    }

    @Test
    @DisplayName("Given available resources, " +
            "when reserving resources with MODAddHandset configuration Id, " +
            "then the areResourcesReserved variable should be set to true")
    void shouldReserveResourcesWhenAvailableResourcesForModificationAddHandset() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID_MOD_ADD_HANDSET);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createProductSpecifications()).when(productSpecificationService).fetchProductSpecifications(any());
        doReturn(createReservedProductOrderItemProductStocks()).when(productStockManagementService).reserveProductStocks(any());
        doReturn(createGetReservedProductOrderItemProductStocks()).when(productStockManagementService).getReservedProductStocks(any());
        doReturn(createReservedResourceList()).when(resourceInventoryService).checkAndReserveLogicalResources(any());
        doNothing().when(productOrderService).addResourceRef(any(), any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        Map<String, String> physicalProductOrderItemSerialNumberMap = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.PHYSICAL_PRODUCT_ORDER_ITEM_SERIAL_NUMBER_MAP);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertTrue(areResourcesReserved);
        Assertions.assertEquals(DEFAULT_SERIAL_NUMBER, physicalProductOrderItemSerialNumberMap.get(PRODUCT_ORDER_ITEM_ID_1));
    }

    @Test
    @DisplayName("Given available resources, " +
            "and isReservePhysicalResourceEnabled set to false, " +
            "when reserving resources with ACK configuration Id, " +
            "then the areResourcesReserved variable should be set to true")
    void shouldReserveResourcesWhenPhysicalReservationFlagIsDisabled() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(false);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createReservedResourceList()).when(resourceInventoryService).checkAndReserveLogicalResources(any());
        doNothing().when(productOrderService).addResourceRef(any(), any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertTrue(areResourcesReserved);
    }

    @Test
    @DisplayName("Given a product with multiple known characteristic types, " +
            "when reserving resources, " +
            "then each type is handled and resources are reserved")
    void shouldCoverAllKnownCharacteristicTypes() {
        // Given
        StateContext<String, String> context = mockStateContext(DEFAULT_CONFIGURATION_ID);
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);

        List<Characteristic> characteristics = List.of(
                StringCharacteristic.builder().name("Color").value("Red").build(),
                BooleanCharacteristic.builder().name("Waterproof").value(true).build(),
                IntegerCharacteristic.builder().name("Size").value(42).build(),
                ObjectCharacteristic.builder().name("Material").value("Steel").build()  // Ajout de l'ObjectCharacteristic
        );

        Product product = Product.builder()
                .productCharacteristic(characteristics)
                .productSpecification(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef.builder()
                        .id("SPEC-001")
                        .name("Standard Spec")
                        .build())
                .build();

        ProductOrderItem productOrderItem = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_1)
                .product(product)
                .build();

        ProductOrder productOrder = ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .productOrderItem(List.of(productOrderItem))
                .build();

        context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);

        StockedProduct stockedProduct = StockedProduct.builder()
                .serialNumber(DEFAULT_SERIAL_NUMBER)
                .build();

        ProductStockReserved productStockReserved = ProductStockReserved.builder()
                .id("PSR-001")
                .build();

        ReserveProductStockItem reserveProductStockItem = ReserveProductStockItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_1)
                .stockItem(StockItem.builder().id("stock-item-001").build())
                .quantityRequested(QuantityRequested.builder().amount(1).build())
                .productStockReserved(productStockReserved)
                .type("PhysicalResource")
                .build();

        ProductStock productStock = ProductStock.builder()
                .stockedProduct(stockedProduct)
                .resource(com.orange.discobole.ordermanagement.commons.dto.product.stock.Resource.builder()
                        .id(DEFAULT_RESOURCE_ID)
                        .build())
                .reserveProductStockItem(List.of(reserveProductStockItem))
                .build();

        Map<String, ProductStock> reservedStocks = Map.of(PRODUCT_ORDER_ITEM_ID_1, productStock);

        doReturn(reservedStocks).when(productStockManagementService).reserveProductStocks(any());
        doReturn(reservedStocks).when(productStockManagementService).getReservedProductStocks(any());

        List<String> physicalProductOrderItems = List.of(PRODUCT_ORDER_ITEM_ID_1);
        context.getExtendedState().getVariables()
                .put(OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, physicalProductOrderItems);

        Resource mockResource = Resource.builder().id("RES-001").build();
        List<Resource> resourceList = List.of(mockResource);

        Map<String, List<Resource>> reservedLogicalResourceMap = Map.of(
                PRODUCT_ORDER_ITEM_ID_1, resourceList
        );

        doReturn(reservedLogicalResourceMap).when(resourceInventoryService).checkAndReserveLogicalResources(any());

        doNothing().when(productOrderService).addResourceRef(any(), any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        Assertions.assertTrue(areResourcesReserved);
    }

    @Test
    @DisplayName("Given a product order with RELIESON relationship type, " +
            "when reserving resources, " +
            "then should fail because relationship type does not equal REQUIRES")
    void shouldFailWhenRelationshipTypeDoesNotEqualRequires() {
        // Given
        StateContext<String, String> context = mockStateContextWithReliesOnRelationship();
        SettingsEntity settingsEntity = createSettings(true);
        when(settingsService.getSettings()).thenReturn(settingsEntity);
        doReturn(createProductSpecifications()).when(productSpecificationService).fetchProductSpecifications(any());

        // When
        Mono<Void> result = resourcesReservationAction.apply(context);

        // Then
        boolean areResourcesReserved = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.ARE_RESOURCES_RESERVED, FALSE);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Assertions.assertFalse(areResourcesReserved);
    }

    private DefaultStateContext<String, String> mockStateContextWithReliesOnRelationship() {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createProductOrderWithReliesOnRelationship());
        extendedState.getVariables().put(OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES, createProdOrderItemRelatedResourcesIfReserved());
        extendedState.getVariables().put(OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, createPhysicalProductOrderItems());
        extendedState.getVariables().put(OrderCaptureConstants.CONFIGURATION_ID, DEFAULT_CONFIGURATION_ID);
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }

    private ProductOrder createProductOrderWithReliesOnRelationship() {
        ProductOrderItem productOrderItemPS = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_2)
                .product(Product.builder()
                        .productSpecification(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef.builder()
                                .id(PRODUCT_SPECIFICATION_ID)
                                .atType("ProductSpecificationRef")
                                .build())
                        .productCharacteristic(Collections.singletonList(StringCharacteristic.builder()
                                .value(VALUE)
                                .name("ICCID")
                                .valueType("String")
                                .atType("StringCharacteristic")
                                .build()))
                        .build())
                .state(ProductOrderItemStateType.DRAFT)
                .build();

        OrderItemRelationship orderItemRelationship = OrderItemRelationship.builder()
                .id(PRODUCT_ORDER_ITEM_ID_2)
                .relationshipType(RelationshipType.RELIESON)
                .build();

        ProductOrderItem shippingProductOrderItemPS = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_4)
                .product(Product.builder()
                        .productSpecification(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef.builder()
                                .id(PRODUCT_SPECIFICATION_ID)
                                .atType("ProductSpecificationRef")
                                .atBaseType(OrderCaptureConstants.SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE)
                                .build())
                        .productCharacteristic(Collections.singletonList(StringCharacteristic.builder()
                                .name(OrderCaptureConstants.REQUESTED_DELIVERY_DATE)
                                .value(DELIVERY_DATE)
                                .build()))
                        .build())
                .productOrderItemRelationship(Collections.singletonList(orderItemRelationship))
                .state(ProductOrderItemStateType.DRAFT)
                .build();

        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(List.of(productOrderItemPS, shippingProductOrderItemPS))
                .build();
    }

    private Map<String, List<Resource>> createReservedResourceList() {
        Map<String, List<Resource>> reservedResourceList = new HashMap<>();
        reservedResourceList.put(PRODUCT_ORDER_ITEM_ID_1, List.of(Resource.builder().id(DEFAULT_RESOURCE_ID_1)
                .resourceCharacteristic(List.of(ResourceCharacteristic.builder().name("MSISDN").value("123").valueType("String")
                        .type("StringCharacteristic").build()))
                .build()));
        reservedResourceList.put(PRODUCT_ORDER_ITEM_ID_2, List.of(Resource.builder().id(DEFAULT_RESOURCE_ID_2)
                .resourceCharacteristic(List.of(ResourceCharacteristic.builder().name("ICCID").value("123").valueType("String")
                        .type("StringCharacteristic").build()))
                .build()));
        return reservedResourceList;
    }

    private ProductOrder createProductOrder() {
        ProductOrderItem productOrderItemPO = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_1)
                .state(ProductOrderItemStateType.DRAFT)
                .productOffering(ProductOfferingRef.builder()
                        .id(PRODUCT_OFFERING_ID_1)
                        .atType("ProductOfferingRef")
                        .build())
                .product(Product.builder()
                        .id("123")
                        .build())
                .build();
        ProductOrderItem productOrderItemPS = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_2)
                .product(Product.builder()
                        .productSpecification(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef.builder()
                                .id(PRODUCT_SPECIFICATION_ID)
                                .atType("ProductSpecificationRef")
                                .build())
                        .productCharacteristic(Collections.singletonList(StringCharacteristic.builder()
                                .value(VALUE)
                                .name("ICCID")
                                .valueType("String")
                                .atType("StringCharacteristic")
                                .build()))
                        .build())
                .state(ProductOrderItemStateType.DRAFT)
                .build();
        ProductOrderItem shippingProductOrderItemPO = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_3)
                .state(ProductOrderItemStateType.DRAFT)
                .productOffering(ProductOfferingRef.builder()
                        .id(PRODUCT_OFFERING_ID_2)
                        .atType("ProductOfferingRef")
                        .build())
                .build();
        OrderItemRelationship orderItemRelationship = OrderItemRelationship.builder()
                .id(PRODUCT_ORDER_ITEM_ID_2)
                .relationshipType(RelationshipType.REQUIRES)
                .build();
        ProductOrderItem shippingProductOrderItemPS = ProductOrderItem.builder()
                .id(PRODUCT_ORDER_ITEM_ID_4)
                .product(Product.builder()
                        .productSpecification(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef.builder()
                                .id(PRODUCT_SPECIFICATION_ID)
                                .atType("ProductSpecificationRef")
                                .atBaseType(OrderCaptureConstants.SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE)
                                .build())
                        .productCharacteristic(Collections.singletonList(StringCharacteristic.builder()
                                .name(OrderCaptureConstants.REQUESTED_DELIVERY_DATE)
                                .value(DELIVERY_DATE)
                                .build()))
                        .build())
                .productOrderItemRelationship(Collections.singletonList(orderItemRelationship))
                .state(ProductOrderItemStateType.DRAFT)
                .build();
        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .productOrderItem(List.of(productOrderItemPO, productOrderItemPS, shippingProductOrderItemPO, shippingProductOrderItemPS))
                .build();
    }

    private Map<String, List<String>> createProdOrderItemRelatedResourcesIfReserved() {
        Map<String, List<String>> prodOrderItemRelatedResources = new HashMap<>();
        prodOrderItemRelatedResources.put(PRODUCT_ORDER_ITEM_ID_1, List.of("IMSI"));
        prodOrderItemRelatedResources.put(PRODUCT_ORDER_ITEM_ID_2, List.of("ICCID"));
        return prodOrderItemRelatedResources;
    }

    private DefaultStateContext<String, String> mockStateContext(String configurationId) {
        ExtendedState extendedState = new DefaultExtendedState();
        extendedState.getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, createProductOrder());
        extendedState.getVariables().put(OrderCaptureConstants.PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES, createProdOrderItemRelatedResourcesIfReserved());
        extendedState.getVariables().put(OrderCaptureConstants.PHYSICAL_PRODUCT_ITEM_ID_LIST, createPhysicalProductOrderItems());
        extendedState.getVariables().put(OrderCaptureConstants.CONFIGURATION_ID, configurationId);
        ObjectStateMachine<String, String> stateMachine = new ObjectStateMachine<>(null, null, null, null, null,
                extendedState, null);
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, extendedState, null, stateMachine, null,
                null, null);
    }

    private List<String> createPhysicalProductOrderItems() {
        return Collections.singletonList(PRODUCT_ORDER_ITEM_ID_2);
    }

    private Map<String, ProductStock> createReservedProductOrderItemProductStocks() {
        ProductStock productStock = new ProductStock();
        ReserveProductStockItem reserveProductStockItem = new ReserveProductStockItem();
        ProductStockReserved productStockReserved = new ProductStockReserved();
        productStockReserved.setId(DEFAULT_PRODUCT_STOCK_RESERVED);
        reserveProductStockItem.setProductStockReserved(productStockReserved);
        productStock.setReserveProductStockItem(Collections.singletonList(reserveProductStockItem));
        Map<String, ProductStock> reservedProductOrderItemProductStocks = new HashMap<>();
        reservedProductOrderItemProductStocks.put(PRODUCT_ORDER_ITEM_ID_1, productStock);
        return reservedProductOrderItemProductStocks;
    }

    private Map<String, ProductStock> createGetReservedProductOrderItemProductStocks() {
        StockedProduct stockedProduct = StockedProduct.builder()
                .serialNumber(DEFAULT_SERIAL_NUMBER)
                .build();

        ProductStock productStock = ProductStock.builder()
                .stockedProduct(stockedProduct)
                .resource(com.orange.discobole.ordermanagement.commons.dto.product.stock.Resource.builder()
                        .id(DEFAULT_RESOURCE_ID)
                        .build())
                .build();

        Map<String, ProductStock> reservedProductOrderItemProductStocks = new HashMap<>();
        reservedProductOrderItemProductStocks.put(PRODUCT_ORDER_ITEM_ID_1, productStock);

        return reservedProductOrderItemProductStocks;
    }

    private List<ProductSpecification> createProductSpecifications() {
        ProductConfigurationSpec productConfiguration = ProductConfigurationSpec.builder()
                .stockItemRef(StockItemRef.builder()
                        .id(STOCK_ITEM_ID)
                        .build())
                .productSpecification(ProductSpecificationRef.builder()
                        .id(PRODUCT_SPECIFICATION_ID)
                        .build())
                .definedBy(Collections.singletonList(ProductConfSpecCharacteristicValue.builder()
                        .value(VALUE)
                        .build()))
                .build();
        ProductSpecCharacteristic productSpecCharacteristic = ProductSpecCharacteristic
                .builder().name("IMSI").build();

        ProductSpecification productSpecification = ProductSpecification.builder()
                .id(PRODUCT_SPECIFICATION_ID)
                .productConfiguration(Collections.singletonList(productConfiguration))
                .type("ProductSpecification")
                .productSpecCharacteristic(List.of(productSpecCharacteristic))
                .build();
        return Collections.singletonList(productSpecification);
    }

    private SettingsEntity createSettings(boolean isReservePhysicalResourceEnabled) {
        return SettingsEntity.builder()
                .reservePhysicalResourceEnabled(isReservePhysicalResourceEnabled)
                .reserveLogicalResourceEnabled(true)
                .checkCommercialEligibilityEnabled(true)
                .checkPaymentRefEnabled(true)
                .build();
    }
}