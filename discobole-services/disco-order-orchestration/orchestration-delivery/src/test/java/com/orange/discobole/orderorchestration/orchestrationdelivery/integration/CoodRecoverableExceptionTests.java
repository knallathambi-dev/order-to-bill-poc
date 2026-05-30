// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.ordermanagement.event.om.ProductOrderPayloadEvent;
import com.orange.discobole.ordermanagement.event.om.ProductOrderStateChangeEvent;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.AbstractIntegrationUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.mocks.producer.ProductOrderStateChangeEventProducer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
@Slf4j
class CoodRecoverableExceptionTests extends BaseAbstractionIntegrationTest {
    public static final String PRODUCT_ORDER_DTO_JSON_FILE = "/integration/IntegrationProductOrderDTO.json";
    private static final String SIM_CARD_PRODUCT_SPECIFICATION = "productspecification/SimCardProductSpecification.json";
    private static final String ALL_PRODUCT_SPECIFICATIONS = "productspecification/AllProductSpecifications.json";
    private static final String CONNECTIVITY_PRODUCT_SPECIFICATION = "productspecification/ConnectivityProductSpecification.json";
    private static final String MOBILE_LINE_PRODUCT_SPECIFICATION = "productspecification/MobileLineProductSpecification.json";
    private static final String TIME_BUNDLE_PRODUCT_SPECIFICATION = "productspecification/TimeBundleProductSpecification.json";

    @Value("disco.order-orchestration.orchestrationPlanStateChange-event-dlt")
    private String outputDltTopicDestination;
    @Autowired
    private ProductOrderStateChangeEventProducer producer;

    public static ProductOrderStateChangeEvent getProductOrderEvent(ProductOrder productOrder, EventType eventType) {
        ProductOrderPayloadEvent productOrderStateChangePayloadEvent = ProductOrderPayloadEvent
                .builder()
                .productOrder(productOrder)
                .build();

        return ProductOrderStateChangeEvent
                .builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(Instant.now())
                .eventType(eventType.getValue())
                .event(productOrderStateChangePayloadEvent)
                .build();
    }

    @Test
    @SneakyThrows
    void givenProductOrderDTO_whenBeginProcessingAndThrowNotFound_thenThrowTechnicalExceptionAndSentToDLT() {
        setupWiremock();

        //given
        ProductOrder productOrder = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });
        productOrder.setId("1027");
        String partitionKey = productOrder.getId();
        //when
        ProductOrderStateChangeEvent productOrderStateChangeEvent = getProductOrderEvent(productOrder, EventType.PRODUCT_ORDER_STATE_CHANGE_EVENT);

        Message<ProductOrderStateChangeEvent> message = MessageBuilder.withPayload(productOrderStateChangeEvent)
                .setHeader(MessageHeaders.CONTENT_TYPE, "application/json") // Set the content type header
                .setHeader("partitionKey", partitionKey)
                .build();

        producer.publishEvent(message.getPayload());
        //then
        KafkaConsumer<String, OrchestrationPlanStateChangeEvent> orchestrationPlanEventKafkaConsumer = AbstractIntegrationUtil.createKafkaConsumer(outputDltTopicDestination, "test-output-group-1", OrchestrationPlanStateChangeEvent.class);
        ConsumerRecords<String, OrchestrationPlanStateChangeEvent> records = orchestrationPlanEventKafkaConsumer.poll(Duration.ofSeconds(30));

        for (ConsumerRecord<String, OrchestrationPlanStateChangeEvent> eventConsumerRecord : records) {
            OrchestrationPlanStateChangeEvent actualOrchestrationPlanEvent = eventConsumerRecord.value();
            log.info("orchestration plan id: {}", actualOrchestrationPlanEvent.getEvent().getOrchestrationPlan().getId());
            log.info("headers {}", eventConsumerRecord.headers());
            org.assertj.core.api.Assertions.assertThat(eventConsumerRecord.headers().headers("exception-message")).hasSize(1);
            org.assertj.core.api.Assertions.assertThat(eventConsumerRecord.headers().headers("exception-code")).hasSize(1);
            org.assertj.core.api.Assertions.assertThat(eventConsumerRecord.headers().headers("exception-reason")).hasSize(1);
            eventConsumerRecord.headers().forEach(header -> {
                log.info("header key: {}", header.key());
                log.info("header value: {}", header.value());
                if (header.key().equals("exception-reason")) {
                    org.assertj.core.api.Assertions.assertThat(new String(header.value(), StandardCharsets.UTF_8))
                            .containsPattern(ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND.getReason());
                }
                if (header.key().equals("exception-code")) {
                    Assertions.assertEquals(ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND.getCode(), new String(header.value(), StandardCharsets.UTF_8));
                }
            });
        }

        orchestrationPlanEventKafkaConsumer.unsubscribe();
        orchestrationPlanEventKafkaConsumer.close(Duration.ofMillis(1));
    }

    private void setupWiremock() {
        log.info("Wiremock server started on: {}", wireMockServer.baseUrl());
        wireMockServer.stubFor(get("/productCatalogManagement/v1/productSpecification/df32402e-ceb9-4467-aafd-fec0bbff3124")
                .willReturn(ok().withBodyFile(SIM_CARD_PRODUCT_SPECIFICATION).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        wireMockServer.stubFor(get("/productCatalogManagement/v1/productSpecification/5e18402d-c964-4d52-b362-22ef79c27c01")
                .willReturn(ok().withBodyFile(CONNECTIVITY_PRODUCT_SPECIFICATION).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        wireMockServer.stubFor(get("/productCatalogManagement/v1/productSpecification/c458d968-3718-4754-9408-7bd81526e7b2")
                .willReturn(ok().withBodyFile(MOBILE_LINE_PRODUCT_SPECIFICATION).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        wireMockServer.stubFor(get("/productCatalogManagement/v1/productSpecification/82f3bab6-63a1-4008-96a3-8d411d0c5b38")
                .willReturn(ok().withBodyFile(TIME_BUNDLE_PRODUCT_SPECIFICATION).withHeader(CONTENT_TYPE, APPLICATION_JSON))
        );

        wireMockServer.stubFor(get(urlMatching("\\/productCatalogManagement\\/v1\\/productSpecification\\?id=.*"))
                .willReturn(ok().withBodyFile(ALL_PRODUCT_SPECIFICATIONS).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

}
