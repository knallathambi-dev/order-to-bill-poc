// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.kafka.handler.exceuter;

import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class OrchestrationPlanNodeStateChangeEventReExecutionExecuter implements KafkaEventReExecutionExecutor<OrchestrationPlanNodeStateChangeEvent> {

    private final KafkaTemplate<String, OrchestrationPlanNodeStateChangeEvent> kafkaTemplate;

    @Override
    public void execute(OrchestrationPlanNodeStateChangeEvent event) {
        OrchestrationPlanNode orchestrationPlanNode = event.getEvent().getOrchestrationPlanNode();
        orchestrationPlanNode.setState(orchestrationPlanNode.getPreviousState());
        kafkaTemplate.send(KafkaTopic.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_TMR_TOPIC, event);
    }

    @Override
    public Class<OrchestrationPlanNodeStateChangeEvent> getEventType() {
        return OrchestrationPlanNodeStateChangeEvent.class;
    }
}
