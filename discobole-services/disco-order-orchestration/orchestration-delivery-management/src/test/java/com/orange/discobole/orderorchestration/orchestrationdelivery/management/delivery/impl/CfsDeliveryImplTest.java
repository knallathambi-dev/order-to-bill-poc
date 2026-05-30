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

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.CharacteristicSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceCatalogManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceOrderManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator.ServiceOrderCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.ErrorMessageMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.RelatedPartyMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.DiscoServiceUrl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
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
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.orange.discobole.orderorchestration.outbox.consts.Headers.CONSUMER_ERROR;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {CfsDeliveryImpl.class, ErrorMessageMapperImpl.class, com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.DeliveryStatusMapperImpl.class}, initializers = {CfsDeliveryImplTest.ConversionInitializer.class})
@ExtendWith(SpringExtension.class)
class CfsDeliveryImplTest {

    @Autowired
    ErrorMessageMapper errorMessageMapper;
    @MockBean
    ServiceOrderCreator serviceOrderCreator;
    @MockBean
    ServiceOrderManagementService serviceOrderManagementService;
    @MockBean
    DiscoServiceUrl discoServiceUrl;
    @MockBean
    EventPublisher eventPublisher;
    @Autowired
    private CfsDeliveryImpl cfsDeliveryImpl;
    @MockBean
    private ServiceCatalogManagementService serviceCatalogManagementService;

    @MockBean
    private DeliveryOrderRepository deliveryOrderRepository;

    @MockBean
    CharacteristicMapper characteristicMapper;

    @MockBean
    NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @MockBean
    RelatedPartyMapper relatedPartyMapper;

    @Test
    void testGetServiceCatalogManagementUrlFromOrderItemRef() {
        // Arrange
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("productOrderItemId")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder()
                                .id("1")
                                .href("v1/href")
                                .build()))
                        .build())
                .build();

        // Act
        String serviceCatalogManagementUrl = cfsDeliveryImpl.getServiceCatalogManagementUrl(orderItemRef);

        // Assert
        assertEquals(serviceCatalogManagementUrl, orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().get(0).getHref());
    }

    @Test
    void testGetServiceCatalogManagementUrlFromApplicationProperties() {
        // Arrange
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("productOrderItemId")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of())
                        .build())
                .build();
        // Act
        String serviceCatalogManagementUrl = cfsDeliveryImpl.getServiceCatalogManagementUrl(orderItemRef);

        // Assert
        assertEquals(serviceCatalogManagementUrl, discoServiceUrl.getServiceCatalogManagementUrl());
    }

    @Test
    void testGetServiceOrderingUrlFromDeliveryOrder() {
        // Arrange
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .href(URI.create("somref").toString())
                        .build())
                .build();

        // Act
        String serviceCatalogManagementUrl = cfsDeliveryImpl.getServiceOrderingUrl(deliveryOrder);

        // Assert
        assertEquals(serviceCatalogManagementUrl, deliveryOrder.getDeliveryFactoryRef().getHref());
    }

    @Test
    void testGetServiceOrderingUrlFromApplicationProperties() {
        // Arrange
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("productOrderItemId")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of())
                        .build())
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .orderItemRef(List.of(orderItemRef))
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .build())
                .build();

        // Act
        String serviceCatalogManagementUrl = cfsDeliveryImpl.getServiceCatalogManagementUrl(deliveryOrder.getOrderItemRef().get(0));

        // Assert
        assertEquals(serviceCatalogManagementUrl, discoServiceUrl.getServiceOrderingUrl());
    }

    @Test
    void givenOrderItemAndServiceOrderEvent_whenPostProcessDelivery_thenEventPublished() {
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("productOrderItemId")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of())
                        .build())
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .orderItemRef(List.of(orderItemRef))
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .build())
                .build();

        when(deliveryOrderRepository.findByFactoryOrderId(any()))
                .thenReturn(Optional.ofNullable(deliveryOrder));

        ServiceOrderEvent serviceOrderEvent = ServiceOrderEvent.builder()
                .event(ServiceOrderPayloadEvent.builder()
                        .serviceOrder(ServiceOrder.builder()
                                .serviceOrderItem(List.of(ServiceOrderItem.builder()
                                        .state(ServiceOrderItem.State.ACKNOWLEDGED)
                                        .service(Service.builder()
                                                .serviceCharacteristic(List.of())
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();

        cfsDeliveryImpl.executePostProcessDelivery(serviceOrderEvent);

        verify(eventPublisher).publishEvent(any(), any(), any());
    }

    @Test
    void givenDeliveryOrderAndServiceOrderEventWithError_whenPostProcessDelivery_thenEventPublished() {
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("productOrderItemId")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of())
                        .build())
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .orderItemRef(List.of(orderItemRef))
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .build())
                .build();

        when(deliveryOrderRepository.findByFactoryOrderId(any()))
                .thenReturn(Optional.ofNullable(deliveryOrder));

        ServiceOrderEvent serviceOrderEvent = ServiceOrderEvent.builder()
                .event(ServiceOrderPayloadEvent.builder()
                        .serviceOrder(ServiceOrder.builder()
                                .serviceOrderItem(List.of(ServiceOrderItem.builder()
                                        .state(ServiceOrderItem.State.ACKNOWLEDGED)
                                        .service(Service.builder()
                                                .serviceCharacteristic(List.of())
                                                .build())
                                        .errorMessage(List.of(ServiceOrderItemErrorMessage.builder()
                                                .reason("test")
                                                .message("test")
                                                .code("test")
                                                .build()))
                                        .build()))
                                .build())
                        .build())
                .build();

        cfsDeliveryImpl.executePostProcessDelivery(serviceOrderEvent);

        ArgumentCaptor<Map<String, Object>> headersArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventPublisher).publishEvent(any(), any(), headersArgumentCaptor.capture());
        Map<String, Object> headers = headersArgumentCaptor.getValue();
        assertEquals(2, headers.size());
        assertTrue(headers.containsKey(CONSUMER_ERROR));
    }

    @Test
    void givenDeliveryOrderAndServiceOrderEventWithErrorMessage_whenPostProcessDelivery_thenEventPublished() {
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("productOrderItemId")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of())
                        .build())
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .orderItemRef(List.of(orderItemRef))
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .build())
                .build();

        when(deliveryOrderRepository.findByFactoryOrderId(any()))
                .thenReturn(Optional.ofNullable(deliveryOrder));

        ServiceOrderEvent serviceOrderEvent = ServiceOrderEvent.builder()
                .event(ServiceOrderPayloadEvent.builder()
                        .serviceOrder(ServiceOrder.builder()
                                .serviceOrderItem(List.of(ServiceOrderItem.builder()
                                        .state(ServiceOrderItem.State.ACKNOWLEDGED)
                                        .errorMessage(List.of(ServiceOrderItemErrorMessage.builder()
                                                .code("test")
                                                .message("test")
                                                .reason("test")
                                                .build()))
                                        .service(Service.builder()
                                                .serviceCharacteristic(List.of())
                                                .id("id")
                                                .href("href")
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();

        cfsDeliveryImpl.executePostProcessDelivery(serviceOrderEvent);

        when(serviceOrderCreator.prepareServiceOrderRequest(any(), any(), any(), any()))
                .thenReturn(ServiceOrder.builder()
                        .id("id")
                        .build()
                );

        verify(eventPublisher).publishEvent(eq(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT), any(), any());
    }

    @Test
    void givenDeliveryOrderWithOutServiceOrderItemId_whenStartDelivery_thenExecute() {

        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification> serviceSpecification =
                List.of(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification.builder()
                        .id("1").name("ali")
                        .href(URI.create("aaa"))
                        .type("qq")
                        .baseType("www")
                        .isBundle(false)
                        .specCharacteristic(Set.of(CharacteristicSpecification.builder().name("relatedProduct").build()))
                        .build());

        when(serviceCatalogManagementService.getServiceSpecificationByIds(any(), any()))
                .thenReturn(serviceSpecification);

        when(serviceOrderCreator.prepareServiceOrderRequest(any(), any(), any(), any()))
                .thenReturn(ServiceOrder.builder()
                        .id("id")
                        .build()
                );

        when(serviceOrderManagementService.createServiceOrderInSOM(any(), any()))
                .thenReturn(Mono.fromCallable(this::createServiceOrderResponse));

        // Arrange
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder()
                                .id("1")
                                .href("v1/href")
                                .build()))
                        .build())
                .orderItemCharacteristics(List.of(
                        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic.builder()
                                .value("value")
                                .valueType("type")
                                .name("relatedProduct").build())
                )
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .requestedDeliveryDate(Instant.now().minusSeconds(100))
                .factoryOrderId("factoryOrderId")
                .orderItemRef(List.of(orderItemRef))
                .build();

        DeliveryOrderPayloadEvent deliveryOrderPayloadEvent = DeliveryOrderPayloadEvent.builder()
                .deliveryOrder(deliveryOrder)
                .build();
        DeliveryOrderEvent deliveryOrderEvent = deliveryOrderEventCreation(deliveryOrderPayloadEvent);

        when(deliveryOrderRepository.findByFactoryOrderId(
                deliveryOrderEvent.getEvent().getDeliveryOrder().getFactoryOrderId()
        )).thenReturn(Optional.of(deliveryOrder));

        assertDoesNotThrow(() -> cfsDeliveryImpl.deliver(deliveryOrderEvent));

        await().atMost(Duration.ofSeconds(60)).untilAsserted(() ->
                verify(serviceOrderManagementService, times(1)).createServiceOrderInSOM(any(), any()));

    }

    @Test
    void givenDeliveryOrderWithServiceOrderItemId_whenStartDelivery_thenSkipExecute() {

        when(serviceOrderManagementService.createServiceOrderInSOM(any(), any()))
                .thenReturn(any());

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("productOrderItemId")
                .factoryOrderItemId("orderItemId")
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .requestedDeliveryDate(Instant.now().minusSeconds(100))
                .factoryOrderId("factoryOrderId")
                .orderItemRef(List.of(orderItemRef))
                .build();

        DeliveryOrderPayloadEvent deliveryOrderPayloadEvent = DeliveryOrderPayloadEvent.builder()
                .deliveryOrder(DeliveryOrder.builder().requestedDeliveryDate(Instant.now().minusSeconds(100))
                        .orderItemRef(List.of(orderItemRef)).build())
                .build();

        DeliveryOrderEvent deliveryOrderEvent = deliveryOrderEventCreation(deliveryOrderPayloadEvent);

        when(deliveryOrderRepository.findByFactoryOrderId(
                deliveryOrderEvent.getEvent().getDeliveryOrder().getFactoryOrderId()
        )).thenReturn(Optional.of(deliveryOrder));

        assertDoesNotThrow(() -> cfsDeliveryImpl.deliver(deliveryOrderEvent));
        verify(serviceOrderManagementService, times(0)).createServiceOrderInSOM(any(), any());
    }


    private static DeliveryOrderEvent deliveryOrderEventCreation(DeliveryOrderPayloadEvent deliveryOrderPayloadEvent) {
        return DeliveryOrderEvent
                .builder().event(
                        deliveryOrderPayloadEvent
                ).build();
    }

    public ServiceOrder createServiceOrderResponse() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification serviceSpecification = com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification.builder()
                .id("MobileLine")
                .build();
        Characteristic serviceCharacteristic = StringCharacteristic.builder()
                .id("5243")
                .name("volume")
                .valueType("string")
                .value("1h")
                .build();
        List<Characteristic> serviceCharacteristicList = new ArrayList<>();
        serviceCharacteristicList.add(serviceCharacteristic);
        Service service = Service.builder()
                .serviceType("CFS")
                .serviceCharacteristic(serviceCharacteristicList)
                .serviceSpecification(serviceSpecification)
                .build();
        ServiceOrderItem serviceOrderItem = ServiceOrderItem.builder()
                .id("1")
                .action(ServiceOrderItem.Action.ADD)
                .service(service)
                .build();
        List<ServiceOrderItem> serviceOrderItemList = new ArrayList<>();
        serviceOrderItemList.add(serviceOrderItem);
        ServiceOrderRelatedParty relatedParty = ServiceOrderRelatedParty.builder()
                .id("231-mf4")
                .name("Abir")
                .role("customer")
                .referredType("individual")
                .build();
        List<ServiceOrderRelatedParty> relatedPartyList = new ArrayList<>();
        relatedPartyList.add(relatedParty);
        return ServiceOrder.builder()
                .id("123")
                .state(ServiceOrder.State.ACKNOWLEDGED)
                .description("Service order Time Bundle")
                .category("string")
                .requestedCompletionDate(Instant.ofEpochSecond(1626436900L))
                .type("ServiceOrder")
                .relatedParty(relatedPartyList)
                .serviceOrderItem(serviceOrderItemList)
                .build();
    }

    static class ConversionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().setConversionService(new ApplicationConversionService());
        }
    }
}
