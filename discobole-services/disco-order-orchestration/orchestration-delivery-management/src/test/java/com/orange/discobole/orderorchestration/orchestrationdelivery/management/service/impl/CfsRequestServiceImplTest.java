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


import com.orange.discobole.orderorchestration.exception.DiscoException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.CharacteristicSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceCatalogManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceOrderManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator.ServiceOrderCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.CfsDeliveryImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.CharacteristicMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.DeliveryStatusMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {CfsDeliveryImpl.class, ServiceOrderCreator.class, CharacteristicMapperImpl.class, ErrorMessageMapperImpl.class}, initializers = {CfsRequestServiceImplTest.ConversionInitializer.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "config.serviceCatalogValidation.enabled = true"
})
class CfsRequestServiceImplTest {

    @MockBean
    DiscoServiceUrl discoServiceUrl;

    @MockBean
    ServiceOrderManagementService serviceOrderManagementService;

    @MockBean
    ServiceCatalogManagementService serviceCatalogManagementService;

    @Autowired
    CfsDeliveryImpl cfsDelivery;

    @Autowired
    ServiceOrderCreator serviceOrderCreator;

    @MockBean
    DeliveryOrderRepository deliveryOrderRepository;

    @MockBean
    EventPublisher eventPublisher;

    @MockBean
    NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    @MockBean
    RelatedPartyMapper relatedPartyMapper;

    @MockBean
    DeliveryStatusMapper deliveryStatusMapper;

    private static final String OK_SERVICE_ORDER_URL = "http://localhost:8082/serviceOrdering/v4/serviceOrder";

    @Test
    void testCreateRequestForCfsForEachServiceOrderWithValidServiceSpecification() throws DiscoException {
        // Given
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> productCharacteristics = createProductCharacteristic();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification serviceSpecification = createServiceSpecification();

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("8")
                .action("add")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder().id(serviceSpecification.getId()).build()))
                        .build())
                .orderItemCharacteristics(productCharacteristics)
                .build();

        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderItemRef(List.of(orderItemRef));
        deliveryOrder.setRequestedDeliveryDate(Instant.now());
        deliveryOrder.setDeliveryFactoryRef(DeliveryFactoryRef.builder()
                .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                .build());
        mockingServiceOrderRequest(createServiceOrderResponse());

        when(serviceCatalogManagementService.getServiceSpecificationByIds(anyList(), any()))
                .thenReturn(List.of(serviceSpecification));

        //when
        DeliveryOrderEvent deliveryOrderEvent = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        when(deliveryOrderRepository.findByFactoryOrderId(
                deliveryOrderEvent.getEvent().getDeliveryOrder().getFactoryOrderId()))
                .thenReturn(Optional.of(deliveryOrder));
        cfsDelivery.deliver(deliveryOrderEvent);

        //then
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            verify(serviceOrderManagementService).createServiceOrderInSOM(any(), any());
        });
    }


    @Test
    void testCreateRequestForCfsForEachServiceOrderWithInValidServiceSpecification() throws DiscoException {
        // Given
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> productCharacteristics = createProductCharacteristic();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification serviceSpecification = createServiceSpecification();
        serviceSpecification.getSpecCharacteristic().forEach(ch -> ch.setName("NO_NAME"));

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("8")
                .action("add")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder().id(serviceSpecification.getId()).build()))
                        .build())
                .orderItemCharacteristics(productCharacteristics)
                .build();

        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderItemRef(List.of(orderItemRef));
        deliveryOrder.setRequestedDeliveryDate(Instant.now());
        deliveryOrder.setDeliveryFactoryRef(DeliveryFactoryRef.builder()
                .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                .build());
        mockingServiceOrderRequest(createServiceOrderResponse());

        when(serviceCatalogManagementService.getServiceSpecificationByIds(anyList(), any())).thenReturn(List.of(serviceSpecification));

        //when
        DeliveryOrderEvent deliveryOrderEvent = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        when(deliveryOrderRepository.findByFactoryOrderId(
                deliveryOrderEvent.getEvent().getDeliveryOrder().getFactoryOrderId()))
                .thenReturn(Optional.of(deliveryOrder));
        cfsDelivery.deliver(deliveryOrderEvent);

        //then
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            verify(eventPublisher).publishEvent(eq(CDCEvent.DELIVERY_ORDER_DLT_EVENT), any(), any());
        });
    }

    private void mockingServiceOrderRequest(ServiceOrder serviceOrder) {
        when(discoServiceUrl.getServiceOrderingUrl()).thenReturn(OK_SERVICE_ORDER_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        when(serviceOrderManagementService.createServiceOrderInSOM(any(), any())
        ).thenReturn(Mono.fromCallable(() -> serviceOrder));
    }

    @Test
    void testCreateRequestForCfs_whenServiceCatalogReturnsEmpty_thenDLTPublished() throws DiscoException {
        // Given

        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> productCharacteristics = createProductCharacteristic();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification serviceSpecification = createServiceSpecification();

        // No service spec returned by catalog
        when(serviceCatalogManagementService.getServiceSpecificationByIds(anyList(), any()))
                .thenReturn(Collections.emptyList());

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("8")
                .action("add")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder().id(serviceSpecification.getId()).build()))
                        .build())
                .orderItemCharacteristics(productCharacteristics)
                .build();

        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderItemRef(List.of(orderItemRef));
        deliveryOrder.setRequestedDeliveryDate(Instant.now());
        deliveryOrder.setDeliveryFactoryRef(DeliveryFactoryRef.builder()
                .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                .build());
        mockingServiceOrderRequest(createServiceOrderResponse());

        //when
        DeliveryOrderEvent deliveryOrderEvent = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        when(deliveryOrderRepository.findByFactoryOrderId(
                deliveryOrderEvent.getEvent().getDeliveryOrder().getFactoryOrderId()))
                .thenReturn(Optional.of(deliveryOrder));

        // When
        cfsDelivery.deliver(deliveryOrderEvent);

        // Then
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            verify(eventPublisher).publishEvent(eq(CDCEvent.DELIVERY_ORDER_DLT_EVENT), any(), any());
            verifyNoInteractions(serviceOrderManagementService);
        });
    }

    @Test
    void testCreateRequestForCfs_whenSomCreateFails_thenDLTPublished() throws DiscoException {
        // Given
        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> productCharacteristics = createProductCharacteristic();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification serviceSpecification = createServiceSpecification();

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("8")
                .action("add")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder().id(serviceSpecification.getId()).build()))
                        .build())
                .orderItemCharacteristics(productCharacteristics)
                .build();

        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderItemRef(List.of(orderItemRef));
        deliveryOrder.setRequestedDeliveryDate(Instant.now());
        deliveryOrder.setDeliveryFactoryRef(DeliveryFactoryRef.builder()
                .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                .build());
        mockingServiceOrderRequest(createServiceOrderResponse());

        when(serviceCatalogManagementService.getServiceSpecificationByIds(anyList(), any())).thenReturn(List.of(serviceSpecification));

        //when
        when(discoServiceUrl.getServiceOrderingUrl()).thenReturn(OK_SERVICE_ORDER_URL);
        when(serviceOrderManagementService.createServiceOrderInSOM(any(), any()))
                .thenThrow(new RuntimeException("SOM down"));

        DeliveryOrderEvent deliveryOrderEvent = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        // When
        cfsDelivery.deliver(deliveryOrderEvent);

        // Then
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            verify(eventPublisher).publishEvent(eq(CDCEvent.DELIVERY_ORDER_DLT_EVENT), any(), any());
        });
    }

    @Test
    void testPrepareServiceOrder() {
        //given
        ServiceOrder serviceOrderExpected = createServiceOrderRequest();

        List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> productCharacteristics = createProductCharacteristic();
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification serviceSpecification = createServiceSpecification();
        serviceSpecification.getSpecCharacteristic().forEach(ch -> ch.setName("NO_NAME"));

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("id1")
                .action("add")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder().id(serviceSpecification.getId()).build()))
                        .build())
                .realizingResourceRef(RealizingResourceRef.builder()
                        .id("realizingServiceId")
                        .href("href")
                        .build())
                .orderItemCharacteristics(productCharacteristics)
                .build();

        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderItemRef(List.of(orderItemRef));
        deliveryOrder.setRequestedDeliveryDate(Instant.ofEpochSecond(1626436900L));
        deliveryOrder.setDeliveryFactoryRef(DeliveryFactoryRef.builder()
                .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                .build());
        mockingServiceOrderRequest(createServiceOrderResponse());


        //when
        ServiceOrder serviceOrderCreated = serviceOrderCreator.prepareServiceOrderRequest(orderItemRef, deliveryOrder.getRequestedDeliveryDate(), createProductCharacteristic(), createServiceSpecification());

        //then
        assertNotNull(serviceOrderCreated);
        assertEquals(serviceOrderExpected.getRequestedCompletionDate(), serviceOrderCreated.getRequestedCompletionDate());

    }

    public ServiceOrder createServiceOrderRequest() {
        ServiceSpecification serviceSpecification = ServiceSpecification.builder()
                .id("MobileLine")
                .build();
        StringCharacteristic serviceCharacteristic = StringCharacteristic.builder()
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
                .serviceRelationship(List.of(ServiceRelationship.builder().serviceRelationshipType("reliesOn")
                        .service(ServiceRef.builder().id("realizingServiceId").href("href").build()).build()))
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
                .description("Service order Time Bundle")
                .category("string")
                .requestedCompletionDate(Instant.ofEpochSecond(1626436900L))
                .type("ServiceOrder")
                .relatedParty(relatedPartyList)
                .serviceOrderItem(serviceOrderItemList)
                .build();
    }

    public ServiceOrder createServiceOrderResponse() {
        ServiceSpecification serviceSpecification = ServiceSpecification.builder()
                .id("MobileLine")
                .build();
        StringCharacteristic serviceCharacteristic = StringCharacteristic.builder()
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

    public List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> createProductCharacteristic() {
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic characteristicDTO = com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic.builder()
                .name("volume")
                .value("1h")
                .valueType("string")
                .build();
        return List.of(characteristicDTO);
    }

    public com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification createServiceSpecification() {
        CharacteristicSpecification serviceCharacteristic = CharacteristicSpecification.builder()
                .id("5243")
                .name("volume")
                .build();
        Set<CharacteristicSpecification> characteristicSpecificationList = new HashSet<>();
        characteristicSpecificationList.add(serviceCharacteristic);
        return com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification.builder()
                .id("154")
                .specCharacteristic(characteristicSpecificationList)
                .build();
    }

    static class ConversionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.getBeanFactory().setConversionService(new ApplicationConversionService());
        }
    }
}
