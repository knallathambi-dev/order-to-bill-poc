// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.records.NodeStateChangeRecord;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.records.PlanStateChangeRecord;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
public class KafkaConsumerSession {

    private final List<NodeStateChangeRecord> nodeStateChangeList = new ArrayList<>();
    private final List<PlanStateChangeRecord> planStateChangeList = new ArrayList<>();
    private final List<OrchestrationPlanNode> nodeRelatedProductChangeList = new ArrayList<>();
    private OrchestrationPlan planToBePersisted = null;
    @Getter
    private Map<String, String> outboxHeaders = new HashMap<>();

    public void addNodeStateChange(OrchestrationPlanNode node) {
        boolean isExists = nodeStateChangeList.stream().anyMatch(nodeStateChangeRecord -> nodeStateChangeRecord.nodeId().equals(node.getId()) && nodeStateChangeRecord.state().equals(node.getState()));

        if (!isExists) {
            nodeStateChangeList.add(new NodeStateChangeRecord(node.getId(), node.getState(), node.getPreviousState(), node.getErrorMessage()));
        }
    }

    public void addPlanStateChange(OrchestrationPlan plan) {
        boolean isExists = planStateChangeList.stream().anyMatch(nodeStateChangeRecord -> nodeStateChangeRecord.planId().equals(plan.getId()) && nodeStateChangeRecord.state().equals(plan.getState()));

        if (!isExists) {
            planStateChangeList.add(new PlanStateChangeRecord(plan.getId(), plan.getState()));
        }
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    public void setPlanToBePersisted(OrchestrationPlan orchestrationPlan) {
        if (StringUtils.isEmpty(orchestrationPlan.getId())) {
            orchestrationPlan.setId(UUID.randomUUID().toString());
        }
        this.planToBePersisted = orchestrationPlan;
    }

    public void addNodeRelatedProductChange(OrchestrationPlanNode node) {
        nodeRelatedProductChangeList.add(node);
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    public List<NodeStateChangeRecord> getNodeStateChangeList() {
        return nodeStateChangeList;
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    public List<PlanStateChangeRecord> getPlanStateChangeList() {
        return planStateChangeList;
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    public List<OrchestrationPlanNode> getNodeRelatedProductChangeList() {
        return nodeRelatedProductChangeList;
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
    public OrchestrationPlan getPlanToBePersisted() {
        return planToBePersisted;
    }

    public void clear() {
        nodeStateChangeList.clear();
        planStateChangeList.clear();
        nodeRelatedProductChangeList.clear();
        planToBePersisted = null;
    }

    public void setOutboxHeaders(Map<String, String> headers) {
        if (Objects.nonNull(headers)) {
            this.outboxHeaders = headers;
        }
    }
}

