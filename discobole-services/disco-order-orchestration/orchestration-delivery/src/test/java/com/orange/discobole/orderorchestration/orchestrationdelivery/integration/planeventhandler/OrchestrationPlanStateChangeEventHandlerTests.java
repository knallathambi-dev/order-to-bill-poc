// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.integration.planeventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.BaseAbstractionIntegrationTest;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.CleanMongoDBExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.cleanup.ResetWireMockExtension;
import com.orange.discobole.orderorchestration.orchestrationdelivery.base.debezium.EnableDebeziumIntegration;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.OrchestrationPlanEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps.CommonStepDefinitions.PRODUCT_MANAGEMENT_URL;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@EnableDebeziumIntegration
@ExtendWith({CleanMongoDBExtension.class, ResetWireMockExtension.class})
class OrchestrationPlanStateChangeEventHandlerTests extends BaseAbstractionIntegrationTest {

    @Autowired
    private OrchestrationPlanEventHandler orchestrationPlanEventHandler;

    @Autowired
    private OrchestrationPlanRepository orchestrationPlanRepository;
    @SneakyThrows
    @Test
    void givenAckPlan_whenHandlePlanStateChangeEvent_thenSimCardDelivered() {
        OrchestrationPlan orchestrationPlan = JsonUtil.readObjectFromResource("/integration/planeventhandler/planBefore.json", new TypeReference<>() {
        });
        orchestrationPlanRepository.save(orchestrationPlan);
        OrchestrationPlan orchestrationPlanEvent = JsonUtil.readObjectFromResource("/integration/planeventhandler/event.json", new TypeReference<>() {
        });
        mockProductOrder();

        mockCpibRequests();
        orchestrationPlanEventHandler.handleEvent(OrchestrationPlanStateChangeEvent.builder().eventTime(Instant.now()).eventId("1").eventType("PlanEvent")
                .event(OrchestrationPlanStateChangePayloadEvent.builder().orchestrationPlan(orchestrationPlanEvent).build()).build());
        OrchestrationPlan actualPlan = orchestrationPlanRepository.findOrchestrationPlanById(orchestrationPlan.getId()).get();
        Assertions.assertNotNull(actualPlan);
    }

    private void mockCpibRequests() {
        wireMockServer.stubFor(get(urlMatching(PRODUCT_MANAGEMENT_URL.concat(".*productOrderItem.orderItemId=10.*")))
                .willReturn(ok().withBody(JsonUtil.readStringFromResource("/integration/planeventhandler/SimCardProduct.json")).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        wireMockServer.stubFor(patch(PRODUCT_MANAGEMENT_URL.concat("/649aaf9f2b4b3225cb5737ed"))
                .willReturn(ok().withBody(JsonUtil.readStringFromResource("/integration/planeventhandler/SimCardProductObj.json")).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        wireMockServer.stubFor(get(urlMatching(PRODUCT_MANAGEMENT_URL.concat(".*productOrderItem.orderItemId=07.*")))
                .willReturn(ok().withBody(JsonUtil.readStringFromResource("/integration/planeventhandler/MobileLineAndConnectivity.json")).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        wireMockServer.stubFor(get(urlMatching(PRODUCT_MANAGEMENT_URL.concat(".*649aaf9f2b4b3225cb5737ed.*"))).willReturn(ok().withBody(JsonUtil.readStringFromResource("/integration/nodeeventhandler/SimCardProduct.json")).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    private void mockProductOrder() throws JsonProcessingException {
        ProductOrder productOrder = JsonUtil.readObjectFromResource("/integration/planeventhandler/order.json", new TypeReference<>() {
        });
        wireMockServer.stubFor(get("/orderManagement/v1/productOrder/1109")
                .willReturn(ok().withBody(objectMapper.writeValueAsString(productOrder)).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }
}
