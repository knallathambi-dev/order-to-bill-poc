// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.CharacteristicSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.EventType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.impl.MockServiceOrderManagementServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records.DeliveryOrderRefRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.CfsDeliveryImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
public class MobileServiceOrderBatchingSteps {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockServiceOrderManagementServiceImpl serviceOrderManagementService;

    @Autowired
    private CfsDeliveryImpl cfsDeliveryImpl;

    private DeliveryOrder deliveryOrder;

    private DeliveryOrderPayloadEvent deliveryOrderPayloadEvent;

    private final Map<String, String> serviceSpecToUrlMap = new HashMap<>();

    @Before
    public void init() {
        serviceSpecToUrlMap.clear();
        deliveryOrder = null;
        deliveryOrderPayloadEvent = null;
        
        // Ensure batching is enabled for mobile service order tests
        ReflectionTestUtils.setField(serviceOrderManagementService, "serviceOrderBatchingEnabled", true);

        wireMockServer.resetAll();
        log.info("Initialized mobile batching tests");
    }

    @Given("batching is disabled")
    public void batchingIsDisabled() {
        ReflectionTestUtils.setField(serviceOrderManagementService, "serviceOrderBatchingEnabled", false);
        log.info("Batching disabled");
    }

    @Given("service specification {string} has service ordering url {string}")
    public void serviceSpecificationHasServiceOrderingUrl(String specId, String url) {
        serviceSpecToUrlMap.put(specId, url);
        log.info("Mapped spec {} -> URL {}", specId, url);
    }

    @And("mobile delivery order event for order with id {string} and factory order id {string} has the following order item refs:")
    public void mobileDeliveryOrderEventForOrderWithIdHasTheFollowingOrderItemRefs(String id, String factoryOrderId, List<DeliveryOrderRefRecord> refs) {
        List<OrderItemRef> orderItemRefList = refs.stream()
                .map(r -> {
                    return OrderItemRef.builder()
                            .productOrderItemId(r.productOrderItemId())
                            .orchestrationNodeId(r.orchestrationNodeId())
                            .productSpecificationRef(ProductSpecificationRef.builder()
                                    .serviceSpecificationRef(List.of(ServiceSpecificationRef.builder()
                                            .id(r.serviceSpecificationId())
                                            // DON'T set href here - it's for service catalog, not service ordering!
                                            .build()))
                                    .build())
                            .action(r.action())
                            .orderItemCharacteristics(List.of(
                                    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic.builder()
                                    .name("volume")
                                    .value("1")
                                    .build()))
                            .build();
                })
                .toList();

        // Always create a NEW delivery order for each step
        deliveryOrder = new DeliveryOrder();
        deliveryOrder.setOrderItemRef(new ArrayList<>());

        deliveryOrder.setId(id);
        deliveryOrder.setFactoryOrderId(factoryOrderId);
        deliveryOrder.getOrderItemRef().addAll(new ArrayList<>(orderItemRefList));
        
        // Get the service ordering URL from the serviceSpecToUrlMap
        String serviceOrderingUrl = "http://localhost:9997/serviceOrdering/v1/serviceOrder"; // default
        if (!orderItemRefList.isEmpty() && !orderItemRefList.get(0).getProductSpecificationRef().getServiceSpecificationRef().isEmpty()) {
            String serviceSpecId = orderItemRefList.get(0).getProductSpecificationRef().getServiceSpecificationRef().get(0).getId();
            String mappedUrl = serviceSpecToUrlMap.get(serviceSpecId);
            if (mappedUrl != null) {
                serviceOrderingUrl = mappedUrl;
            }
        }
        
        deliveryOrder.setDeliveryFactoryRef(DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .href(serviceOrderingUrl)  // THIS IS THE KEY!
                        .build());
        
        log.info("Created delivery order {} with {} item refs, service ordering URL: {}", id, orderItemRefList.size(), serviceOrderingUrl);
    }

    @And("mobile delivery order request has the following data:")
    public void mobileDeliveryOrderHasTheFollowingData(List<DeliveryOrderRefRecord> deliveryOrderRefRecords) throws Exception {
        DeliveryOrderRefRecord deliveryOrderData = deliveryOrderRefRecords.get(0);

        deliveryOrder.setProductOrderId(deliveryOrderData.productOrderId());
        deliveryOrder.setRequestedDeliveryDate(Instant.parse(deliveryOrderData.requestedDeliveryDate()));
        deliveryOrder.setStartDate(Instant.parse(deliveryOrderData.startDate()));
        deliveryOrder.setOrchestrationPlanId(deliveryOrderData.orchestrationPlanId());
        
        // Add related party if not already set
        if (deliveryOrder.getRelatedParty() == null || deliveryOrder.getRelatedParty().isEmpty()) {
            deliveryOrder.setRelatedParty(new ArrayList<>());
        }

        deliveryOrderPayloadEvent = DeliveryOrderPayloadEvent.builder()
                .deliveryOrder(deliveryOrder)
                .build();
        
        log.debug("Created payload event with delivery order: {}", deliveryOrder.getId());
    }

    @And("service catalog returns service specification {string} for id {string}")
    public void serviceCatalogReturnsServiceSpecificationForId(String specName, String specId) throws Exception {
        CharacteristicSpecification serviceCharacteristic =
                CharacteristicSpecification.builder()
                        .id(specId)
                        .name("volume")
                        .build();
        Set<CharacteristicSpecification> characteristicSpecificationSet = new HashSet<>();
        characteristicSpecificationSet.add(serviceCharacteristic);
        
        com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification serviceSpecification =
                com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification
                        .builder()
                        .id(specId)
                        .specCharacteristic(characteristicSpecificationSet)
                        .build();

        wireMockServer.stubFor(get("/serviceCatalogManagement/v1/serviceSpecification?id=" + specId)
                .willReturn(ok().withBody(objectMapper.writeValueAsString(List.of(serviceSpecification)))
                        .withHeader("Content-Type", "application/json")));
                        
        log.info("Stubbed service catalog for {}", specId);
    }

    @And("service order batch response returns successfully for url {string}")
    public void serviceOrderBatchResponseReturnsSuccessfullyForUrl(String fullUrl) throws Exception {
        String batchPath = fullUrl.replace("http://localhost:9997", "");
        
        log.debug("Stubbing batch endpoint: {}", batchPath);
        
        ServiceOrder responseOrder = ServiceOrder.builder()
                .id("BATCH-RESP-" + System.currentTimeMillis())
                .serviceOrderItem(List.of(
                        ServiceOrderItem.builder()
                                .id("BATCH-ITEM-" + System.currentTimeMillis())
                                .action(ServiceOrderItem.Action.ADD)
                                .build()
                ))
                .build();

        wireMockServer.stubFor(post(urlEqualTo(batchPath))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(List.of(responseOrder)))));
                        
        log.info("Stubbed batch endpoint: {}", batchPath);
    }

    @When("the mobile batching system consumes delivery order event on topic {string} with factory order id {string} and state {string}")
    public void theMobileBatchingSystemConsumesDeliveryOrderEventOnTopicWithFactoryOrderIdAndState(String topicName, String factoryOrderId, String state) {
        // Create the event with our delivery order that has the mobile URL in DeliveryFactoryRef.href
        log.debug("Processing delivery order payload event");
        if (deliveryOrderPayloadEvent != null && deliveryOrderPayloadEvent.getDeliveryOrder() != null) {
            DeliveryOrder order = deliveryOrderPayloadEvent.getDeliveryOrder();
            log.debug("Payload DO ID: {}", order.getId());
            log.debug("Payload DO Factory ID: {}", order.getFactoryOrderId());
            if (order.getDeliveryFactoryRef() != null) {
                log.debug("Payload DO DeliveryFactoryRef href: {}", order.getDeliveryFactoryRef().getHref());
            }
        } else {
            log.warn("Payload event is null!");
        }
        
        DeliveryOrderEvent deliveryStartEvent = DeliveryOrderEvent.builder()
                .event(deliveryOrderPayloadEvent)
                .eventId("mobile-batch-test-" + System.currentTimeMillis())
                .eventTime(Instant.now())
                .eventType(EventType.DELIVERY_ORDER_EVENT.getValue())
                .build();

        log.debug("Calling CFS delivery directly");
        try {
            cfsDeliveryImpl.deliver(deliveryStartEvent);
            log.debug("Delivery completed");
        } catch (Exception e) {
            // Expected - service order call might fail but we just want to verify the batch endpoint was called
            log.debug("Delivery exception (expected): {}", e.getMessage());
        }
    }

    @When("wait for {int} seconds")
    public void waitForSeconds(int seconds) throws InterruptedException {
        log.info("Waiting {} seconds", seconds);
        await().pollDelay(Duration.ofSeconds(seconds))
                .atMost(Duration.ofSeconds(seconds + 1))
                .until(() -> true);
    }

    @Then("a batched POST request should be made to {string} with {int} service orders")
    public void aBatchedPostRequestShouldBeMadeToWithServiceOrders(String expectedPath, int expectedCount) {
        log.debug("Verifying batch request to: {}", expectedPath);
        
        // Check all POST requests made
        var allRequests = wireMockServer.findAll(postRequestedFor(urlMatching(".*")));
        log.debug("Total POST requests: {}", allRequests.size());
        allRequests.forEach(req -> log.debug("POST to: {}", req.getUrl()));
        
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            try {
                wireMockServer.verify(postRequestedFor(urlPathMatching(".*" + expectedPath)));
                
                var requests = wireMockServer.findAll(postRequestedFor(urlPathMatching(".*" + expectedPath)));
                assertThat(requests).as("Should have requests to " + expectedPath).isNotEmpty();
                
                String requestBody = requests.get(0).getBodyAsString();
                List<ServiceOrder> serviceOrders = objectMapper.readValue(
                        requestBody,
                        new TypeReference<List<ServiceOrder>>() {}
                );
                
                assertThat(serviceOrders).hasSize(expectedCount);
                log.info("Verified batch POST to {} with {} orders", expectedPath, expectedCount);
            } catch (Exception e) {
                log.error("Verification failed: {}", e.getMessage());
                throw e;
            }
        });
    }

    @Then("a non-batched POST request should be made to {string}")
    public void aNonBatchedPostRequestShouldBeMadeTo(String expectedPath) {
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            wireMockServer.verify(postRequestedFor(urlPathMatching(".*" + expectedPath)));
            log.info("Verified non-batch POST to {}", expectedPath);
        });
    }

    @Then("no batched POST request should be made to {string}")
    public void noBatchedPostRequestShouldBeMadeTo(String path) {
        await().pollDelay(Duration.ofSeconds(2))
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var requests = wireMockServer.findAll(postRequestedFor(urlPathMatching(".*" + path)));
                    assertThat(requests).isEmpty();
                    log.info("Verified no batch POST to {}", path);
                });
    }
}
