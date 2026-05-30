// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.AbstractIntegrationUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.OrchestrationPlanNodeStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl.OrchestrationPlanStateChangeEventHandlerImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.dto.v1.ServiceRef;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class CommonStepDefinitions {

    public static Instant fixedInstant;

    @Autowired
    OrchestrationPlanRepository orchestrationPlanRepository;

    @Autowired
    private KafkaTemplate<String, OrchestrationPlanNodeStateChangeEvent> kafkaTemplate;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String PRODUCT_MANAGEMENT_URL = "/productInventoryManagement/v1/product";

    @Autowired
    private OrchestrationPlanNodeStateChangeEventHandler orchestrationPlanNodeStateChangeEventHandler;

    @Autowired
    private OrchestrationPlanStateChangeEventHandlerImpl orchestrationPlanStateChangeEventHandler;

    @Autowired
    private UpdateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler updateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final Duration timeout = Duration.ofSeconds(20L);

    @Before
    public void beforeEach() {
        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
        wireMockServer.resetAll();
    }

    @Given("the system has orchestration plan with id {string} and state {string}")
    public void theSystemHasOrchestrationPlanWithIdAndState(String planId, String planState) {
        OrchestrationPlan orchestrationPlan = OrchestrationPlan.builder()
                .id(planId)
                .state(State.fromValue(planState))
                .relatedParty(List.of(RelatedParty.builder().id("231-mf4").role("customer").name("Abir").href(null).build()))
                .relatedProductOrder(RelatedProductOrder.builder().id("order1").build())
                .build();

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @Given("the system has the following orchestration plans:")
    public void theSystemHasTheFollowingOrchestrationPlans(List<OrchestrationPlanRecord> orchestrationPlanRecords) {
        orchestrationPlanRecords.forEach(orchestrationPlanRecord -> {
            boolean nodeHasActualOrderCompletionDate = Objects.nonNull(orchestrationPlanRecord.actualOrderCompletionDate());
            OrchestrationPlan orchestrationPlan = OrchestrationPlan.builder()
                    .id(orchestrationPlanRecord.planId())
                    .state(orchestrationPlanRecord.state() != null ? State.fromValue(orchestrationPlanRecord.state()) : null)
                    .relatedContractName(orchestrationPlanRecord.contractName())
                    .orchestrationPlanSchedule(OrchestrationPlanSchedule.builder().actualOrderStartDate(orchestrationPlanRecord.actualOrderStartDate() != null ? Instant.parse(orchestrationPlanRecord.actualOrderStartDate()) : null).build())
                    .relatedParty(List.of(RelatedParty.builder().id("231-mf4").role("customer").name("Abir").href(null).build()))
                    .relatedProductOrder(RelatedProductOrder.builder().id("order1").build())
                    .receivedDate(orchestrationPlanRecord.receivedDate() != null ? Instant.parse(orchestrationPlanRecord.receivedDate()) : null)
                    .requestedDeliveryDate(orchestrationPlanRecord.requestedDeliveryDate() != null ? Instant.parse(orchestrationPlanRecord.requestedDeliveryDate()) : null)
                    .orchestrationPlanNodes(
                            Set.of(
                                    OrchestrationPlanNode
                                            .builder()
                                            .state(nodeHasActualOrderCompletionDate ? OrchestrationPlanNodeState.COMPLETED : OrchestrationPlanNodeState.IN_PROGRESS)
                                            .orchestrationNodeSchedule(OrchestrationNodeSchedule.builder()
                                                    .actualOrderItemCompletionDate(nodeHasActualOrderCompletionDate ? Instant.parse(orchestrationPlanRecord.actualOrderCompletionDate()) : null)
                                                    .build())
                                            .build()
                            )
                    )
                    .build();

            orchestrationPlanRepository.save(orchestrationPlan);
        });
    }

    @And("the plan with id {string} has the following nodes:")
    public void thePlanWithIdHasOrchestrationPlanNodesWithTheFollowingData(String planId, List<OrchestrationPlanNodeRecord> nodesList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanById(planId).orElseThrow(() -> new RuntimeException("No orchestration plan with id " + planId));

        Set<OrchestrationPlanNode> nodesSet = nodesList.stream().map(node -> OrchestrationPlanNode.builder()
                .id(node.id())
                .relatedProductOrder(Objects.nonNull(node.orderId()) ? RelatedProductOrder.builder().id(node.orderId()).build() : null)
                .state(OrchestrationPlanNodeState.fromValue(node.state()))
                .previousState(Objects.nonNull(node.previousState()) ? OrchestrationPlanNodeState.fromValue(node.previousState()) : null)
                .relatedProduct(List.of(RelatedProduct.builder()
                        .id("id")
                        .type(RelatedProductType.PHYSICAL_PRODUCT)
                        .relationshipType(RelatedProductRelationType.DELIVERS)
                        .productOrderItemId("pOId")
                        .realisingService(
                                List.of(RealisingService.builder()
                                        .id(node.realizingServiceId())
                                        .href(node.realizingServiceHref())
                                        .build()))
                        .build()))
                .relatedProductOrderItem(List.of(RelatedProductOrderItem.builder()
                        .id("id")
                        .action("")
                        .build()))
                .orchestrationNodeSchedule(OrchestrationNodeSchedule.builder()
                        .orderItemStartDate(node.orderItemStartDate() != null ? Instant.parse(node.orderItemStartDate()) : null)
                        .build())
                .relatedServiceOrder(RelatedServiceOrder.builder()
                        .id("id")
                        .build())
                .build()).collect(Collectors.toSet());

        orchestrationPlan.setOrchestrationPlanNodes(nodesSet);
        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the system orchestration plan node state with id {string} still be {string}")
    @And("the system orchestration plan node state with id {string} will be {string}")
    public void orchestrationPlanNodeStateWithIdWillBe(String nodeId, String state) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            Optional<OrchestrationPlanNode> orchestrationPlanNode = orchestrationPlanRepository.findOrchestrationPlanNodeById(nodeId);
            assertThat(orchestrationPlanNode).isPresent();
            assertThat(orchestrationPlanNode.get().getState().value()).isEqualTo(state);
        });
    }

    @When("the orchestration plan node with id {string} in plan {string} is updated to state {string}")
    public void updateOrchestrationPlanNodeState(String nodeId, String planId, String state) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanById(planId).orElseThrow(() -> new RuntimeException("No orchestration plan with id " + planId));
        orchestrationPlan.getOrchestrationPlanNodes().forEach(node -> {
            if (node.getId().equals(nodeId)) {
                node.setState(OrchestrationPlanNodeState.fromValue(state));
            }
        });

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @When("the system consumes orchestration plan node state change event on topic {string} with node id {string} and state {string}")
    public void theTopicReceivesEventWithNodeWithStatus(String topicName, String nodeId, String status) {
        String deadLetterKeyword = "dlt";

        OrchestrationPlanNode node = orchestrationPlanRepository.findOrchestrationPlanNodeById(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));
        node.setState(OrchestrationPlanNodeState.fromValue(status));

        OrchestrationPlanNodeStateChangeEvent planNodeStateChangeEvent = OrchestrationPlanNodeStateChangeEvent.builder()
                .event(OrchestrationPlanNodeStateChangePayloadEvent.builder().orchestrationPlanNode(node).build())
                .eventType(OrchestrationPlanNodeStateChangeEvent.class.getSimpleName())
                .eventId("id")
                .eventTime(Instant.MAX)
                .build();

        if (Objects.isNull(fixedInstant) || topicName.contains(deadLetterKeyword)) {
            kafkaTemplate.send(topicName, planNodeStateChangeEvent);
            return;
        }

        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            mockedInstant.when(Instant::now).thenReturn(fixedInstant);
            orchestrationPlanNodeStateChangeEventHandler.handleEvent(planNodeStateChangeEvent);
            updateOrchestrationPlanStateBasedOnNodeStateChangeEventHandler.handleEvent(planNodeStateChangeEvent);
        }
    }

    @Then("The plan {string} is not going to be updated by the system")
    public void thePlanIsNotGoingToBeUpdatedByTheSystem(String planId) {
        // this step is added for business clarity, the actual result is verified in the following steps
    }

    @And("The orchestration plan {string} is going to have status {string}")
    public void theOrchestrationPlanIsGoingToHaveStatus(String planId, String state) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            Optional<OrchestrationPlan> orchestrationPlan = orchestrationPlanRepository.findById(planId);
            assertThat(orchestrationPlan).isPresent();
            assertThat(orchestrationPlan.get().getState().value()).isEqualTo(state);
        });
    }

    @And("The orchestration plan is not going to fire any event to topic {string}")
    public void theOrchestrationPlanIsNotGoingToFireAnyEventToTopic(String topicName) {
        // this step should be implemented when refactoring the deprecated feature files to the mocked features
    }

    @Then("The orchestration plan {string} is going to be updated with status {string}")
    public void theOrchestrationPlanIsGoingToBeUpdatedWithStatus(String planId, String state) {
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            Optional<OrchestrationPlan> orchestrationPlan = orchestrationPlanRepository.findById(planId);
            assertThat(orchestrationPlan).isPresent();
            assertThat(orchestrationPlan.get().getState().value()).isEqualTo(state);
        });
    }

    @And("The system is going to send {string} event for plan {string} to topic {string} with status {string}")
    public void theSystemIsGoingToSendEventForPlanToTopicWithStatus(String eventName, String planId, String topicName, String state) {
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            KafkaConsumer<String, OrchestrationPlanStateChangeEvent> orchestrationPlanEventKafkaConsumer = AbstractIntegrationUtil.createKafkaConsumer(topicName, "test-input-group-2", OrchestrationPlanStateChangeEvent.class);
            ConsumerRecords<String, OrchestrationPlanStateChangeEvent> orchestrationPlanEventConsumerRecords = orchestrationPlanEventKafkaConsumer.poll(Duration.ofSeconds(5));
            assertThat(orchestrationPlanEventConsumerRecords.count()).isNotZero();

            orchestrationPlanEventKafkaConsumer.unsubscribe();
            orchestrationPlanEventKafkaConsumer.close(Duration.ofMillis(1));
        });
    }

    @And("the CPIB has installed product with id {string} and type {string} associated to order id {string} and order item id {string} in {string} main state and {string} operational state")
    public void theCPIBHasInstalledProductWithIdAndTypeAssociatedToOrderIdAndOrderItemIdInStateAndOperationalState(String productId, String productType, String orderId, String orderItemId, String state, String operationalState) throws JsonProcessingException {
        Product product = Product.builder()
                .id(productId)
                .status(ProductStatusType.fromValue(state))
                .operationalStatus(ProductOperationalStatusType.fromValue(operationalState))
                .atType(productType)
                .build();
        wireMockServer.stubFor(get(urlPathEqualTo(PRODUCT_MANAGEMENT_URL))
                .withQueryParam("productOrderItem.productOrderId", equalTo(orderId))
                .withQueryParam("productOrderItem.orderItemId", equalTo(orderItemId))
                .willReturn(ok().withBody("[%s]".formatted(objectMapper.writeValueAsString(product))).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @And("the PATCH request for the CPIB installed product with id {string} and type {string} and {string} main state and {string} operational state returns success")
    public void thePatchRequestForTheCPIBInstalledProductWithIdAndTypeAndMainStateAndOperationalStateReturnsSuccess(String productId, String productType, String state, String operationalState) throws JsonProcessingException {
        Product product = Product.builder()
                .id(productId)
                .status(ProductStatusType.fromValue(state))
                .operationalStatus(ProductOperationalStatusType.fromValue(operationalState))
                .atType(productType)
                .build();

        wireMockServer.stubFor(patch(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}")))
                .withPathParam("id", equalTo(productId))
                .withRequestBody(matchingJsonPath("$.status", equalTo(state)))
                .withRequestBody(matchingJsonPath("$.operationalStatus", equalTo(operationalState)))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody((objectMapper.writeValueAsString(product)))));
    }

    @Then("the CPIB installed product with id {string} and type {string} main state will be {string} and operational state will be {string}")
    public void theCPIBInstalledProductWithIdStateWillBeAndOperationalStateWillBe(String productId, String productType, String state, String operationalState) {
        Product product = Product.builder()
                .id(productId)
                .atType(productType)
                .status(ProductStatusType.fromValue(state))
                .operationalStatus(ProductOperationalStatusType.fromValue(operationalState))
                .productOrderItem(List.of())
                .build();
        Awaitility.await().atMost(Duration.ofSeconds(50)).untilAsserted(() -> {
            wireMockServer.verify(1, patchRequestedFor(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}")))
                    .withPathParam("id", equalTo(productId))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(product), true, true))
            );
        });
    }

    @Then("the CPIB installed product with id {string} will not be updated")
    public void theCPIBInstalledProductWithIdAndTypeWillNotBeUpdated(String productId) {
        Awaitility.await().atMost(Duration.ofSeconds(50)).untilAsserted(() -> {
            wireMockServer.verify(0, patchRequestedFor(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}")))
                    .withPathParam("id", equalTo(productId))
            );
        });
    }


    @And("the node with id {string} has the following shipping orders:")
    public void theNodeWithHasShippingOrderWithTheFollowingData(String nodeId, List<OrchestrationPlanNodeRecord> shipingOrderList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode node = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(planNode -> planNode.getId().equals(nodeId)).findFirst().get();

        RelatedSupplyChainOrder relatedSupplyChainOrder = RelatedSupplyChainOrder.builder()
                .id(shipingOrderList.get(0).shippingOrderId())
                .orderItemId(shipingOrderList.get(0).shippingOrderId())
                .build();

        node.setRelatedSupplyChainOrder(relatedSupplyChainOrder);

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the node with id {string} has the following realizing services:")
    public void theNodeWithHasRealizingServiceWithTheFollowingData(String nodeId, List<OrchestrationPlanNodeRecord> realizingDataList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode node = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(planNode -> planNode.getId().equals(nodeId)).findFirst().get();
        List<RealisingService> realisingServices = realizingDataList.stream().map(realizingData -> RealisingService.builder()
                .id(realizingData.realizingServiceId())
                .href(realizingData.realizingServiceHref())
                .build()
        ).toList();

        RelatedProduct product = node.getRelatedProduct().stream().filter(relatedProduct -> RelatedProductRelationType.DELIVERS.equals(relatedProduct.getRelationshipType())).findFirst().get();
        product.setRealisingService(realisingServices);

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @Then("the CPIB installed product with id {string} and type {string} main state will be {string} and operational state will be {string} with the following realizing service data:")
    public void theCPIBInstalledProductWithIdAndTypeMainStateWillBeAndOperationalStateWillBeWithTheFollowingRealizingServiceData(String productId, String productType, String state, String operationalState, List<OrchestrationPlanNodeRecord> realizingDataList) {
        List<ServiceRef> serviceRefs = (List<ServiceRef>) realizingDataList.stream().map(realizingData -> ServiceRef.builder()
                .id(realizingData.realizingServiceId())
                .href(realizingData.realizingServiceHref())
                .build()
        ).toList();

        Product product = Product.builder()
                .id(productId)
                .atType(productType)
                .status(ProductStatusType.fromValue(state))
                .operationalStatus(ProductOperationalStatusType.fromValue(operationalState))
                .productOrderItem(List.of())
                .realizingService(serviceRefs)
                .build();
        Awaitility.await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            wireMockServer.verify(1, patchRequestedFor(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}")))
                    .withPathParam("id", equalTo(productId))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(product), true, true))
            );
        });
    }

    @Given("the system has orchestration plan with id {string}, state {string} and order id {string}")
    public void theSystemHasOrchestrationPlanWithIdStateAndOrderId(String planId, String planState, String orderId) {
        OrchestrationPlan orchestrationPlan = OrchestrationPlan.builder()
                .id(planId)
                .state(State.fromValue(planState))
                .relatedParty(List.of(RelatedParty.builder().id("231-mf4").role("customer").name("Abir").href(null).build()))
                .relatedProductOrder(RelatedProductOrder.builder().id(orderId).build())
                .build();

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the system will fire orchestration plan node state change event on topic {string} with node id {string} and state {string}")
    public void theSystemWillFireOrchestrationPlanNodeStateChangeEventOnTopicWithNodeIdAndState(String topicName, String nodeId, String state) {
        Awaitility.await().atMost(timeout).untilAsserted(() -> {
            ArgumentCaptor<OrchestrationPlanNode> nodeCaptor = ArgumentCaptor.forClass(OrchestrationPlanNode.class);
            Mockito.verify(eventPublisher, atLeastOnce()).publishEvent(eq(CDCEvent.fromTopicName(topicName)), nodeCaptor.capture(), ArgumentMatchers.any());
            List<OrchestrationPlanNode> nodes = nodeCaptor.getAllValues();
            assertThat(nodes.stream().map(OrchestrationPlanNode::getId).collect(Collectors.toSet())).contains(nodeId);
            nodes.stream().filter(node -> nodeId.equals(node.getId())).findAny().ifPresent(node -> {
                assertThat(node.getState().value()).isEqualTo(state);
            });
        });
    }

    @And("the PATCH request for CPIB installed product with id {string} returns service unavailable")
    public void thePATCHRequestForCPIBInstalledProductWithIdReturnsServiceUnavailable(String productId) {
        wireMockServer.stubFor(patch(urlEqualTo("/productInventoryManagement/v1/product/" + productId))
                .willReturn(serviceUnavailable()));
        wireMockServer.stubFor(post("/processManagement/v1/processFlow").willReturn(ok().withBody("{}").withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @And("the PATCH request for the CPIB installed product with id {string} and type {string} and {string} main state and {string} operational state returns service unavailable")
    public void thePATCHRequestForTheCPIBInstalledProductWithIdAndTypeAndMainStateAndOperationalStateReturnsServiceUnavailable(String productId, String productType, String state, String operationalState) {
        wireMockServer.stubFor(patch(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}")))
                .withPathParam("id", equalTo(productId))
                .withRequestBody(matchingJsonPath("$.status", equalTo(state)))
                .withRequestBody(matchingJsonPath("$.operationalStatus", equalTo(operationalState)))
                .willReturn(serviceUnavailable()));
        wireMockServer.stubFor(post("/processManagement/v1/processFlow").willReturn(ok().withBody("{}").withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @And("the following specifications are missed from product catalog")
    public void theFollowingSpecificationsAreMissedFromProductCatalog(List<ProductSpecificationRecord> productSpecificationRecords) {
        // product catalog has no specifications
    }

    @Then("the system will create orchestration plan without nodes with order id {string} and state {string}")
    public void theSystemWillCreateOrchestrationPlanWithoutNodesWithOrderIdAndState(String orderId, String state) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(orderId));
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(orchestrationPlan).isNotNull();
            assertThat(orchestrationPlan.getState()).isEqualTo(State.fromValue(state));
            assertThat(orchestrationPlan.getOrchestrationPlanNodes()).isEmpty();
        });
    }

    @And("the plan with id {string} has no orchestration plan nodes")
    public void thePlanWithIdHasNoOrchestrationPlanNodes(String planId) {
        // the plan has no orchestration plan nodes
    }

    @Given("the CPIB has installed product with id {string} and type {string} associated to order id {string} and order item id {string} in {string} main state and {string} operational state and the following characteristics")
    public void theCPIBHasInstalledProductWithIdAndTypeAssociatedToOrderIdAndOrderItemIdInMainStateAndOperationalStateAndTheFollowingCharacteristics(String productId, String productType, String orderId, String orderItemId, String state, String operationalState, List<CharacteristicRecord> characteristicRecords) throws JsonProcessingException {
        Product product = Product.builder()
                .id(productId)
                .status(ProductStatusType.fromValue(state))
                .operationalStatus(ProductOperationalStatusType.fromValue(operationalState))
                .atType(productType)
                .productCharacteristic(characteristicRecords.stream().map(characteristicRecord ->
                        com.orange.discobole.productinventory.dto.v1.Characteristic.builder().id(characteristicRecord.id()).name(characteristicRecord.name()).build()).collect(Collectors.toList()))
                .build();
        wireMockServer.stubFor(get(urlPathEqualTo(PRODUCT_MANAGEMENT_URL))
                .withQueryParam("productOrderItem.productOrderId", equalTo(orderId))
                .withQueryParam("productOrderItem.orderItemId", equalTo(orderItemId))
                .willReturn(ok().withBody("[%s]".formatted(objectMapper.writeValueAsString(product))).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @And("the node {string} actual related product has the following characteristics:")
    public void theNodeActualRelatedProductHasTheFollowingCharacteristics(String nodeId, List<CharacteristicRecord> characteristicRecords) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeById(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan node with id " + nodeId));
        orchestrationPlan.getOrchestrationPlanNodes().remove(orchestrationPlanNode);

        Set<Characteristic> characteristics = characteristicRecords.stream().map(
                characteristicRecord -> Characteristic.builder()
                        .id(characteristicRecord.id())
                        .name(characteristicRecord.name()).build()).collect(Collectors.toSet());

        orchestrationPlanNode.getActualRelatedProductOptional().get().setProductCharacteristic(characteristics);

        orchestrationPlan.getOrchestrationPlanNodes().add(orchestrationPlanNode);
        orchestrationPlanRepository.save(orchestrationPlan);

    }

    @And("the CPIB installed product {string} will be patched with the following characteristics:")
    public void theCPIBInstalledProductWillBePatchedWithTheFollowingCharacteristics(String productId, List<CharacteristicRecord> characteristicRecords) {
        List<com.orange.discobole.productinventory.dto.v1.Characteristic> productCharacteristics =
                characteristicRecords.stream().map(
                        characteristicRecord -> com.orange.discobole.productinventory.dto.v1.Characteristic.builder()
                                .id(characteristicRecord.id())
                                .name(characteristicRecord.name()).build()).collect(Collectors.toList());

        Product product = Product.builder()
                .id(productId)
                .productCharacteristic(productCharacteristics)
                .build();
        Awaitility.await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            // note that wire mock server could be checked multiple times giving the same result
            // in a previous step we check for the CPIB patched body
            // in this step we check only for the characteristics
            wireMockServer.verify(1, patchRequestedFor(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}")))
                    .withPathParam("id", equalTo(productId))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(product), true, true))
            );
        });
    }

    @When("the system consumes orchestration plan state change event on topic {string} with plan id {string} and state {string}")
    public void theSystemConsumesOrchestrationPlanStateChangeEventOnTopicWithPlanIdAndState(String topicName, String planId, String status) {
        try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class, withSettings().defaultAnswer(InvocationOnMock::callRealMethod))) {
            Instant currentTime = Objects.nonNull(fixedInstant) ? fixedInstant : Instant.now();
            mockedInstant.when(Instant::now).thenReturn(currentTime);

            OrchestrationPlan plan = orchestrationPlanRepository.findOrchestrationPlanById(planId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan with id " + planId));
            plan.setState(State.fromValue(status));

            OrchestrationPlanStateChangeEvent planStateChangeEvent = OrchestrationPlanStateChangeEvent.builder()
                    .event(OrchestrationPlanStateChangePayloadEvent.builder().orchestrationPlan(plan).build())
                    .eventType(OrchestrationPlanNodeStateChangeEvent.class.getSimpleName())
                    .eventId("id")
                    .eventTime(Instant.MAX)
                    .build();

            orchestrationPlanStateChangeEventHandler.handleEvent(planStateChangeEvent);
        }
    }

    @And("the node with id {string} has the following related service orders:")
    public void theNodeWithIdHasRelatedServiceOrderWithTheFollowingData(String nodeId, List<OrchestrationPlanNodeRecord> relatedServiceOrderList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow();
        OrchestrationPlanNode node = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(planNode -> planNode.getId().equals(nodeId)).findFirst().get();
        RelatedServiceOrder relatedServiceOrder = RelatedServiceOrder.builder()
                .id(relatedServiceOrderList.get(0).id())
                .somRef(relatedServiceOrderList.get(0).somRef())
                .orderItemId(relatedServiceOrderList.get(0).orderItemId())
                .build();

        node.setRelatedServiceOrder(relatedServiceOrder);

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @Then("The system will create orchestration plan with order id {string} and state {string}")
    public void theSystemWillCreateOrchestrationPlanWithOrderIdAndState(String orderId, String state) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(orderId));
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(orchestrationPlan).isNotNull();
            assertThat(orchestrationPlan.getState()).isEqualTo(State.fromValue(state));
        });
    }

    @Then("The system will create orchestration plan with order id {string}, state {string} and contract name {string}")
    public void theSystemWillCreateOrchestrationPlanWithOrderIdStateAndContractName(String orderId, String state, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("relatedProductOrder.id").is(orderId));
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            OrchestrationPlan orchestrationPlan = mongoTemplate.findOne(query, OrchestrationPlan.class);
            assertThat(orchestrationPlan).isNotNull();
            assertThat(orchestrationPlan.getState()).isEqualTo(State.fromValue(state));
            assertThat(orchestrationPlan.getRelatedContractName()).isEqualTo(name);
        });
    }

    @And("the POST request for the process flow returns success")
    public void thePOSTRequestForTheProcessFlowReturnsSuccess() {
        wireMockServer.stubFor(post("/processManagement/v1/processFlow").willReturn(ok().withBody("{}").withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    @And("the plan with order id {string} has requested delivery date {string}")
    public void thePlanWithIdHasRequestedDeliveryDate(String orderId, String requestedDeliveryDate) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

        orchestrationPlan.setRequestedDeliveryDate(Instant.parse(requestedDeliveryDate));

        orchestrationPlanRepository.save(orchestrationPlan);
    }

    @And("the orchestration node delivering order item {string} in order {string} will be in state {string}")
    public void theOrchestrationNodeDeliveringOrderItemWillBeInState(String orderItemId, String orderId, String state) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeByOrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("No orchestration plan with order item id " + orderItemId));

        assertThat(orchestrationPlanNode.getState()).isEqualTo(state);
    }

    @And("the order item {string} in order {string} will contain error message {string}")
    public void theOrchestrationNodeWillContainErrorMessage(String orderId, String orderItemId, String errorMessage) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeByOrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("No orchestration plan with order item id " + orderItemId));

        assertEquals(errorMessage, orchestrationPlanNode.getErrorMessage().get(0).getMessage());
    }

    @And("the plan with product order {string} which contains order item {string} will have the following related products")
    public void thePlanWithProductOrderWhichContainsOrderItemWillHaveTheFollowingRelatedProducts(String orderId, String orderItemId, List<OrchestrationPlanNodeRecord> productsList) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByRelatedProductOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Could not find orchestration plan with order id " + orderId));

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodeByOrderItemId(orderItemId)
                .orElseThrow(() -> new RuntimeException("No orchestration plan with order item id " + orderItemId));

        assertThat(orchestrationPlanNode.getRelatedProduct().size()).isEqualTo(productsList.size());

        productsList.forEach(product -> {
            Optional<RelatedProduct> relatedProduct = orchestrationPlanNode.getRelatedProduct().stream().filter(relatedProduct1 -> Objects.equals(relatedProduct1.getProductOrderItemId(), product.orderItemId())).findFirst();
            assertThat(relatedProduct).isPresent();
            assertThat(relatedProduct.get().getRelationshipType()).isEqualTo(RelatedProductRelationType.fromValue(product.relationshipType()));
            assertThat(relatedProduct.get().getType()).isEqualTo(RelatedProductType.fromValue(product.productType()));
        });
    }

    @And("the node with id {string} and order item id {string} has the fallowing shipping characteristics:")
    public void theNodeWithIdAndOrderItemHasTheFallowingShippingCharacteristics(String nodeId, String orderItemId, ShippingCharacteristicRecord shippingCharacteristicRecord) {
        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(nodeId).orElseThrow(() -> new RuntimeException("Could not find orchestration plan with node id " + nodeId));
        orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(node -> nodeId.equals(node.getId()))
                .findFirst()
                .flatMap(node -> node.getRelatedProduct().stream()
                        .filter(relatedProduct -> orderItemId.equals(relatedProduct.getProductOrderItemId())).findFirst())
                .ifPresent(relatedProduct -> {
                    relatedProduct.setProductCharacteristic(
                            Set.of(
                                    StringCharacteristic.builder().name("Shipping Address").value(shippingCharacteristicRecord.shippingAddress()).atType(StringCharacteristic.class.getSimpleName()).build(),
                                    StringCharacteristic.builder().name("Shipping mode").value(shippingCharacteristicRecord.shippingMode()).atType(StringCharacteristic.class.getSimpleName()).build(),
                                    DateCharacteristic.builder().name("Requested delivery date").value(shippingCharacteristicRecord.requestedDeliveryDate()).atType(DateCharacteristic.class.getSimpleName()).build()
                            )
                    );
                });
        orchestrationPlanRepository.save(orchestrationPlan);
    }
}
