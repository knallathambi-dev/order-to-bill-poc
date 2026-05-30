// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.deliver_selected_nodes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.FilterTangibleNodes;
import com.orange.discobole.orderorchestration.orchestrationdelivery.testutil.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Set;

class FilterTangibleNodesTest {

    @InjectMocks
    FilterTangibleNodes filterTangibleNodes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFilterTangibleAndNonTangibleNodes() {
        Set<OrchestrationPlanNode> listOfNodesInput = JsonUtil.readObjectFromResource("/listOfOrchestrationPlanNodes.json", new TypeReference<>() {
        });
        FilterTangibleNodes result = filterTangibleNodes.filterNodes(listOfNodesInput);

        Assertions.assertEquals(2, result.getCfsDeliveryNodes().size(), "Number of non-tangible (CFS) nodes doesn't match expected value.");
    }
}

