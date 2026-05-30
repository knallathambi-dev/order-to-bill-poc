// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.FalloutIncidentRecord;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangePayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
 import com.orange.discobole.processflow.dto.generated.Links;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskLink;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.like;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class DecideToReExecuteOrNotForResolvedFalloutIncident {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private KafkaTemplate<String, OrchestrationPlanNodeStateChangeEvent> eventPublisher;

    @And("the fallout service has the following fallout incidents:")
    public void theFalloutServiceHasFalloutIncidentsWithTheFollowingData(List<FalloutIncidentRecord> falloutIncidentsList) {
        Map<String, List<FalloutIncidentRecord>> falloutIncidentsMapsLists = falloutIncidentsList.stream().collect(Collectors.groupingBy(FalloutIncidentRecord::relatedEntityId));
        System.out.println("falloutIncidentsMapsLists"+falloutIncidentsMapsLists);
        falloutIncidentsMapsLists.forEach((relatedEntityId, followingIncidentsMaps) -> {
            List<ProcessFlow> processFlows = followingIncidentsMaps.stream().map(followingIncidentsMap -> {
                ProcessFlow processFlow = new ProcessFlow();
                TaskLink taskLink = new TaskLink();
                String href = "/processManagement/v1/processFlow/%s/taskFlow/%s".formatted(followingIncidentsMap.processId(), followingIncidentsMap.taskId());
                wireMockServer.stubFor(patch(urlEqualTo(href))
                        .willReturn(ok().withBody("{}").withHeader(CONTENT_TYPE, APPLICATION_JSON)));
                taskLink.setHref(wireMockServer.baseUrl() + href);
                Links links = new Links();
                links.setNextTaskstoBePerformed(List.of(taskLink));
                processFlow.setLinks(links);
                return processFlow;
            }).toList();
            wireMockServer.stubFor(get(urlPathEqualTo("/processManagement/v1/processFlow"))
                    .withQueryParam("relatedEntity.id", equalTo(relatedEntityId))
                    .willReturn(like(ResponseDefinitionBuilder.jsonResponse(processFlows)))
            );
        });
    }

    @Then("the system consumes orchestration plan node state change event to topic {string} and fallout source topic name {string} with node id {string} and state {string}")
    public void theSystemWillFireOrchestrationPlanNodeStateChangeEventToTopicWithNodeIdAndState(String topicName, String sourceTopicName, String nodeId, String state) {
        OrchestrationPlanNode node = OrchestrationPlanNode.builder()
                .id(nodeId)
                .state(OrchestrationPlanNodeState.fromValue(state))
                .previousState(OrchestrationPlanNodeState.HELD)
                .build();


        OrchestrationPlanNodeStateChangeEvent event = OrchestrationPlanNodeStateChangeEvent.builder()
                .event(OrchestrationPlanNodeStateChangePayloadEvent.builder()
                        .orchestrationPlanNode(node)
                        .build())
                .eventType(OrchestrationPlanNodeStateChangeEvent.class.getSimpleName())
                .build();

        Message<OrchestrationPlanNodeStateChangeEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader("headers", Map.of(Headers.SOURCE_TOPIC_NAME, sourceTopicName))
                .build();

        eventPublisher.send(message);
    }

    @And("the fallout service will be called will the following data:")
    public void theFalloutServiceWillBeCalledWillTheFollowingData(List<FalloutIncidentRecord> falloutIncidentsList) {
        falloutIncidentsList.forEach(falloutIncidentsMap -> {
            Awaitility.await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
                wireMockServer.verify(1, patchRequestedFor(urlPathTemplate("/processManagement/v1/processFlow/{processId}/taskFlow/{taskId}"))
                        .withPathParam("processId", equalTo(falloutIncidentsMap.processId()))
                        .withPathParam("taskId", equalTo(falloutIncidentsMap.taskId()))
                        .withRequestBody(containing(falloutIncidentsMap.resolutionState())));
            });
        });

    }
}
