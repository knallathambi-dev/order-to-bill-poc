// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.controller;

import com.orange.discobole.ordermanagement.orderinventory.IntegrationTest;
import com.orange.discobole.ordermanagement.orderinventory.config.TestSecurityConfig;
import com.orange.discobole.ordermanagement.orderinventory.constant.PreAuthorizeExpressions;
import com.orange.discobole.ordermanagement.orderinventory.domain.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.repository.ProductOrderRepository;
import com.orange.discobole.ordermanagement.orderinventory.service.AuthorizationService;
import com.orange.discobole.ordermanagement.orderinventory.service.mapper.ProductOrderMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the { ProductOrderControllerImpl } REST controller.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@IntegrationTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@WithMockUser(username = "testuser", authorities = {"USER"})
class ProductOrderControllerImplTest {

    private static final String PRODUCT_ORDER_API_URL = "/productOrderingManagement/v1/productOrder";
    private static final String DEFAULT_PRODUCT_ORDER_ITEM_ID = "1";
    private static final ProductOrderItemStateType DEFAULT_PRODUCT_ORDER_ITEM_STATUS = ProductOrderItemStateType.DRAFT;
    private static final ItemActionType DEFAULT_PRODUCT_ORDER_ITEM_ACTION = ItemActionType.ADD;
    private static final String PRODUCT_ORDER_ITEM_TYPE = "ProductOrderItem";
    private static final Instant DEFAULT_CANCELLATION_DATE = Instant.ofEpochMilli(0L);
    private static final String DEFAULT_HREF = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_CANCELLATION_REASON = RandomStringUtils.randomAlphabetic(10);
    private static final Instant DEFAULT_COMPLETION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant DEFAULT_EXPECTED_COMPLETION_DATE = Instant.ofEpochMilli(0L);
    private static final String DEFAULT_CATEGORY = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_DESCRIPTION = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_NOTIFICATION_CONTACT = RandomStringUtils.randomAlphabetic(10);
    private static final Instant DEFAULT_CREATION_DATE = Instant.parse("2024-02-13T14:02:00Z");
    private static final ProductOrderStateType DEFAULT_STATE = ProductOrderStateType.DRAFT;
    private static final Instant DEFAULT_REQUESTED_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant DEFAULT_REQUESTED_COMPLETION_DATE = Instant.ofEpochMilli(0L);
    private static final String DEFAULT_PRIORITY = RandomStringUtils.randomAlphabetic(10);
    private static final String DEFAULT_TYPE = "ProductOrder";
    private static final String PRODUCT_ORDER_API_URL_ID = PRODUCT_ORDER_API_URL + "/{id}";
    private static final String ADMIN_RELATED_PARTY_ID = "admin123";
    private static final String USER_RELATED_PARTY_ID = "user456";
    private static final String OTHER_USER_RELATED_PARTY_ID = "other789";

    @Autowired
    private ProductOrderRepository productOrderRepository;
    @SpyBean
    private AuthorizationService authorizationService;
    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private MockMvc restProductOrderMockMvc;

    private ProductOrderEntity productOrderEntity;
    private ProductOrderEntity adminProductOrder;
    private ProductOrderEntity userProductOrder;
    private ProductOrderEntity otherUserProductOrder;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductOrderEntity createEntity() {
        ProductOrderItemEntity productOrderItem = ProductOrderItemEntity.builder()
                .id(DEFAULT_PRODUCT_ORDER_ITEM_ID)
                .state(DEFAULT_PRODUCT_ORDER_ITEM_STATUS)
                .action(DEFAULT_PRODUCT_ORDER_ITEM_ACTION)
                .atType(PRODUCT_ORDER_ITEM_TYPE)
                .build();
        return ProductOrderEntity.builder()
                .cancellationDate(DEFAULT_CANCELLATION_DATE)
                .href(DEFAULT_HREF)
                .cancellationReason(DEFAULT_CANCELLATION_REASON)
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
                .atType(DEFAULT_TYPE)
                .productOrderItem(Collections.singletonList(productOrderItem))
                .build();
    }

    @BeforeEach
    void initTest() {
        productOrderRepository.deleteAll();
        productOrderEntity = createEntity();
        adminProductOrder = createProductOrderWithRelatedParty(ADMIN_RELATED_PARTY_ID);
        userProductOrder = createProductOrderWithRelatedParty(USER_RELATED_PARTY_ID);
        otherUserProductOrder = createProductOrderWithRelatedParty(OTHER_USER_RELATED_PARTY_ID);

        productOrderRepository.save(adminProductOrder);
        productOrderRepository.save(userProductOrder);
        productOrderRepository.save(otherUserProductOrder);
    }

    @Test
    @SneakyThrows
    @DisplayName("Given a valid product order, " +
            "when creating the product order, " +
            "then the product order is created successfully")
    void shouldCreateProductOrderSuccessfully() {
        productOrderEntity.setCancellationDate(null);
        productOrderEntity.setCancellationReason(null);
        ProductOrder productOrder = productOrderMapper.mapToDto(this.productOrderEntity);
        int databaseSizeBeforeCreate = productOrderRepository.findAll().size();

        restProductOrderMockMvc.perform(post(PRODUCT_ORDER_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productOrder))).andExpect(status().isCreated());

        // Validate the ProductOrder in the database
        List<ProductOrderEntity> productOrderList = productOrderRepository.findAll();
        assertThat(productOrderList).hasSize(databaseSizeBeforeCreate + 1);
        ProductOrderEntity testProductOrder = productOrderList.get(productOrderList.size() - 1);
        assertThat(testProductOrder.getCancellationDate()).isNull();
        assertThat(testProductOrder.getHref()).isEqualTo(DEFAULT_HREF);
        assertThat(testProductOrder.getProductOrderItem()).hasSize(1);
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
        assertThat(testProductOrder.getAtType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @SneakyThrows
    @DisplayName("Given a product order with an existing ID, " +
            "when creating the product order, " +
            "then the creation fails with a bad request")
    void shouldFailToCreateProductOrderWhenIdExists() {
        // Create the ProductOrder with an existing ID
        productOrderEntity.setId("existing_id");
        ProductOrder productOrderDto = productOrderMapper.mapToDto(this.productOrderEntity);

        int databaseSizeBeforeCreate = productOrderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductOrderMockMvc
                .perform(
                        post(PRODUCT_ORDER_API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(productOrderDto))
                )
                .andExpect(status().isBadRequest());

        // Validate the ProductOrder in the database
        List<ProductOrderEntity> productOrderList = productOrderRepository.findAll();
        assertThat(productOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @ParameterizedTest
    @MethodSource("provideParametersForGetAllProductOrders")
    @SneakyThrows
    @DisplayName("Given a set of product orders, " +
            "when retrieving all product orders, " +
            "then the response matches the expected status and content")
    @WithMockUser(authorities = {PreAuthorizeExpressions.CAN_READ_PRODUCT, "disco-admin"})
    void shouldRetrieveAllProductOrders(String urlSuffix, HttpStatus expectedStatus, boolean expectValidResponse) {
        // Initialize the database
        productOrderEntity.setId(UUID.randomUUID().toString());
        productOrderRepository.save(productOrderEntity);

        // Perform the request
        ResultActions resultActions = restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL + urlSuffix));

        // Check the status
        resultActions.andExpect(status().is(expectedStatus.value()));

        // If a valid response is expected, check the JSON content
        if (expectValidResponse) {
            resultActions
                    .andExpect(jsonPath("$.[*].id").value(hasItem(productOrderEntity.getId())))
                    .andExpect(jsonPath("$.[*].cancellationDate").value(hasItem(DEFAULT_CANCELLATION_DATE.toString())))
                    .andExpect(jsonPath("$.[*].href").value(hasItem(generateHref(productOrderEntity).getHref())))
                    .andExpect(jsonPath("$.[*].cancellationReason").value(hasItem(DEFAULT_CANCELLATION_REASON)))
                    .andExpect(jsonPath("$.[*].completionDate").value(hasItem(DEFAULT_COMPLETION_DATE.toString())))
                    .andExpect(jsonPath("$.[*].expectedCompletionDate").value(hasItem(DEFAULT_EXPECTED_COMPLETION_DATE.toString())))
                    .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
                    .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                    .andExpect(jsonPath("$.[*].notificationContact").value(hasItem(DEFAULT_NOTIFICATION_CONTACT)))
                    .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
                    .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
                    .andExpect(jsonPath("$.[*].requestedStartDate").value(hasItem(DEFAULT_REQUESTED_START_DATE.toString())))
                    .andExpect(jsonPath("$.[*].requestedCompletionDate").value(hasItem(DEFAULT_REQUESTED_COMPLETION_DATE.toString())))
                    .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)))
                    .andExpect(jsonPath("$.[*].['@type']").value(hasItem(DEFAULT_TYPE)));
        }
    }

    @Test
    @SneakyThrows
    @DisplayName("Given no product orders in the database, " +
            "when retrieving all product orders, " +
            "then an empty array is returned")
    @WithMockUser(authorities = {PreAuthorizeExpressions.CAN_READ_PRODUCT, "disco-admin"})
    void shouldReturnEmptyArrayWhenNoProductOrdersExist() {
        productOrderRepository.deleteAll();
        // Get the productOrder
        restProductOrderMockMvc
                .perform(get(PRODUCT_ORDER_API_URL))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given a valid product order ID, " +
            "when retrieving the product order, " +
            "then the correct product order is returned")
    @WithMockUser(authorities = {PreAuthorizeExpressions.CAN_READ_PRODUCT, "disco-admin"})
    void shouldRetrieveProductOrderById() {
        // Initialize the database
        productOrderEntity.setId(UUID.randomUUID().toString());
        productOrderRepository.save(productOrderEntity);

        // Get the productOrder
        restProductOrderMockMvc
                .perform(get(PRODUCT_ORDER_API_URL_ID, productOrderEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productOrderEntity.getId()))
                .andExpect(jsonPath("$.cancellationDate").value(DEFAULT_CANCELLATION_DATE.toString()))
                .andExpect(jsonPath("$.href").value(generateHref(productOrderEntity).getHref()))
                .andExpect(jsonPath("$.cancellationReason").value(DEFAULT_CANCELLATION_REASON))
                .andExpect(jsonPath("$.completionDate").value(DEFAULT_COMPLETION_DATE.toString()))
                .andExpect(jsonPath("$.expectedCompletionDate").value(DEFAULT_EXPECTED_COMPLETION_DATE.toString()))
                .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                .andExpect(jsonPath("$.notificationContact").value(DEFAULT_NOTIFICATION_CONTACT))
                .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
                .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
                .andExpect(jsonPath("$.requestedStartDate").value(DEFAULT_REQUESTED_START_DATE.toString()))
                .andExpect(jsonPath("$.requestedCompletionDate").value(DEFAULT_REQUESTED_COMPLETION_DATE.toString()))
                .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY))
                .andExpect(jsonPath("$.['@type']").value(DEFAULT_TYPE));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given a non-existing product order ID, " +
            "when retrieving the product order, " +
            "then a not found status is returned")
    @WithMockUser(authorities = {PreAuthorizeExpressions.CAN_READ_PRODUCT, "disco-admin"})
    void shouldReturnNotFoundWhenProductOrderDoesNotExist() {

        // Get the productOrder
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    private ProductOrderEntity generateHref(ProductOrderEntity productOrder) {
        String productOrderId = productOrder.getId();
        String selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductOrderControllerImpl.class)
                        .retrieveProductOrder(productOrderId, null))
                .withSelfRel()
                .toUri()
                .toString();
        productOrder.setHref(selfLink);
        return productOrder;
    }

    private Stream<Arguments> provideParametersForGetAllProductOrders() {
        return Stream.of(
                Arguments.of("", HttpStatus.OK, true),
                Arguments.of("?creationDate.gt=2024-02-13", HttpStatus.OK, true),
                Arguments.of("?creationDate.gt=2024-02-13T14:00:01Z", HttpStatus.OK, true),
                Arguments.of("?creationDate.gt=2024-15-13T14:02:00Z", HttpStatus.BAD_REQUEST, false),
                Arguments.of("?creationDate=2024-02-13", HttpStatus.OK, true),
                Arguments.of("?creationDate=2024-02-13T14:02:00Z", HttpStatus.OK, true),
                Arguments.of("?creationDate=2024-15-13T14:02:00Z", HttpStatus.BAD_REQUEST, false)
        );
    }

    // ==================== List Product Orders - Admin Tests ====================

    @Test
    @SneakyThrows
    @DisplayName("Given admin user, when listing all product orders without filter, then return all product orders")
    void listProductOrders_AdminUser_ReturnsAllOrders() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(true);

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].id", hasItem(adminProductOrder.getId())))
                .andExpect(jsonPath("$.[*].id", hasItem(userProductOrder.getId())))
                .andExpect(jsonPath("$.[*].id", hasItem(otherUserProductOrder.getId())));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given admin user, when listing product orders with specific relatedPartyId filter, then return filtered results")
    void listProductOrders_AdminUserWithFilter_ReturnsFilteredOrders() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(true);

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL)
                        .param("relatedParty.id", USER_RELATED_PARTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(userProductOrder.getId()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given admin user, when listing product orders with different user's relatedPartyId, then return that user's orders")
    void listProductOrders_AdminUserRequestingOtherUserId_ReturnsOtherUserOrders() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(true);

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL)
                        .param("relatedParty.id", OTHER_USER_RELATED_PARTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(otherUserProductOrder.getId()));
    }

    // ==================== List Product Orders - Non-Admin Tests ====================

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user, when listing all product orders without filter, then return only user's orders")
    void listProductOrders_NonAdminUser_ReturnsOnlyUserOrders() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.of(USER_RELATED_PARTY_ID));

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(userProductOrder.getId()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user, when listing product orders with their own relatedPartyId, then return their orders")
    void listProductOrders_NonAdminUserWithOwnId_ReturnsUserOrders() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.of(USER_RELATED_PARTY_ID));

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL)
                        .param("relatedParty.id", USER_RELATED_PARTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id").value(userProductOrder.getId()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user, when listing product orders with different relatedPartyId, then return forbidden")
    void listProductOrders_NonAdminUserWithDifferentId_ReturnsForbidden() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.of(USER_RELATED_PARTY_ID));

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL)
                        .param("relatedParty.id", OTHER_USER_RELATED_PARTY_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user without relatedPartyId in token, when listing product orders, then return forbidden")
    void listProductOrders_NonAdminUserWithoutRelatedPartyId_ReturnsForbidden() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.empty());

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL))
                .andExpect(status().isForbidden());
    }

    // ==================== Get Product Order By ID - Admin Tests ====================

    @Test
    @SneakyThrows
    @DisplayName("Given admin user, when getting any product order by ID, then return the order")
    void getProductOrderById_AdminUser_ReturnsOrder() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(true);

        // When & Then - can access any order
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL_ID, userProductOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userProductOrder.getId()));

        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL_ID, otherUserProductOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(otherUserProductOrder.getId()));
    }

    // ==================== Get Product Order By ID - Non-Admin Tests ====================

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user, when getting their own product order by ID, then return the order")
    void getProductOrderById_NonAdminUserOwnOrder_ReturnsOrder() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.of(USER_RELATED_PARTY_ID));

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL_ID, userProductOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userProductOrder.getId()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user, when getting another user's product order by ID, then return forbidden")
    void getProductOrderById_NonAdminUserOtherOrder_ReturnsForbidden() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.of(USER_RELATED_PARTY_ID));

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL_ID, otherUserProductOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user without relatedPartyId in token, when getting product order by ID, then return forbidden")
    void getProductOrderById_NonAdminUserWithoutRelatedPartyId_ReturnsForbidden() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.empty());

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL_ID, userProductOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("Given non-admin user, when getting non-existent product order, then return forbidden (not 404)")
    void getProductOrderById_NonAdminUserNonExistentOrder_ReturnsForbidden() {
        // Given
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.getRelatedPartyId()).thenReturn(Optional.of(USER_RELATED_PARTY_ID));
        String nonExistentId = UUID.randomUUID().toString();

        // When & Then
        restProductOrderMockMvc.perform(get(PRODUCT_ORDER_API_URL_ID, nonExistentId))
                .andExpect(status().isForbidden());
    }

    private ProductOrderEntity createProductOrderWithRelatedParty(String relatedPartyId) {
        RelatedPartyRefOrPartyRoleRefEntity relatedParty = RelatedPartyRefOrPartyRoleRefEntity.builder()
                .partyOrPartyRole(PartyRefEntity.builder()
                        .id(relatedPartyId)
                        .name("Party " + relatedPartyId)
                        .build())
                .role("Customer")
                .build();

        ProductOrderItemEntity productOrderItem = ProductOrderItemEntity.builder()
                .id(UUID.randomUUID().toString())
                .state(ProductOrderItemStateType.DRAFT)
                .action(ItemActionType.ADD)
                .atType("ProductOrderItem")
                .build();

        return ProductOrderEntity.builder()
                .id(UUID.randomUUID().toString())
                .cancellationDate(Instant.ofEpochMilli(0L))
                .href(RandomStringUtils.randomAlphabetic(10))
                .cancellationReason(RandomStringUtils.randomAlphabetic(10))
                .completionDate(Instant.ofEpochMilli(0L))
                .expectedCompletionDate(Instant.ofEpochMilli(0L))
                .category(RandomStringUtils.randomAlphabetic(10))
                .description(RandomStringUtils.randomAlphabetic(10))
                .notificationContact(RandomStringUtils.randomAlphabetic(10))
                .creationDate(Instant.parse("2024-02-13T14:02:00Z"))
                .state(ProductOrderStateType.DRAFT)
                .requestedStartDate(Instant.ofEpochMilli(0L))
                .requestedCompletionDate(Instant.ofEpochMilli(0L))
                .priority(RandomStringUtils.randomAlphabetic(10))
                .atType("ProductOrder")
                .relatedParty(Collections.singletonList(relatedParty))
                .productOrderItem(Collections.singletonList(productOrderItem))
                .build();
    }
}