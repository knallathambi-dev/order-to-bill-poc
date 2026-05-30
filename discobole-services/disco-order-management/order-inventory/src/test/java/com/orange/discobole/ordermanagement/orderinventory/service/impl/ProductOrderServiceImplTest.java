// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.impl;

import com.orange.discobole.ordermanagement.orderinventory.IntegrationTest;
import com.orange.discobole.ordermanagement.orderinventory.domain.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.Error;
import com.orange.discobole.ordermanagement.orderinventory.dto.ProductOrderResponse;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.PartyRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.RelatedPartyRefOrPartyRoleRef;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.SortEnum;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import com.orange.discobole.ordermanagement.orderinventory.exception.model.BusinessException;
import com.orange.discobole.ordermanagement.orderinventory.repository.ProductOrderRepository;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.ProductOrderAttributeValueChangeEventProducer;
import com.orange.discobole.ordermanagement.orderinventory.service.kafka.producer.ProductOrderEventProducer;
import com.orange.discobole.ordermanagement.orderinventory.service.mapper.ProductOrderMapper;
import com.orange.discobole.ordermanagement.orderinventory.util.ProductOrderAssertionUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.orderinventory.constant.Constant.LIMIT;
import static com.orange.discobole.ordermanagement.orderinventory.constant.Constant.NONE;
import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;

@IntegrationTest
class ProductOrderServiceImplTest {

    public static final String PRODUCT_ORDER_ITEM_TYPE = "ProductOrderItem";
    public static final String CONTRACT_TYPE = "contract";
    public static final String MOBILE_PACKAGE_1 = "Mobile Package 1";
    public static final String MOBILE_PACKAGE_2 = "Mobile Package 2";
    public static final String MOBILE_LINE = "Mobile line";
    public static final String TIME_BUNDLE = "Time Bundle";
    public static final String CONTRACT_PRODUCT_ORDER_ITEM_ID_1 = "1";
    public static final String CONTRACT_PRODUCT_ORDER_ITEM_ID_2 = "2";
    public static final String BUNDLE_PRODUCT_ORDER_ITEM_ID_1 = "3";
    public static final String BUNDLE_PRODUCT_ORDER_ITEM_ID_2 = "4";
    public static final String ATOMIC_PRODUCT_ORDER_ITEM_ID_1 = "5";
    public static final String ATOMIC_PRODUCT_ORDER_ITEM_ID_2 = "6";
    public static final String ATOMIC_PRODUCT_ORDER_ITEM_ID_3 = "7";
    public static final String BUNDLED_PRODUCT_OFFERING = "BundleProductOffering";
    public static final String ATOMIC_PRODUCT_OFFERING = "AtomicProductOffering";
    public static final String INVALID_ID = "invalidId";
    public static final String DEFAULT_REFERRED_TYPE = "individual";
    public static final String DEFAULT_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    public static final String DEFAULT_PARTY_NAME = DEFAULT_PARTY_ID;
    public static final String BILLING_ACCOUNT_REFERRED_TYPE = "BillingAccount";
    public static final String PRODUCT_REF_HREF_1 = "http://productInventoryUrl/productManagement/v1/product/productRefId1";
    public static final String PRODUCT_REF_HREF_2 = "http://productInventoryUrl/productManagement/v1/product/productRefId2";
    public static final float DUTY_FREE_AMOUNT_VALUE = 10f;
    public static final float TAX_INCLUDED_AMOUNT_VALUE = 10f;
    public static final String UNIT = "Euro";
    public static final String PRICE_TYPE = "NRC";
    public static final String PRODUCT_SPECIFICATION_ID_1 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_SPECIFICATION_ID_2 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ID_1 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ID_2 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ID_3 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ID_4 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ID_5 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ID_6 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_ID_7 = RandomStringUtils.randomAlphabetic(5);
    public static final String PRODUCT_REF_TYPE = "ProductRef";
    public static final String PRODUCT_TYPE = "Product";
    private static final Instant DEFAULT_CANCELLATION_DATE = Instant.ofEpochMilli(0L);
    private static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(5);
    private static final String REALIZING_RESOURCE_ID_1 = RandomStringUtils.randomAlphabetic(5);
    private static final String REALIZING_RESOURCE_ID_2 = RandomStringUtils.randomAlphabetic(5);
    private static final String PAYMENT_ID_1 = RandomStringUtils.randomAlphabetic(5);
    private static final String PAYMENT_ID_2 = RandomStringUtils.randomAlphabetic(5);
    private static final String BILLING_ACCOUNT_ID_1 = RandomStringUtils.randomAlphabetic(5);
    private static final String BILLING_ACCOUNT_ID_2 = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_CANCELLATION_REASON = RandomStringUtils.randomAlphabetic(5);
    private static final Instant DEFAULT_COMPLETION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant DEFAULT_EXPECTED_COMPLETION_DATE = Instant.ofEpochMilli(0L);
    private static final String DEFAULT_CATEGORY = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_DESCRIPTION = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_ORDER_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_ORDER_ID_1 = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_NOTIFICATION_CONTACT = RandomStringUtils.randomAlphabetic(5);
    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final ProductOrderStateType DEFAULT_STATE = ProductOrderStateType.DRAFT;
    private static final Instant DEFAULT_REQUESTED_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant DEFAULT_REQUESTED_COMPLETION_DATE = Instant.ofEpochMilli(0L);
    private static final String DEFAULT_PRIORITY = RandomStringUtils.randomAlphabetic(5);
    private static final String PRODUCT_ORDER_TYPE = "ProductOrder";
    private static final Instant creationDate1 = Instant.parse("2024-02-13T14:02:00Z");
    private static final Instant creationDate2 = creationDate1.plusSeconds(10);
    private static final String PROSPECT = "prospect";
    private static final String CUSTOMER = "customer";
    private static final String APPOINTMENT_REF_ID_1 = RandomStringUtils.randomAlphabetic(6);
    private static final String APPOINTMENT_REF_ID_2 = RandomStringUtils.randomAlphabetic(6);
    private static final String APPOINTMENT_REF = "AppointmentRef";
    private static final String PRODUCT_SPECIFICATION_REF = "ProductSpecificationRef";

    @Autowired
    private ProductOrderRepository productOrderRepository;
    @MockBean
    private ProductOrderEventProducer productOrderEventProducer;
    @MockBean
    private ProductOrderAttributeValueChangeEventProducer productOrderAttributeValueChangeEventProducer;
    @Autowired
    private ProductOrderMapper productOrderMapper;
    @Autowired

    private ProductOrderService productOrderService;
    private ProductOrderEntity.ProductOrderEntityBuilder productOrderEntityBuilder;

    @BeforeEach
    void initTest() {
        productOrderRepository.deleteAll();
        productOrderEntityBuilder = createEntity();
    }

    @DisplayName("Given a product order with empty items, " +
            "when attempting to save the product order, " +
            "then a ProductOrderInventoryException should be thrown")
    @Test
    void shouldThrowExceptionWhenSavingProductOrderWithEmptyItems() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.productOrderItem(null).build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);

        // When & Then
        assertThrows(ProductOrderInventoryException.class, () -> productOrderService.saveProductOrder(productOrder));
    }

    @DisplayName("Given a product order with a non-null cancellation date, " +
            "when attempting to save the product order, " +
            "then a ProductOrderInventoryException should be thrown")
    @Test
    void shouldThrowExceptionWhenSavingProductOrderWithNotNullCancellationDate() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.cancellationDate(DEFAULT_CANCELLATION_DATE).build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);

        // When & Then
        assertThrows(ProductOrderInventoryException.class, () -> productOrderService.saveProductOrder(productOrder));
    }

    @DisplayName("Given a product order with a non-null cancellation reason, " +
            "when attempting to save the product order, " +
            "then a ProductOrderInventoryException should be thrown")
    @Test
    void shouldThrowExceptionWhenSavingProductOrderWithNotNullCancellationReason() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.cancellationReason(DEFAULT_CANCELLATION_REASON).build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);

        // When & Then
        assertThrows(ProductOrderInventoryException.class, () -> productOrderService.saveProductOrder(productOrder));
    }

    @DisplayName("Given a product order with null creation date, " +
            "when saving the product order, " +
            "then the product order should be persisted with creation date is set")
    @Test
    void shouldSaveProductOrderWithNullCreationDate() {
        // Given
        int databaseSizeBeforeCreate = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        productOrder.setCreationDate(null);

        // When
        productOrderService.saveProductOrder(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(databaseSizeBeforeCreate + 1);
        ProductOrderEntity testProductOrder = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(testProductOrder.getCancellationDate()).isNull();
        assertThat(testProductOrder.getHref()).isEqualTo(DEFAULT_HREF);
        assertThat(testProductOrder.getCancellationReason()).isNull();
        assertThat(testProductOrder.getCompletionDate()).isEqualTo(DEFAULT_COMPLETION_DATE);
        assertThat(testProductOrder.getExpectedCompletionDate()).isEqualTo(DEFAULT_EXPECTED_COMPLETION_DATE);
        assertThat(testProductOrder.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testProductOrder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductOrder.getNotificationContact()).isEqualTo(DEFAULT_NOTIFICATION_CONTACT);
        assertThat(testProductOrder.getCreationDate()).isNotNull();
        assertThat(testProductOrder.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testProductOrder.getRequestedStartDate()).isEqualTo(DEFAULT_REQUESTED_START_DATE);
        assertThat(testProductOrder.getRequestedCompletionDate()).isEqualTo(DEFAULT_REQUESTED_COMPLETION_DATE);
        assertThat(testProductOrder.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testProductOrder.getAtType()).isEqualTo(PRODUCT_ORDER_TYPE);
        assertThat(testProductOrder.getProductOrderItem()).isNotEmpty();
    }

    @DisplayName("Given a product order with null order state, " +
            "when saving the product order, " +
            "then the product order should be persisted with the state is DRAFT")
    @Test
    void shouldSaveProductOrderWithNullOrderState() {
        // Given
        int databaseSizeBeforeCreate = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        productOrder.setState(null);
        // When
        productOrderService.saveProductOrder(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();

        assertThat(productOrderEntities).hasSize(databaseSizeBeforeCreate + 1);
        ProductOrderEntity testProductOrder = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(testProductOrder.getCancellationDate()).isNull();
        assertThat(testProductOrder.getHref()).isEqualTo(DEFAULT_HREF);
        assertThat(testProductOrder.getCancellationReason()).isNull();
        assertThat(testProductOrder.getCompletionDate()).isEqualTo(DEFAULT_COMPLETION_DATE);
        assertThat(testProductOrder.getExpectedCompletionDate()).isEqualTo(DEFAULT_EXPECTED_COMPLETION_DATE);
        assertThat(testProductOrder.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testProductOrder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductOrder.getNotificationContact()).isEqualTo(DEFAULT_NOTIFICATION_CONTACT);
        assertThat(testProductOrder.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testProductOrder.getState()).isEqualTo(ProductOrderStateType.DRAFT);
        assertThat(testProductOrder.getRequestedStartDate()).isEqualTo(DEFAULT_REQUESTED_START_DATE);
        assertThat(testProductOrder.getRequestedCompletionDate()).isEqualTo(DEFAULT_REQUESTED_COMPLETION_DATE);
        assertThat(testProductOrder.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testProductOrder.getAtType()).isEqualTo(PRODUCT_ORDER_TYPE);
        assertThat(testProductOrder.getProductOrderItem()).isNotEmpty();
    }

    @DisplayName("Given a product order with default values, " +
            "when saving the product order, " +
            "then the product order should be persisted with the expected values")
    @Test
    void shouldSaveProductOrderWithDefaultValues() {
        // Given
        int databaseSizeBeforeCreate = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);

        // When
        productOrderService.saveProductOrder(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();

        assertThat(productOrderEntities).hasSize(databaseSizeBeforeCreate + 1);
        ProductOrderEntity testProductOrder = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(testProductOrder.getCancellationDate()).isNull();
        assertThat(testProductOrder.getHref()).isEqualTo(DEFAULT_HREF);
        assertThat(testProductOrder.getCancellationReason()).isNull();
        assertThat(testProductOrder.getCompletionDate()).isEqualTo(DEFAULT_COMPLETION_DATE);
        assertThat(testProductOrder.getExpectedCompletionDate()).isEqualTo(DEFAULT_EXPECTED_COMPLETION_DATE);
        assertThat(testProductOrder.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testProductOrder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductOrder.getNotificationContact()).isEqualTo(DEFAULT_NOTIFICATION_CONTACT);
        assertThat(testProductOrder.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testProductOrder.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testProductOrder.getRequestedStartDate()).isEqualTo(DEFAULT_REQUESTED_START_DATE);
        assertThat(testProductOrder.getRequestedCompletionDate()).isEqualTo(DEFAULT_REQUESTED_COMPLETION_DATE);
        assertThat(testProductOrder.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testProductOrder.getAtType()).isEqualTo(PRODUCT_ORDER_TYPE);
        assertThat(testProductOrder.getProductOrderItem()).isNotEmpty();
    }

    @DisplayName("Given a product order with a specific ID, " +
            "when updating the product order with resource data, " +
            "then the product order should be updated with the new realizing resource(s)")
    @Test
    void shouldUpdateProductOrderWithRealizingResourceData() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrderEntity);

        ProductOrderEntity partialProductOrderEntity = new ProductOrderEntity();
        partialProductOrderEntity.setId(productOrderEntity.getId());

        List<ProductOrderItemEntity> productOrderItemEntities = createProductOrderItemsWithResources();
        partialProductOrderEntity.setProductOrderItem(productOrderItemEntities);

        ProductOrder productOrder = productOrderMapper.mapToDto(partialProductOrderEntity);

        // When
        int databaseSizeBeforeUpdate = productOrderRepository.findAll().size();
        productOrderService.updateProductOrderRealizingResource(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(databaseSizeBeforeUpdate);
        ProductOrderEntity testProductOrder = productOrderEntities.get(productOrderEntities.size() - 1);

        List<String> realizingResourceRef4 = findResourceRefsIdById(testProductOrder.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1);
        List<String> realizingResourceRef6 = findResourceRefsIdById(testProductOrder.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_2);

        assertTrue(realizingResourceRef4.contains(REALIZING_RESOURCE_ID_1));
        assertTrue(realizingResourceRef6.contains(REALIZING_RESOURCE_ID_2));
    }

    @DisplayName("Given a product order with a specific ID, " +
            "when updating the product order with payment data, " +
            "then the product order should be updated with the new payment references")
    @Test
    void shouldUpdateProductOrderWithPaymentData() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrderEntity);

        // Update the productOrder using partial update
        ProductOrderEntity partialProductOrderEntity = new ProductOrderEntity();
        partialProductOrderEntity.setId(productOrderEntity.getId());

        List<ProductOrderItemEntity> updatedProductOrderItems = createProductOrderItemsWithPayment();
        partialProductOrderEntity.setProductOrderItem(updatedProductOrderItems);

        ProductOrder productOrder = productOrderMapper.mapToDto(partialProductOrderEntity);

        // When
        int databaseSizeBeforeUpdate = productOrderRepository.findAll().size();
        productOrderService.updateProductOrderPayment(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(databaseSizeBeforeUpdate);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        List<String> payments3 = findPaymentIdsById(updatedProductOrderEntity.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1);
        List<String> payments5 = findPaymentIdsById(updatedProductOrderEntity.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_2);

        assertTrue(payments3.contains(PAYMENT_ID_1));
        assertTrue(payments5.contains(PAYMENT_ID_2));
    }

    @DisplayName("Given a product order with a specific ID, " +
            "when updating the product order with billing account data, " +
            "then the billing account references should be set correctly")
    @Test
    void shouldSetBillingAccountRefsCorrectlyWhenProductOrderIsUpdated() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrderEntity);

        // Create partial update
        ProductOrderEntity partialProductOrderEntity = new ProductOrderEntity();
        partialProductOrderEntity.setId(productOrderEntity.getId());

        List<ProductOrderItemEntity> productOrderItemEntities = createProductOrderItemsWithBillingAccount();
        partialProductOrderEntity.setProductOrderItem(productOrderItemEntities);

        ProductOrder productOrder = productOrderMapper.mapToDto(partialProductOrderEntity);

        // When
        int initialDatabaseSize = productOrderRepository.findAll().size();
        productOrderService.updateProductOrderBillingAccount(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize);

        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        String billingAccountRef1 = findBillingAccountRefIdById(updatedProductOrderEntity.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1);
        String billingAccountRef2 = findBillingAccountRefIdById(updatedProductOrderEntity.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_2);

        assertEquals(BILLING_ACCOUNT_ID_1, billingAccountRef1);
        assertEquals(BILLING_ACCOUNT_ID_2, billingAccountRef2);
    }

    @DisplayName("Given a product order with a specific ID, " +
            "when updating the product order with product reference data, " +
            "then the product references should be set correctly")
    @Test
    void shouldSetProductRefsCorrectlyWhenProductOrderIsUpdated() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrderEntity);

        // Create partial update
        ProductOrderEntity partialProductOrderEntity = new ProductOrderEntity();
        partialProductOrderEntity.setId(productOrderEntity.getId());

        List<ProductOrderItemEntity> productOrderItemEntities = createProductOrderItemsWithProductRef();
        partialProductOrderEntity.setProductOrderItem(productOrderItemEntities);

        ProductOrder productOrderPartialUpdate = productOrderMapper.mapToDto(partialProductOrderEntity);

        // When
        int initialDatabaseSize = productOrderRepository.findAll().size();
        productOrderService.updateProductOrderProduct(productOrderPartialUpdate);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize);

        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        String productId1 = findProductIdById(updatedProductOrderEntity.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1);
        String productId2 = findProductIdById(updatedProductOrderEntity.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_2);

        assertEquals(PRODUCT_ID_1, productId1);
        assertEquals(PRODUCT_ID_2, productId2);
    }

    @DisplayName("Given a product order with a specific ID, " +
            "when updating the product order items and total price, " +
            "then the product order should have the updated items and total price correctly set")
    @Test
    void shouldUpdateProductOrderItemsAndTotalPriceCorrectly() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrderEntity);

        ProductOrderEntity partialProductOrderEntity = new ProductOrderEntity();
        partialProductOrderEntity.setId(productOrderEntity.getId());

        List<ProductOrderItemEntity> productOrderItemEntities = createProductOrderItems(ProductOrderItemStateType.ACCEPTED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.ACCEPTED);
        List<OrderPriceEntity> orderPrice = createOrderPrice();

        partialProductOrderEntity.setProductOrderItem(productOrderItemEntities);
        partialProductOrderEntity.setOrderTotalPrice(orderPrice);

        ProductOrder productOrder = productOrderMapper.mapToDto(partialProductOrderEntity);

        // When
        int initialDatabaseSize = productOrderRepository.findAll().size();
        productOrderService.updateOrderItemsAndOrderTotalPrice(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        List<String> expectedIdList = productOrderItemEntities.stream().map(ProductOrderItemEntity::getId).toList();
        List<String> actualIdList = updatedProductOrderEntity.getProductOrderItem().stream().map(ProductOrderItemEntity::getId).toList();
        assertEquals(expectedIdList, actualIdList);

        List<OrderPriceEntity> actualPriceList = updatedProductOrderEntity.getOrderTotalPrice();
        assertNotNull(actualPriceList);
    }

    @DisplayName("Given a product order with updated related parties, " +
            "when updating the product order related parties, " +
            "then the product order should have the updated related parties correctly set")
    @Test
    void shouldUpdateProductOrderRelatedPartiesCorrectly() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);

        // When
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        PartyRef partyRef = PartyRef.builder()
                .id(DEFAULT_PARTY_ID)
                .name(DEFAULT_PARTY_NAME)
                .atReferredType(DEFAULT_REFERRED_TYPE)
                .build();

        List<RelatedPartyRefOrPartyRoleRef> relatedParties = Collections.singletonList(RelatedPartyRefOrPartyRoleRef.builder()
                .role(CUSTOMER)
                .partyOrPartyRole(partyRef)
                .build());

        ProductOrder updatedProductOrder = ProductOrder.builder()
                .id(savedProductOrder.getId())
                .relatedParty(relatedParties)
                .build();

        productOrderService.updateProductOrderRelatedParties(updatedProductOrder);

        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        // Then
        assertThat(updatedProductOrderEntity.getRelatedParty().get(0).getRole()).isEqualTo(CUSTOMER);
    }

    @DisplayName("Given a null product order ID, " +
            "when attempting to update the product order hierarchy, " +
            "then the product order should not be updated and no events should be published")
    @Test
    void shouldNotUpdateProductOrderHierarchyOrPublishEventsWhenIdIsNull() {
        // Given
        int initialDatabaseSize = productOrderRepository.findAll().size();

        // When
        productOrderService.updateProductOrderHierarchy(null, ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.INPROGRESS);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize);
        Mockito.verify(productOrderEventProducer, Mockito.times(0)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(0)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with a valid state and an invalid ID, " +
            "when attempting to update the product order hierarchy, " +
            "then the product order should not be updated and no events should be published")
    @Test
    void shouldNotUpdateProductOrderHierarchyOrPublishEventsWhenIdIsInvalid() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.state(ProductOrderStateType.ACCEPTED).build();
        ProductOrder productOrderToUpdate = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        productOrderService.saveProductOrder(productOrderToUpdate);

        // When
        productOrderService.updateProductOrderHierarchy(INVALID_ID, ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.INPROGRESS);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.ACCEPTED);
        Mockito.verify(productOrderEventProducer, Mockito.times(0)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(0)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and valid product order items, " +
            "when updating the product order hierarchy to INPROGRESS, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyAndPublishEventsWhenStateIsInProgress() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.ACCEPTED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.ACCEPTED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.ACCEPTED)
                .build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateProductOrderHierarchy(savedProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.INPROGRESS);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.INPROGRESS);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and valid product order items, " +
            "when updating the product order hierarchy to HELD, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyAndPublishEventsWhenStateIsHeld() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.ACCEPTED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.ACCEPTED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.ACCEPTED)
                .build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(savedProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.HELD);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.HELD);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and product order items in FAILED state, " +
            "when updating the product order hierarchy to FAILED, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyAndPublishEventsWhenStateIsFailed() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.FAILED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.FAILED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.INPROGRESS)
                .build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder createdProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(createdProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.FAILED);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.FAILED);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and product order items in PARTIAL state, " +
            "when updating the product order hierarchy to INPROGRESS, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyToInProgressWhenStateIsPartial() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.ACCEPTED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.ACCEPTED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.ACCEPTED)
                .build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder createdProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(createdProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.PARTIAL);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.INPROGRESS);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and product order items in FAILED state, " +
            "when updating the product order hierarchy to PARTIAL, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyToPartialStateWhenInitialStateIsFailed() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.FAILED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.FAILED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.ACCEPTED)
                .build();

        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder createdProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(createdProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.PARTIAL);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.PARTIAL);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and product order items in FAILED state and ACCEPTED state, " +
            "when updating the product order hierarchy to INPROGRESS, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyToInProgressStateWhenInitialStateIsFailedAndAcceptedAndUpdatedStateCompleted() {
        // Given
        List<ProductOrderItemEntity> productOrderItemEntities = createProductOrderItems(ProductOrderItemStateType.FAILED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.ACCEPTED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItemEntities)
                .state(ProductOrderStateType.ACCEPTED)
                .build();

        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder createdProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(createdProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.COMPLETED);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.INPROGRESS);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and product order items in FAILED state and PARTIAL, " +
            "when updating the product order hierarchy to PARTIAL, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyToPartialStateWhenInitialStateIsFailedAndPartialAndUpdatedStateCompleted() {
        // Given
        List<ProductOrderItemEntity> productOrderItemEntities = createProductOrderItems(ProductOrderItemStateType.FAILED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.PARTIAL);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItemEntities)
                .state(ProductOrderStateType.ACCEPTED)
                .build();

        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder createdProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(createdProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.COMPLETED);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.PARTIAL);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and product order items in Partial state, " +
            "when updating the product order hierarchy to PARTIAL, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyToPartialStateWhenInitialStateIsPartialAndUpdatedStatePartial() {
        // Given
        List<ProductOrderItemEntity> productOrderItemEntities = createProductOrderItems(ProductOrderItemStateType.PARTIAL, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.PARTIAL);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItemEntities)
                .state(ProductOrderStateType.ACCEPTED)
                .build();

        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(savedProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.PARTIAL);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.PARTIAL);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and product order items in COMPLETED state, " +
            "when updating the product order hierarchy to COMPLETED, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void shouldUpdateProductOrderHierarchyToCompletedStateWhenInitialStateIsCompleted() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.COMPLETED, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.COMPLETED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.ACCEPTED)
                .build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder createdProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(createdProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.COMPLETED);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.COMPLETED);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with items in COMPLETED state and non-installable products, " +
            "when updating the product order hierarchy to COMPLETED state, " +
            "then the product order state should be updated and the relevant events should be published")
    @Test
    void testUpdateProductOrderHierarchyWithCompletedStateInCaseOfNonInstallableProduct() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.COMPLETED, Boolean.FALSE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.COMPLETED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.ACCEPTED)
                .build();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        int databaseSizeBeforeCreate = productOrderRepository.findAll().size();
        ProductOrder createdProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateProductOrderHierarchy(createdProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.COMPLETED);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(databaseSizeBeforeCreate + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.COMPLETED);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(3)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with an ACCEPTED state and valid product order items, " +
            "when updating the product order hierarchy to HELD, " +
            "then the old state state should be the same updated state after the update processing")
    @Test
    void shouldUpdateProductOrderHierarchyAndHaveTheSameState() {
        // Given
        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.HELD, Boolean.TRUE, ProductOrderItemStateType.HELD, ProductOrderItemStateType.ACCEPTED);
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder
                .productOrderItem(productOrderItems)
                .state(ProductOrderStateType.ACCEPTED)
                .build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        productOrderService.updateProductOrderHierarchy(savedProductOrder.getId(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1, ProductOrderItemStateType.HELD);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.HELD);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
        Mockito.verify(productOrderAttributeValueChangeEventProducer, Mockito.times(1)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order is saved with all fields defined, " +
            "when retrieving the product order by ID with no specific fields requested, " +
            "then the retrieved product order should match the saved product order")
    @Test
    void shouldReturnProductOrderWhenNoFieldsAreDefinedInGetById() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);
        String productOrderId = expectedProductOrder.getId();

        // When
        ProductOrder actualProductOrder = productOrderService.getProductOrderById(productOrderId, null);

        // Then
        ProductOrderAssertionUtil.assertProductEqualsToProduct(expectedProductOrder, actualProductOrder);
    }

    @DisplayName("Given a product order is saved with all fields defined, " +
            "when retrieving the product order by ID with specific fields requested (state only), " +
            "then the retrieved product order should contain only the specified fields with other fields being null or empty")
    @Test
    void shouldReturnProductOrderWithSpecificFieldsWhenRequested() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);
        String productOrderId = expectedProductOrder.getId();

        // When
        ProductOrder actualProductOrder = productOrderService.getProductOrderById(productOrderId, "state");

        // Then
        assertThat(actualProductOrder.getId()).isEqualTo(productOrderId);
        assertThat(actualProductOrder.getHref()).isNotBlank();
        assertNotNull(actualProductOrder.getState());
        assertNull(actualProductOrder.getProductOrderItem());
        assertNull(actualProductOrder.getCancellationDate());
        assertNull(actualProductOrder.getCancellationReason());
        assertNull(actualProductOrder.getCategory());
        assertNull(actualProductOrder.getDescription());
        assertNull(actualProductOrder.getNotificationContact());
        assertNull(actualProductOrder.getCreationDate());
        assertNull(actualProductOrder.getRequestedStartDate());
        assertNull(actualProductOrder.getRequestedCompletionDate());
        assertNull(actualProductOrder.getPriority());
        assertNotNull(actualProductOrder.getAtType());
    }

    @DisplayName("Given a product order is saved with all fields defined, " +
            "when retrieving the product order by ID with specific fields requested (orderTotalPrice,productOrderItem.itemPrice,productOrderItem.product), " +
            "then the retrieved product order should contain only the specified fields with other fields being null or empty")
    @Test
    void shouldReturnProductOrderWithSpecificFieldsWhenRequestedWithSpecificFields() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);
        String productOrderId = expectedProductOrder.getId();

        // When
        ProductOrder actualProductOrder = productOrderService.getProductOrderById(productOrderId, "orderTotalPrice,productOrderItem.itemPrice,productOrderItem.product");

        // Then
        assertThat(actualProductOrder.getId()).isEqualTo(productOrderId);
        assertThat(actualProductOrder.getHref()).isNotBlank();
        assertNull(actualProductOrder.getState());
        assertFalse(actualProductOrder.getProductOrderItem().isEmpty());
        assertFalse(actualProductOrder.getProductOrderItem().get(0).getItemPrice().isEmpty());
        assertNotNull(actualProductOrder.getProductOrderItem().get(0).getProduct());
        assertFalse(actualProductOrder.getOrderTotalPrice().isEmpty());
        assertNull(actualProductOrder.getCancellationDate());
        assertNull(actualProductOrder.getCancellationReason());
        assertNull(actualProductOrder.getCategory());
        assertNull(actualProductOrder.getDescription());
        assertNull(actualProductOrder.getNotificationContact());
        assertNull(actualProductOrder.getCreationDate());
        assertNull(actualProductOrder.getRequestedStartDate());
        assertNull(actualProductOrder.getRequestedCompletionDate());
        assertNull(actualProductOrder.getPriority());
        assertNotNull(actualProductOrder.getAtType());
    }

    @DisplayName("Given a product order is saved with all fields defined, " +
            "when retrieving the product order by ID with an invalid field request, " +
            "then a ProductOrderInventoryException should be thrown")
    @Test
    void shouldThrowExceptionWhenRequestingProductOrderWithInvalidFields() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);
        String productOrderId = expectedProductOrder.getId();

        // When & Then
        assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrderById(productOrderId, "state,test"));
    }

    @DisplayName("Given a product order is saved with all fields defined, " +
            "when retrieving the product order by ID with 'NONE' field request, " +
            "then only the ID and HREF should be present, and all other fields should be null or empty")
    @Test
    void shouldReturnProductOrderWithOnlyIdAndHrefWhenFieldsNoneIsRequested() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);
        String productOrderId = expectedProductOrder.getId();

        // When
        ProductOrder actualProductOrder = productOrderService.getProductOrderById(productOrderId, NONE);

        // Then
        assertThat(actualProductOrder.getId()).isEqualTo(productOrderId);
        assertThat(actualProductOrder.getHref()).isNotBlank();
        assertNull(actualProductOrder.getState());
        assertNull(actualProductOrder.getProductOrderItem());
        assertNull(actualProductOrder.getCancellationDate());
        assertNull(actualProductOrder.getCancellationReason());
        assertNull(actualProductOrder.getCategory());
        assertNull(actualProductOrder.getDescription());
        assertNull(actualProductOrder.getNotificationContact());
        assertNull(actualProductOrder.getCreationDate());
        assertNull(actualProductOrder.getRequestedStartDate());
        assertNull(actualProductOrder.getRequestedCompletionDate());
        assertNull(actualProductOrder.getPriority());
        assertNull(actualProductOrder.getAtType());
    }

    @DisplayName("Given a product order is saved, " +
            "when retrieving a product order by an invalid ID, " +
            "then a ProductOrderInventoryException should be thrown")
    @Test
    void shouldThrowExceptionWhenRetrievingProductOrderWithInvalidId() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        // When & Then
        assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrderById(INVALID_ID, null)
        );
    }

    @DisplayName("Given an empty database, " +
            "when retrieving product orders with no fields defined, " +
            "then an empty list is returned")
    @Test
    void shouldThrowExceptionWhenGettingProductsFromEmptyDatabaseWithNoFieldsDefined() {
        // Given
        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(
                orderResponse.getProductOrders(),
                Collections.emptyList()
        );
    }

    @DisplayName("Given a non-empty database, " +
            "when retrieving product orders with no fields defined, " +
            "then the list of product orders should match the saved orders")
    @Test
    void shouldRetrieveProductsFromNonEmptyDatabaseWithNoFieldsDefined() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(
                orderResponse.getProductOrders(),
                List.of(expectedProductOrder)
        );
    }

    @DisplayName("Given a negative limit parameter, " +
            "when calling the getProductOrders method, " +
            "then it should throw a ProductOrderInventoryException with the correct error details")
    @Test
    void shouldThrowExceptionForNegativeLimit() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("limit", "-10");

        Error expectedError = new Error(
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                BusinessException.LIMIT_SHOULD_NOT_BE_NEGATIVE,
                HttpStatus.BAD_REQUEST
        );

        // When
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));

        // Then
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given a negative offset parameter and a positive limit, " +
            "when calling the getProductOrders method, " +
            "then it should throw a ProductOrderInventoryException with the correct error details")
    @Test
    void shouldThrowExceptionForNegativeOffsetWithPositiveLimit() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("offset", "-10");
        queryParametersMap.set("limit", "10");

        Error expectedError = new Error(
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                BusinessException.OFFSET_SHOULD_NOT_BE_NEGATIVE,
                HttpStatus.BAD_REQUEST
        );

        // When
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));

        // Then
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given both offset and limit parameters are negative, " +
            "when calling the getProductOrders method, " +
            "then it should throw a ProductOrderInventoryException with the correct error details")
    @Test
    void shouldThrowExceptionForNegativeOffsetAndLimit() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("offset", "-10");
        queryParametersMap.set("limit", "-10");

        Error expectedError = new Error(
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                BusinessException.OFFSET_AND_LIMIT_SHOULD_NOT_BE_NEGATIVE,
                HttpStatus.BAD_REQUEST
        );

        // When
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));

        // Then
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given the limit parameter is not a valid integer, " +
            "when calling the getProductOrders method, " +
            "then it should throw a ProductOrderInventoryException with the correct error details")
    @Test
    void shouldThrowExceptionForInvalidLimitType() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("limit", "invalid");

        Error expectedError = new Error(
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                format(BusinessException.VALUE_IS_NOT_A_VALID_INTEGER, LIMIT),
                HttpStatus.BAD_REQUEST
        );

        // When
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));

        // Then
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given an offset greater than the total count of products, " +
            "when calling the getProductOrders method, " +
            "then it should throw a ProductOrderInventoryException with the correct error details")
    @Test
    void shouldThrowExceptionForOffsetGreaterThanTotalCount() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("offset", "10");

        Error expectedError = new Error(
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                BusinessException.ERROR_IN_OFFSET,
                HttpStatus.BAD_REQUEST
        );

        // When
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));

        // Then
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given an offset parameter equal to the total count of products, " +
            "when calling the getProductOrders method, " +
            "then it should return an empty list with HTTP status PARTIAL_CONTENT")
    @Test
    void shouldReturnEmptyListForOffsetEqualToTotalCount() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("offset", "1");

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        assertThat(orderResponse.getHttpStatus()).isEqualTo(HttpStatus.PARTIAL_CONTENT);
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(orderResponse.getProductOrders(), Collections.emptyList());
    }

    @DisplayName("Given a non-empty database with valid offset and limit, " +
            "when getProductOrders is called, " +
            "then it should return the correct subset of products")
    @Test
    void shouldReturnProductsForValidOffsetAndLimit() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("offset", "0");
        queryParametersMap.set("limit", "1");
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        assertThat(orderResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(orderResponse.getProductOrders(), List.of(expectedProductOrder));
    }

    @DisplayName("Given a non-empty database with fields containing spaces, " +
            "when getProductOrders is called, " +
            "then it should throw a ProductOrderInventoryException with the appropriate error")
    @Test
    void shouldThrowExceptionForFieldsContainingSpaces() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("fields", "state, creationDate ");

        Error expectedError = new Error(INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                BusinessException.SUPPLEMENT_SPACES_CANNOT_BE_INCLUDED_ON_FIELDS,
                HttpStatus.BAD_REQUEST
        );

        // When
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));

        // Then
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given a non-empty database with specific fields requested, " +
            "when getProductOrders is called, " +
            "then it should return a product order with only the specified fields populated")
    @Test
    void shouldReturnProductOrderWithSpecificFields() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("fields", "state,creationDate");

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);
        ProductOrder actualProductOrder = orderResponse.getProductOrders().get(0);

        // Then
        assertThat(actualProductOrder.getId()).isEqualTo(expectedProductOrder.getId());
        assertThat(actualProductOrder.getHref()).isNotBlank();
        assertNotNull(actualProductOrder.getState());
        assertNotNull(actualProductOrder.getCreationDate());
        assertNull(actualProductOrder.getProductOrderItem());
        assertNull(actualProductOrder.getCancellationDate());
        assertNull(actualProductOrder.getCancellationReason());
        assertNull(actualProductOrder.getCategory());
        assertNull(actualProductOrder.getDescription());
        assertNull(actualProductOrder.getNotificationContact());
        assertNull(actualProductOrder.getRequestedStartDate());
        assertNull(actualProductOrder.getRequestedCompletionDate());
        assertNull(actualProductOrder.getPriority());
        assertNotNull(actualProductOrder.getAtType());
    }

    @DisplayName("Given a non-empty database with an invalid field requested, " +
            "when getProductOrders is called, " +
            "then it should throw an exception indicating the field is not included in product order fields")
    @Test
    void shouldThrowExceptionForInvalidField() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("creationDate", DEFAULT_CREATION_DATE);
        queryParametersMap.set("statee", "draft");

        Error expectedError = new Error(
                INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                format(BusinessException.NOT_INCLUDED_IN_PRODUCT_ORDER_FIELDS, "statee"),
                HttpStatus.BAD_REQUEST
        );

        // When
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));

        // Then
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given a non-empty database with filter and fields specified, " +
            "when getProductOrders is called, " +
            "then it should return product orders with the filtered fields and the specified fields")
    @Test
    void shouldReturnFilteredProductOrdersWithSpecifiedFields() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.build();
        ProductOrder productOrderToSave = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder expectedProductOrder = productOrderService.saveProductOrder(productOrderToSave);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("state", "draft");
        queryParametersMap.set("fields", "state,creationDate");

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);
        ProductOrder actualProductOrder = orderResponse.getProductOrders().get(0);

        // Then
        assertThat(actualProductOrder.getId()).isEqualTo(expectedProductOrder.getId());
        assertThat(actualProductOrder.getHref()).isNotBlank();
        assertNotNull(actualProductOrder.getState());
        assertNotNull(actualProductOrder.getCreationDate());
        assertNull(actualProductOrder.getProductOrderItem());
        assertNull(actualProductOrder.getCancellationDate());
        assertNull(actualProductOrder.getCancellationReason());
        assertNull(actualProductOrder.getCategory());
        assertNull(actualProductOrder.getDescription());
        assertNull(actualProductOrder.getNotificationContact());
        assertNull(actualProductOrder.getRequestedStartDate());
        assertNull(actualProductOrder.getRequestedCompletionDate());
        assertNull(actualProductOrder.getPriority());
        assertNotNull(actualProductOrder.getAtType());
    }

    @DisplayName("Given a non-empty database with sorting by creation date in ascending order, " +
            "when getProductOrders is called, " +
            "then it should return orders sorted by creation date in ascending order")
    @Test
    void shouldReturnOrdersSortedByCreationDateAsc() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).creationDate(creationDate1).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).creationDate(creationDate2).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.addAll("sort", List.of(SortEnum.CREATIONDATE));

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);
        ProductOrder productOrder1 = orderResponse.getProductOrders().get(0);
        ProductOrder productOrder2 = orderResponse.getProductOrders().get(1);

        // Then
        assertThat(productOrder1.getCreationDate()).isBefore(productOrder2.getCreationDate());
    }

    @DisplayName("Given a non-empty database with sorting by creation date in descending order, " +
            "when getProductOrders is called, " +
            "then it should return orders sorted by creation date in descending order")
    @Test
    void shouldReturnOrdersSortedByCreationDateDesc() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).creationDate(creationDate1).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).creationDate(creationDate2).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.addAll("sort", List.of(SortEnum._CREATIONDATE));

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);
        ProductOrder productOrder1 = orderResponse.getProductOrders().get(0);
        ProductOrder productOrder2 = orderResponse.getProductOrders().get(1);

        // Then
        assertThat(productOrder1.getCreationDate()).isAfter(productOrder2.getCreationDate());
    }

    @DisplayName("Given a non-empty database with sorting by state in ascending order, " +
            "when getProductOrders is called, " +
            "then it should return orders sorted by state in ascending order")
    @Test
    void shouldReturnOrdersSortedByStateAsc() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).state(ProductOrderStateType.INPROGRESS).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).state(ProductOrderStateType.ACCEPTED).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.addAll("sort", List.of(SortEnum.STATE));

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);
        ProductOrder productOrder1 = orderResponse.getProductOrders().get(0);
        ProductOrder productOrder2 = orderResponse.getProductOrders().get(1);

        // Then
        assertThat(productOrder1.getState()).isGreaterThan(productOrder2.getState());
    }

    @DisplayName("Given a non-empty database with sorting by state in descending order, " +
            "when getProductOrders is called, " +
            "then it should return orders sorted by state in descending order")
    @Test
    void shouldReturnOrdersSortedByStateDesc() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).state(ProductOrderStateType.INPROGRESS).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).state(ProductOrderStateType.ACCEPTED).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.addAll("sort", List.of(SortEnum._STATE));

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);
        ProductOrder productOrder1 = orderResponse.getProductOrders().get(0);
        ProductOrder productOrder2 = orderResponse.getProductOrders().get(1);

        // Then
        assertThat(productOrder1.getState()).isLessThan(productOrder2.getState());
    }

    @DisplayName("Given a non-empty database with duplicated sort parameters, " +
            "when getProductOrders is called, " +
            "then it should return a Bad Request error indicating duplicate sort parameters")
    @Test
    void shouldThrowBadRequestForDuplicatedSortParameters() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).state(ProductOrderStateType.INPROGRESS).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).state(ProductOrderStateType.ACCEPTED).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.addAll("sort", List.of(SortEnum._STATE, SortEnum.STATE));

        Error expectedError = new Error(INVALID_QUERY_STRING_PARAMETER.getCode(),
                INVALID_QUERY_STRING_PARAMETER.getStatus(),
                BusinessException.DUPLICATE_SORT_PARAMETER,
                HttpStatus.BAD_REQUEST
        );

        // When & Then
        ProductOrderInventoryException exception = assertThrows(ProductOrderInventoryException.class, () -> productOrderService.getProductOrders(queryParametersMap));
        assertThat(exception.getExceptionResponse().getCode()).isEqualTo(expectedError.getCode());
        assertThat(exception.getExceptionResponse().getStatus()).isEqualTo(expectedError.getStatus());
        assertThat(exception.getExceptionResponse().getMessage()).isEqualTo(expectedError.getMessage());
        assertThat(exception.getExceptionResponse().getReason()).isEqualTo(expectedError.getReason());
    }

    @DisplayName("Given a non-empty database with filter by creation date greater than a specific date, " +
            "when getProductOrders is called, " +
            "then it should return orders with creation dates greater than the specified date")
    @Test
    void shouldReturnOrdersWithCreationDateGreaterThanSpecifiedDate() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).creationDate(creationDate1).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).creationDate(creationDate2).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("creationDate.gt", creationDate1);

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        assertThat(orderResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(orderResponse.getProductOrders(), List.of(productOrderToSave2));
    }

    @DisplayName("Given a non-empty database with filter by creation date greater than or equal to a specific date, " +
            "when getProductOrders is called, " +
            "then it should return orders with creation dates greater than or equal to the specified date")
    @Test
    void shouldReturnOrdersWithCreationDateGreaterThanOrEqualToSpecifiedDate() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).creationDate(creationDate1).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).creationDate(creationDate2).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("creationDate.gte", creationDate1);

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        assertThat(orderResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(orderResponse.getProductOrders(), List.of(productOrderToSave2, productOrderToSave1));
    }

    @DisplayName("Given a non-empty database with filter by creation date less than a specific date, " +
            "when getProductOrders is called, " +
            "then it should return orders with creation dates less than the specified date")
    @Test
    void shouldReturnOrdersWithCreationDateLessThanSpecifiedDate() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).creationDate(creationDate1).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).creationDate(creationDate2).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("creationDate.lt", creationDate2);

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        assertThat(orderResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(orderResponse.getProductOrders(), List.of(productOrderToSave1));
    }

    @DisplayName("Given a non-empty database with filter by creation date less than or equal to a specific date, " +
            "when getProductOrders is called, " +
            "then it should return orders with creation dates less than or equal to the specified date")
    @Test
    void shouldReturnOrdersWithCreationDateLessThanOrEqualToSpecifiedDate() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).creationDate(creationDate1).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).creationDate(creationDate2).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("creationDate.lte", creationDate2);

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        assertThat(orderResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(orderResponse.getProductOrders(), List.of(productOrderToSave2, productOrderToSave1));
    }

    @DisplayName("Given a non-empty database with filter by creation date, " +
            "when getProductOrders is called with exact creation date, " +
            "then it should return orders with the exact creation date")
    @Test
    void shouldReturnOrdersWithExactCreationDate() {
        // Given
        ProductOrderEntity productOrderEntity1 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID).creationDate(creationDate1).build();
        ProductOrderEntity productOrderEntity2 = productOrderEntityBuilder.id(DEFAULT_ORDER_ID_1).creationDate(creationDate2).build();
        ProductOrder productOrderToSave1 = productOrderMapper.mapToDto(productOrderEntity1);
        ProductOrder productOrderToSave2 = productOrderMapper.mapToDto(productOrderEntity2);
        productOrderService.saveProductOrder(productOrderToSave1);
        productOrderService.saveProductOrder(productOrderToSave2);

        MultiValueMap<String, Object> queryParametersMap = new LinkedMultiValueMap<>();
        queryParametersMap.set("creationDate", creationDate1);

        // When
        ProductOrderResponse orderResponse = productOrderService.getProductOrders(queryParametersMap);

        // Then
        assertThat(orderResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        ProductOrderAssertionUtil.assertListProductEqualsToListProduct(orderResponse.getProductOrders(), List.of(productOrderToSave1));
    }

    @DisplayName("Given a product order with a specific ID, " +
            "when updating the product order with appointment data, " +
            "then the appointment references should be set correctly")
    @Test
    void shouldSetAppointmentRefsCorrectlyWhenProductOrderIsUpdated() {
        //Given
        ProductOrderEntity productOrderEntity =
                productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrderEntity);
        ProductOrderEntity partialProductOrderEntity = new ProductOrderEntity();
        partialProductOrderEntity.setId(productOrderEntity.getId());
        partialProductOrderEntity.setProductOrderItem(createProductOrderItemsWithAppointment());
        ProductOrder productOrderDTO = productOrderMapper.mapToDto(partialProductOrderEntity);

        //When
        int productOrderSize = productOrderRepository.findAll().size();
        productOrderService.updateProductOrderAppointment(productOrderDTO);

        //Then
        List<ProductOrderEntity> productOrderRepositoryAll = productOrderRepository.findAll();
        assertThat(productOrderRepositoryAll).hasSize(productOrderSize);
        ProductOrderEntity updated = productOrderRepositoryAll.get(productOrderRepositoryAll.size() - 1);
        String appRef1 = findAppointmentRefIdById(
                updated.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_1);
        String appRef2 = findAppointmentRefIdById(
                updated.getProductOrderItem(), ATOMIC_PRODUCT_ORDER_ITEM_ID_2);

        assertEquals(APPOINTMENT_REF_ID_1, appRef1);
        assertEquals(APPOINTMENT_REF_ID_2, appRef2);
    }

    @DisplayName("Given a product order without ID, " +
            "when updating appointment data, " +
            "then nothing is changed in the database")
    @Test
    void shouldNotUpdateWhenProductOrderIdIsNull() {
        //Given
        ProductOrderEntity productOrder = productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrder);

        ProductOrder order = productOrderMapper.mapToDto(new ProductOrderEntity());
        order.setProductOrderItem(List.of());
        // When
        int before = productOrderRepository.findAll().size();
        productOrderService.updateProductOrderAppointment(order);

        // Then
        assertThat(productOrderRepository.findAll()).hasSize(before);
    }

    @ParameterizedTest(name = "When product order state is {0}, then it should remain {1}")
    @MethodSource("provideProductOrderStates")
    void shouldRetainSameProductOrderStateAndPublishEvent(ProductOrderStateType initialState, ProductOrderStateType expectedState) {
        // Given
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = buildProductOrder(initialState);
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateState(savedProductOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(expectedState);
        Mockito.verify(productOrderEventProducer, Mockito.times(1)).publishEvent(any(), any());
    }

    @DisplayName("Given a product order with ID having only noChange item, " +
            "when updating state , " +
            "then product order state should be completed")
    @Test
    void shouldRetainSameProductOrderStateAndPublishEvent() {
        // Given
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = buildProductOrderForMigration();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateState(savedProductOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.COMPLETED);
        Mockito.verify(productOrderEventProducer, Mockito.times(2)).publishEvent(any(), any());
    }
    @DisplayName("Given a product order in ACCEPTED state with a bundle item that has no children, " +
            "when updating the state, " +
            "then the bundle item without children should be completed and events should be published")
    @Test
    void shouldCompleteBundleItemWithoutChildrenWhenStateIsAccepted() {
        // Given
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = buildProductOrderWithBundleItemWithoutChildren();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateState(savedProductOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);
        assertThat(updatedProductOrderEntity.getState()).isEqualTo(ProductOrderStateType.COMPLETED);

        ProductOrderItemEntity bundleItemWithoutChildren = updatedProductOrderEntity.getProductOrderItem().stream()
                .filter(item -> "bundleNoChildren".equals(item.getId()))
                .findFirst()
                .orElse(null);
        assertThat(bundleItemWithoutChildren).isNotNull();
        assertThat(bundleItemWithoutChildren.getState()).isEqualTo(ProductOrderItemStateType.COMPLETED);
    }

    @DisplayName("Given a product order in ACCEPTED state with a bundle item that has children, " +
            "when updating the state, " +
            "then the bundle item with children should not be completed by processBundleItemWithoutChildren")
    @Test
    void shouldNotCompleteBundleItemWithChildrenWhenStateIsAccepted() {
        // Given
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = buildProductOrderWithBundleItemWithChildren();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateState(savedProductOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        ProductOrderItemEntity bundleItem = updatedProductOrderEntity.getProductOrderItem().stream()
                .filter(item -> "bundleWithChildren".equals(item.getId()))
                .findFirst()
                .orElse(null);
        assertThat(bundleItem).isNotNull();
        assertThat(bundleItem.getState()).isEqualTo(ProductOrderItemStateType.ACCEPTED);
    }

    @DisplayName("Given a product order in ACCEPTED state with multiple bundle items where some have no children, " +
            "when updating the state, " +
            "then only bundle items without children should be completed")
    @Test
    void shouldCompleteOnlyBundleItemsWithoutChildrenWhenMixedBundles() {
        // Given
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = buildProductOrderWithMixedBundleItems();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateState(savedProductOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        ProductOrderItemEntity bundleWithChildren = updatedProductOrderEntity.getProductOrderItem().stream()
                .filter(item -> "bundleWithChildren".equals(item.getId()))
                .findFirst()
                .orElse(null);
        assertThat(bundleWithChildren).isNotNull();
        assertThat(bundleWithChildren.getState()).isEqualTo(ProductOrderItemStateType.ACCEPTED);

        ProductOrderItemEntity bundleWithoutChildren = updatedProductOrderEntity.getProductOrderItem().stream()
                .filter(item -> "bundleNoChildren".equals(item.getId()))
                .findFirst()
                .orElse(null);
        assertThat(bundleWithoutChildren).isNotNull();
        assertThat(bundleWithoutChildren.getState()).isEqualTo(ProductOrderItemStateType.COMPLETED);
    }

    @DisplayName("Given a product order in non-ACCEPTED state with a bundle item that has no children, " +
            "when updating the state, " +
            "then the bundle item without children should not be completed")
    @Test
    void shouldNotCompleteBundleItemWithoutChildrenWhenStateIsNotAccepted() {
        // Given
        ProductOrderEntity productOrderEntity = buildProductOrderWithBundleItemWithoutChildren();
        productOrderEntity.setState(ProductOrderStateType.INPROGRESS);
        productOrderEntity.getProductOrderItem().forEach(item -> item.setState(ProductOrderItemStateType.INPROGRESS));
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateState(savedProductOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        ProductOrderItemEntity bundleItemWithoutChildren = updatedProductOrderEntity.getProductOrderItem().stream()
                .filter(item -> "bundleNoChildren".equals(item.getId()))
                .findFirst()
                .orElse(null);
        assertThat(bundleItemWithoutChildren).isNotNull();
        assertThat(bundleItemWithoutChildren.getState()).isEqualTo(ProductOrderItemStateType.INPROGRESS);
    }

    @DisplayName("Given a product order in ACCEPTED state with a bundle item without children having a parent, " +
            "when updating the state, " +
            "then the parent state should be updated based on children states")
    @Test
    void shouldUpdateParentStateWhenBundleItemWithoutChildrenIsCompleted() {
        // Given
        int initialDatabaseSize = productOrderRepository.findAll().size();
        ProductOrderEntity productOrderEntity = buildProductOrderWithBundleWithoutChildrenAndParent();
        ProductOrder productOrder = productOrderMapper.mapToDto(productOrderEntity);
        ProductOrder savedProductOrder = productOrderService.saveProductOrder(productOrder);

        // When
        productOrderService.updateState(savedProductOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize + 1);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);

        ProductOrderItemEntity parentContract = updatedProductOrderEntity.getProductOrderItem().stream()
                .filter(item -> "parentContract".equals(item.getId()))
                .findFirst()
                .orElse(null);
        assertThat(parentContract).isNotNull();
        assertThat(parentContract.getState()).isEqualTo(ProductOrderItemStateType.COMPLETED);

        ProductOrderItemEntity bundleItemWithoutChildren = updatedProductOrderEntity.getProductOrderItem().stream()
                .filter(item -> "bundleNoChildren".equals(item.getId()))
                .findFirst()
                .orElse(null);
        assertThat(bundleItemWithoutChildren).isNotNull();
        assertThat(bundleItemWithoutChildren.getState()).isEqualTo(ProductOrderItemStateType.COMPLETED);
    }

    @DisplayName("Given a product order with a specific ID, " +
            "when updating the requested completion date, " +
            "then the product order should have the updated requested completion date correctly set")
    @Test
    void shouldUpdateRequestedCompletionDateCorrectly() {
        // Given
        ProductOrderEntity productOrderEntity = productOrderEntityBuilder.id(UUID.randomUUID().toString()).build();
        productOrderRepository.save(productOrderEntity);

        ProductOrderEntity partialProductOrderEntity = new ProductOrderEntity();
        partialProductOrderEntity.setId(productOrderEntity.getId());



        partialProductOrderEntity.setCompletionDate(Instant.parse("2026-01-22T00:00:00Z"));


        ProductOrder productOrder = productOrderMapper.mapToDto(partialProductOrderEntity);

        // When
        int initialDatabaseSize = productOrderRepository.findAll().size();
        productOrderService.updateRequestedCompletionDate(productOrder);

        // Then
        List<ProductOrderEntity> productOrderEntities = productOrderRepository.findAll();
        assertThat(productOrderEntities).hasSize(initialDatabaseSize);
        ProductOrderEntity updatedProductOrderEntity = productOrderEntities.get(productOrderEntities.size() - 1);


        assertNotNull(updatedProductOrderEntity.getCompletionDate());
    }

    private ProductOrderEntity buildProductOrderForMigration() {
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        OrderItemRelationshipEntity orderItemRelationship = OrderItemRelationshipEntity.builder()
                .id("234")
                .relationshipType(RelationshipType.BUNDLES)
                .build();

        ProductOrderItemEntity contractItem = ProductOrderItemEntity.builder()
                .id("345")
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("123")
                        .atType(CONTRACT_TYPE)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(orderItemRelationship))
                .build();

        productOrderItems.add(contractItem);

        OrderItemRelationshipEntity orderItemRelationshipForBundles = OrderItemRelationshipEntity.builder()
                .id("987")
                .relationshipType(RelationshipType.BUNDLES)
                .build();
        OrderItemRelationshipEntity isChildBundlesRelationship = OrderItemRelationshipEntity.builder()
                .id("345")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity firstBundleItem = ProductOrderItemEntity.builder()
                .id("234")
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("234")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .productOrderItemRelationship(List.of(orderItemRelationshipForBundles, isChildBundlesRelationship))
                .state(ProductOrderItemStateType.ACCEPTED)
                .build();

        productOrderItems.add(firstBundleItem);

        OrderItemRelationshipEntity atomicIsChildRelationship = OrderItemRelationshipEntity.builder()
                .id("234")
                .relationshipType(RelationshipType.ISCHILD)
                .build();
        OrderItemRelationshipEntity atomicIsChildRelationship2 = OrderItemRelationshipEntity.builder()
                .id("546")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity atomicItem = ProductOrderItemEntity.builder()
                .id("987")
                .action(ItemActionType.NOCHANGE)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("564")
                        .atType(ATOMIC_PRODUCT_OFFERING)
                        .build())
                .productOrderItemRelationship(List.of(atomicIsChildRelationship, atomicIsChildRelationship2))
                .state(ProductOrderItemStateType.ACCEPTED)
                .build();

        productOrderItems.add(atomicItem);

        //migrate contract level
        OrderItemRelationshipEntity orderItemRelationship2 = OrderItemRelationshipEntity.builder()
                .id("546")
                .relationshipType(RelationshipType.BUNDLES)
                .build();

        ProductOrderItemEntity contractMigrateItem = ProductOrderItemEntity.builder()
                .id("879")
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("123")
                        .atType(CONTRACT_TYPE)
                        .build())
                .action(ItemActionType.MIGRATE)
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(orderItemRelationship2))
                .build();
        productOrderItems.add(contractMigrateItem);

        //migrate bundle item
        OrderItemRelationshipEntity orderItemRelationshipForBundles1 = OrderItemRelationshipEntity.builder()
                .id("987")
                .relationshipType(RelationshipType.BUNDLES)
                .build();
        OrderItemRelationshipEntity isChildBundlesRelationship3 = OrderItemRelationshipEntity.builder()
                .id("879")
                .relationshipType(RelationshipType.ISCHILD)
                .build();
        OrderItemRelationshipEntity migrateFromRelationship = OrderItemRelationshipEntity.builder()
                .id("234")
                .relationshipType(RelationshipType.MIGRATEFROM)
                .build();


        ProductOrderItemEntity migrateBundleItem = ProductOrderItemEntity.builder()
                .id("546")
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("234")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .productOrderItemRelationship(List.of(orderItemRelationshipForBundles1, isChildBundlesRelationship3, migrateFromRelationship))
                .state(ProductOrderItemStateType.ACCEPTED)
                .build();

        productOrderItems.add(migrateBundleItem);

        return ProductOrderEntity.builder()
                .id(DEFAULT_ORDER_ID)
                .href(DEFAULT_HREF)
                .completionDate(DEFAULT_COMPLETION_DATE)
                .expectedCompletionDate(DEFAULT_EXPECTED_COMPLETION_DATE)
                .category(DEFAULT_CATEGORY)
                .description(DEFAULT_DESCRIPTION)
                .notificationContact(DEFAULT_NOTIFICATION_CONTACT)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.ACCEPTED)
                .requestedStartDate(DEFAULT_REQUESTED_START_DATE)
                .requestedCompletionDate(DEFAULT_REQUESTED_COMPLETION_DATE)
                .priority(DEFAULT_PRIORITY)
                .atType(PRODUCT_ORDER_TYPE)
                .productOrderItem(productOrderItems).build();
    }

    private ProductOrderEntity buildProductOrderWithBundleItemWithoutChildren() {
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        ProductOrderItemEntity bundleItemNoChildren = ProductOrderItemEntity.builder()
                .id("bundleNoChildren")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("offering1")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(Collections.emptyList())
                .build();

        productOrderItems.add(bundleItemNoChildren);

        return ProductOrderEntity.builder()
                .id(DEFAULT_ORDER_ID)
                .href(DEFAULT_HREF)
                .completionDate(DEFAULT_COMPLETION_DATE)
                .expectedCompletionDate(DEFAULT_EXPECTED_COMPLETION_DATE)
                .category(DEFAULT_CATEGORY)
                .description(DEFAULT_DESCRIPTION)
                .notificationContact(DEFAULT_NOTIFICATION_CONTACT)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.ACCEPTED)
                .requestedStartDate(DEFAULT_REQUESTED_START_DATE)
                .requestedCompletionDate(DEFAULT_REQUESTED_COMPLETION_DATE)
                .priority(DEFAULT_PRIORITY)
                .atType(PRODUCT_ORDER_TYPE)
                .productOrderItem(productOrderItems)
                .build();
    }

    private ProductOrderEntity buildProductOrderWithBundleItemWithChildren() {
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        OrderItemRelationshipEntity bundlesChild = OrderItemRelationshipEntity.builder()
                .id("atomicChild")
                .relationshipType(RelationshipType.BUNDLES)
                .build();

        ProductOrderItemEntity bundleItemWithChildren = ProductOrderItemEntity.builder()
                .id("bundleWithChildren")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("offering1")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(bundlesChild))
                .build();

        productOrderItems.add(bundleItemWithChildren);

        OrderItemRelationshipEntity isChildRelationship = OrderItemRelationshipEntity.builder()
                .id("bundleWithChildren")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity atomicChild = ProductOrderItemEntity.builder()
                .id("atomicChild")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("offering2")
                        .atType(ATOMIC_PRODUCT_OFFERING)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(isChildRelationship))
                .build();

        productOrderItems.add(atomicChild);

        return ProductOrderEntity.builder()
                .id(DEFAULT_ORDER_ID)
                .href(DEFAULT_HREF)
                .completionDate(DEFAULT_COMPLETION_DATE)
                .expectedCompletionDate(DEFAULT_EXPECTED_COMPLETION_DATE)
                .category(DEFAULT_CATEGORY)
                .description(DEFAULT_DESCRIPTION)
                .notificationContact(DEFAULT_NOTIFICATION_CONTACT)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.ACCEPTED)
                .requestedStartDate(DEFAULT_REQUESTED_START_DATE)
                .requestedCompletionDate(DEFAULT_REQUESTED_COMPLETION_DATE)
                .priority(DEFAULT_PRIORITY)
                .atType(PRODUCT_ORDER_TYPE)
                .productOrderItem(productOrderItems)
                .build();
    }

    private ProductOrderEntity buildProductOrderWithMixedBundleItems() {
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        OrderItemRelationshipEntity bundlesChild = OrderItemRelationshipEntity.builder()
                .id("atomicChild")
                .relationshipType(RelationshipType.BUNDLES)
                .build();

        ProductOrderItemEntity bundleItemWithChildren = ProductOrderItemEntity.builder()
                .id("bundleWithChildren")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("offering1")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(bundlesChild))
                .build();

        productOrderItems.add(bundleItemWithChildren);

        OrderItemRelationshipEntity isChildRelationship = OrderItemRelationshipEntity.builder()
                .id("bundleWithChildren")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity atomicChild = ProductOrderItemEntity.builder()
                .id("atomicChild")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("offering2")
                        .atType(ATOMIC_PRODUCT_OFFERING)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(isChildRelationship))
                .build();

        productOrderItems.add(atomicChild);

        ProductOrderItemEntity bundleItemNoChildren = ProductOrderItemEntity.builder()
                .id("bundleNoChildren")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("offering3")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(Collections.emptyList())
                .build();

        productOrderItems.add(bundleItemNoChildren);

        return ProductOrderEntity.builder()
                .id(DEFAULT_ORDER_ID)
                .href(DEFAULT_HREF)
                .completionDate(DEFAULT_COMPLETION_DATE)
                .expectedCompletionDate(DEFAULT_EXPECTED_COMPLETION_DATE)
                .category(DEFAULT_CATEGORY)
                .description(DEFAULT_DESCRIPTION)
                .notificationContact(DEFAULT_NOTIFICATION_CONTACT)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.ACCEPTED)
                .requestedStartDate(DEFAULT_REQUESTED_START_DATE)
                .requestedCompletionDate(DEFAULT_REQUESTED_COMPLETION_DATE)
                .priority(DEFAULT_PRIORITY)
                .atType(PRODUCT_ORDER_TYPE)
                .productOrderItem(productOrderItems)
                .build();
    }

    private ProductOrderEntity buildProductOrderWithBundleWithoutChildrenAndParent() {
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        OrderItemRelationshipEntity contractBundlesRelationship = OrderItemRelationshipEntity.builder()
                .id("bundleNoChildren")
                .relationshipType(RelationshipType.BUNDLES)
                .build();

        ProductOrderItemEntity parentContract = ProductOrderItemEntity.builder()
                .id("parentContract")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("contractOffering")
                        .atType(CONTRACT_TYPE)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(contractBundlesRelationship))
                .build();

        productOrderItems.add(parentContract);

        OrderItemRelationshipEntity isChildRelationship = OrderItemRelationshipEntity.builder()
                .id("parentContract")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity bundleItemNoChildren = ProductOrderItemEntity.builder()
                .id("bundleNoChildren")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("bundleOffering")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(isChildRelationship))
                .build();

        productOrderItems.add(bundleItemNoChildren);

        return ProductOrderEntity.builder()
                .id(DEFAULT_ORDER_ID)
                .href(DEFAULT_HREF)
                .completionDate(DEFAULT_COMPLETION_DATE)
                .expectedCompletionDate(DEFAULT_EXPECTED_COMPLETION_DATE)
                .category(DEFAULT_CATEGORY)
                .description(DEFAULT_DESCRIPTION)
                .notificationContact(DEFAULT_NOTIFICATION_CONTACT)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(ProductOrderStateType.ACCEPTED)
                .requestedStartDate(DEFAULT_REQUESTED_START_DATE)
                .requestedCompletionDate(DEFAULT_REQUESTED_COMPLETION_DATE)
                .priority(DEFAULT_PRIORITY)
                .atType(PRODUCT_ORDER_TYPE)
                .productOrderItem(productOrderItems)
                .build();
    }

    private static Stream<Arguments> provideProductOrderStates() {
        return Stream.of(
                Arguments.of(ProductOrderStateType.DRAFT, ProductOrderStateType.DRAFT),
                Arguments.of(ProductOrderStateType.ACKNOWLEDGED, ProductOrderStateType.ACKNOWLEDGED),
                Arguments.of(ProductOrderStateType.ACCEPTED, ProductOrderStateType.ACCEPTED),
                Arguments.of(ProductOrderStateType.INPROGRESS, ProductOrderStateType.INPROGRESS),
                Arguments.of(ProductOrderStateType.HELD, ProductOrderStateType.HELD),
                Arguments.of(ProductOrderStateType.PENDING, ProductOrderStateType.PENDING),
                Arguments.of(ProductOrderStateType.PENDINGCANCELLATION, ProductOrderStateType.PENDINGCANCELLATION),
                Arguments.of(ProductOrderStateType.ASSESSINGCANCELLATION, ProductOrderStateType.ASSESSINGCANCELLATION),
                Arguments.of(ProductOrderStateType.CANCELLED, ProductOrderStateType.CANCELLED),
                Arguments.of(ProductOrderStateType.REJECTED, ProductOrderStateType.REJECTED),
                Arguments.of(ProductOrderStateType.COMPLETED, ProductOrderStateType.COMPLETED),
                Arguments.of(ProductOrderStateType.FAILED, ProductOrderStateType.FAILED)
        );
    }

    private ProductOrderEntity.ProductOrderEntityBuilder createEntity() {

        List<ProductOrderItemEntity> productOrderItems = createProductOrderItems(ProductOrderItemStateType.DRAFT, Boolean.TRUE, ProductOrderItemStateType.ACCEPTED, ProductOrderItemStateType.ACCEPTED);

        PartyRefEntity partyRefEntity = PartyRefEntity.builder()
                .id(DEFAULT_PARTY_ID)
                .name(DEFAULT_PARTY_NAME)
                .atReferredType(DEFAULT_REFERRED_TYPE)
                .build();

        List<RelatedPartyRefOrPartyRoleRefEntity> relatedParties = Collections.singletonList(RelatedPartyRefOrPartyRoleRefEntity.builder()
                .role(PROSPECT)
                .partyOrPartyRole(partyRefEntity)
                .build());

        List<OrderPriceEntity> orderPrice = createOrderPrice();

        return ProductOrderEntity.builder()
                .id(DEFAULT_ORDER_ID)
                .href(DEFAULT_HREF)
                .completionDate(DEFAULT_COMPLETION_DATE)
                .expectedCompletionDate(DEFAULT_EXPECTED_COMPLETION_DATE)
                .category(DEFAULT_CATEGORY)
                .description(DEFAULT_DESCRIPTION)
                .notificationContact(DEFAULT_NOTIFICATION_CONTACT)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(DEFAULT_STATE)
                .requestedStartDate(DEFAULT_REQUESTED_START_DATE)
                .requestedCompletionDate(DEFAULT_REQUESTED_COMPLETION_DATE)
                .priority(DEFAULT_PRIORITY)
                .relatedParty(relatedParties)
                .atType(PRODUCT_ORDER_TYPE)
                .orderTotalPrice(orderPrice)
                .productOrderItem(productOrderItems);
    }

    private List<OrderPriceEntity> createOrderPrice() {
        PriceEntity priceEntity = PriceEntity.builder()
                .dutyFreeAmount(MoneyEntity.builder()
                        .unit(UNIT)
                        .value(DUTY_FREE_AMOUNT_VALUE)
                        .build())
                .taxIncludedAmount(MoneyEntity.builder()
                        .unit(UNIT)
                        .value(TAX_INCLUDED_AMOUNT_VALUE)
                        .build())
                .build();

        return List.of(OrderPriceEntity.builder()
                .price(priceEntity)
                .priceType(PRICE_TYPE)
                .build());
    }

    private List<ProductOrderItemEntity> createProductOrderItems(ProductOrderItemStateType productOrderItemStateType, Boolean isInstallable, ProductOrderItemStateType intialProductOrderItemStateType, ProductOrderItemStateType secondConractProductOrderItemStateType) {
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        // Create contract item => 1
        productOrderItems.add(createProductOrderItem(
                CONTRACT_PRODUCT_ORDER_ITEM_ID_1,
                PRODUCT_ID_1,
                MOBILE_PACKAGE_1,
                CONTRACT_TYPE,
                intialProductOrderItemStateType,
                isInstallable,
                Collections.singletonList(createOrderItemRelationship(BUNDLE_PRODUCT_ORDER_ITEM_ID_1, RelationshipType.BUNDLES))
        ));

        // Create bundle item => 2
        List<OrderItemRelationshipEntity> bundleRelationships = new ArrayList<>();
        bundleRelationships.add(createOrderItemRelationship(ATOMIC_PRODUCT_ORDER_ITEM_ID_1, RelationshipType.BUNDLES));
        bundleRelationships.add(createOrderItemRelationship(ATOMIC_PRODUCT_ORDER_ITEM_ID_2, RelationshipType.BUNDLES));
        bundleRelationships.add(createOrderItemRelationship(CONTRACT_PRODUCT_ORDER_ITEM_ID_1, RelationshipType.ISCHILD));
        productOrderItems.add(createProductOrderItem(
                BUNDLE_PRODUCT_ORDER_ITEM_ID_1,
                PRODUCT_ID_2,
                MOBILE_PACKAGE_1,
                BUNDLED_PRODUCT_OFFERING,
                intialProductOrderItemStateType,
                isInstallable,
                bundleRelationships
        ));

        // Create atomic bundle item => 3
        productOrderItems.add(createProductOrderItem(
                ATOMIC_PRODUCT_ORDER_ITEM_ID_1,
                PRODUCT_ID_3,
                MOBILE_LINE,
                ATOMIC_PRODUCT_OFFERING,
                intialProductOrderItemStateType,
                isInstallable,
                Collections.singletonList(createOrderItemRelationship(BUNDLE_PRODUCT_ORDER_ITEM_ID_1, RelationshipType.ISCHILD)),
                false,
                PRODUCT_SPECIFICATION_ID_1
        ));

        // Create atomic bundle item => 4
        productOrderItems.add(createProductOrderItem(
                ATOMIC_PRODUCT_ORDER_ITEM_ID_2,
                PRODUCT_ID_4,
                TIME_BUNDLE,
                ATOMIC_PRODUCT_OFFERING,
                productOrderItemStateType,
                isInstallable,
                Collections.singletonList(createOrderItemRelationship(BUNDLE_PRODUCT_ORDER_ITEM_ID_1, RelationshipType.ISCHILD)),
                false,
                PRODUCT_SPECIFICATION_ID_2
        ));

        // Create contract item => 2
        productOrderItems.add(createProductOrderItem(
                CONTRACT_PRODUCT_ORDER_ITEM_ID_2,
                PRODUCT_ID_5,
                MOBILE_PACKAGE_2,
                CONTRACT_TYPE,
                secondConractProductOrderItemStateType,
                isInstallable,
                Collections.singletonList(createOrderItemRelationship(BUNDLE_PRODUCT_ORDER_ITEM_ID_2, RelationshipType.BUNDLES))
        ));

        // Create bundle item => 2
        List<OrderItemRelationshipEntity> bundleRelationships2 = new ArrayList<>();
        bundleRelationships2.add(createOrderItemRelationship(ATOMIC_PRODUCT_ORDER_ITEM_ID_3, RelationshipType.BUNDLES));
        bundleRelationships2.add(createOrderItemRelationship(CONTRACT_PRODUCT_ORDER_ITEM_ID_2, RelationshipType.ISCHILD));
        productOrderItems.add(createProductOrderItem(
                BUNDLE_PRODUCT_ORDER_ITEM_ID_2,
                PRODUCT_ID_6,
                MOBILE_PACKAGE_2,
                BUNDLED_PRODUCT_OFFERING,
                secondConractProductOrderItemStateType,
                isInstallable,
                bundleRelationships2
        ));

        // Create atomic bundle item => 3
        productOrderItems.add(createProductOrderItem(
                ATOMIC_PRODUCT_ORDER_ITEM_ID_3,
                PRODUCT_ID_7,
                MOBILE_LINE,
                ATOMIC_PRODUCT_OFFERING,
                secondConractProductOrderItemStateType,
                isInstallable,
                Collections.singletonList(createOrderItemRelationship(BUNDLE_PRODUCT_ORDER_ITEM_ID_2, RelationshipType.ISCHILD)),
                false,
                PRODUCT_SPECIFICATION_ID_1
        ));

        return productOrderItems;
    }

    private ProductOrderItemEntity createProductOrderItem(String orderId, String productId, String productName, String productType,
                                                          ProductOrderItemStateType state, Boolean isInstallable, List<OrderItemRelationshipEntity> relationships) {
        return createProductOrderItem(orderId, productId, productName, productType, state, isInstallable, relationships, true, null);
    }

    private ProductOrderItemEntity createProductOrderItem(String orderId, String productId, String productName, String productType,
                                                          ProductOrderItemStateType state, Boolean isInstallable, List<OrderItemRelationshipEntity> relationships, boolean isBundle, String specificationId) {

        ProductOfferingRefEntity productOffering = ProductOfferingRefEntity.builder()
                .name(productName)
                .atType(productType)
                .build();

        ProductEntity.ProductEntityBuilder<?, ?> productEntityBuilder = ProductEntity.builder()
                .id(productId)
                .isBundle(isBundle)
                .atType(PRODUCT_TYPE);

        if (specificationId != null) {
            ProductSpecificationRefEntity productSpecification = ProductSpecificationRefEntity.builder()
                    .id(specificationId)
                    .name(productName)
                    .atType(PRODUCT_SPECIFICATION_REF)
                    .build();
            productEntityBuilder.productSpecification(productSpecification);
        }

        List<OrderPriceEntity> orderPrice = createOrderPrice();

        return ProductOrderItemEntity.builder()
                .id(orderId)
                .quantity(1)
                .action(ItemActionType.ADD)
                .product(productEntityBuilder.build())
                .state(state)
                .productOrderItemRelationship(relationships)
                .productOffering(productOffering)
                .atType(PRODUCT_ORDER_ITEM_TYPE)
                .isInstallable(isInstallable)
                .itemPrice(orderPrice)
                .build();
    }

    private OrderItemRelationshipEntity createOrderItemRelationship(String id, RelationshipType relationshipType) {
        return OrderItemRelationshipEntity.builder()
                .id(id)
                .relationshipType(relationshipType)
                .build();
    }

    private ProductOrderEntity buildProductOrder(ProductOrderStateType productOrderStateType) {
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        OrderItemRelationshipEntity orderItemRelationship = OrderItemRelationshipEntity.builder()
                .id("234")
                .relationshipType(RelationshipType.BUNDLES)
                .build();

        ProductOrderItemEntity contractItem = ProductOrderItemEntity.builder()
                .id("345")
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("123")
                        .atType(CONTRACT_TYPE)
                        .build())
                .state(ProductOrderItemStateType.ACCEPTED)
                .productOrderItemRelationship(List.of(orderItemRelationship))
                .build();

        productOrderItems.add(contractItem);

        OrderItemRelationshipEntity orderItemRelationshipForBundles = OrderItemRelationshipEntity.builder()
                .id("123")
                .relationshipType(RelationshipType.BUNDLES)
                .build();
        OrderItemRelationshipEntity isChildBundlesRelationship = OrderItemRelationshipEntity.builder()
                .id("345")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity firstBundleItem = ProductOrderItemEntity.builder()
                .id("234")
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("234")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .productOrderItemRelationship(List.of(orderItemRelationshipForBundles, isChildBundlesRelationship))
                .state(ProductOrderItemStateType.ACCEPTED)
                .build();

        productOrderItems.add(firstBundleItem);

        OrderItemRelationshipEntity atomicBundleRelationship = OrderItemRelationshipEntity.builder()
                .id("987")
                .relationshipType(RelationshipType.BUNDLES)
                .build();
        OrderItemRelationshipEntity isChildBundlesRelationship2 = OrderItemRelationshipEntity.builder()
                .id("234")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity secondBundleItem = ProductOrderItemEntity.builder()
                .id("123")
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("564")
                        .atType(BUNDLED_PRODUCT_OFFERING)
                        .build())
                .productOrderItemRelationship(List.of(atomicBundleRelationship, isChildBundlesRelationship2))
                .state(ProductOrderItemStateType.ACCEPTED)
                .build();

        productOrderItems.add(secondBundleItem);

        OrderItemRelationshipEntity atomicIsChildRelationship = OrderItemRelationshipEntity.builder()
                .id("123")
                .relationshipType(RelationshipType.ISCHILD)
                .build();

        ProductOrderItemEntity atomicItem = ProductOrderItemEntity.builder()
                .id("987")
                .action(ItemActionType.ADD)
                .productOffering(ProductOfferingRefEntity.builder()
                        .id("564")
                        .atType(ATOMIC_PRODUCT_OFFERING)
                        .build())
                .productOrderItemRelationship(List.of(atomicIsChildRelationship))
                .state(ProductOrderItemStateType.ACCEPTED)
                .build();

        productOrderItems.add(atomicItem);

        return ProductOrderEntity.builder()
                .id(DEFAULT_ORDER_ID)
                .href(DEFAULT_HREF)
                .completionDate(DEFAULT_COMPLETION_DATE)
                .expectedCompletionDate(DEFAULT_EXPECTED_COMPLETION_DATE)
                .category(DEFAULT_CATEGORY)
                .description(DEFAULT_DESCRIPTION)
                .notificationContact(DEFAULT_NOTIFICATION_CONTACT)
                .creationDate(DEFAULT_CREATION_DATE)
                .state(productOrderStateType)
                .requestedStartDate(DEFAULT_REQUESTED_START_DATE)
                .requestedCompletionDate(DEFAULT_REQUESTED_COMPLETION_DATE)
                .priority(DEFAULT_PRIORITY)
                .atType(PRODUCT_ORDER_TYPE)
                .productOrderItem(productOrderItems).build();
    }

    private List<String> findPaymentIdsById(List<ProductOrderItemEntity> productOrderItems, String id) {
        return productOrderItems.stream()
                .filter(item -> id.equals(item.getId()))
                .map(ProductOrderItemEntity::getPayment)
                .flatMap(paymentRefEntities -> paymentRefEntities.stream().map(PaymentRefEntity::getId))
                .toList();
    }

    private String findBillingAccountRefIdById(List<ProductOrderItemEntity> productOrderItems, String id) {
        return productOrderItems.stream()
                .filter(item -> id.equals(item.getId()))
                .map(productOrderItemEntity -> productOrderItemEntity.getBillingAccount().getId())
                .findFirst()
                .orElse(null);
    }

    private String findProductIdById(List<ProductOrderItemEntity> productOrderItems, String id) {
        return productOrderItems.stream()
                .filter(item -> id.equals(item.getId()))
                .map(productOrderItemEntity -> ((ProductRefEntity) productOrderItemEntity.getProduct()).getId())
                .findFirst()
                .orElse(null);
    }

    private List<ProductOrderItemEntity> createProductOrderItemsWithAppointment() {
        AppointmentRefEntity ref1 = AppointmentRefEntity.builder()
                .id(APPOINTMENT_REF_ID_1)
                .atType(APPOINTMENT_REF)
                .build();

        AppointmentRefEntity ref2 = AppointmentRefEntity.builder()
                .id(APPOINTMENT_REF_ID_2)
                .atType(APPOINTMENT_REF)
                .build();

        ProductOrderItemEntity item1 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_1)
                .appointment(ref1)
                .build();

        ProductOrderItemEntity item2 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_2)
                .appointment(ref2)
                .build();

        return List.of(item1, item2);
    }

    private String findAppointmentRefIdById(List<ProductOrderItemEntity> items, String id) {
        return items.stream()
                .filter(i -> id.equals(i.getId()))
                .map(ProductOrderItemEntity::getAppointment)
                .filter(Objects::nonNull)
                .map(AppointmentRefEntity::getId)
                .findFirst()
                .orElse(null);
    }

    private List<ProductOrderItemEntity> createProductOrderItemsWithResources() {

        ResourceRefEntity resourceRef43 = ResourceRefEntity.builder()
                .id(REALIZING_RESOURCE_ID_1)
                .build();

        List<ResourceRefEntity> resourceRef4 = new ArrayList<>();
        resourceRef4.add(resourceRef43);

        ProductEntity productEntity4 = ProductEntity.builder()
                .realizingResource(resourceRef4)
                .atType(PRODUCT_TYPE)
                .build();

        ProductOrderItemEntity productOrderItem4 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_1)
                .product(productEntity4)
                .build();

        ResourceRefEntity resourceRef65 = ResourceRefEntity.builder()
                .id(REALIZING_RESOURCE_ID_2)
                .build();

        List<ResourceRefEntity> resourceRef6 = new ArrayList<>();
        resourceRef6.add(resourceRef65);

        ProductEntity productEntity6 = ProductEntity.builder()
                .realizingResource(resourceRef6)
                .atType(PRODUCT_TYPE)
                .build();

        ProductOrderItemEntity productOrderItem6 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_2)
                .product(productEntity6)
                .build();

        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();
        productOrderItems.add(productOrderItem4);
        productOrderItems.add(productOrderItem6);

        return productOrderItems;
    }

    private List<String> findResourceRefsIdById(List<ProductOrderItemEntity> productOrderItems, String id) {
        return productOrderItems.stream()
                .filter(item -> id.equals(item.getId()))
                .map(productOrderItemEntity -> ((ProductEntity) productOrderItemEntity.getProduct()).getRealizingResource())
                .flatMap(resourceRefEntities -> resourceRefEntities.stream().map(ResourceRefEntity::getId))
                .toList();
    }

    private List<ProductOrderItemEntity> createProductOrderItemsWithPayment() {
        PaymentRefEntity paymentRef31 = PaymentRefEntity.builder()
                .id(PAYMENT_ID_1)
                .build();

        List<PaymentRefEntity> paymentRef3 = new ArrayList<>();
        paymentRef3.add(paymentRef31);

        ProductOrderItemEntity productOrderItem3 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_1)
                .payment(paymentRef3)
                .build();

        PaymentRefEntity paymentRef51 = PaymentRefEntity.builder()
                .id(PAYMENT_ID_2)
                .build();

        List<PaymentRefEntity> paymentRef5 = new ArrayList<>();
        paymentRef5.add(paymentRef51);
        ProductOrderItemEntity productOrderItem5 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_2)
                .payment(paymentRef5)
                .build();

        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();
        productOrderItems.add(productOrderItem3);
        productOrderItems.add(productOrderItem5);

        return productOrderItems;
    }

    private List<ProductOrderItemEntity> createProductOrderItemsWithBillingAccount() {
        // Create BillingAccountRefEntity objects
        BillingAccountRefEntity billingAccountRefEntity1 = BillingAccountRefEntity.builder()
                .id(BILLING_ACCOUNT_ID_1)
                .atReferredType(BILLING_ACCOUNT_REFERRED_TYPE)
                .build();

        BillingAccountRefEntity billingAccountRefEntity2 = BillingAccountRefEntity.builder()
                .id(BILLING_ACCOUNT_ID_2)
                .atReferredType(BILLING_ACCOUNT_REFERRED_TYPE)
                .build();

        // Create ProductOrderItemEntity objects with BillingAccountRefEntity
        ProductOrderItemEntity productOrderItem1 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_1)
                .billingAccount(billingAccountRefEntity1)
                .build();

        ProductOrderItemEntity productOrderItem2 = ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_2)
                .billingAccount(billingAccountRefEntity2)
                .build();

        // Add ProductOrderItemEntity objects to the list
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();
        productOrderItems.add(productOrderItem1);
        productOrderItems.add(productOrderItem2);

        return productOrderItems;
    }

    private List<ProductOrderItemEntity> createProductOrderItemsWithProductRef() {
        // Create ProductRefOrValueEntity objects
        ProductRefEntity productRefEntity1 = ProductRefEntity.builder()
                .id(PRODUCT_ID_1)
                .href(PRODUCT_REF_HREF_1)
                .atType(PRODUCT_REF_TYPE)
                .build();

        ProductRefEntity productRefEntity2 = ProductRefEntity.builder()
                .id(PRODUCT_ID_2)
                .href(PRODUCT_REF_HREF_2)
                .atType(PRODUCT_REF_TYPE)
                .build();

        // Create a list to hold ProductOrderItemEntity objects
        List<ProductOrderItemEntity> productOrderItems = new ArrayList<>();

        // Create and add ProductOrderItemEntity objects to the list
        productOrderItems.add(ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_1)
                .product(productRefEntity1)
                .build());

        productOrderItems.add(ProductOrderItemEntity.builder()
                .id(ATOMIC_PRODUCT_ORDER_ITEM_ID_2)
                .product(productRefEntity2)
                .build());

        return productOrderItems;
    }
}