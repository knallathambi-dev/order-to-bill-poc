// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class FilterTangibleNodes {

    @SuppressFBWarnings("EI_EXPOSE_REP")
    private Set<OrchestrationPlanNode> cfsDeliveryNodes;  // For non-tangible products


    @SuppressFBWarnings("EI_EXPOSE_REP2")
    private FilterTangibleNodes(Set<OrchestrationPlanNode> cfsDeliveryNodes) {
        this.cfsDeliveryNodes = cfsDeliveryNodes;
    }

    public static FilterTangibleNodes filterNodes(Set<OrchestrationPlanNode> inputNodes) {
        Set<OrchestrationPlanNode> cfsDeliveryNodesResult = inputNodes.stream()
                .filter(node -> (node.isCFSOrchestrationPlanNode()))
                .collect(Collectors.toSet());

        return new FilterTangibleNodes(cfsDeliveryNodesResult);
    }
}
