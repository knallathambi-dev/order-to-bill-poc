// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records.OrchestrationPlanNodeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliverSelectedNodesService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.impl.OrchestrationPlanExecutionServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ModifyOrchestrationNodesStatusInExecuteOrchestrationPlan {

    @InjectMocks
    OrchestrationPlanExecutionServiceImpl orchestrationPlanExecutionService;

    @Mock
    OrchestrationPlanRepository orchestrationPlanRepository;

    @Mock
    OrchestrationPlanService orchestrationPlanService;

    @Mock
    DeliverSelectedNodesService deliverSelectedNodesService;

    ObjectMapper objectMapper;

    OrchestrationPlan orchestrationPlan;

    @Before("@BeforeModifyOrchestrationNodesStatusInExecuteOrchestrationPlan")
    public void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Given("an Orchestration plan exists with {int} nodes")
    @SneakyThrows
    public void anOrchestrationPlanExistsWithNodes(int nodesCount, List<OrchestrationPlanNodeRecord> nodesList) {
        Set<OrchestrationPlanNode> orchestrationPlanNodes = new HashSet<>();
        for (OrchestrationPlanNodeRecord node : nodesList) {
            orchestrationPlanNodes.add(OrchestrationPlanNode.builder()
                    .id(node.id())
                    .state(OrchestrationPlanNodeState.fromValue(node.state()))
                    .relatedProduct(Instancio.createList(RelatedProduct.class))
                    .relatedOrchestrationPlanNode(objectMapper.readValue(node.relatedNodes(), new TypeReference<>() {
                    }))
                    .build());
        }
        orchestrationPlan = OrchestrationPlan.builder()
                .id(UUID.randomUUID().toString())
                .orchestrationPlanNodes(orchestrationPlanNodes)
                .state(State.INITIALIZED).build();
        when(orchestrationPlanRepository.findOrchestrationPlanByOrchestrationPlanNodes_id(any())).thenReturn(Optional.of(orchestrationPlan));
    }

    @When("Node {string} state is changed to {string} and NodeStateChange event is consumed")
    public void nodeStateIsChangedToAndNodeStateChangeEventIsConsumed(String nodeId, String nodeState) {
        OrchestrationPlanNode node = orchestrationPlan.getOrchestrationPlanNodes().stream().filter(orchestrationPlanNode -> orchestrationPlanNode.getId().equals(nodeId)).findFirst().orElseThrow(() -> new IllegalStateException("Node " + nodeId + " not found"));
        node.setState(OrchestrationPlanNodeState.fromValue(nodeState));
        orchestrationPlanExecutionService.executeOrchestrationPlanNode(node);
    }

    @Then("Node {string} and Node {string} state = {string}")
    public void nodeAndNodeState(String firstNodeId, String secondNodeId, String state) {
        ArgumentCaptor<OrchestrationPlanNode> orchestrationPlanNodeArgumentCaptor = ArgumentCaptor.forClass(OrchestrationPlanNode.class);
        verify(orchestrationPlanService, times(2)).updateOrchestrationPlanNodeState(orchestrationPlanNodeArgumentCaptor.capture(), eq(OrchestrationPlanNodeState.fromValue(state)));
        List<OrchestrationPlanNode> orchestrationPlanNodes = orchestrationPlanNodeArgumentCaptor.getAllValues();
        List<String> orchestrationPlanIds = orchestrationPlanNodes.stream().map(OrchestrationPlanNode::getId).toList();
        Assertions.assertThat(orchestrationPlanIds).containsExactlyInAnyOrder(firstNodeId, secondNodeId);
    }

    @Then("Node {string} and Node {string} state Not changed")
    public void nodeAndNodeStateNotChanged(String firstNodeId, String secondNodeId) {
        orchestrationPlanRepository.findOrchestrationPlanNodeById(firstNodeId).ifPresent(orchestrationPlanNode -> {
            Assertions.assertThat(orchestrationPlanNode.getState()).isEqualTo(OrchestrationPlanNodeState.ACKNOWLEDGED);
        });
        orchestrationPlanRepository.findOrchestrationPlanNodeById(secondNodeId).ifPresent(orchestrationPlanNode -> {
            Assertions.assertThat(orchestrationPlanNode.getState()).isEqualTo(OrchestrationPlanNodeState.ACKNOWLEDGED);
        });
    }
}
