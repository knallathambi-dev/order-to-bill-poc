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

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.ReactiveDeliveryOrderRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {ServiceOrderStateChangeServiceMockScheduler.class}, initializers = {ServiceOrderStateChangeServiceTests.ConversionInitializer.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "config.deliveryFactoryJobArraySize=2000",
        "mocks.serviceOrderStateChangeScheduler.maxDelay=PT0S",
        "mocks.serviceOrderStateChangeScheduler.minDelay=PT0S"
})
class ServiceOrderStateChangeServiceTests {

    @Autowired
    ServiceOrderStateChangeServiceMockScheduler serviceOrderStateChangeServiceMockScheduler;

    @MockBean
    EventPublisher eventPublisher;

    @MockBean
    ReactiveDeliveryOrderRepository reactiveDeliveryOrderRepository;

    @MockBean
    NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceOrderStateChangeEnabled_ThenServiceOrderStateChangeEventProducerIsNotCalled() {
        serviceOrderStateChangeServiceMockScheduler.setEnabled(true);
        Mockito.when(reactiveDeliveryOrderRepository.findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull())
                .thenReturn(Flux.empty());
        serviceOrderStateChangeServiceMockScheduler.mockServiceOrderStateChange();

        Mockito.verify(eventPublisher, Mockito.times(0))
                .publishEvents(any(), any());
    }

    @Test
    void testServiceOrderStateChangeWithoutEnabled_ThenServiceOrderStateChangeEventProducerIsCalled() {
        serviceOrderStateChangeServiceMockScheduler.setEnabled(true);
        List<ServiceSpecificationRef> serviceSpecificationRef = Arrays.asList(ServiceSpecificationRef.builder().id("spec-1").build());
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .factoryOrderItemId("orderItem1")
                .productOrderItemId("item-1")
                .orderItemCharacteristics(new ArrayList<>())
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(serviceSpecificationRef)
                        .build())
                .build();
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .factoryOrderId("order1")
                .factoryOrderId("factory-order-1")
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .build())
                .orderItemRef(List.of(orderItemRef))
                .build();
        Mockito.when(reactiveDeliveryOrderRepository.findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull())
                .thenReturn(Flux.just(deliveryOrder));
        serviceOrderStateChangeServiceMockScheduler.mockServiceOrderStateChange();

        Mockito.verify(eventPublisher, Mockito.times(1))
                .publishEvents(any(), any());
    }

    static class ConversionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().setConversionService(new ApplicationConversionService());
        }
    }
}
