// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.unit;

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryFactoryRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.scheduler.ShippingOrderStateChangeServiceMockScheduler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.ReactiveDeliveryOrderRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ShippingOrderStateChangeServiceMockScheduler.class})
class ShippingOrderStateChangeServiceMockSchedulerTest {

    @MockBean
    ReactiveDeliveryOrderRepository reactiveDeliveryOrderRepository;

    @MockBean
    EventPublisher eventPublisher;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @Autowired
    private ShippingOrderStateChangeServiceMockScheduler scheduler;

    private Method filterDeliveryOrdersMethod;

    @BeforeEach
    void setup() throws Exception {
        filterDeliveryOrdersMethod = ShippingOrderStateChangeServiceMockScheduler.class
                .getDeclaredMethod("filterDeliveryOrdersThatShouldBeMocked", List.class);
        filterDeliveryOrdersMethod.setAccessible(true);
    }

    // =====================================================
    // filterDeliveryOrdersThatShouldBeMocked
    // =====================================================

    @Test
    void givenEligibleDeliveryOrder_whenFilterDeliveryOrdersThatShouldBeMocked_thenReturnDeliveryOrder() throws Exception {
        DeliveryOrder deliveryOrder = createDeliveryOrder("factory-1", "node-1", "item-1");

        when(nodeDeliveryTimeRepository.findByNodeIdIn(any()))
                .thenReturn(List.of()); // no delivery time → eligible immediately

        List<DeliveryOrder> result =
                (List<DeliveryOrder>) filterDeliveryOrdersMethod.invoke(scheduler, List.of(deliveryOrder));

        assertThat(result).hasSize(1);
    }

    @Test
    void givenDeliveryOrderWithFutureDeliveryTime_whenFilterDeliveryOrdersThatShouldBeMocked_thenNotReturned() throws Exception {
        DeliveryOrder deliveryOrder = createDeliveryOrder("factory-1", "node-1", "item-1");

        NodeDeliveryTimeRepository.NodeDeliveryTime futureTime = NodeDeliveryTimeRepository.NodeDeliveryTime.builder()
                .nodeId("node-1")
                .deliveryTime(Instant.now().plusSeconds(60))
                .build();

        when(nodeDeliveryTimeRepository.findByNodeIdIn(any()))
                .thenReturn(List.of(futureTime));

        List<DeliveryOrder> result =
                (List<DeliveryOrder>) filterDeliveryOrdersMethod.invoke(scheduler, List.of(deliveryOrder));

        assertThat(result).isEmpty();
    }

    @Test
    void givenDeliveryOrderWithPastDeliveryTime_whenFilterDeliveryOrdersThatShouldBeMocked_thenReturned() throws Exception {
        DeliveryOrder deliveryOrder = createDeliveryOrder("factory-1", "node-1", "item-1");

        NodeDeliveryTimeRepository.NodeDeliveryTime pastTime = NodeDeliveryTimeRepository.NodeDeliveryTime.builder()
                .nodeId("node-1")
                .deliveryTime(Instant.now().minusSeconds(60))
                .build();

        when(nodeDeliveryTimeRepository.findByNodeIdIn(any()))
                .thenReturn(List.of(pastTime));

        List<DeliveryOrder> result =
                (List<DeliveryOrder>) filterDeliveryOrdersMethod.invoke(scheduler, List.of(deliveryOrder));

        assertThat(result).hasSize(1);
    }

    @Test
    void givenEmptyDeliveryOrderList_whenFilterDeliveryOrdersThatShouldBeMocked_thenReturnEmpty() throws Exception {
        when(nodeDeliveryTimeRepository.findByNodeIdIn(any()))
                .thenReturn(List.of());

        List<DeliveryOrder> result =
                (List<DeliveryOrder>) filterDeliveryOrdersMethod.invoke(scheduler, List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void givenNonShomDeliveryOrder_whenFilterDeliveryOrdersThatShouldBeMocked_thenNotReturned() throws Exception {
        DeliveryOrder deliveryOrder = createDeliveryOrder("factory-1", "node-1", "item-1",
                DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT);

        when(nodeDeliveryTimeRepository.findByNodeIdIn(any()))
                .thenReturn(List.of());

        List<DeliveryOrder> result =
                (List<DeliveryOrder>) filterDeliveryOrdersMethod.invoke(scheduler, List.of(deliveryOrder));

        assertThat(result).isEmpty();
    }

    // Utility methods for creating test data
    private DeliveryOrder createDeliveryOrder(String factoryOrderId, String nodeId, String factoryItemId) {
        return createDeliveryOrder(factoryOrderId, nodeId, factoryItemId,
                DeliveryFactoryRef.DeliveryFactoryEnum.SHIPPING_ORDER_MANAGEMENT);
    }

    private DeliveryOrder createDeliveryOrder(String factoryOrderId, String nodeId, String factoryItemId,
                                              DeliveryFactoryRef.DeliveryFactoryEnum factoryType) {
        OrderItemRef ref = OrderItemRef.builder()
                .productOrderItemId("product-item-1")
                .factoryOrderItemId(factoryItemId)
                .orchestrationNodeId(nodeId)
                .build();
        return DeliveryOrder.builder()
                .id("id-" + factoryOrderId)
                .factoryOrderId(factoryOrderId)
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(factoryType)
                        .build())
                .orderItemRef(List.of(ref))
                .build();
    }
}
