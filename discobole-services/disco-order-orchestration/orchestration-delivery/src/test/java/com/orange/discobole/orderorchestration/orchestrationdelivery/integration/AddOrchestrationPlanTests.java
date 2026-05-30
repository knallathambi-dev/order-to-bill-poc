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
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.AbstractIntegrationUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductSpecificationService;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanInitService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service.OrchestrationPlanNodeRelationshipService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
@Slf4j
public class AddOrchestrationPlanTests extends BaseAbstractionIntegrationTest {
    public static final String PRODUCT_ORDER_DTO_JSON_FILE = "/integration/IntegrationProductOrderDTO.json";
    public static final String PRODUCT_ORDER_DTO_DELETE_ACTION_JSON_FILE = "/integration/IntegrationProductOrderWithDeleteActionDto.json";

    private static final String SIM_CARD_PRODUCT_SPECIFICATION = "productspecification/SimCardProductSpecification.json";

    private static final String ALL_PRODUCT_SPECIFICATIONS = "productspecification/AllProductSpecifications.json";

    private static final String CONNECTIVITY_PRODUCT_SPECIFICATION = "productspecification/ConnectivityProductSpecification.json";

    private static final String MOBILE_LINE_PRODUCT_SPECIFICATION = "productspecification/MobileLineProductSpecification.json";

    private static final String TIME_BUNDLE_PRODUCT_SPECIFICATION = "productspecification/TimeBundleProductSpecification.json";

    public static final String ORCHESTRATION_PLAN_AFTER_BUILD_JSON_FILE = "/integration/OrchestrationPlanAfterAdd.json";
    @Autowired
    private OrchestrationPlanInitService orchestrationPlanInitService;

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;

    @Autowired
    @SuppressWarnings("PMD.UnusedPrivateField")
    private OrchestrationPlanService orchestrationPlanService;

    @Autowired
    @SuppressWarnings("PMD.UnusedPrivateField")
    private OrchestrationPlanNodeRelationshipService orchestrationPlanNodeRelationshipService;

    @Autowired
    @SuppressWarnings("PMD.UnusedPrivateField")
    private ProductSpecificationService productSpecificationService;

    @Value("disco.order-orchestration.orchestrationPlanStateChange-event")
    private String outputTopicDestination;

    @Value("${app.security.keycloakEntitlements}")
    private String role;
    
    @Autowired
    @SuppressWarnings("PMD.UnusedPrivateField")
    DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;

    @Test
    @SneakyThrows
    void givenProductOrderDto_whenCreateOrchestrationPlan_thenOrchestrationPlanSavedSuccessfully() {
        //given
        ProductOrder productOrder = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });

        //when
        OrchestrationPlan orchestrationPlan = orchestrationPlanInitService.createOrchestrationPlan(productOrder);

        assertThat(orchestrationPlan).isNotNull();
    }

    @Test
    void givenProductOrderDtoAndOrchestrationPlanCreatedWithSameOrderId_whenCreateOrchestrationPlan_thenExceptionThrown() {
        ProductOrder productOrder = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });
        OrchestrationPlan orchestrationPlan = OrchestrationPlan.builder()
                .relatedProductOrder(RelatedProductOrder.builder()
                        .id(productOrder.getId())
                        .build())
                .build();
        orchestrationPlanRepository.save(orchestrationPlan);

        assertThrows(CoodNonRecoverableAndNonRetryableException.class, () -> {
            orchestrationPlanInitService.createOrchestrationPlan(productOrder);
        });
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

    @Test
    @SneakyThrows
    void givenProductOrderDTO_whenSentToTestController_thenPublishSuccessfully() {
        setupWiremock();

        //given
        ProductOrder productOrder = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_JSON_FILE, new TypeReference<>() {
        });
        //when

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/cood/test/productOrderStatChange/sendMessage")
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(role))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJsonStringFromObject(productOrder)))
                .andExpect(status().isOk());
        //then
        KafkaConsumer<String, OrchestrationPlanStateChangeEvent> orchestrationPlanEventKafkaConsumer = AbstractIntegrationUtil.createKafkaConsumer(outputTopicDestination, "test-output-group-1", OrchestrationPlanStateChangeEvent.class);
        ConsumerRecords<String, OrchestrationPlanStateChangeEvent> records = orchestrationPlanEventKafkaConsumer.poll(Duration.ofMillis(10));

        for (ConsumerRecord<String, OrchestrationPlanStateChangeEvent> eventConsumerRecord : records) {
            OrchestrationPlanStateChangeEvent actualOrchestrationPlanEvent = eventConsumerRecord.value();
            log.info("orchestration plan id: {}", actualOrchestrationPlanEvent.getEvent().getOrchestrationPlan().getId());
            }

        orchestrationPlanEventKafkaConsumer.unsubscribe();
        orchestrationPlanEventKafkaConsumer.close(Duration.ofMillis(1));
    }

    @Test
    @SneakyThrows
    void givenProductOrderDTOWithDeleteAction_whenSentToTestController_thenPublishSuccessfully() {
        setupWiremock();

        //given
        ProductOrder productOrderDTO = JsonUtil.readObjectFromResource(PRODUCT_ORDER_DTO_DELETE_ACTION_JSON_FILE, new TypeReference<>() {
        });

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/cood/test/productOrderStatChange/sendMessage")
                        .with(jwt().authorities(List.of(new SimpleGrantedAuthority(role))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJsonStringFromObject(productOrderDTO)))
                .andExpect(status().isOk());
        //then

        KafkaConsumer<String, OrchestrationPlanStateChangeEvent> orchestrationPlanEventKafkaConsumer = AbstractIntegrationUtil.createKafkaConsumer(outputTopicDestination, "test-output-group-1", OrchestrationPlanStateChangeEvent.class);
        ConsumerRecords<String, OrchestrationPlanStateChangeEvent> records = orchestrationPlanEventKafkaConsumer.poll(Duration.ofMillis(10));

        for (ConsumerRecord<String, OrchestrationPlanStateChangeEvent> eventConsumerRecord : records) {
            OrchestrationPlanStateChangeEvent actualOrchestrationPlanEvent = eventConsumerRecord.value();
            log.info("orchestration plan id: {}", actualOrchestrationPlanEvent.getEvent().getOrchestrationPlan().getId());
            Optional<OrchestrationPlan> orchestrationPlanOptional = orchestrationPlanRepository.findById(actualOrchestrationPlanEvent.getEvent().getOrchestrationPlan().getId());
            orchestrationPlanOptional.ifPresent(orchestrationPlan -> orchestrationPlan.getOrchestrationPlanNodes().forEach(expectedNode -> {
                OrchestrationPlanNode actualNode = actualOrchestrationPlanEvent.getEvent().getOrchestrationPlan().getOrchestrationPlanNodes().stream().filter(
                        node -> node.getId().equals(expectedNode.getId())
                ).findAny().get();
                Assertions.assertEquals(expectedNode.getRelatedOrchestrationPlanNode(), actualNode.getRelatedOrchestrationPlanNode());
            }));
        }

        orchestrationPlanEventKafkaConsumer.unsubscribe();
        orchestrationPlanEventKafkaConsumer.close(Duration.ofMillis(1));
    }

}
