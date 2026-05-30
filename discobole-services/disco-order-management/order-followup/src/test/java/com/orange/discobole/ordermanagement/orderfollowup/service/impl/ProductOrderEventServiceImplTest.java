// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.impl;

import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributePayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderAttributeValueChangeEvent;
import com.orange.discobole.ordermanagement.orderfollowup.IntegrationTest;
import com.orange.discobole.ordermanagement.orderfollowup.domain.ProductOrderItemEntity;
import com.orange.discobole.ordermanagement.orderfollowup.domain.ProductOrderItemStateChangedEvent;
import com.orange.discobole.ordermanagement.orderfollowup.repository.ProductOrderEventRepository;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import com.orange.discobole.processflow.dto.generated.Links;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import com.orange.discobole.processflow.exception.TaskFlowNotFoundException;
import com.orange.discobole.processflow.service.ProcessFlowCommandService;
import com.orange.discobole.processflow.service.TaskFLowCommandService;
import org.apache.commons.lang3.RandomStringUtils;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.orange.discobole.ordermanagement.orderfollowup.constant.FollowUpConstants.NEW_PRODUCT_ORDER_ITEM_STATE_CHANGE_EVENT_TITLE;
import static com.orange.discobole.ordermanagement.orderfollowup.enums.OfupStateType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@IntegrationTest
class ProductOrderEventServiceImplTest {
    public static final Instant EVENT_TIME = Instant.now();
    public static final String INVALID_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PROCESS_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String TASK_FLOW_SPECIFICATION_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String DEFAULT_PRODUCT_ORDER_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String DEFAULT_PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String TASK_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String EVENT_ID = RandomStringUtils.randomAlphabetic(8);
    public static final String EVENT_ID_2 = RandomStringUtils.randomAlphabetic(8);
    public static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(8);
    private static final String DEFAULT_RELATED_PARTY_ID = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_RELATED_PARTY_NAME = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_RELATED_PARTY_ROLE = RandomStringUtils.randomAlphabetic(5);
    private static final String DEFAULT_RELATED_PARTY_REFERRED_TYPE = RandomStringUtils.randomAlphabetic(5);

    @Autowired
    private ProductOrderEventRepository productOrderEventRepository;
    @MockBean
    private ProcessFlowCommandService processFlowService;
    @MockBean
    private TaskFLowCommandService taskFlowService;
    @Autowired
    private ProductOrderEventServiceImpl productOrderEventService;

    @AfterEach
    void clearDb() {
        productOrderEventRepository.deleteAll();
    }

    @DisplayName("Given a non-existent product order ID, " +
            "when finding a product order event by ID, " +
            "then return an empty response")
    @Test
    void shouldReturnEmptyResponseWhenProductOrderIdDoesNotExist() {
        // Given & When
        Optional<ProductOrderItemStateChangedEvent> result = productOrderEventService.findByProductOrderId(INVALID_PRODUCT_ORDER_ID);

        // Then
        Assertions.assertEquals(Optional.empty(), result);
    }

    @DisplayName("Given a product order event, " +
            "when finding a product order event by ID, " +
            "then return the product order event")
    @Test
    void shouldReturnProductOrderEventWhenProductOrderIdExists() {
        // Given
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = createDefaultProductOrderItemStateChangedEvent();
        ProductOrderItemStateChangedEvent createdProductOrderItemStateChangedEvent = productOrderEventRepository.save(productOrderItemStateChangedEvent);

        // When
        Optional<ProductOrderItemStateChangedEvent> result = productOrderEventService.findByProductOrderId(PRODUCT_ORDER_ID);

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(createdProductOrderItemStateChangedEvent.getProductOrderId(), result.get().getProductOrderId());
        Assertions.assertEquals(createdProductOrderItemStateChangedEvent.getProductOrderState(), result.get().getProductOrderState());
        Assertions.assertEquals(createdProductOrderItemStateChangedEvent.getProcessId(), result.get().getProcessId());
        Assertions.assertEquals(createdProductOrderItemStateChangedEvent.getNextTaskToBePerformed(), result.get().getNextTaskToBePerformed());
    }

    @DisplayName("Given an exception thrown by processFlowService, " +
            "when creating a product order event, " +
            "then return null")
    @Test
    void shouldReturnNullWhenProcessFlowServiceThrowsException() {
        // Given
        when(processFlowService.createProcessFlow(any())).thenThrow(RuntimeException.class);
        ProductOrder productOrder = createProductOrder(ProductOrderItemStateType.COMPLETED, DEFAULT_PRODUCT_ORDER_ITEM_ID);

        // When
        ProductOrderItemStateChangedEvent result = productOrderEventService.createProductOrderItemStateChangedEvent(productOrder, EVENT_TIME);

        // Then
        Assertions.assertNull(result);
    }

    @DisplayName("Given no task to be performed, " +
            "when creating a product order event, " +
            "then return null")
    @Test
    void shouldReturnNullWhenNoTaskToBePerformed() {
        // Given
        ProcessFlow processFlow = createTaskFlowProcess();
        processFlow.setLinks(null);
        when(processFlowService.createProcessFlow(any())).thenReturn(processFlow);
        ProductOrder productOrder = createProductOrder(ProductOrderItemStateType.COMPLETED, DEFAULT_PRODUCT_ORDER_ITEM_ID);

        // When
        ProductOrderItemStateChangedEvent result = productOrderEventService.createProductOrderItemStateChangedEvent(productOrder, EVENT_TIME);

        // Then
        Assertions.assertNull(result);
    }

    @DisplayName("Given a productOrder DTO, " +
            "when creating a product order event, " +
            "then return the created ProductOrderItemStateChangedEvent")
    @Test
    void shouldReturnCreatedProductOrderItemStateChangedEvent() {
        // Given
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();
        ProcessFlow processFlow = createTaskFlowProcess();
        when(processFlowService.createProcessFlow(any())).thenReturn(processFlow);
        ProductOrder productOrder = createProductOrder(ProductOrderItemStateType.COMPLETED, DEFAULT_PRODUCT_ORDER_ITEM_ID);

        // When
        productOrderEventService.createProductOrderItemStateChangedEvent(productOrder, EVENT_TIME);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate + 1);
        ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderId()).isEqualTo(PRODUCT_ORDER_ID);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderState()).isEqualTo(ProductOrderStateType.ACCEPTED);
        assertThat(testProductOrderItemStateChangedEvent.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(testProductOrderItemStateChangedEvent.getNextTaskToBePerformed()).isEqualTo(TASK_ID);
    }

    @DisplayName("Given an already created ProductOrderItemStateChangedEvent, " +
            "when creating a product order event, " +
            "then return the created ProductOrderItemStateChangedEvent")
    @Test
    void shouldReturnCreatedProductOrderItemStateChangedEventWhenAlreadyCreated() {
        // Given
        ProductOrder productOrder = createProductOrder(ProductOrderItemStateType.COMPLETED, DEFAULT_PRODUCT_ORDER_ITEM_ID);

        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = createDefaultProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();

        // When
        productOrderEventService.createProductOrderItemStateChangedEvent(productOrder, EVENT_TIME);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate);
        ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderId()).isEqualTo(PRODUCT_ORDER_ID);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderState()).isEqualTo(ProductOrderStateType.ACCEPTED);
        assertThat(testProductOrderItemStateChangedEvent.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(testProductOrderItemStateChangedEvent.getNextTaskToBePerformed()).isEqualTo(TASK_FLOW_SPECIFICATION_ID);
    }

    @DisplayName("Given no ProductOrderItemStateChangedEvent in DB, " +
            "when adding a new product order item event, " +
            "then create a new ProductOrderItemStateChangedEvent in DB, " +
            "and add the new ProductOrderItemEntity")
    @Test
    void shouldCreateNewProductOrderItemStateChangedEventWhenNoneExistsInDB() {
        // Given
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();
        ProcessFlow processFlow = createTaskFlowProcess();
        when(processFlowService.createProcessFlow(any())).thenReturn(processFlow);

        // When
        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = createProductOrderItemEvent(ProductOrderItemStateType.ACCEPTED, DEFAULT_PRODUCT_ORDER_ITEM_ID, EVENT_ID);
        productOrderEventService.addProductOrderItem(productOrderAttributeValueChangeEvent);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate + 1);
        ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderId()).isEqualTo(PRODUCT_ORDER_ID);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderState()).isEqualTo(ProductOrderStateType.ACCEPTED);
        assertThat(testProductOrderItemStateChangedEvent.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(testProductOrderItemStateChangedEvent.getNextTaskToBePerformed()).isEqualTo(TASK_ID);

        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("productOrderItemId", is(DEFAULT_PRODUCT_ORDER_ITEM_ID))));
        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("state", is(ProductOrderItemStateType.ACCEPTED))));
    }

    @DisplayName("Given a ProductOrderItemStateChangedEvent in DB, " +
            "when adding a new product order item event, " +
            "then update the existing ProductOrderItemStateChangedEvent in DB " +
            "and add the new ProductOrderItemEntity")
    @Test
    void shouldUpdateProductOrderItemStateChangedEventAndAddNewProductOrderItemEntity() {
        // Given
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = createDefaultProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();

        // When
        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = createProductOrderItemEvent(ProductOrderItemStateType.COMPLETED, DEFAULT_PRODUCT_ORDER_ITEM_ID, EVENT_ID_2);
        productOrderEventService.addProductOrderItem(productOrderAttributeValueChangeEvent);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate);
        ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderId()).isEqualTo(PRODUCT_ORDER_ID);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderState()).isEqualTo(ProductOrderStateType.ACCEPTED);
        assertThat(testProductOrderItemStateChangedEvent.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(testProductOrderItemStateChangedEvent.getNextTaskToBePerformed()).isEqualTo(TASK_FLOW_SPECIFICATION_ID);

        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("productOrderItemId", is(DEFAULT_PRODUCT_ORDER_ITEM_ID))));
        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("state", is(ProductOrderItemStateType.COMPLETED))));
    }

    @DisplayName("Given no ProductOrderItemStateChangedEvent in DB, " +
            "when adding a new product order item event, " +
            "then create a new ProductOrderItemStateChangedEvent in DB " +
            "and add the new ProductOrderItemEntity")
    @Test
    void shouldCreateNewProductOrderItemStateChangedEventAndAddNewProductOrderItemEntity() {
        // Given
        ProcessFlow processFlow = createTaskFlowProcess();
        when(processFlowService.createProcessFlow(any())).thenReturn(processFlow);
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();

        // When
        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = createProductOrderItemEvent(ProductOrderItemStateType.ACCEPTED, DEFAULT_PRODUCT_ORDER_ITEM_ID, EVENT_ID_2);
        productOrderEventService.addProductOrderItem(productOrderAttributeValueChangeEvent);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate);
        ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderId()).isEqualTo(PRODUCT_ORDER_ID);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderState()).isEqualTo(ProductOrderStateType.ACCEPTED);
        assertThat(testProductOrderItemStateChangedEvent.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(testProductOrderItemStateChangedEvent.getNextTaskToBePerformed()).isEqualTo(TASK_FLOW_SPECIFICATION_ID);

        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("productOrderItemId", is(PRODUCT_ORDER_ITEM_ID)),
                hasProperty("productOrderItemId", is(DEFAULT_PRODUCT_ORDER_ITEM_ID))));
        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("state", is(ProductOrderItemStateType.COMPLETED)),
                hasProperty("state", is(ProductOrderItemStateType.ACCEPTED))));
    }


    @DisplayName("Given existing ProductOrderItemStateChangedEvent, " +
            "when adding duplicate event, " +
            " then no new event is created")

    @ParameterizedTest
    @MethodSource("invalidEvent")
    void shouldNotCreateNewProductOrderItemStateChangedEventAndNotAddNewProductOrderItemEntity(ProductOrderItemStateType orderItemStateType, String orderItemId, String eventId) {
        // Given
        ProcessFlow processFlow = createTaskFlowProcess();
        when(processFlowService.createProcessFlow(any())).thenReturn(processFlow);
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();

        // When
        ProductOrderAttributeValueChangeEvent productOrderAttributeValueChangeEvent = createProductOrderItemEvent(orderItemStateType, orderItemId, eventId);
        productOrderEventService.addProductOrderItem(productOrderAttributeValueChangeEvent);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate);
        ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderId()).isEqualTo(PRODUCT_ORDER_ID);
        assertThat(testProductOrderItemStateChangedEvent.getProductOrderState()).isEqualTo(ProductOrderStateType.ACCEPTED);
        assertThat(testProductOrderItemStateChangedEvent.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(testProductOrderItemStateChangedEvent.getNextTaskToBePerformed()).isEqualTo(TASK_FLOW_SPECIFICATION_ID);

        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("productOrderItemId", is(PRODUCT_ORDER_ITEM_ID))));
        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("state", is(ProductOrderItemStateType.COMPLETED))));
    }

    @DisplayName("Given no ProductOrderItemStateChangedEvent in DB, " +
            "when adding a new product order item event with null, " +
            "then throw an exception")
    @Test
    void shouldThrowExceptionWhenAddingNullProductOrderItemEvent() {
        // Given
        ProcessFlow processFlow = createTaskFlowProcess();
        when(processFlowService.createProcessFlow(any())).thenReturn(processFlow);
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);

        // When & Then
        assertThrows(Exception.class, () -> productOrderEventService.addProductOrderItem(null));
    }

    @DisplayName("Given an exception thrown by taskFlowService, " +
            "when executing ReceiveNewProductStateChangeEventTaskFlow, " +
            "then set ofupState to NEW")
    @Test
    void shouldSetOfupStateToNewWhenTaskFlowServiceThrowsException() {
        // Given
        when(taskFlowService.updateTaskFlow(any(), any(), any())).thenThrow(RuntimeException.class);
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();

        // When
        productOrderEventService.executeReceiveNewProductStateChangeEventTaskFlow(productOrderItemStateChangedEvent);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate);
        ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);

        org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                hasProperty("ofupState", is(NEW))));
    }

    @DisplayName("Given a TaskFlowNotFoundException thrown by taskFlowService, " +
            "when executing ReceiveNewProductStateChangeEventTaskFlow, " +
            "then set ofupState to DEPRECATED")
    @Test
    void shouldSetOfupStateToDeprecatedWhenTaskFlowNotFoundExceptionIsThrown() {
        // Given
        when(taskFlowService.updateTaskFlow(any(), any(), any())).thenThrow(TaskFlowNotFoundException.class);
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();

        // When
        productOrderEventService.executeReceiveNewProductStateChangeEventTaskFlow(productOrderItemStateChangedEvent);

        // Then
        await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
            List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
            assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate);
            ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);

            org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                    hasProperty("ofupState", is(DEPRECATED))));
        });
    }

    @DisplayName("Given a RuntimeException thrown by taskFlowService, " +
            "when executing ReceiveNewProductStateChangeEventTaskFlow, " +
            "then set ofupState to NEW")
    @Test
    void shouldSetOfupStateToNewWhenRuntimeExceptionIsThrown() {
        // Given
        when(taskFlowService.updateTaskFlow(any(), any(), any())).thenThrow(RuntimeException.class);
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeCreate = productOrderEventRepository.findAll().size();

        // When
        productOrderEventService.executeReceiveNewProductStateChangeEventTaskFlow(productOrderItemStateChangedEvent);

        // Then
        await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> {
            List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
            assertThat(productOrderEventList).hasSize(databaseSizeBeforeCreate);
            ProductOrderItemStateChangedEvent testProductOrderItemStateChangedEvent = productOrderEventList.get(productOrderEventList.size() - 1);

            org.hamcrest.MatcherAssert.assertThat(testProductOrderItemStateChangedEvent.getProductOrderItems(), contains(
                    hasProperty("ofupState", is(NEW))));
        });
    }

    @DisplayName("Given a product order ID, " +
            "when deleting a ProductOrderItemStateChangedEvent, " +
            "then the ProductOrderItemStateChangedEvent is deleted")
    @Test
    void shouldDeleteProductOrderItemStateChangedEventWhenProductOrderIdIsProvided() {
        // Given
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        int databaseSizeBeforeDelete = productOrderEventRepository.findAll().size();

        // When
        productOrderEventService.deleteByProductOrderId(PRODUCT_ORDER_ID);

        // Then
        List<ProductOrderItemStateChangedEvent> productOrderEventList = productOrderEventRepository.findAll();
        assertThat(productOrderEventList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @DisplayName("Given ProductOrderItemStateChangedEvents with different states, " +
            "when retrieving new ProductOrderItemStateChangedEvent, " +
            "then return the ProductOrderItemStateChangedEvent with 'NEW' state")
    @Test
    void shouldReturnNewProductOrderItemStateChangedEvent() {
        // Given
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = getProductOrderItemStateChangedEvent();
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent2 = getProductOrderItemStateChangedEvent();
        productOrderItemStateChangedEvent2.setProductOrderId(DEFAULT_PRODUCT_ORDER_ID);

        ProductOrderItemEntity productOrderItem = ProductOrderItemEntity.builder()
                .productOrderItemId(PRODUCT_ORDER_ITEM_ID)
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .state(ProductOrderItemStateType.COMPLETED)
                .ofupState(IN_PROGRESS)
                .build();

        productOrderItemStateChangedEvent2.setProductOrderItems(Collections.singletonList(productOrderItem));

        productOrderEventRepository.save(productOrderItemStateChangedEvent);
        productOrderEventRepository.save(productOrderItemStateChangedEvent2);

        // When
        List<ProductOrderItemStateChangedEvent> productOrderItemStateChangedEvents = productOrderEventService.getNewProductOrderItemEvents();

        // Then
        assertThat(productOrderItemStateChangedEvents).isNotEmpty();
        org.hamcrest.MatcherAssert.assertThat(productOrderItemStateChangedEvents.get(0).getProductOrderItems(), contains(
                hasProperty("ofupState", is(NEW))));
    }

    private ProductOrderItemStateChangedEvent getProductOrderItemStateChangedEvent() {
        ProductOrderItemStateChangedEvent productOrderItemStateChangedEvent = createDefaultProductOrderItemStateChangedEvent();
        ProductOrderItemEntity productOrderItem = ProductOrderItemEntity.builder()
                .productOrderItemId(PRODUCT_ORDER_ITEM_ID)
                .eventId(EVENT_ID)
                .eventTime(EVENT_TIME)
                .state(ProductOrderItemStateType.COMPLETED)
                .ofupState(NEW).build();

        productOrderItemStateChangedEvent.setProductOrderItems(Collections.singletonList(productOrderItem));
        return productOrderItemStateChangedEvent;
    }

    private ProcessFlow createTaskFlowProcess() {
        ProcessFlow processFlow = new ProcessFlow();
        processFlow.setId(PROCESS_ID);
        TaskLink taskLink = new TaskLink();
        taskLink.setTitle(NEW_PRODUCT_ORDER_ITEM_STATE_CHANGE_EVENT_TITLE);
        taskLink.setTaskFlowSpecificationId(TASK_ID);
        Links links = new Links();
        links.setNextTaskstoBePerformed(Collections.singletonList(taskLink));
        processFlow.setLinks(links);
        return processFlow;
    }

    private ProductOrderItemStateChangedEvent createDefaultProductOrderItemStateChangedEvent() {
        return ProductOrderItemStateChangedEvent
                .builder()
                .productOrderId(PRODUCT_ORDER_ID)
                .productOrderState(ProductOrderStateType.ACCEPTED)
                .processId(PROCESS_ID)
                .nextTaskToBePerformed(TASK_FLOW_SPECIFICATION_ID)
                .build();
    }

    private ProductOrder createProductOrder(ProductOrderItemStateType itemStateType, String orderItemId) {
        ProductOrderItem defaultOrderItem = ProductOrderItem.builder()
                .id(orderItemId)
                .state(itemStateType)
                .atType("ProductOrderItem")
                .build();

        PartyRef partyRef = PartyRef.builder()
                .id(DEFAULT_RELATED_PARTY_ID)
                .name(DEFAULT_RELATED_PARTY_NAME)
                .atReferredType(DEFAULT_RELATED_PARTY_REFERRED_TYPE)
                .atType("PartyRef")
                .build();

        RelatedPartyRefOrPartyRoleRef relatedParty = RelatedPartyRefOrPartyRoleRef.builder()
                .role(DEFAULT_RELATED_PARTY_ROLE)
                .partyOrPartyRole(partyRef)
                .build();

        return ProductOrder.builder()
                .id(PRODUCT_ORDER_ID)
                .state(ProductOrderStateType.ACCEPTED)
                .productOrderItem(List.of(defaultOrderItem))
                .relatedParty(List.of(relatedParty))
                .atType("ProductOrder")
                .build();
    }

    private ProductOrderAttributeValueChangeEvent createProductOrderItemEvent(ProductOrderItemStateType itemStateType, String orderItemId, String eventId) {
        ProductOrder productOrder = createProductOrder(itemStateType, orderItemId);

        ProductOrderAttributePayloadEvent productOrderAttributePayloadEvent = ProductOrderAttributePayloadEvent.builder()
                .productOrder(productOrder)
                .build();

        return ProductOrderAttributeValueChangeEvent.builder()
                .event(productOrderAttributePayloadEvent)
                .eventId(eventId)
                .build();
    }

    private static Stream<Arguments> invalidEvent() {
        return Stream.of(
                Arguments.of(ProductOrderItemStateType.INPROGRESS, DEFAULT_PRODUCT_ORDER_ITEM_ID, EVENT_ID),
                Arguments.of(ProductOrderItemStateType.COMPLETED, PRODUCT_ORDER_ITEM_ID, EVENT_ID_2)
        );
    }
}