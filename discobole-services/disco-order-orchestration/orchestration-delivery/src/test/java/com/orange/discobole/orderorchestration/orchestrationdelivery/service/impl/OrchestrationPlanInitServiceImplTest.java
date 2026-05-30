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
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OrchestrationPlanInitServiceImplTest {
    @InjectMocks
    OrchestrationPlanServiceImpl orchestrationPlanStateService;

    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private OrchestrationPlanModificationService orchestrationPlanModificationService;

    @Mock
    private DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateOrchestrationPlanNodeState_ACKNOWLEDGED() {
        OrchestrationPlanNode orchestrationPlanNode = new OrchestrationPlanNode();
        orchestrationPlanNode.setState(OrchestrationPlanNodeState.INITIALIZED);
        orchestrationPlanStateService.setNextOrchestrationPlanNodeState(orchestrationPlanNode);
        OrchestrationPlanNodeState expectedState = OrchestrationPlanNodeState.ACKNOWLEDGED;
        verify(dataPersistenceKafkaSessionService, times(1)).addNodeStateChange(orchestrationPlanNode);
        Assertions.assertEquals(expectedState, orchestrationPlanNode.getState());
    }

    @Test
    void updateOrchestrationPlanNodeState_IN_PROGRESS() {
        OrchestrationPlanNode orchestrationPlanNode = new OrchestrationPlanNode();
        orchestrationPlanNode.setState(OrchestrationPlanNodeState.ACKNOWLEDGED);
        orchestrationPlanStateService.setNextOrchestrationPlanNodeState(orchestrationPlanNode);
        OrchestrationPlanNodeState expectedState = OrchestrationPlanNodeState.IN_PROGRESS;
        verify(dataPersistenceKafkaSessionService, times(1)).addNodeStateChange(orchestrationPlanNode);
        Assertions.assertEquals(expectedState, orchestrationPlanNode.getState());
    }

    @Test
    void updateOrchestrationPlanNodeState_IN_DELIVERY() {
        OrchestrationPlanNode orchestrationPlanNode = new OrchestrationPlanNode();
        orchestrationPlanNode.setState(OrchestrationPlanNodeState.IN_PROGRESS);
        orchestrationPlanStateService.setNextOrchestrationPlanNodeState(orchestrationPlanNode);
        OrchestrationPlanNodeState expectedState = OrchestrationPlanNodeState.IN_DELIVERY;
        verify(dataPersistenceKafkaSessionService, times(1)).addNodeStateChange(orchestrationPlanNode);
        Assertions.assertEquals(expectedState, orchestrationPlanNode.getState());
    }


}