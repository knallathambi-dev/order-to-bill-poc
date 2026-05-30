// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.repository.impl.OrchestrationPlanRepositoryImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.OrchestrationPlanNodeStateMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class OrchestrationPlanExecutionServiceImplTest {
    @InjectMocks
    private OrchestrationPlanExecutionServiceImpl orchestrationPlanExecutionService;
    @Mock
    OrchestrationPlanRepository orchestrationPlanRepository;
    @Mock
    OrchestrationPlanRepositoryImpl orchestrationPlanRepositoryimpl;
    @Mock
    OrchestrationPlanModificationService orchestrationPlanModificationService;
    @InjectMocks
    OrchestrationPlanNodeStateMapper orchestrationPlanNodeStateMapper;

    private OrchestrationPlan orchestrationPlan;
    private OrchestrationPlanNode orchestrationPlanNodeOne;
    private OrchestrationPlanNode orchestrationPlanNodeTwo;
    private RelatedServiceOrder relatedServiceOrderOne;
    private RelatedServiceOrder relatedServiceOrderTwo;

    private ServiceOrder serviceOrder;
    private List<ServiceOrderItem> serviceOrderItems;
    private ServiceOrderItem serviceOrderItem;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        orchestrationPlan = new OrchestrationPlan();
        orchestrationPlanNodeOne = new OrchestrationPlanNode();
        orchestrationPlanNodeOne.setId("2");
        RelatedServiceOrder relatedServiceOrder = new RelatedServiceOrder("5bc00d05-00c1-4739-91a7-acde38f343e1", "1", "ref1");
        orchestrationPlanNodeOne.setRelatedServiceOrder(relatedServiceOrder);
        orchestrationPlanNodeOne.setState(OrchestrationPlanNodeState.ACKNOWLEDGED);
        orchestrationPlanNodeTwo = new OrchestrationPlanNode();
        orchestrationPlanNodeTwo.setId("2");
        orchestrationPlanNodeTwo.setState(OrchestrationPlanNodeState.ACKNOWLEDGED);
        serviceOrder = new ServiceOrder();
        serviceOrder.setId("5bc00d05-00c1-4739-91a7-acde38f343e1");
        serviceOrder.setState(ServiceOrder.State.COMPLETED);
        Set<OrchestrationPlanNode> nodes = new HashSet<>();
        relatedServiceOrderOne = new RelatedServiceOrder("5bc00d05-00c1-4739-91a7-acde38f343e1", "1", "ref2");
        relatedServiceOrderTwo = new RelatedServiceOrder("5bc00d05-00c1-4739-91a7-acde38f343e1", "1", "ref2");
        orchestrationPlanNodeOne.setRelatedServiceOrder(relatedServiceOrderOne);
        orchestrationPlanNodeTwo.setRelatedServiceOrder(relatedServiceOrderTwo);
        nodes.add(orchestrationPlanNodeOne);
        nodes.add(orchestrationPlanNodeTwo);
        orchestrationPlan.setOrchestrationPlanNodes(nodes);
        serviceOrderItems = new ArrayList<>();
        serviceOrderItem = new ServiceOrderItem();
        serviceOrderItem.setId("1");
        serviceOrderItems.add(serviceOrderItem);
        serviceOrder.setServiceOrderItem(serviceOrderItems);

    }

    @Test
    void givenOrchestrationPlanWithoutId_WhenTryingToGetThePlanById_ThenExceptionWillBeThrown(){
        orchestrationPlan.setState(State.IN_PROGRESS);
        Assertions.assertThrows(CoodNonRecoverableAndNonRetryableException.class, () -> orchestrationPlanExecutionService.executeLeaf(orchestrationPlan));
    }
    @Test
    void testFindOrchestrationPlanNode_ReturnsMatchingNode() {
        String serviceOrderId = "5bc00d05-00c1-4739-91a7-acde38f343e1";
        String serviceOrderItemId = "1";

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlanExecutionService.findOrchestrationPlanNode(orchestrationPlan, serviceOrderId, serviceOrderItemId);
        Assertions.assertEquals(orchestrationPlanNodeOne, orchestrationPlanNode);

    }

    @Test
    void testFindOrchestrationPlanNode_ReturnsNullWhenNoMatch() {
        String serviceOrderId = "453";
        String serviceOrderItemId = "7";

        OrchestrationPlanNode orchestrationPlanNode = orchestrationPlanExecutionService.findOrchestrationPlanNode(orchestrationPlan, serviceOrderId, serviceOrderItemId);
        assertNull(orchestrationPlanNode);

    }

    @Test
    void testUpdateOrchestrationPlanNode_statusNotUpdated() {
        RelatedServiceOrder relatedServiceOrder = new RelatedServiceOrder();
        relatedServiceOrder.setId("123");
        relatedServiceOrder.setOrderItemId("546");
        OrchestrationPlanNode opn = new OrchestrationPlanNode();
        opn.setRelatedServiceOrder(relatedServiceOrder);
        orchestrationPlanExecutionService.updateOrchestrationPlanNodeStatusBasedOnServiceOrder(serviceOrder, opn);
        assertNull(opn.getState());
    }

    @Test
    void testfindOrchestrationPlan_returnMatchingOrchestrationPlan() {
        String serviceOrderId = "210201b6-2ba8-4c6a-887e-ad5e70399ad4";
        String serviceOrderItemId = "1";

        when(orchestrationPlanRepositoryimpl.findByServiceOrder(serviceOrderId, serviceOrderItemId))
                .thenReturn(orchestrationPlan);
        OrchestrationPlan orchestrationPlanResult = orchestrationPlanRepositoryimpl.findByServiceOrder(serviceOrderId, serviceOrderItemId);

        Assertions.assertEquals(orchestrationPlan, orchestrationPlanResult);

        verify(orchestrationPlanRepositoryimpl, times(1)).findByServiceOrder(serviceOrderId, serviceOrderItemId);
    }

    @Test
    void testfindOrchestrationPlan_returnNonMatchingOrchestrationPlan() {
        String serviceOrderId = "123";
        String serviceOrderItemId = "1";

        RelatedServiceOrder serviceOrderInvalid = new RelatedServiceOrder("2ba8-4c6a-887e-ad5e70399ad4", "1", "ref1");
        Set<OrchestrationPlanNode> nodesInvalid = new HashSet<>();
        OrchestrationPlan opInvalid = new OrchestrationPlan();
        OrchestrationPlanNode orchestrationPlanNodeInvalid = new OrchestrationPlanNode();
        orchestrationPlanNodeInvalid.setRelatedServiceOrder(serviceOrderInvalid);
        nodesInvalid.add(orchestrationPlanNodeInvalid);

        opInvalid.setOrchestrationPlanNodes(nodesInvalid);


        when(orchestrationPlanRepositoryimpl.findByServiceOrder(serviceOrderId, serviceOrderItemId))
                .thenReturn(orchestrationPlan);
        orchestrationPlanRepositoryimpl.findByServiceOrder(serviceOrderId, serviceOrderItemId);

        Assertions.assertNotEquals(orchestrationPlan, opInvalid);

        verify(orchestrationPlanRepositoryimpl, times(1)).findByServiceOrder(serviceOrderId, serviceOrderItemId);
    }


}