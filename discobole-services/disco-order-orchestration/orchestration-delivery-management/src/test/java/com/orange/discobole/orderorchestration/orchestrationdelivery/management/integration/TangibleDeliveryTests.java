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


import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.TangibleDeliveryImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class})
@Slf4j
class TangibleDeliveryTests extends BaseAbstractionIntegrationTest {

    private static final String SHIPPING_ORDER_RESPONSE_FILE_PATH = "ShippingOrderResponse.json";

    @Autowired
    private TangibleDeliveryImpl tangibleDelivery;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @BeforeEach
    void setupWiremock() {
        wireMockServer.stubFor(post("/shippingOrder/v1/shippingOrder")
                .willReturn(ok().withBodyFile(SHIPPING_ORDER_RESPONSE_FILE_PATH).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @Test
    void givenDeliveryOrderEvent_whenStartTangibleDelivery_thenFactoryOrderIdSetOnDeliveryOrder() {
        String deliveryOrderId = UUID.randomUUID().toString();

        // productOrderItemId "100" matches ShippingOrderResponse.json shippingOrderItem[0].productOrderItem.id
        OrderItemRef orderItemRef = OrderItemRef.builder()
                .productOrderItemId("100")
                .orchestrationNodeId(UUID.randomUUID().toString())
                .quantity(1)
                .action("add")
                .orderItemCharacteristics(List.of(
                        DateCharacteristic.builder()
                                .name("requested delivery date")
                                .value(OffsetDateTime.parse("2025-01-06T10:22:23.155Z"))
                                .build()))
                .build();

        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id(deliveryOrderId)
                .productOrderId("7800")
                .relatedParty(List.of())
                .orderItemRef(List.of(orderItemRef))
                .build();

        DeliveryOrderEvent event = DeliveryOrderEvent.builder()
                .event(DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(deliveryOrder)
                        .build())
                .build();

        tangibleDelivery.deliver(event);

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            Optional<DeliveryOrder> saved = deliveryOrderRepository.findById(deliveryOrderId);
            assertTrue(saved.isPresent());
            // factoryOrderId comes from ShippingOrderResponse.json "id" field
            assertEquals("19fbb001-3455-4e8e-b8a8-b6db8778ddb5", saved.get().getFactoryOrderId());
            assertNotNull(saved.get().getOrderItemRef());
            assertEquals(1, saved.get().getOrderItemRef().size());
            // factoryOrderItemId comes from ShippingOrderResponse.json shippingOrderItem[0].id
            assertEquals("1", saved.get().getOrderItemRef().get(0).getFactoryOrderItemId());
        });
    }
}
