// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.integration.helpers.OrchestrationPlanNodeBuilders;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.ProcessFlowCreateMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.integration.helpers.OrchestrationPlanBuilders.orchestrationPlanInProgressWithoutOrchestrationPlanNodesBuilder;


@ExtendWith(MockitoExtension.class)
class OrchestrationDeliveryFalloutManagementImplTest {

    @InjectMocks
    ProcessFlowCreateMapperImpl orchestrationDeliveryFalloutManagement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenOrchestrationPlanNode_whenCreateOrderOrchestrationFallout_thenProcessFlowCreated() {
        OrchestrationPlanNode orchestrationPlanNode = OrchestrationPlanNodeBuilders.orchestrationPlanNodeConnectivityModifyBuilder("1109", "649aaf9f2b4b3225cb5737eb", "5e18402d-c964-4d52-b362-22ef79c27c01")
                .state(OrchestrationPlanNodeState.HELD)
                .build();

        OrchestrationPlan orchestrationPlan = orchestrationPlanInProgressWithoutOrchestrationPlanNodesBuilder("1109").orchestrationPlanNodes(Set.of(orchestrationPlanNode))
                .id(UUID.randomUUID().toString())
                .build();

        ProcessFlowCreate processFlowCreate = orchestrationDeliveryFalloutManagement.from(orchestrationPlan, orchestrationPlanNode);

        Assertions.assertEquals(processFlowCreate.getRelatedEntity().get(0).getId(), orchestrationPlanNode.getId());
        Assertions.assertEquals(processFlowCreate.getRelatedParty().get(0).getId(), orchestrationPlan.getRelatedParty().get(0).getId());
        Assertions.assertEquals(processFlowCreate.getRelatedParty().get(0).getName(), orchestrationPlan.getRelatedParty().get(0).getName());


    }

}
