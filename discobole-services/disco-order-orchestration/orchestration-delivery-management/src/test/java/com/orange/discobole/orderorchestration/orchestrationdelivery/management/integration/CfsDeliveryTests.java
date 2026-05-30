// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.CfsDeliveryImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType.DELIVERY_ORDER_EVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
@Slf4j
class CfsDeliveryTests extends BaseAbstractionIntegrationTest {

    private static final String SERVICE_ORDER_RESPONSE_FILE_PATH = "ServiceOrderResponse.json";
    private static final String SERVICE_CATALOG_MANAGEMENT_TIME_BUNDLE_RESPONSE_FILE_PATH = "ServiceCatalogManagementTimeBundle.json";

    @Autowired
    private CfsDeliveryImpl cfsDelivery;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private KafkaTemplate<String, DeliveryOrderEvent> kafkaTemplate;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        eventRepository.deleteAll();
        deliveryOrderRepository.deleteAll();
    }

    @Test
    void givenDeliveryOrderEvent_whenStartInTangibleDelivery_thenFactoryOrderIdAndFactoryOrderItemIdShouldBeUpdatedOnly() {
        //Given
        wireMockServer.stubFor(post("/serviceOrdering/v1/serviceOrder")
                .willReturn(ok().withBodyFile(SERVICE_ORDER_RESPONSE_FILE_PATH).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        wireMockServer.stubFor(get("/serviceCatalogManagement/v1/serviceSpecification?id=Time%20Bundle3")
                .willReturn(ok().withBodyFile(SERVICE_CATALOG_MANAGEMENT_TIME_BUNDLE_RESPONSE_FILE_PATH).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        String relatedProductOrderId = UUID.randomUUID().toString();

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .orchestrationNodeId("node1")
                .action("add")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder()
                                .id("Time Bundle3")
                                .href("http://localhost:9997/serviceCatalogManagement/v1/serviceSpecification")
                                .build()))
                        .build())
                .orderItemCharacteristics(List.of(StringCharacteristic.builder()
                        .name("volume")
                        .value("1h")
                        .valueType("string")
                        .build()))
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id("deliveryOrderId")
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .href("http://localhost:9997/serviceOrdering/v1/serviceOrder")
                        .build())
                .productOrderId(relatedProductOrderId)
                .relatedParty(List.of())
                .orderItemRef(List.of(orderItemRef))
                .build();

        DeliveryOrderEvent deliveryStartEvent = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();


        //when
        cfsDelivery.deliver(deliveryStartEvent);
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            //then
            DeliveryOrder actualDeliveryOrder = deliveryOrderRepository.findById(deliveryOrder.getId()).get();
            assertNotNull(actualDeliveryOrder);
            assertEquals(1, actualDeliveryOrder.getOrderItemRef().size());
            Optional<OrderItemRef> optionalOrderItemRef = actualDeliveryOrder.getOrderItemRef().stream().findFirst();
            assertTrue(optionalOrderItemRef.isPresent());

            //Assert RelatedServiceOrder
            ServiceOrder serviceOrder = objectMapper.readValue(new File("src/test/resources/__files/ServiceOrderResponse.json"), ServiceOrder.class);
            RelatedServiceOrder expectedRelatedServiceOrder = RelatedServiceOrder.builder()
                    .id(serviceOrder.getId())
                    .somRef(actualDeliveryOrder.getDeliveryFactoryRef().getHref())
                    .orderItemId(serviceOrder.getServiceOrderItem().get(0).getId()).build();

            String somRef = deliveryOrder.getDeliveryFactoryRef().getHref();
            assertNotNull(somRef);
            assertEquals(expectedRelatedServiceOrder.getId(), actualDeliveryOrder.getFactoryOrderId());
            assertEquals(expectedRelatedServiceOrder.getSomRef(), actualDeliveryOrder.getDeliveryFactoryRef().getHref());
            assertEquals(expectedRelatedServiceOrder.getOrderItemId(), actualDeliveryOrder.getOrderItemRef().get(0).getFactoryOrderItemId());
        });
    }

    @Test
    void givenDeliveryOrderAndGetServiceSpecificationThrowException_whenCfsDelivery_thenOrderItemRefHeldAndFalloutCreated() {
        wireMockServer.stubFor(get("/serviceCatalogManagement/v1/serviceSpecification?id=Time%20Bundle3")
                .willReturn(ok().withBody("[]").withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        String relatedProductOrderId = UUID.randomUUID().toString();

        OrderItemRef orderItemRef = OrderItemRef.builder()
                .orchestrationNodeId("node1")
                .action("add")
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder()
                                .id("Time Bundle3")
                                .href("http://localhost:9997/serviceCatalogManagement/v1/serviceSpecification")
                                .build()))
                        .build())
                .orderItemCharacteristics(List.of(StringCharacteristic.builder()
                        .name("volume")
                        .value("1h")
                        .valueType("string")
                        .build()))
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id("deliveryOrderId")
                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .href("http://localhost:9997/serviceOrdering/v1/serviceOrder")
                        .build())
                .productOrderId(relatedProductOrderId)
                .relatedParty(List.of())
                .orderItemRef(List.of(orderItemRef))
                .build();

        DeliveryOrderEvent deliveryOrderEvent = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .eventType(DELIVERY_ORDER_EVENT.getValue())
                .build();

        kafkaTemplate.send("disco.delivery-management.deliveryOrder-event", deliveryOrderEvent);

        await().atMost(Duration.ofSeconds(50)).untilAsserted(() -> {
            List<EventEntity> eventEntities = eventRepository.findAll();
            assertThat(eventEntities.stream().map(EventEntity::getAggregateType).toList()).contains(EventType.DELIVERY_ORDER_ITEM_STATUS_EVENT.getValue());
            EventEntity eventEntity = eventEntities.stream().filter(event -> EventType.DELIVERY_ORDER_ITEM_STATUS_EVENT.getValue().equals(event.getAggregateType())).findAny().get();
            DeliveryOrderItemStatusEvent event = objectMapper.readValue(eventEntity.getPayload(), DeliveryOrderItemStatusEvent.class);
            assertThat(event.getEvent().getOrderItemRef().getOrchestrationNodeId()).isEqualTo(deliveryOrder.getOrderItemRef().get(0).getOrchestrationNodeId());
            assertThat(eventEntity.getHeaders().get(Headers.CONSUMER_ERROR)).isNotNull();
        });
    }
}
