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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.generated.fallout.FalloutIncidentStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.AbstractIntegrationUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.RelatedEntityRole;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.enums.ResolutionState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.MongoTemplateWrapperService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.like;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.mongodb.assertions.Assertions.assertTrue;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.CleanMongoDBExtension.mongo;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic.FALLOUT_INCIDENT_STATE_CHANGE_TOPIC;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions.PRODUCT_MANAGEMENT_URL;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
public class ProcessFalloutStateChange {
    public static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final String FALLOUT_JSON = "/integration/fallouts/FalloutIncident.json";
    @Autowired
    private final KafkaTemplate<String, FalloutIncidentStateChangeEvent> kafkaTemplateDelivery;

    protected MockMvc mvc;
    private OrchestrationPlan orchestrationPlan;
    private FalloutIncident falloutIncident;
    private OrchestrationPlanNode orchestrationPlanNode;
    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MongoTemplateWrapperService mongoTemplateWrapperService;

    @Autowired
    private ObjectMapper objectMapper;

    public ProcessFalloutStateChange(KafkaTemplate<String, FalloutIncidentStateChangeEvent> kafkaTemplateDelivery) {
        this.kafkaTemplateDelivery = kafkaTemplateDelivery;
    }


    @Before("@BeforeProcessFalloutStateChange")
    @SneakyThrows
    public void setup() {
        log.info("initialize testing");
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        new MongoTemplate(mongo(), "COOD").dropCollection("orchestrationPlan");
        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
        wireMockServer.resetAll();
        wireMockServer.stubFor(get("\\/productManagement\\/v1\\/product.*").willReturn(ok().withBody("{}").withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        wireMockServer.stubFor(post(urlPathMatching("\\/processManagement\\/v1\\/processFlow.*")).willReturn(ok().withBody("{}").withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }


    @Given("an orchestration node has an associated fallout with json {string}")
    public void an_orchestration_has_an_associated_fallout(String jsonFile) throws Throwable {
        log.info("Start testing");
        this.orchestrationPlan = JsonUtil.readObjectFromResource("/integration/%s".formatted(jsonFile), new TypeReference<>() {
        });
        this.orchestrationPlan = mongoTemplateWrapperService.save(this.orchestrationPlan);
        this.orchestrationPlan.getOrchestrationPlanNodes().forEach(this::mockGetProducts);
        this.orchestrationPlan.getOrchestrationPlanNodes().forEach(this::mockPatchProducts);
        orchestrationPlanNode = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(node -> Objects.equals(node.getState(), HELD)).findFirst().get();
        falloutIncident = JsonUtil.readObjectFromResource(FALLOUT_JSON, new TypeReference<>() {
        });
        falloutIncident.getRelatedEntity().stream().findFirst().filter(relatedEntity -> relatedEntity.getRole().equals(RelatedEntityRole.INITIATOR))
                .ifPresent(relatedEntity -> {
                    relatedEntity.setId(orchestrationPlanNode.getId());
                    relatedEntity.setAtReferredType(OrchestrationPlanNode.class.getSimpleName());
                });
        falloutIncident.getRelatedEntity().stream().filter(relatedEntity -> relatedEntity.getAtReferredType().equals(OrchestrationPlan.class.getSimpleName()))
                .findFirst()
                .ifPresent(relatedEntity -> {
                    relatedEntity.setId(orchestrationPlan.getId());
                });
    }

    private void mockPatchProducts(OrchestrationPlanNode node) {
        Product product = Product.builder()
                .id(node.getActualRelatedProductOptional().get().getId())
                .status(ProductStatusType.fromValue("Active"))
                .operationalStatus(ProductOperationalStatusType.fromValue("Active"))
                .atType("Product")
                .build();

        wireMockServer.stubFor(patch(urlPathTemplate(PRODUCT_MANAGEMENT_URL.concat("/{id}")))
                .withPathParam("id", equalTo(product.getId()))
                .willReturn(like(ResponseDefinitionBuilder.jsonResponse(product))));
    }

    private void mockGetProducts(OrchestrationPlanNode orchestrationPlanNode) {
        Product product = Product.builder()
                .id(orchestrationPlanNode.getActualRelatedProductOptional().get().getId())
                .status(ProductStatusType.fromValue("Active"))
                .operationalStatus(ProductOperationalStatusType.fromValue("Active"))
                .atType("Product")
                .build();
        try {
            wireMockServer.stubFor(get(urlPathEqualTo(PRODUCT_MANAGEMENT_URL))
                    .withQueryParam("productOrderItem.productOrderId", equalTo(orchestrationPlanNode.getRelatedProductOrder().getId()))
                    .withQueryParam("productOrderItem.orderItemId", equalTo(orchestrationPlanNode.getActualOrderItemId()))
                    .willReturn(ok().withBody("[%s]".formatted(objectMapper.writeValueAsString(product))).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @And("the orchestration node state = {string}")
    public void theOrchestrationNodeState(String state) {
        this.orchestrationPlan.getOrchestrationPlanNodes().stream().filter(node -> node.getId().equals(orchestrationPlanNode.getId()))
                .findFirst().ifPresent(node -> node.setState(OrchestrationPlanNodeState.valueOf(state)));

        this.orchestrationPlan = mongoTemplateWrapperService.save(this.orchestrationPlan);
    }

    @And("other orchestration nodes have a {string} relationship with this node")
    public void otherOrchestrationNodesHaveARelationshipWithThisNode(String state) {
        //ignore
    }

    @And("the fallout has been completed with resolution = {string}")
    public void theFalloutHasBeenCompletedWithResolution(String falloutState) {
        this.falloutIncident.getResolution().setStatus(ResolutionState.valueOf(falloutState));
    }

    @When("COOD is notified of the completion of the fallout")
    public void coodIsNotifiedOfTheCompletionOfTheFallout() {
        await().atMost(TIMEOUT).untilAsserted(() -> {
            kafkaTemplateDelivery.send(FALLOUT_INCIDENT_STATE_CHANGE_TOPIC, FalloutIncidentStateChangeEvent.builder().event(falloutIncident).eventType("FalloutIncidentStateChangeEvent").build()).get();
        });
    }

    @Then("it sets the orchestration node state = held")
    public void itSetsTheOrchestrationNodeStateHeld() {
        //ignore
    }

    @And("it updates the state of the delivered product in CPIB to {string}")
    public void itUpdatesTheStateOfTheDeliveredProductInCPIBTo(String state) {
        //ignored until CPIB status study is finished
    }

    @And("the {string} event is published with the updated node details")
    public void theEventIsPublishedWithTheUpdatedNodeDetails(String orchestrationPlanNodeTopic) {
        await().atMost(TIMEOUT).until(() -> {
            KafkaConsumer<String, OrchestrationPlanNodeStateChangeEvent> orchestrationPlanEventKafkaConsumer = AbstractIntegrationUtil.createKafkaConsumer(orchestrationPlanNodeTopic, UUID.randomUUID().toString(), OrchestrationPlanNodeStateChangeEvent.class);
            ConsumerRecords<String, OrchestrationPlanNodeStateChangeEvent> orchestrationPlanEventConsumerRecords = orchestrationPlanEventKafkaConsumer.poll(TIMEOUT);
            for (ConsumerRecord<String, OrchestrationPlanNodeStateChangeEvent> orchestrationPlanNodeStateChangeEventConsumerRecord : orchestrationPlanEventConsumerRecords) {
                OrchestrationPlanNodeStateChangeEvent actualOrchestrationPlanEvent = orchestrationPlanNodeStateChangeEventConsumerRecord.value();
                if (actualOrchestrationPlanEvent.getEvent().getOrchestrationPlanNode().getId().equals(orchestrationPlanNode.getId()) && actualOrchestrationPlanEvent.getEvent().getOrchestrationPlanNode().getState().equals(FAILED)) {
                    return true;
                }
            }
            return false;
        });
    }

    @And("all the nodes relying on this node will be updated to have state = {string}")
    public void allTheNodesRelyingOnThisNodeWillBeUpdatedToHaveState(String state) {
        AtomicBoolean allStateChanged = new AtomicBoolean(false);
        await().atMost(TIMEOUT).until(() -> {
            orchestrationPlanRepository.findOrchestrationPlanById(this.orchestrationPlan.getId()).ifPresent(plan -> {
                List<OrchestrationPlanNode> planNodeList = plan.getOrchestrationPlanNodes().stream().filter(node -> !node.getId().equals(orchestrationPlanNode.getId())).toList();
                allStateChanged.set(planNodeList.stream().allMatch(node -> node.getState().equals(ABORTED)));
            });
            return allStateChanged.get();
        });
        assertTrue(allStateChanged.get());
    }

    @And("all other orchestration nodes in the orchestration plan have state = {string} or {string}")
    public void allOtherOrchestrationNodesInTheOrchestrationPlanHaveStateOr(String failedState, String completedState) {
        orchestrationPlan.getOrchestrationPlanNodes().stream().filter(node -> !node.getId().equals(orchestrationPlanNode.getId())).forEach(node -> {
            node.setState(COMPLETED);
        });
        mongoTemplateWrapperService.save(orchestrationPlan);
    }

    @And("orchestration plan state will be updated to {string}")
    public void orchestrationPlanStateWillBeUpdatedTo(String planState) {
        AtomicBoolean stateMatched = new AtomicBoolean(false);
        await().atMost(TIMEOUT).until(() -> {
            orchestrationPlanRepository.findOrchestrationPlanById(this.orchestrationPlan.getId()).ifPresent(plan -> {
                stateMatched.set(State.valueOf(planState).equals(plan.getState()));
            });
            return stateMatched.get();
        });
        assertTrue(stateMatched.get());
    }

    @Then("it ignores the fallout with node state remains {string}")
    public void itIgnoresTheFallout(String state) {
        orchestrationPlanRepository.findOrchestrationPlanById(this.orchestrationPlan.getId()).ifPresent(plan -> {
            plan.getOrchestrationPlanNodes().stream().filter(node -> node.getId().equals(orchestrationPlanNode.getId())).findFirst()
                    .ifPresent(node -> Assertions.assertEquals(OrchestrationPlanNodeState.valueOf(state), node.getState()));
        });
    }

    @Given("an orchestration plan has an associated fallout with json {string}")
    public void anOrchestrationPlanHasAnAssociatedFalloutWithJson(String jsonFile) {
        log.info("Start testing");
        this.orchestrationPlan = JsonUtil.readObjectFromResource("/integration/%s".formatted(jsonFile), new TypeReference<>() {
        });
        orchestrationPlan.setState(State.IN_PROGRESS);
        this.orchestrationPlan = mongoTemplateWrapperService.save(this.orchestrationPlan);
        falloutIncident = JsonUtil.readObjectFromResource(FALLOUT_JSON, new TypeReference<>() {
        });
        falloutIncident.getRelatedEntity().stream().findFirst().filter(relatedEntity -> relatedEntity.getRole().equals(RelatedEntityRole.INITIATOR))
                .ifPresent(relatedEntity -> {
                    relatedEntity.setId(orchestrationPlan.getId());
                    relatedEntity.setAtReferredType(OrchestrationPlan.class.getSimpleName());
                });
        falloutIncident.getRelatedEntity().stream().filter(relatedEntity -> relatedEntity.getAtReferredType().equals(OrchestrationPlan.class.getSimpleName()))
                .findFirst()
                .ifPresent(relatedEntity -> {
                    relatedEntity.setId(orchestrationPlan.getId());
                });

    }

    @Then("it ignores the fallout with plan state remains {string}")
    public void itIgnoresTheFalloutWithPlanStateRemains(String state) {
        orchestrationPlanRepository.findOrchestrationPlanById(this.orchestrationPlan.getId()).ifPresent(plan -> {
            Assertions.assertEquals(State.valueOf(state), plan.getState());
        });
    }
}
