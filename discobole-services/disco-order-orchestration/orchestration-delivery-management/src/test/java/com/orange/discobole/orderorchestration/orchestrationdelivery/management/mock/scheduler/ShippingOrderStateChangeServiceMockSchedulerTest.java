// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.scheduler;


import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ShippingOrderItemStatus;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryFactoryRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.util.ShippingOrderDtoBuilderUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.ReactiveDeliveryOrderRepository;
import reactor.core.publisher.Flux;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ShippingOrderStateChangeServiceMockScheduler.class}, initializers = {ShippingOrderStateChangeServiceMockSchedulerTest.ConversionInitializer.class})
class ShippingOrderStateChangeServiceMockSchedulerTest {

    @MockBean
    private ReactiveDeliveryOrderRepository reactiveDeliveryOrderRepository;

    @MockBean
    private EventPublisher eventPublisher;

    @Autowired
    private ShippingOrderStateChangeServiceMockScheduler scheduler;

    @MockBean
    NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @BeforeEach
    void resetMocks() {
        reset(eventPublisher);
        scheduler.setEnabled(true);
    }

    @Test
    void givenNoDeliveryOrders_whenMockShippingOrderStateChange_thenNoEventsPublished() {
        // Given
        when(reactiveDeliveryOrderRepository.findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull()).thenReturn(Flux.empty());

        // When
        scheduler.mockShippingOrderStateChange();

        // Then
        verify(reactiveDeliveryOrderRepository).findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull();
        verify(eventPublisher, never()).publishEvents(any(), any());
    }


    @Test
    void givenMockableDeliveryOrderNotPublished_whenMockShippingOrderStateChange_thenEventPublished() {
        // Given
        DeliveryOrder deliveryOrder = createDeliveryOrder("factory-order-1", createOrderItemRefWithNullStatus("node-1", "item-1"));
        when(reactiveDeliveryOrderRepository.findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull()).thenReturn(Flux.just(deliveryOrder));
        when(nodeDeliveryTimeRepository.findByNodeIdIn(anyList())).thenReturn(List.of());

        // When
        scheduler.mockShippingOrderStateChange();

        // Then
        verify(eventPublisher).publishEvents(eq(CDCEvent.SHIPPING_ORDER_STATE_CHANGE_EVENT), any());
    }

    @Test
    void givenSchedulerDisabled_whenMockShippingOrderStateChange_thenNoRepositoryCalls() {
        // Given
        scheduler.setEnabled(false);

        // When
        scheduler.mockShippingOrderStateChange();

        // Then
        verifyNoInteractions(reactiveDeliveryOrderRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void givenDeliveryOrder_whenBuildMockedShippingOrder_thenCorrectShippingOrderCreated() {
        // Given
        DeliveryOrder deliveryOrder = createDeliveryOrder("factory-order-1", createOrderItemRef("node-1", "item-1" , DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED , DeliveryStatusMapping.NodeStatusEnum.COMPLETED));

        // When
        ShippingOrder shippingOrder = ShippingOrderDtoBuilderUtil.buildShippingOrderFromDeliveryOrderItem(deliveryOrder);

        // Then
        assertEquals("factory-order-1", shippingOrder.getId());
        assertEquals(1, shippingOrder.getShippingOrderItem().size());
        assertEquals("item-1", shippingOrder.getShippingOrderItem().get(0).getId());
        assertEquals("product-item-1", shippingOrder.getShippingOrderItem().get(0).getProductOrderItem().getId());
        assertEquals(ShippingOrderItemStatus.COMPLETED.getValue(), shippingOrder.getShippingOrderItem().get(0).getStatus());
    }

    private OrderItemRef createOrderItemRefWithNullStatus(String nodeId, String factoryItemId) {
        return OrderItemRef.builder().productOrderItemId("product-item-1").factoryOrderItemId(factoryItemId).orchestrationNodeId(nodeId).build();
    }
    private OrderItemRef createOrderItemRef(String nodeId, String factoryItemId, DeliveryStatusMapping.DeliveryStatusEnum deliveryStatus, DeliveryStatusMapping.NodeStatusEnum nodeStatus) {
        return OrderItemRef.builder().productOrderItemId("product-item-1").factoryOrderItemId(factoryItemId).orchestrationNodeId(nodeId).deliveryStatusMapping(DeliveryStatusMapping.builder().deliveryStatus(deliveryStatus).nodeStatus(nodeStatus).build()).build();
    }

    // Utility method for creating test data
    private DeliveryOrder createDeliveryOrder(String factoryOrderId, OrderItemRef orderItemRef) {
        return DeliveryOrder.builder().id(UUID.randomUUID().toString()).factoryOrderId(factoryOrderId).deliveryFactoryRef(DeliveryFactoryRef.builder().deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SHIPPING_ORDER_MANAGEMENT).build()).orderItemRef(List.of(orderItemRef)).build();
    }

    static class ConversionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().setConversionService(new ApplicationConversionService());
        }
    }
}
