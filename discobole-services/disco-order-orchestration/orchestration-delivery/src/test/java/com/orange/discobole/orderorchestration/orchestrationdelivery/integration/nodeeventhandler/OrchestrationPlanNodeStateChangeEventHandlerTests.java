// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.integration.nodeeventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.OrchestrationPlanNodeStateChangeEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions.PRODUCT_MANAGEMENT_URL;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.IN_DELIVERY;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
class OrchestrationPlanNodeStateChangeEventHandlerTests extends BaseAbstractionIntegrationTest {

    @Autowired
    private OrchestrationPlanNodeStateChangeEventHandler orchestrationPlanNodeStateChangeEventHandler;

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;

    @SneakyThrows
    @Test
    void givenCompletedNode_whenHandleNodeStateChangeEvent_thenRelatedNodesExecutionStarted() {
        OrchestrationPlan orchestrationPlan = JsonUtil.readObjectFromResource("/integration/nodeeventhandler/planBefore.json", new TypeReference<>() {
        });
        orchestrationPlanRepository.save(orchestrationPlan);
        OrchestrationPlanNode orchestrationPlanNode = JsonUtil.readObjectFromResource("/integration/nodeeventhandler/event.json", new TypeReference<>() {
        });
        mockProductOrder();

        mockCpibRequests();

        mockSomRequests();

        orchestrationPlanNodeStateChangeEventHandler.handleEvent(OrchestrationPlanNodeStateChangeEvent.builder()
                .eventId("id")
                .event(OrchestrationPlanNodeStateChangePayloadEvent.builder().orchestrationPlanNode(orchestrationPlanNode).build())
                .correlationId("coId")
                .eventType("type")
                .eventTime(Instant.MAX)
                .build());

        OrchestrationPlan actualPlan = orchestrationPlanRepository.findOrchestrationPlanById(orchestrationPlan.getId()).get();
        Assertions.assertNotNull(actualPlan);

        actualPlan.getOrchestrationPlanNodes().stream()
                .filter(opn -> !opn.getRelatedOrchestrationPlanNode().stream()
                        .filter(
                                relatedOrchestrationPlanNode -> DELIVER_AFTER.equals(relatedOrchestrationPlanNode.getRelationshipType()) && relatedOrchestrationPlanNode.getRelatedNodeId().equals(orchestrationPlan.getId())
                        ).toList().isEmpty()
                )
                .forEach(opn -> assertThat(opn.getState()).isEqualTo(IN_DELIVERY));

    }

    private void mockSomRequests() {
        wireMockServer.stubFor(get(urlMatching("/serviceCatalogManagement/v1/serviceSpecification.*"))
                .willReturn(ok().withBody(JsonUtil.readStringFromResource("/integration/nodeeventhandler/ServiceCatalog.json"))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        wireMockServer.stubFor(post(urlMatching("/serviceOrdering/v1/serviceOrder.*"))
                .willReturn(created().withBody(JsonUtil.readStringFromResource("/integration/nodeeventhandler/ServiceOrder.json"))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    private void mockCpibRequests() {
        wireMockServer.stubFor(get(urlMatching(PRODUCT_MANAGEMENT_URL.concat(".*productOrderItem.productOrderId=1109.*")))
                .willReturn(ok().withBody(JsonUtil.readStringFromResource("/integration/nodeeventhandler/MobileLineAndConnectivity.json")).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        wireMockServer.stubFor(get(urlMatching(PRODUCT_MANAGEMENT_URL.concat(".*649aaf9f2b4b3225cb5737ed.*"))).willReturn(ok().withBody(JsonUtil.readStringFromResource("/integration/nodeeventhandler/SimCardProduct.json")).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    private void mockProductOrder() throws JsonProcessingException {
        ProductOrder productOrder = JsonUtil.readObjectFromResource("/integration/nodeeventhandler/order.json", new TypeReference<>() {
        });
        wireMockServer.stubFor(get("/orderManagement/v1/productOrder/1109")
                .willReturn(ok().withBody(objectMapper.writeValueAsString(productOrder)).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }
}
