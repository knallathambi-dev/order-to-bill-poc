// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.service.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ProductOrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEventPayload;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ShippingOrderManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator.ShippingOrderCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.TangibleDeliveryImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.DeliveryStatusMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.DeliveryStatusMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import org.mockito.ArgumentCaptor;

@ContextConfiguration(classes = {TangibleDeliveryImpl.class, DeliveryStatusMapperImpl.class}, initializers = {TangibleNodeServiceImplDiffblueTest.ConversionInitializer.class})
@ExtendWith(SpringExtension.class)
class TangibleNodeServiceImplDiffblueTest {

    @MockBean
    DeliveryOrderRepository deliveryOrderRepository;

    @MockBean
    ShippingOrderCreator shippingOrderCreator;

    @MockBean
    ShippingOrderManagementService shippingOrderManagementService;

    @MockBean
    EventPublisher eventPublisher;

    @MockBean
    ErrorMessageMapper errorMessageMapper;

    @MockBean
    NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @Autowired
    TangibleDeliveryImpl tangibleDelivery;

    @Autowired
    DeliveryStatusMapper deliveryStatusMapper;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(deliveryOrderRepository, eventPublisher);
    }

    @Test
    void givenShippingOrderCompleted_whenExecutePostProcessDelivery_thenSavesDeliveryOrderWithUpdatedStatus() {
        String shippingOrderId = "so-1";
        String productOrderItemId = "poi-1";
        OrderItemRef ref = OrderItemRef.builder()
                .productOrderItemId(productOrderItemId)
                .orchestrationNodeId("node-1")
                .build();
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id(UUID.randomUUID().toString())
                .factoryOrderId(shippingOrderId)
                .orderItemRef(new ArrayList<>(List.of(ref)))
                .build();
        Mockito.when(deliveryOrderRepository.findByFactoryOrderId(shippingOrderId))
                .thenReturn(Optional.of(deliveryOrder));

        ShippingOrderStateChangeEvent event = buildShippingOrderEvent(shippingOrderId, productOrderItemId);

        tangibleDelivery.executePostProcessDelivery(event);

        ArgumentCaptor<DeliveryOrder> captor = ArgumentCaptor.forClass(DeliveryOrder.class);
        verify(deliveryOrderRepository).save(captor.capture());
        DeliveryOrder saved = captor.getValue();
        DeliveryStatusMapping savedStatus = saved.getOrderItemRef().get(0).getDeliveryStatusMapping();
        assertNotNull(savedStatus);
        assertEquals(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED, savedStatus.getDeliveryStatus());
        assertEquals(DeliveryStatusMapping.NodeStatusEnum.COMPLETED, savedStatus.getNodeStatus());
    }

    @Test
    void givenNoDeliveryOrderForShippingOrder_whenExecutePostProcessDelivery_thenThrowsRuntimeException() {
        String shippingOrderId = "so-missing";
        Mockito.when(deliveryOrderRepository.findByFactoryOrderId(shippingOrderId))
                .thenReturn(Optional.empty());

        ShippingOrderStateChangeEvent event = buildShippingOrderEvent(shippingOrderId, "poi-1");

        assertThrows(RuntimeException.class, () -> tangibleDelivery.executePostProcessDelivery(event));
    }

    private ShippingOrderStateChangeEvent buildShippingOrderEvent(String shippingOrderId, String productOrderItemId) {
        return ShippingOrderStateChangeEvent.builder()
                .event(ShippingOrderStateChangeEventPayload.builder()
                        .shippingOrder(ShippingOrder.builder()
                                .id(shippingOrderId)
                                .shippingOrderItem(List.of(ShippingOrderItem.builder()
                                        .id(UUID.randomUUID().toString())
                                        .status("completed")
                                        .productOrderItem(ProductOrderItemRef.builder()
                                                .id(productOrderItemId)
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();
    }

    static class ConversionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().setConversionService(new ApplicationConversionService());
        }
    }
}
