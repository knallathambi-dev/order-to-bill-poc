// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl;


import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ProductOrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderCreate;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEventPayload;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ShippingOrderManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator.ShippingOrderCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.impl.ShippingOrderStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.ErrorMessageMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {TangibleDeliveryImpl.class, ShippingOrderStateChangeEventHandler.class, ErrorMessageMapperImpl.class, com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.DeliveryStatusMapperImpl.class}, initializers = {TangibleDeliveryImplTest.ConversionInitializer.class})
@ExtendWith(SpringExtension.class)
class TangibleDeliveryImplTest {

    @Autowired
    private TangibleDeliveryImpl tangibleDeliveryImpl;

    @MockBean
    private ShippingOrderManagementService shippingOrderManagementService;

    @MockBean
    private ShippingOrderCreator shippingOrderCreator;

    @Autowired
    private ShippingOrderStateChangeEventHandler shippingOrderStateChangeEventHandler;

    @MockBean
    private EventPublisher eventPublisher;

    @MockBean
    NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @MockBean
    private DeliveryOrderRepository deliveryOrderRepository;

    @BeforeEach
    void setup() {
        reset(eventPublisher, deliveryOrderRepository);
    }

    @Test
    void givenDeliveryOrderEvent_whenDeliver_thenShippingOrderCreatedAndDeliveryOrderSaved() {
        OrderItemRef ref = OrderItemRef.builder()
                .productOrderItemId("poi-1")
                .orchestrationNodeId("node-1")
                .quantity(1)
                .action("add")
                .build();
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id("do-1")
                .productOrderId("po-1")
                .relatedParty(List.of())
                .orderItemRef(List.of(ref))
                .build();
        DeliveryOrderEvent event = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        when(deliveryOrderRepository.findById("do-1")).thenReturn(Optional.empty());
        when(shippingOrderCreator.prepareShippingOrderRequest(any(), any(), any()))
                .thenReturn(ShippingOrderCreate.builder().build());
        ShippingOrder shippingOrder = ShippingOrder.builder()
                .id("so-1")
                .shippingOrderItem(List.of(ShippingOrderItem.builder()
                        .id("soi-1")
                        .productOrderItem(ProductOrderItemRef.builder().id("poi-1").build())
                        .build()))
                .build();
        when(shippingOrderManagementService.createShippingOrder(any())).thenReturn(shippingOrder);

        tangibleDeliveryImpl.deliver(event);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                verify(shippingOrderManagementService).createShippingOrder(any()));
        verify(deliveryOrderRepository, atLeastOnce()).save(any());
    }

    @Test
    void givenDeliveryOrderEvent_whenDuplicateDetected_thenSkipProcessing() {
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id("do-1")
                .orderItemRef(List.of())
                .build();
        DeliveryOrderEvent event = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        when(deliveryOrderRepository.findById("do-1")).thenReturn(Optional.of(deliveryOrder));

        tangibleDeliveryImpl.deliver(event);

        verify(shippingOrderManagementService, never()).createShippingOrder(any());
        verify(deliveryOrderRepository, never()).save(any());
    }

    @Test
    void givenCompletedShippingOrderStateChangeEvent_whenHandleEvent_thenDeliveryStatusMappedToExecutedCompleted() {
        String shippingOrderId = "shippingOrderId";
        OrderItemRef ref = OrderItemRef.builder()
                .orchestrationNodeId("node-1")
                .productOrderItemId("poi-1")
                .build();
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id("do-1")
                .factoryOrderId(shippingOrderId)
                .orderItemRef(List.of(ref))
                .build();
        when(deliveryOrderRepository.findByFactoryOrderId(shippingOrderId))
                .thenReturn(Optional.of(deliveryOrder));

        ShippingOrderStateChangeEvent event = ShippingOrderStateChangeEvent.builder()
                .event(ShippingOrderStateChangeEventPayload.builder()
                        .shippingOrder(ShippingOrder.builder()
                                .id(shippingOrderId)
                                .shippingOrderItem(List.of(ShippingOrderItem.builder()
                                        .status("completed")
                                        .id("soi-1")
                                        .productOrderItem(ProductOrderItemRef.builder().id("poi-1").build())
                                        .build()))
                                .build())
                        .build())
                .build();

        shippingOrderStateChangeEventHandler.handleEvent(event);

        ArgumentCaptor<List<DeliveryOrderItemStatusPayloadEvent>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publishEvents(any(), captor.capture());
        DeliveryStatusMapping mapping = captor.getValue().get(0).getOrderItemRef().getDeliveryStatusMapping();
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED, mapping.getDeliveryStatus());
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.COMPLETED, mapping.getNodeStatus());
    }

    @Test
    void givenFailedShippingOrderStateChangeEvent_whenHandleEvent_thenDeliveryStatusMappedToExecutedFailed() {
        String shippingOrderId = "shippingOrderId";
        OrderItemRef ref = OrderItemRef.builder()
                .orchestrationNodeId("node-1")
                .productOrderItemId("poi-1")
                .build();
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id("do-1")
                .factoryOrderId(shippingOrderId)
                .orderItemRef(List.of(ref))
                .build();
        when(deliveryOrderRepository.findByFactoryOrderId(shippingOrderId))
                .thenReturn(Optional.of(deliveryOrder));

        ShippingOrderStateChangeEvent event = ShippingOrderStateChangeEvent.builder()
                .event(ShippingOrderStateChangeEventPayload.builder()
                        .shippingOrder(ShippingOrder.builder()
                                .id(shippingOrderId)
                                .shippingOrderItem(List.of(ShippingOrderItem.builder()
                                        .status("failed")
                                        .id("soi-1")
                                        .productOrderItem(ProductOrderItemRef.builder().id("poi-1").build())
                                        .build()))
                                .build())
                        .build())
                .build();

        shippingOrderStateChangeEventHandler.handleEvent(event);

        ArgumentCaptor<List<DeliveryOrderItemStatusPayloadEvent>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publishEvents(any(), captor.capture());
        DeliveryStatusMapping mapping = captor.getValue().get(0).getOrderItemRef().getDeliveryStatusMapping();
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED, mapping.getDeliveryStatus());
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.FAILED, mapping.getNodeStatus());
    }

    @Test
    void givenHeldShippingOrderStateChangeEvent_whenHandleEvent_thenDeliveryStatusMappedToHeld() {
        String shippingOrderId = "shippingOrderId";
        OrderItemRef ref = OrderItemRef.builder()
                .orchestrationNodeId("node-1")
                .productOrderItemId("poi-1")
                .build();
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id("do-1")
                .factoryOrderId(shippingOrderId)
                .orderItemRef(List.of(ref))
                .build();
        when(deliveryOrderRepository.findByFactoryOrderId(shippingOrderId))
                .thenReturn(Optional.of(deliveryOrder));

        ShippingOrderStateChangeEvent event = ShippingOrderStateChangeEvent.builder()
                .event(ShippingOrderStateChangeEventPayload.builder()
                        .shippingOrder(ShippingOrder.builder()
                                .id(shippingOrderId)
                                .shippingOrderItem(List.of(ShippingOrderItem.builder()
                                        .status("held")
                                        .id("soi-1")
                                        .productOrderItem(ProductOrderItemRef.builder().id("poi-1").build())
                                        .build()))
                                .build())
                        .build())
                .build();

        shippingOrderStateChangeEventHandler.handleEvent(event);

        ArgumentCaptor<List<DeliveryOrderItemStatusPayloadEvent>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventPublisher).publishEvents(any(), captor.capture());
        DeliveryStatusMapping mapping = captor.getValue().get(0).getOrderItemRef().getDeliveryStatusMapping();
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.HELD, mapping.getDeliveryStatus());
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.HELD, mapping.getNodeStatus());
    }

    static class ConversionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().setConversionService(new ApplicationConversionService());
        }
    }
}
