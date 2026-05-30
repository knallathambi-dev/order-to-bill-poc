// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.steps;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.generated.fallout.FalloutIncidentStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.RelatedEntityRole;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.RelatedEntity;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.Resolution;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.pojo.enums.ResolutionState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

public class CompletedFalloutIncidentStateChangeEventConsumer {

    @Autowired
    private KafkaTemplate<String, FalloutIncidentStateChangeEvent> kafkaTemplate;

    @When("the system consumes fallout incident state change event from topic {string} with id {string}, state {string}, resolution state {string} and initiator node id {string}")
    public void consumeFalloutIncidentStateChangeEventFromTopicWithIdStateResolutionStateAndInitiatorNodeId(String topic, String flowId, String incidentState, String resolutionState, String initiatorNodeId) {
        FalloutIncidentStateChangeEvent falloutIncidentStateChangeEvent = FalloutIncidentStateChangeEvent.builder().
                event(FalloutIncident.builder()
                        .id(flowId)
                        .relatedEntity(List.of(RelatedEntity.builder()
                                .role(RelatedEntityRole.INITIATOR)
                                .atReferredType(OrchestrationPlanNode.class.getSimpleName())
                                .id(initiatorNodeId)
                                .build()))
                        .state(State.fromValue(incidentState))
                        .resolution(Resolution.builder()
                                .status(ResolutionState.fromValue(resolutionState))
                                .build())
                        .build()
                )
                .eventType(FalloutIncidentStateChangeEvent.class.getSimpleName())
                .build();

        kafkaTemplate.send(topic, falloutIncidentStateChangeEvent);
    }

    @When("the system consumes fallout incident state change event from topic {string} with id {string}, state {string}, resolution state {string} and initiator plan id {string}")
    public void theSystemConsumesFalloutIncidentStateChangeEventFromTopicWithIdStateResolutionStateAndInitiatorPlanId(String topic, String flowId, String incidentState, String resolutionState, String initiatorPlanId) {
        FalloutIncidentStateChangeEvent falloutIncidentStateChangeEvent = FalloutIncidentStateChangeEvent.builder().
                event(FalloutIncident.builder()
                        .id(flowId)
                        .relatedEntity(List.of(RelatedEntity.builder()
                                .role(RelatedEntityRole.INITIATOR)
                                .atReferredType(OrchestrationPlan.class.getSimpleName())
                                .id(initiatorPlanId)
                                .build()))
                        .state(State.fromValue(incidentState))
                        .resolution(Resolution.builder()
                                .status(ResolutionState.fromValue(resolutionState))
                                .build())
                        .build()
                )
                .eventType(FalloutIncidentStateChangeEvent.class.getSimpleName())
                .build();

        kafkaTemplate.send(topic, falloutIncidentStateChangeEvent);

    }
}
