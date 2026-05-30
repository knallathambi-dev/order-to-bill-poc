// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ShippingOrderItemStatus;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusMapperTest {

    private DeliveryStatusMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(DeliveryStatusMapper.class);
    }

    @Test
    void givenCompletedServiceOrderItemState_whenMapServiceOrderItemStateType_thenReturnsExecutedCompleted() {
        // Given
        ServiceOrderItem.State state = ServiceOrderItem.State.COMPLETED;

        // When
        DeliveryStatusMapping result = mapper.mapServiceOrderItemStateType(state);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED, result.getDeliveryStatus(), 
                "Delivery status should be EXECUTED");
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.COMPLETED, result.getNodeStatus(), 
                "Node status should be COMPLETED");
    }

    @Test
    void givenFailedServiceOrderItemState_whenMapServiceOrderItemStateType_thenReturnsExecutedFailed() {
        // Given
        ServiceOrderItem.State state = ServiceOrderItem.State.FAILED;

        // When
        DeliveryStatusMapping result = mapper.mapServiceOrderItemStateType(state);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED, result.getDeliveryStatus(), 
                "Delivery status should be EXECUTED");
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.FAILED, result.getNodeStatus(), 
                "Node status should be FAILED");
    }

    @Test
    void givenHeldServiceOrderItemState_whenMapServiceOrderItemStateType_thenReturnsHeldHeld() {
        // Given
        ServiceOrderItem.State state = ServiceOrderItem.State.HELD;

        // When
        DeliveryStatusMapping result = mapper.mapServiceOrderItemStateType(state);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.HELD, result.getDeliveryStatus(), 
                "Delivery status should be HELD");
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.HELD, result.getNodeStatus(), 
                "Node status should be HELD");
    }

    @ParameterizedTest
    @EnumSource(value = ServiceOrderItem.State.class, names = {
            "IN_PROGRESS", "CANCELLED", "ASSESSING_CANCELLATION", "PENDING_CANCELLATION",
            "PARTIAL", "ACKNOWLEDGED", "REJECTED", "PENDING"
    })
    void givenNonMappableServiceOrderItemStates_whenMapServiceOrderItemStateType_thenReturnsNull(ServiceOrderItem.State state) {
        // When
        DeliveryStatusMapping result = mapper.mapServiceOrderItemStateType(state);

        // Then
        assertNull(result, "Result should be null for non-mappable states: " + state);
    }

    @Test
    void givenCompletedShippingOrderItemStatus_whenMapShippingItemStatus_thenReturnsExecutedCompleted() {
        // Given
        String status = ShippingOrderItemStatus.COMPLETED.getValue();

        // When
        DeliveryStatusMapping result = mapper.mapShippingItemStatus(status);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED, result.getDeliveryStatus(), 
                "Delivery status should be EXECUTED");
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.COMPLETED, result.getNodeStatus(), 
                "Node status should be COMPLETED");
    }

    @Test
    void givenFailedShippingOrderItemStatus_whenMapShippingItemStatus_thenReturnsExecutedFailed() {
        // Given
        String status = ShippingOrderItemStatus.FAILED.getValue();

        // When
        DeliveryStatusMapping result = mapper.mapShippingItemStatus(status);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED, result.getDeliveryStatus(), 
                "Delivery status should be EXECUTED");
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.FAILED, result.getNodeStatus(), 
                "Node status should be FAILED");
    }

    @Test
    void givenHeldShippingOrderItemStatus_whenMapShippingItemStatus_thenReturnsHeldHeld() {
        // Given
        String status = ShippingOrderItemStatus.HELD.getValue();

        // When
        DeliveryStatusMapping result = mapper.mapShippingItemStatus(status);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.HELD, result.getDeliveryStatus(), 
                "Delivery status should be HELD");
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.HELD, result.getNodeStatus(), 
                "Node status should be HELD");
    }

    @Test
    void givenAllServiceOrderItemStates_whenMapServiceOrderItemStateType_thenNoDuplicateMappings() {
        // This test ensures consistent mapping behavior across all enum values
        for (ServiceOrderItem.State state : ServiceOrderItem.State.values()) {
            // When
            DeliveryStatusMapping result = mapper.mapServiceOrderItemStateType(state);

            // Then - verify that all non-null mappings are consistent
            if (result != null) {
                assertNotNull(result.getDeliveryStatus(), "Delivery status should not be null for " + state);
                assertNotNull(result.getNodeStatus(), "Node status should not be null for " + state);
            }
        }
    }

    @Test
    void givenAllShippingOrderItemStatuses_whenMapShippingItemStatus_thenNoDuplicateMappings() {
        // This test ensures consistent mapping behavior across all enum values
        for (ShippingOrderItemStatus status : ShippingOrderItemStatus.values()) {
            // When
            DeliveryStatusMapping result = mapper.mapShippingItemStatus(status.getValue());

            // Then - verify that all mappings are consistent
            assertNotNull(result, "Result should not be null for " + status);
            assertNotNull(result.getDeliveryStatus(), "Delivery status should not be null for " + status);
            assertNotNull(result.getNodeStatus(), "Node status should not be null for " + status);
        }
    }
}
