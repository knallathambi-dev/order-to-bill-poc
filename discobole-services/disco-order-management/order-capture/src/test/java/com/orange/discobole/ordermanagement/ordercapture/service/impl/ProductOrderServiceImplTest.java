// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecCharacteristic;
import com.orange.discobole.ordermanagement.commons.dto.product.specification.ProductSpecification;
import com.orange.discobole.ordermanagement.event.om.Command;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.mapper.product.order.ProductOrderItemMapper;
import com.orange.discobole.ordermanagement.ordercapture.producer.ProductOrderCommandProducer;
import com.orange.discobole.ordermanagement.ordercapture.service.OrderInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ShipmentProduct;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItemStateType.ACCEPTED;
import static com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItemStateType.DRAFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOrderServiceImplTest {
    public static final String PRODUCT_TYPE = "Product";
    public static final String UNITS = "month";
    public static final float AMOUNT = 10.0f;
    public static final String PARTY_REF_TYPE = "PartyRef";
    private static final String PRODUCT_ORDER_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_5 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_6 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_7 = RandomStringUtils.randomAlphabetic(10);
    private static final String CONTRACT_PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(10);
    private static final String RESOURCE_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PAYMENT_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String BILLING_ACCOUNT_ID = RandomStringUtils.randomAlphabetic(10);
    private static final Instant DEFAULT_CREATION_DATE = Instant.now();
    private static final String PRODUCT_OFFERING_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_ID_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_NAME_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_NAME_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_NAME_3 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_NAME_4 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_SPECIFICATION_NAME_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_OFFERING_REF = "ProductOfferingRef";
    private static final String PRODUCT_SPECIFICATION_REF = "ProductSpecificationRef";
    private static final String DEFAULT_RELATED_RESOURCES_ID_1 = RandomStringUtils.randomAlphabetic(3);
    private static final String DEFAULT_RELATED_RESOURCES_ID_2 = RandomStringUtils.randomAlphabetic(3);
    private static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_RELATED_PARTY_NAME = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_RELATED_PARTY_ROLE = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_RELATED_PARTY_REFERRED_TYPE = RandomStringUtils.randomAlphabetic(5);
    private static final float DUTY_FREE_AMOUNT_VALUE_1 = 10f;
    private static final float TAX_INCLUDED_AMOUNT_VALUE_1 = 10f;
    private static final float DUTY_FREE_AMOUNT_VALUE_2 = 10f;
    private static final float TAX_INCLUDED_AMOUNT_VALUE_2 = 10f;
    private static final String UNIT = "EUR";
    private static final String RC_PRICE_TYPE = "recurringCharge";
    private static final String NRC_PRICE_TYPE = "nonRecurringCharge";
    private final ProductOrderItemMapper productOrderItemMapperImpl = Mappers.getMapper(ProductOrderItemMapper.class);

    @InjectMocks
    private ProductOrderServiceImpl productOrderService;
    @Mock
    private OrderInventoryService orderInventoryService;
    @Mock
    private ProductInventoryService productInventoryService;
    @Mock
    private ProductOrderCommandProducer productOrderCommandProducer;
    @Mock
    private ProductOrderItemMapper productOrderItemMapper;

    @Test
    @DisplayName("Given new product order, " +
            "when save a product order in DB, " +
            "then product is created with state Draft")
    void shouldCreateProductOrderWhenSaveProductOrderInDB() {
        // Given
        ProductOrder productOrder = createProductOrder();
        when(orderInventoryService.createProductOrder(any())).thenReturn(productOrder);

        // When
        ProductOrder createdProductOrder = productOrderService.createProductOrder(productOrder);

        // Then
        assertNotNull(createdProductOrder.getId());
        assertEquals(ProductOrderStateType.DRAFT, createdProductOrder.getState());
        verify(orderInventoryService, times(1)).createProductOrder(any(ProductOrder.class));
    }

    @Test
    @DisplayName("Given null related resourcesIds, " +
            "when get related resources id List, " +
            "then return empty map")
    void testGetProductOrderItemLogicalResourcesIdsWithNullProductOrder() {
        // Given
        // When
        // Then
        assertEquals(Collections.emptyMap(), productOrderService.getProductOrderItemLogicalResourcesIds(null, Collections.emptyList()));
    }

    @Test
    @DisplayName("Given empty product order item List, " +
            "when get related resources id List, " +
            "then return empty map")
    void shouldReturnEmptyMapWhenProductOrderItemListIsEmpty() {
        // Given
        Map<String, String> productOrderItemProdSpecMap = new HashMap<>();

        // When
        Map<String, List<String>> relatedResourcesIds = productOrderService.getProductOrderItemLogicalResourcesIds(productOrderItemProdSpecMap, Collections.emptyList());

        // Then
        assertEquals(Collections.emptyMap(), relatedResourcesIds);
    }

    @Test
    @DisplayName("Given product specifications without characteristics, " +
            "when get related resources id List, " +
            "then return empty map")
    void shouldReturnEmptyMapWhenProductSpecificationsHaveNoCharacteristics() {
        // Given
        List<String> relatedResourcesIds = createRelatedResourcesIDList();
        Map<String, String> productOrderItemProdSpecMap = new HashMap<>();
        String specId1 = relatedResourcesIds.get(0);
        String specId2 = relatedResourcesIds.get(1);
        productOrderItemProdSpecMap.put("1", specId1);
        productOrderItemProdSpecMap.put("2", specId2);

        List<ProductSpecification> productSpecifications = createProductSpecifications(specId1, specId2);

        // When
        Map<String, List<String>> result = productOrderService.getProductOrderItemLogicalResourcesIds(productOrderItemProdSpecMap, productSpecifications);

        // Then
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    @DisplayName("Given a product order, " +
            "when get related resources id List, " +
            "then return related resources id List")
    void shouldReturnRelatedResourcesIdListForGivenProductOrder() {
        // Given
        List<String> relatedResourcesIds = createRelatedResourcesIDList();
        String specId1 = relatedResourcesIds.get(0);
        String specId2 = relatedResourcesIds.get(1);
        Map<String, String> productOrderItemProdSpecMap = new HashMap<>();
        productOrderItemProdSpecMap.put("1", specId1);
        productOrderItemProdSpecMap.put("2", specId2);

        List<ProductSpecification> productSpecifications = createProductSpecificationsWithCharacteristics(specId1, specId2);

        // When
        Map<String, List<String>> result = productOrderService.getProductOrderItemLogicalResourcesIds(productOrderItemProdSpecMap, productSpecifications);

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Given null product order item map, " +
            "when get physical product order items, " +
            "then return empty list")
    void shouldReturnEmptyListWhenProductOrderItemMapIsNull() {
        // Given
        // When
        List<String> result = productOrderService.getPhysicalProductOrderItems(null, Collections.emptyList());

        // Then
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    @DisplayName("Given empty product order item map, " +
            "when get physical product order items, " +
            "then return empty list")
    void shouldReturnEmptyListWhenProductOrderItemMapIsEmpty() {
        // Given
        Map<String, String> productOrderItemProductSpecIdMap = new HashMap<>();

        // When
        List<String> result = productOrderService.getPhysicalProductOrderItems(productOrderItemProductSpecIdMap, Collections.emptyList());

        // Then
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    @DisplayName("Given product specifications without supportEntity, " +
            "when get physical product order items, " +
            "then return empty list")
    void shouldReturnEmptyListWhenProductSpecificationsHaveNoSupportEntity() {
        // Given
        Map<String, String> productOrderItemProductSpecIdMap = new HashMap<>();
        String specId1 = RandomStringUtils.randomAlphabetic(5);
        String specId2 = RandomStringUtils.randomAlphabetic(5);
        productOrderItemProductSpecIdMap.put("item1", specId1);
        productOrderItemProductSpecIdMap.put("item2", specId2);

        List<ProductSpecification> productSpecifications = createProductSpecifications(specId1, specId2);

        // When
        List<String> result = productOrderService.getPhysicalProductOrderItems(productOrderItemProductSpecIdMap, productSpecifications);

        // Then
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    @DisplayName("Given product specifications with wrong supportEntity, " +
            "when get physical product order items, " +
            "then return empty list")
    void shouldReturnEmptyListWhenProductSpecificationsHaveWrongSupportEntity() {
        // Given
        Map<String, String> productOrderItemProductSpecIdMap = new HashMap<>();
        String specId1 = RandomStringUtils.randomAlphabetic(5);
        String specId2 = RandomStringUtils.randomAlphabetic(5);
        productOrderItemProductSpecIdMap.put("item1", specId1);
        productOrderItemProductSpecIdMap.put("item2", specId2);

        List<ProductSpecification> productSpecifications = List.of(
                ProductSpecification.builder()
                        .id(specId1)
                        .supportEntity("WrongType")
                        .build(),
                ProductSpecification.builder()
                        .id(specId2)
                        .supportEntity("AnotherWrongType")
                        .build()
        );

        // When
        List<String> result = productOrderService.getPhysicalProductOrderItems(productOrderItemProductSpecIdMap, productSpecifications);

        // Then
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    @DisplayName("Given product specifications with correct supportEntity, " +
            "when get physical product order items, " +
            "then return list of product order item IDs")
    void shouldReturnProductOrderItemIdsWhenProductSpecificationsHaveStockItemType() {
        // Given
        Map<String, String> productOrderItemProductSpecIdMap = new HashMap<>();
        String specId1 = RandomStringUtils.randomAlphabetic(5);
        String specId2 = RandomStringUtils.randomAlphabetic(5);
        String itemId1 = "item1";
        String itemId2 = "item2";
        productOrderItemProductSpecIdMap.put(itemId1, specId1);
        productOrderItemProductSpecIdMap.put(itemId2, specId2);

        List<ProductSpecification> productSpecifications = List.of(
                ProductSpecification.builder()
                        .id(specId1)
                        .supportEntity(OrderCaptureConstants.STOCK_ITEM_TYPE)
                        .build(),
                ProductSpecification.builder()
                        .id(specId2)
                        .supportEntity(OrderCaptureConstants.STOCK_ITEM_TYPE)
                        .build()
        );

        // When
        List<String> result = productOrderService.getPhysicalProductOrderItems(productOrderItemProductSpecIdMap, productSpecifications);

        // Then
        assertEquals(2, result.size());
        assertThat(result).containsExactlyInAnyOrder(itemId1, itemId2);
    }

    @Test
    @DisplayName("Given mixed product specifications, " +
            "when get physical product order items, " +
            "then return only items with StockItemType supportEntity")
    void shouldReturnOnlyPhysicalItemsWhenMixedProductSpecifications() {
        // Given
        Map<String, String> productOrderItemProductSpecIdMap = new HashMap<>();
        String specId1 = RandomStringUtils.randomAlphabetic(5);
        String specId2 = RandomStringUtils.randomAlphabetic(5);
        String specId3 = RandomStringUtils.randomAlphabetic(5);
        String itemId1 = "item1";
        String itemId2 = "item2";
        String itemId3 = "item3";
        productOrderItemProductSpecIdMap.put(itemId1, specId1);
        productOrderItemProductSpecIdMap.put(itemId2, specId2);
        productOrderItemProductSpecIdMap.put(itemId3, specId3);

        List<ProductSpecification> productSpecifications = List.of(
                ProductSpecification.builder()
                        .id(specId1)
                        .supportEntity(OrderCaptureConstants.STOCK_ITEM_TYPE)
                        .build(),
                ProductSpecification.builder()
                        .id(specId2)
                        .supportEntity("WrongType")
                        .build(),
                ProductSpecification.builder()
                        .id(specId3)
                        .supportEntity(OrderCaptureConstants.STOCK_ITEM_TYPE)
                        .build()
        );

        // When
        List<String> result = productOrderService.getPhysicalProductOrderItems(productOrderItemProductSpecIdMap, productSpecifications);

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(itemId1, itemId3)
                .doesNotContain(itemId2);
    }

    @Test
    @DisplayName("Given product specifications with case-insensitive supportEntity, " +
            "when get physical product order items, " +
            "then return list of product order item IDs")
    void shouldReturnProductOrderItemIdsWhenSupportEntityIsCaseInsensitive() {
        // Given
        Map<String, String> productOrderItemProductSpecIdMap = new HashMap<>();
        String specId1 = RandomStringUtils.randomAlphabetic(5);
        String itemId1 = "item1";
        productOrderItemProductSpecIdMap.put(itemId1, specId1);

        List<ProductSpecification> productSpecifications = List.of(
                ProductSpecification.builder()
                        .id(specId1)
                        .supportEntity("stockitemtype")
                        .build()
        );

        // When
        List<String> result = productOrderService.getPhysicalProductOrderItems(productOrderItemProductSpecIdMap, productSpecifications);

        // Then
        assertEquals(1, result.size());
        assertThat(result).contains(itemId1);
    }

    @Test
    @DisplayName("Given null product order, " +
            "when update product order state, " +
            "then throw InvalidParameterException")
    void shouldThrowIllegalArgumentExceptionWhenUpdatingStateWithNullProductOrder() {
        // Given
        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> productOrderService.updateProductOrderInventoryState(null, ProductOrderStateType.ACKNOWLEDGED));
    }

    @Test
    @DisplayName("Given null product order Id, " +
            "when update product order state, " +
            "then throw InvalidParameterException")
    void shouldThrowIllegalArgumentExceptionWhenUpdatingStateWithNullProductOrderId() {
        // Given
        ProductOrder productOrder = createProductOrder();
        productOrder.setId(null);

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.ACKNOWLEDGED));
    }

    @Test
    @DisplayName("Given null product order Item, " +
            "when update product order state, " +
            "then throw InvalidParameterException")
    void shouldThrowIllegalArgumentExceptionWhenUpdatingStateWithNullProductOrderItem() {
        // Given
        ProductOrder productOrder = createProductOrder();
        productOrder.setProductOrderItem(null);

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.ACKNOWLEDGED));
    }

    @Test
    @DisplayName("Given null product order state, " +
            "when update product order state, " +
            "then throw InvalidParameterException")
    void shouldThrowIllegalArgumentExceptionWhenUpdatingStateWithNullProductOrderState() {
        // Given
        ProductOrder productOrder = createProductOrder();

        // When
        // Then
        assertThrows(IllegalArgumentException.class, () -> productOrderService.updateProductOrderInventoryState(productOrder, null));
    }

    @Test
    @DisplayName("Given  product order with state draft, " +
            "when update product order state, " +
            "then return product order, " +
            "and the product order state updated, " +
            "and the product order items state updated ")
    void shouldUpdateProductOrderStateAndItemStatesWhenStateIsUpdatedToAccepted() {
        // Given
        ProductOrder productOrder = createProductOrder();
        doNothing().when(productOrderCommandProducer).publishCommand(any(), any());

        // When
        productOrderService.updateProductOrderInventoryState(productOrder, ProductOrderStateType.ACCEPTED);
        List<ProductOrderItem> productOrderItemDTOS = List.copyOf(productOrder.getProductOrderItem());

        // Then
        assertNotNull(productOrder);
        assertEquals(ProductOrderStateType.ACCEPTED, productOrder.getState());
        assertEquals(ACCEPTED, productOrderItemDTOS.get(0).getState());
        assertEquals(ACCEPTED, productOrderItemDTOS.get(1).getState());
    }

    @Test
    @DisplayName("Given a product order, " +
            "when create products, " +
            "then return the created products")
    void shouldReturnCreatedProductsWhenCreatingWithValidProductItems() {
        // Given
        ProductOrder productOrder = createProductOrder();
        Product product;
        ShipmentProduct shipmentProduct = null;

        for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
            product = productOrderItemMapperImpl.mapProductOrderItemToProduct(productOrderItem);
            shipmentProduct = productOrderItemMapperImpl.mapProductOrderItemToShipmentProduct(productOrderItem);
            when(productOrderItemMapper.mapProductOrderItemToProduct(any())).thenReturn(product);
            when(productOrderItemMapper.mapProductOrderItemToShipmentProduct(any())).thenReturn(shipmentProduct);
        }
        when(productInventoryService.createProducts(any())).thenReturn(createdProducts());
        when(productInventoryService.createProducts(shipmentProduct)).thenReturn(createdShippingProducts());

        Map<String, String> physicalProductOrderItemSerialNumberMap = createPhysicalProductOrderItemSerialNumberMap();

        // When
        productOrderService.createProducts(productOrder, physicalProductOrderItemSerialNumberMap, "add", null);

        // Then
        verify(productInventoryService, times(3)).createProducts(any());
        verify(productInventoryService, times(1)).updateProducts(any());
    }

    @Test
    @DisplayName("Given a product order and related resources, " +
            "when adding resource ref, " +
            "then publish resource ref change command")
    void shouldPublishResourceRefChangeCommandWhenAddingResourceRef() {
        // Given
        ProductOrder productOrder = createProductOrder();
        Map<String, List<ResourceRef>> orderItemRelatedResList = new HashMap<>();
        ResourceRef resourceRef1 = ResourceRef.builder()
                .id(RESOURCE_ID_1)
                .atType(OrderCaptureConstants.RESOURCE_TYPE)
                .build();

        orderItemRelatedResList.put(PRODUCT_ORDER_ITEM_ID_1, Collections.singletonList(resourceRef1));

        // When
        productOrderService.addResourceRef(productOrder, orderItemRelatedResList);

        // Then
        ArgumentCaptor<ProductOrder> productOrderCaptor = ArgumentCaptor.forClass(ProductOrder.class);
        verify(productOrderCommandProducer, times(1)).publishCommand(productOrderCaptor.capture(), eq(Command.EventType.REALIZING_RESOURCE_VALUE_CHANGE_COMMAND));

        ProductOrder capturedProductOrder = productOrderCaptor.getValue();
        ProductOrderItem capturedItem = capturedProductOrder.getProductOrderItem().get(0);

        if (capturedItem.getProduct() instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product) {
            List<ResourceRef> capturedResourceRefs = product.getRealizingResource();
            assertEquals(1, capturedResourceRefs.size());
            assertEquals(RESOURCE_ID_1, capturedResourceRefs.get(0).getId());
        }
    }

    @Test
    @DisplayName("Given a product order and updated items, " +
            "when updating order items, " +
            "then publish order items change command")
    void shouldPublishOrderItemsChangeCommandWhenUpdatingProductOrderItems() {
        // Given
        ProductOrder productOrder = createProductOrder();
        List<ProductOrderItem> productOrderItems = createProductOrderItemDTOs();
        List<OrderPrice> orderPrices = createOrderTotalPrice();
        ProductOrder updatedProductOrder = ProductOrder.builder()
                .id(productOrder.getId())
                .productOrderItem(productOrderItems)
                .orderTotalPrice(orderPrices)
                .build();

        // When
        productOrderService.updateOrderItemsAndOrderTotalPrice(productOrder, productOrderItems, orderPrices);

        // Then
        verify(productOrderCommandProducer, times(1)).publishCommand(updatedProductOrder, Command.EventType.ORDER_ITEMS_AND_ORDER_TOTAL_PRICE_VALUE_CHANGE_COMMAND);
    }

    @Test
    @DisplayName("Given a product order and payment references, " +
            "when adding payment references, " +
            "then publish payment ref change command")
    void shouldPublishPaymentRefChangeCommandAndUpdateProductOrderWhenAddingPaymentReferences() {
        // Given
        ProductOrder productOrder = createProductOrder();
        Map<String, List<String>> orderItemPaymentRefList = new HashMap<>();
        orderItemPaymentRefList.put(PRODUCT_ORDER_ITEM_ID_1, Collections.singletonList(PAYMENT_ID_1));

        // When
        productOrderService.addPaymentRef(productOrder, orderItemPaymentRefList);

        // Then
        ArgumentCaptor<ProductOrder> productOrderCaptor = ArgumentCaptor.forClass(ProductOrder.class);
        verify(productOrderCommandProducer, times(1)).publishCommand(productOrderCaptor.capture(), eq(Command.EventType.PAYMENT_VALUE_CHANGE_COMMAND));

        ProductOrder capturedProductOrder = productOrderCaptor.getValue();
        assertEquals(1, capturedProductOrder.getProductOrderItem().get(0).getPayment().size());
        assertEquals(PAYMENT_ID_1, capturedProductOrder.getProductOrderItem().get(0).getPayment().get(0).getId());
    }

    @Test
    @DisplayName("Given valid ProductOrder and BillingAccountRefs, " +
            "when addBillingAccountRef is called, " +
            "then it should update ProductOrder with BillingAccountRefs")
    void shouldUpdateProductOrderWithBillingAccountRefsWhenAddBillingAccountRefIsCalled() {
        // Given
        ProductOrder productOrder = createProductOrder();
        Map<String, String> orderItemBillingAccountRefList = new HashMap<>();
        orderItemBillingAccountRefList.put(PRODUCT_ORDER_ITEM_ID_1, BILLING_ACCOUNT_ID);

        // When
        productOrderService.addBillingAccountRef(productOrder, orderItemBillingAccountRefList);

        // Then
        ArgumentCaptor<ProductOrder> productOrderCaptor = ArgumentCaptor.forClass(ProductOrder.class);
        verify(productOrderCommandProducer, times(1))
                .publishCommand(productOrderCaptor.capture(), eq(Command.EventType.BILLING_ACCOUNT_VALUE_CHANGE_COMMAND));

        ProductOrder capturedProductOrder = productOrderCaptor.getValue();
        assertNotNull(capturedProductOrder.getProductOrderItem().get(0).getBillingAccount());
        assertEquals(BILLING_ACCOUNT_ID, capturedProductOrder.getProductOrderItem().get(0).getBillingAccount().getId());
    }

    @Test
    @DisplayName("Given valid ProductOrder, " +
            "when addProductRef is called, " +
            "then it should update ProductOrder with ProductRef")
    void shouldUpdateProductOrderWithProductRefsWhenAddProductRefIsCalled() {
        // Given
        ProductOrder productOrder = createProductOrder();

        // When
        productOrderService.updateProduct(productOrder);

        // Then
        ArgumentCaptor<ProductOrder> productOrderCaptor = ArgumentCaptor.forClass(ProductOrder.class);
        verify(productOrderCommandProducer, times(1))
                .publishCommand(productOrderCaptor.capture(), eq(Command.EventType.PRODUCT_VALUE_CHANGE_COMMAND));
    }

    @Test
    @DisplayName("Given a product order, " +
            "when updating related parties, " +
            "then publish related parties change command")
    void shouldPublishRelatedPartiesChangeCommandWhenUpdatingRelatedParties() {
        // Given
        ProductOrder productOrder = ProductOrder.builder()
                .relatedParty(Collections.singletonList(RelatedPartyRefOrPartyRoleRef.builder()
                        .role(OrderCaptureConstants.CUSTOMER)
                        .build()))
                .build();
        // When
        productOrderService.updateProductOrderRelatedParties(productOrder);

        // Then
        verify(productOrderCommandProducer, times(1)).publishCommand(productOrder, Command.EventType.RELATED_PARTIES_VALUE_CHANGE_COMMAND);
    }

    @Test
    @DisplayName("Given a product order, " +
            "when create products, " +
            "then return the created products")
    void shouldReturnCreatedProductsWhenCreatingWithValidProductItemsForMigrationUseCase() {
        // Given
        ProductOrder productOrder = createProductOrderForMigration();
        Product product;

        for (ProductOrderItem productOrderItem : productOrder.getProductOrderItem()) {
            product = productOrderItemMapperImpl.mapProductOrderItemToProduct(productOrderItem);
            when(productOrderItemMapper.mapProductOrderItemToProduct(any())).thenReturn(product);

        }
        when(productInventoryService.createProducts(any())).thenReturn(createdProducts());


        Map<String, String> physicalProductOrderItemSerialNumberMap = createPhysicalProductOrderItemSerialNumberMap();

        // When
        productOrderService.createProducts(productOrder, physicalProductOrderItemSerialNumberMap, "migrate", CONTRACT_PRODUCT_ID);

        // Then
        verify(productInventoryService, times(1)).createProducts(any());
        verify(productInventoryService, times(1)).updateProducts(any());

    }

    @Test
    @DisplayName("Given valid ProductOrder and AppointmentRefs, " +
            "when addAppointmentRef is called, " +
            "then it should update ProductOrder with AppointmentRefs")
    void shouldUpdateProductOrderWithAppointmentRefsWhenAddAppointmentRefIsCalled() {
        //Given
        ProductOrder productOrder = createProductOrder();
        String appointmentId = RandomStringUtils.randomAlphabetic(12);
        Map<String, String> orderItemAppointmentRefMap = new HashMap<>();
        orderItemAppointmentRefMap.put(PRODUCT_ORDER_ITEM_ID_1, appointmentId);
        //When
        productOrderService.addAppointmentRef(productOrder, orderItemAppointmentRefMap);
        ArgumentCaptor<ProductOrder> orderCaptor = ArgumentCaptor.forClass(ProductOrder.class);
        verify(productOrderCommandProducer, times(1))
                .publishCommand(orderCaptor.capture(),
                        eq(Command.EventType.APPOINTMENT_VALUE_CHANGE_COMMAND));
        ProductOrder capturedOrder = orderCaptor.getValue();
        AppointmentRef appointmentRef =
                capturedOrder.getProductOrderItem().get(0).getAppointment();
        //Then
        assertNotNull(appointmentRef, "AppointmentRef must be set");
        assertEquals(appointmentId, appointmentRef.getId());
        assertEquals(OrderCaptureConstants.APPOINTMENT_TYPE, appointmentRef.getAtType());
    }

    @Test
    @DisplayName("given ProductOrder and ValidityMap" +
            "when UpdateValidityCharacteristic " +
            "then should update and publish command")
    void shouldUpdateValidityCharacteristicWhenItIsBeforeGivenDate() {
        //Given
        ValidityValue validityValue = new ValidityValue();
        validityValue.setValidTo(Instant.parse("2030-12-31T23:59:59Z"));

        ValidityCharacteristic vc = new ValidityCharacteristic();
        vc.setName("TEST_VALIDITY");
        vc.setValue(validityValue);

        Map<ProductOrderItem, ValidityCharacteristic> validityMap = new HashMap<>();
        ProductOrderItem itemToUpdate = ProductOrderItem.builder()
                .id("item-to-update")
                .product(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                        .productCharacteristic(new ArrayList<>())
                        .build())
                .build();
        validityMap.put(itemToUpdate, vc);
        ProductOrderItem itemUnchanged = ProductOrderItem.builder()
                .id("item-unchanged")
                .product(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                        .productCharacteristic(new ArrayList<>())
                        .build())
                .build();
        ProductOrder productOrder = ProductOrder.builder()
                .id("test-product-order-id")
                .productOrderItem(List.of(itemToUpdate, itemUnchanged))
                .build();
        //When
        productOrderService.updateValidityCharacteristic(productOrder, validityMap);

        //Then
        ArgumentCaptor<ProductOrder> productOrderCaptor = ArgumentCaptor.forClass(ProductOrder.class);
        verify(productOrderCommandProducer, times(1))
                .publishCommand(productOrderCaptor.capture(), eq(Command.EventType.PRODUCT_VALUE_CHANGE_COMMAND));

        ProductOrder capturedOrder = productOrderCaptor.getValue();
        assertEquals("test-product-order-id", capturedOrder.getId(),
                "Should publish updated ProductOrder with the correct ID");
        assertNotNull(capturedOrder.getProductOrderItem(),
                "Captured ProductOrder should have items");

    }

    @Test
    @DisplayName("given ProductOrder with updated requested completion date" +
            "when updateRequestedCompletionDate " +
            "then should update and publish command")
    void shouldUpdateRequestedCompletionDateWhenItIsBeforeGivenDate() {
        //Given
        ValidityValue validityValue = new ValidityValue();
        validityValue.setValidTo(Instant.parse("2030-12-31T23:59:59Z"));

        ProductOrder productOrder = ProductOrder.builder()
                .id("test-product-order-id")
                .requestedCompletionDate(Instant.parse("2030-12-31T23:59:59Z"))
                .build();
        //When
        productOrderService.updateRequestedCompletionDate(productOrder);

        //Then
        ArgumentCaptor<ProductOrder> productOrderCaptor = ArgumentCaptor.forClass(ProductOrder.class);
        verify(productOrderCommandProducer, times(1))
                .publishCommand(productOrderCaptor.capture(), eq(Command.EventType.REQUESTED_COMPLETION_DATE_VALUE_CHANGE_COMMAND));

        ProductOrder capturedOrder = productOrderCaptor.getValue();
        assertEquals("test-product-order-id", capturedOrder.getId(),
                "Should publish updated ProductOrder with the correct ID");
        assertNotNull(capturedOrder.getRequestedCompletionDate());
    }

    private List<ProductOrderItem> createProductOrderItemDTOsForMigration() {
        ProductOfferingRef productOffering1 = createProductOfferingRef(PRODUCT_OFFERING_ID_1, PRODUCT_OFFERING_NAME_1, ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE);
        ProductOfferingRef productOffering2 = createProductOfferingRef(PRODUCT_OFFERING_ID_2, PRODUCT_OFFERING_NAME_2, ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE);
        ProductOfferingRef productOffering3 = createProductOfferingRef(PRODUCT_OFFERING_ID_3, PRODUCT_OFFERING_NAME_3, ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE);


        ProductOfferingRef productOffering5 = createProductOfferingRef(PRODUCT_OFFERING_ID_1, PRODUCT_OFFERING_NAME_1, ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE);
        ProductOfferingRef productOffering6 = createProductOfferingRef(PRODUCT_OFFERING_ID_2, PRODUCT_OFFERING_NAME_2, ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE);
        ProductOfferingRef productOffering7 = createProductOfferingRef(PRODUCT_OFFERING_ID_3, PRODUCT_OFFERING_NAME_3, ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE);


        ProductSpecificationRef productSpecification1 = createProductSpecificationRef();

        OrderItemRelationship bundleRelationship2 = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_3, RelationshipType.BUNDLES);
        OrderItemRelationship childRelationship = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_2, RelationshipType.ISCHILD);

        OrderItemRelationship bundleRelationship1 = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_2, RelationshipType.BUNDLES);

        OrderItemRelationship migrateRelationship = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_5, RelationshipType.MIGRATETO);

        OrderItemRelationship bundleRelationship4 = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_6, RelationshipType.BUNDLES);
        OrderItemRelationship bundleRelationship5 = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_7, RelationshipType.BUNDLES);
        OrderItemRelationship childRelationshipTarget = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_6, RelationshipType.ISCHILD);


        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product1 = createProduct(true, null);
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product2 = createProduct(true, null);
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product3 = createProduct(false, productSpecification1);

        ProductOrderItem productOrderItem1 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_1, productOffering1, product1, List.of(bundleRelationship1, migrateRelationship), ItemActionType.MIGRATE);
        ProductOrderItem productOrderItem2 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_2, productOffering2, product2, List.of(bundleRelationship2), ItemActionType.MIGRATE);
        ProductOrderItem productOrderItem3 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_2, productOffering3, product3, List.of(childRelationship), ItemActionType.MIGRATE);
        OrderItemRelationship migrateFromRelationship = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_1, RelationshipType.MIGRATEFROM);

        ProductOrderItem productOrderItem5 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_5, productOffering5, product1, List.of(bundleRelationship4, migrateFromRelationship), ItemActionType.MIGRATE);
        ProductOrderItem productOrderItem6 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_6, productOffering6, product2, List.of(bundleRelationship5), ItemActionType.MIGRATE);
        ProductOrderItem productOrderItem7 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_7, productOffering7, product3, List.of(childRelationshipTarget), ItemActionType.ADD);


        return List.of(productOrderItem1, productOrderItem2, productOrderItem3, productOrderItem5, productOrderItem6, productOrderItem7);
    }

    private ProductOrder createProductOrderForMigration() {
        List<ProductOrderItem> productOrderItems = createProductOrderItemDTOsForMigration();
        RelatedPartyRefOrPartyRoleRef relatedParty = createRelatedParty();

        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .requestedCompletionDate(Instant.ofEpochMilli(10))
                .productOrderItem(productOrderItems)
                .relatedParty(List.of(relatedParty))
                .build();
    }


    private List<OrderPrice> createOrderTotalPrice() {
        Price price1 = Price.builder()
                .dutyFreeAmount(Money.builder()
                        .value(DUTY_FREE_AMOUNT_VALUE_1)
                        .unit(UNIT)
                        .build())
                .taxIncludedAmount(Money.builder()
                        .value(TAX_INCLUDED_AMOUNT_VALUE_1)
                        .unit(UNIT)
                        .build())
                .build();

        Price price2 = Price.builder()
                .dutyFreeAmount(Money.builder()
                        .value(DUTY_FREE_AMOUNT_VALUE_2)
                        .unit(UNIT)
                        .build())
                .taxIncludedAmount(Money.builder()
                        .value(TAX_INCLUDED_AMOUNT_VALUE_2)
                        .unit(UNIT)
                        .build())
                .build();

        Quantity quantity = Quantity.builder()
                .amount(AMOUNT)
                .units(UNITS)
                .build();

        OrderPrice orderPrice1 = OrderPrice.builder()
                .priceType(RC_PRICE_TYPE)
                .recurringChargePeriod(quantity)
                .price(price1)
                .build();

        OrderPrice orderPrice2 = OrderPrice.builder()
                .priceType(NRC_PRICE_TYPE)
                .price(price2)
                .build();

        return List.of(orderPrice1, orderPrice2);
    }

    private List<ProductOrderItem> createProductOrderItemDTOs() {
        ProductOfferingRef productOffering1 = createProductOfferingRef(PRODUCT_OFFERING_ID_1, PRODUCT_OFFERING_NAME_1, ServiceConstants.CONTRACT_PRODUCT_OFFERING_TYPE);
        ProductOfferingRef productOffering2 = createProductOfferingRef(PRODUCT_OFFERING_ID_2, PRODUCT_OFFERING_NAME_2, ServiceConstants.BUNDLE_PRODUCT_OFFERING_TYPE);
        ProductOfferingRef productOffering3 = createProductOfferingRef(PRODUCT_OFFERING_ID_3, PRODUCT_OFFERING_NAME_3, ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE);
        ProductOfferingRef productOffering4 = createProductOfferingRef(PRODUCT_OFFERING_ID_4, PRODUCT_OFFERING_NAME_4, ServiceConstants.ATOMIC_PRODUCT_OFFERING_TYPE);


        ProductSpecificationRef productSpecification1 = createProductSpecificationRef();

        OrderItemRelationship bundleRelationship1 = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_2, RelationshipType.BUNDLES);
        OrderItemRelationship bundleRelationship2 = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_3, RelationshipType.BUNDLES);
        OrderItemRelationship childRelationship = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_2, RelationshipType.ISCHILD);
        OrderItemRelationship reliesOnRelationship = createOrderItemRelationship(PRODUCT_ORDER_ITEM_ID_2, RelationshipType.RELIESON);

        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product1 = createProduct(true, null);
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product2 = createProduct(true, null);
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product3 = createProduct(false, productSpecification1);

        ProductOrderItem productOrderItem1 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_1, productOffering1, product1, List.of(bundleRelationship1), ItemActionType.ADD);
        ProductOrderItem productOrderItem2 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_2, productOffering2, product2, List.of(bundleRelationship2), ItemActionType.ADD);
        ProductOrderItem productOrderItem3 = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_2, productOffering3, product3, List.of(childRelationship), ItemActionType.ADD);

        ProductSpecificationRef shipmentProductSpecification = createShipmentProductSpecificationRef();
        com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product4 = createProduct(false, shipmentProductSpecification);
        ProductOrderItem shipmentProductOrderItem = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_4, productOffering4, product4, List.of(reliesOnRelationship), ItemActionType.ADD);

        return List.of(productOrderItem1, productOrderItem2, productOrderItem3, shipmentProductOrderItem);
    }

    private ProductSpecificationRef createShipmentProductSpecificationRef() {
        return ProductSpecificationRef.builder()
                .id(PRODUCT_SPECIFICATION_ID_1)
                .name(PRODUCT_SPECIFICATION_NAME_1)
                .atType(PRODUCT_SPECIFICATION_REF)
                .atBaseType(OrderCaptureConstants.SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE)
                .build();
    }

    private ProductOrder createProductOrder() {
        List<ProductOrderItem> productOrderItems = createProductOrderItemDTOs();
        RelatedPartyRefOrPartyRoleRef relatedParty = createRelatedParty();

        return ProductOrder.builder()
                .id(DEFAULT_PRODUCT_ORDER_ID)
                .href(DEFAULT_HREF)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.DRAFT)
                .requestedCompletionDate(Instant.ofEpochMilli(10))
                .productOrderItem(productOrderItems)
                .relatedParty(List.of(relatedParty))
                .build();
    }

    private ProductOfferingRef createProductOfferingRef(String id, String name, String atType) {
        return ProductOfferingRef.builder()
                .id(id)
                .name(name)
                .atType(atType)
                .atReferredType(PRODUCT_OFFERING_REF)
                .build();
    }

    private ProductSpecificationRef createProductSpecificationRef() {
        return ProductSpecificationRef.builder()
                .id(PRODUCT_SPECIFICATION_ID_1)
                .name(PRODUCT_SPECIFICATION_NAME_1)
                .atType(PRODUCT_SPECIFICATION_REF)
                .build();
    }

    private OrderItemRelationship createOrderItemRelationship(String id, RelationshipType relationshipType) {
        return OrderItemRelationship.builder()
                .id(id)
                .relationshipType(relationshipType)
                .build();
    }

    private com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product createProduct(boolean isBundle, ProductSpecificationRef productSpecification) {
        return com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product.builder()
                .id("123")
                .isBundle(isBundle)
                .productSpecification(productSpecification)
                .build();
    }

    private ProductOrderItem createProductOrderItem(String id, ProductOfferingRef productOffering, com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product product, List<OrderItemRelationship> relationships, ItemActionType itemActionType) {
        return ProductOrderItem.builder()
                .id(id)
                .productOffering(productOffering)
                .product(product)
                .action(itemActionType)
                .state(DRAFT)
                .isInstallable(true)
                .productOrderItemRelationship(relationships)
                .build();
    }

    private RelatedPartyRefOrPartyRoleRef createRelatedParty() {
        PartyRef partyRef = PartyRef.builder()
                .id(DEFAULT_RELATED_PARTY_ID)
                .name(DEFAULT_RELATED_PARTY_NAME)
                .atReferredType(DEFAULT_RELATED_PARTY_REFERRED_TYPE)
                .atType(PARTY_REF_TYPE)
                .build();

        return RelatedPartyRefOrPartyRoleRef.builder()
                .role(DEFAULT_RELATED_PARTY_ROLE)
                .partyOrPartyRole(partyRef)
                .build();
    }

    private Product createdProducts() {
        com.orange.discobole.productinventory.dto.v1.ProductRelationship productRelationshipDTO1 =
                com.orange.discobole.productinventory.dto.v1.ProductRelationship
                        .builder()
                        .product(Product.builder()
                                .id(RandomStringUtils.randomAlphabetic(10))
                                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder()
                                        .orderItemId(RandomStringUtils.randomAlphabetic(10))
                                        .orderItemAction(RandomStringUtils.randomAlphabetic(10))
                                        .build()))
                                .atType(PRODUCT_TYPE)
                                .build())
                        .relationshipType(OrderCaptureConstants.BUNDLES)
                        .build();

        return Product.builder()
                .id(RandomStringUtils.randomAlphabetic(10))
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_2)
                        .orderItemAction(RandomStringUtils.randomAlphabetic(10))
                        .build()))
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id(RandomStringUtils.randomAlphabetic(10))
                        .name(RandomStringUtils.randomAlphabetic(10))
                        .atReferredType(RandomStringUtils.randomAlphabetic(10))
                        .build())
                .productSpecification(com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef.builder()
                        .id(RandomStringUtils.randomAlphabetic(10))
                        .name(RandomStringUtils.randomAlphabetic(10))
                        .atType(RandomStringUtils.randomAlphabetic(10))
                        .build())
                .productRelationship(List.of(productRelationshipDTO1))
                .isBundle(Boolean.TRUE)
                .atType(PRODUCT_TYPE)
                .build();
    }

    private ShipmentProduct createdShippingProducts() {

        return ShipmentProduct.builder()
                .id(RandomStringUtils.randomAlphabetic(10))
                .productOrderItem(List.of(com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem.builder()
                        .orderItemId(PRODUCT_ORDER_ITEM_ID_4)
                        .orderItemAction(RandomStringUtils.randomAlphabetic(10))
                        .build()))
                .productOffering(com.orange.discobole.productinventory.dto.v1.ProductOfferingRef.builder()
                        .id(RandomStringUtils.randomAlphabetic(10))
                        .name(RandomStringUtils.randomAlphabetic(10))
                        .atReferredType(RandomStringUtils.randomAlphabetic(10))
                        .build())
                .productSpecification(com.orange.discobole.productinventory.dto.v1.ProductSpecificationRef.builder()
                        .id(RandomStringUtils.randomAlphabetic(10))
                        .name(RandomStringUtils.randomAlphabetic(10))
                        .atType("ShipmentSpecification")
                        .build())
                .isBundle(Boolean.TRUE)
                .atType(OrderCaptureConstants.SHIPMENT_PRODUCT)
                .build();
    }

    private List<String> createRelatedResourcesIDList() {
        return List.of(DEFAULT_RELATED_RESOURCES_ID_1, DEFAULT_RELATED_RESOURCES_ID_2);
    }

    private List<ProductSpecification> createProductSpecifications(String specId1, String specId2) {
        return List.of(
                ProductSpecification.builder()
                        .id(specId1)
                        .build(),
                ProductSpecification.builder()
                        .id(specId2)
                        .build()
        );
    }

    private List<ProductSpecification> createProductSpecificationsWithCharacteristics(String specId1, String specId2) {
        return List.of(
                ProductSpecification.builder()
                        .id(specId1)
                        .productSpecCharacteristic(List.of(
                                ProductSpecCharacteristic.builder()
                                        .name(DEFAULT_RELATED_RESOURCES_ID_1)
                                        .build()
                        ))
                        .build(),
                ProductSpecification.builder()
                        .id(specId2)
                        .productSpecCharacteristic(List.of(
                                ProductSpecCharacteristic.builder()
                                        .name(DEFAULT_RELATED_RESOURCES_ID_2)
                                        .build()
                        ))
                        .build()
        );
    }

    private Map<String, String> createPhysicalProductOrderItemSerialNumberMap() {
        Map<String, String> physicalProductOrderItemSerialNumberMap = new HashMap<>();
        physicalProductOrderItemSerialNumberMap.put(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));
        return physicalProductOrderItemSerialNumberMap;
    }
}