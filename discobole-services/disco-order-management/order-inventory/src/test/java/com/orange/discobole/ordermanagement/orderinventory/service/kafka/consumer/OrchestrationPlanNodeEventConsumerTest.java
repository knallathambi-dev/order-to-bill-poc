// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.kafka.consumer;

import com.orange.discobole.ordermanagement.commons.enumeration.EventType;
import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderItemStateType;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrderItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchestrationPlanNodeEventConsumerTest {
    public static final String PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);
    public static final String SECOND_PRODUCT_ORDER_ITEM_ID = RandomStringUtils.randomAlphabetic(10);

    @Mock
    private ProductOrderService productOrderService;

    @InjectMocks
    private OrchestrationPlanNodeEventConsumer orchestrationPlanNodeEventConsumer;

    @Test
    @DisplayName("Given an event that is not an orchestration plan node state change event, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenEventTypeIsIncorrect() {
        // Given
        OrchestrationPlanNodeStateChangeEvent orchestrationPlanNodeEvent = OrchestrationPlanNodeStateChangeEvent
                .builder()
                .eventType(EventType.PRODUCT_ORDER_STATE_CHANGE_EVENT.getValue())
                .build();
        Message<OrchestrationPlanNodeStateChangeEvent> message = MessageBuilder.withPayload(orchestrationPlanNodeEvent).build();

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given a null event, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenEventIsNull() {
        // Given
        OrchestrationPlanNodeStateChangeEvent orchestrationPlanNodeEvent = OrchestrationPlanNodeStateChangeEvent
                .builder()
                .eventType(EventType.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT.getValue())
                .event(null)
                .build();
        Message<OrchestrationPlanNodeStateChangeEvent> message = MessageBuilder.withPayload(orchestrationPlanNodeEvent).build();

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given a null orchestration plan node, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenOrchestrationPlanNodeIsNull() {
        // Given
        OrchestrationPlanNodeStateChangePayloadEvent orchestrationPlanNodePayloadEvent = OrchestrationPlanNodeStateChangePayloadEvent
                .builder()
                .orchestrationPlanNode(null)
                .build();
        OrchestrationPlanNodeStateChangeEvent orchestrationPlanNodeEvent = OrchestrationPlanNodeStateChangeEvent
                .builder()
                .eventType(EventType.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT.getValue())
                .event(orchestrationPlanNodePayloadEvent)
                .build();
        Message<OrchestrationPlanNodeStateChangeEvent> message = MessageBuilder.withPayload(orchestrationPlanNodeEvent).build();

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given a null related product order, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenRelatedProductOrderIsNull() {
        // Given
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNode.builder().relatedProductOrder(null).build();
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given a null related product order ID, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenRelatedProductOrderIdIsNull() {
        // Given
        RelatedProductOrder productOrder = new RelatedProductOrder();
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNode.builder().relatedProductOrder(productOrder).build();
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given a null related product order item, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenRelatedProductOrderItemIsNull() {
        // Given
        RelatedProductOrder productOrder = new RelatedProductOrder();
        productOrder.setId(PRODUCT_ID);
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNode
                .builder()
                .relatedProductOrder(productOrder)
                .relatedProductOrderItem(null)
                .build();
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given null related product order item IDs, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenRelatedProductOrderItemIdsAreNull() {
        // Given
        RelatedProductOrder productOrder = new RelatedProductOrder();
        productOrder.setId(PRODUCT_ID);
        RelatedProductOrderItem productOrderItem = new RelatedProductOrderItem();
        List<RelatedProductOrderItem> productOrderItems = List.of(productOrderItem);
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNode
                .builder()
                .relatedProductOrder(productOrder)
                .relatedProductOrderItem(productOrderItems)
                .build();
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given a null state, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenStateIsNull() {
        // Given
        OrchestrationPlanNode orchestrationPlanNode = createOrchestrationPlanNode(null);
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @Test
    @DisplayName("Given an invalid orchestration plan state, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenOrchestrationPlanStateIsInvalid() {
        // Given
        OrchestrationPlanNode orchestrationPlanNode = createOrchestrationPlanNode(OrchestrationPlanNodeState.IN_DELIVERY);
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = OrchestrationPlanNodeState.class, names = {"IN_PROGRESS", "HELD", "COMPLETED", "ABORTED", "FAILED"})
    @DisplayName("Given a valid orchestrationPlanNodeState, " +
            "when consumed, " +
            "then update product order hierarchy accordingly")
    void shouldUpdateProductOrderHierarchyForValidStates(OrchestrationPlanNodeState orchestrationPlanNodeState) {
        // Given
        ProductOrderItemStateType expectedStateType = switch (orchestrationPlanNodeState) {
            case IN_PROGRESS -> ProductOrderItemStateType.INPROGRESS;
            case HELD -> ProductOrderItemStateType.HELD;
            case COMPLETED -> ProductOrderItemStateType.COMPLETED;
            case ABORTED, FAILED -> ProductOrderItemStateType.FAILED;
            default ->
                    throw new IllegalArgumentException("Unexpected orchestrationPlanNodeState: " + orchestrationPlanNodeState);
        };

        OrchestrationPlanNode orchestrationPlanNode = createOrchestrationPlanNode(orchestrationPlanNodeState);
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, times(2))
                .updateProductOrderHierarchy(eq(PRODUCT_ID), anyString(), eq(expectedStateType));
    }

    @Test
    @DisplayName("Given an invalid state, " +
            "when consumed, " +
            "then do not update product order hierarchy")
    void shouldNotUpdateProductOrderHierarchyWhenStateIsInvalid() {
        // Given
        OrchestrationPlanNode orchestrationPlanNode = createOrchestrationPlanNode(OrchestrationPlanNodeState.INITIALIZED);
        Message<OrchestrationPlanNodeStateChangeEvent> message = createMessage(orchestrationPlanNode);

        // When
        orchestrationPlanNodeEventConsumer.accept(message);

        // Then
        verify(productOrderService, never()).updateProductOrderHierarchy(any(), any(), any());
    }

    private OrchestrationPlanNode createOrchestrationPlanNode(OrchestrationPlanNodeState orchestrationPlanNodeState) {
        RelatedProductOrder productOrder = new RelatedProductOrder();
        productOrder.setId(PRODUCT_ID);

        RelatedProductOrderItem firstProductOrderItem = new RelatedProductOrderItem();
        RelatedProductOrderItem secondProductOrderItem = new RelatedProductOrderItem();
        firstProductOrderItem.setId(PRODUCT_ORDER_ITEM_ID);
        secondProductOrderItem.setId(SECOND_PRODUCT_ORDER_ITEM_ID);
        List<RelatedProductOrderItem> productOrderItems = List.of(firstProductOrderItem, secondProductOrderItem);

        return OrchestrationPlanNode.builder()
                .relatedProductOrder(productOrder)
                .relatedProductOrderItem(productOrderItems)
                .state(orchestrationPlanNodeState)
                .build();
    }

    private Message<OrchestrationPlanNodeStateChangeEvent> createMessage(OrchestrationPlanNode orchestrationPlanNode) {
        OrchestrationPlanNodeStateChangePayloadEvent orchestrationPlanNodePayloadEvent = OrchestrationPlanNodeStateChangePayloadEvent.builder()
                .orchestrationPlanNode(orchestrationPlanNode)
                .build();

        OrchestrationPlanNodeStateChangeEvent orchestrationPlanNodeEvent = OrchestrationPlanNodeStateChangeEvent.builder()
                .eventType(EventType.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT.getValue())
                .event(orchestrationPlanNodePayloadEvent)
                .build();

        return MessageBuilder.withPayload(orchestrationPlanNodeEvent).build();
    }
}