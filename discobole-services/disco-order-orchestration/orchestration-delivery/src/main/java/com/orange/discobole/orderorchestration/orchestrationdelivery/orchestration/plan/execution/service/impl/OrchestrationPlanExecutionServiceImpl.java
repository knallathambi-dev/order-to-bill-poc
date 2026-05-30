// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliverSelectedNodesService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.service.OrchestrationPlanExecutionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanModificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.impl.OrchestrationPlanServiceImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.OrchestrationPlanNodeStateMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class OrchestrationPlanExecutionServiceImpl implements OrchestrationPlanExecutionService {

    private static final String NODE_EXECUTION_STARTED_LOG = "Execute orchestration plan node with id {} started";

    private static final String NODE_EXECUTION_FINISHED_LOG = "Execute orchestration plan node with id {} finished";

    private static final String NO_ACTION_FOR_NODE_WITH_ID_IN_STATE = "No action for orchestration plan node with id {} in state {}";

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    private final OrchestrationPlanService orchestrationPlanService;

    private final DeliverSelectedNodesService deliverSelectedNodesService;

    private final UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;

    private final OrchestrationPlanModificationService orchestrationPlanModificationService;

    @Override
    public OrchestrationPlanNode findOrchestrationPlanNode(OrchestrationPlan orchestrationPlan, String serviceOrderId, String serviceOrderItemId) {
        for (OrchestrationPlanNode orchestrationPlanNode : orchestrationPlan.getOrchestrationPlanNodes()) {
            if (Objects.nonNull(orchestrationPlanNode.getRelatedServiceOrder()) && orchestrationPlanNode.getRelatedServiceOrder().getId().equals(serviceOrderId) &&
                    orchestrationPlanNode.getRelatedServiceOrder().getOrderItemId().equals(serviceOrderItemId)) {
                return orchestrationPlanNode;
            }
        }
        return null;
    }

    @Override
    public void updateOrchestrationPlanNodeStatusBasedOnServiceOrder(ServiceOrder serviceOrder, OrchestrationPlanNode orchestrationPlanNode) {
        Optional<ServiceOrderItem> serviceOrderItem = serviceOrder.getServiceOrderItem().stream().filter(orderItem -> orderItem.getId().equals(orchestrationPlanNode.getRelatedServiceOrder().getOrderItemId())).findFirst();
        Set<ServiceOrderItem.State> allowedServiceOrderItemStateTypes = Set.of(ServiceOrderItem.State.FAILED,
                ServiceOrderItem.State.HELD, ServiceOrderItem.State.COMPLETED);
        if (serviceOrderItem.isPresent() && allowedServiceOrderItemStateTypes.contains(serviceOrderItem.get().getState())) {
            orchestrationPlanNode.setState(OrchestrationPlanNodeStateMapper.getFromServiceOrderItemStateOrDefault(serviceOrderItem.get().getState(), orchestrationPlanNode.getState()));
            log.info("OrchestrationPlanExecutionServiceImpl | updateOrchestrationPlanNodeStatusBasedOnServiceOrder | orchestrationPlanNode with id {} state became: {}", orchestrationPlanNode.getId(), orchestrationPlanNode.getState());
            log.debug("OrchestrationPlanExecutionServiceImpl | updateOrchestrationPlanNodeStatusBasedOnServiceOrder | orchestrationPlanNode {}", orchestrationPlanNode);
        } else {
            log.info("OrchestrationPlanExecutionServiceImpl | updateOrchestrationPlanNodeStatusBasedOnServiceOrder | orchestration plan node with id : {}, state is not updated ", orchestrationPlanNode.getId());
        }
    }

    @Override
    @Transactional
    public void executeLeaf(OrchestrationPlan orchestrationPlan) {
        //IPCEISCOOD-60: TODO separation - to be changed to fetch the leafs from the db
        if (!orchestrationPlan.getState().equals(State.IN_PROGRESS)) {
            throw new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "Plan state should be InProgress state to execute leaves");
        }

        log.debug("Starting executing orchestration plan {}", orchestrationPlan);

        OrchestrationPlan savedOrchestrationPlan = orchestrationPlanRepository.findOrchestrationPlanById(orchestrationPlan.getId())
                .orElseThrow(() -> new CoodNonRecoverableAndNonRetryableException(new OrchestrationPlanNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NOT_FOUND, orchestrationPlan.getId())));

        savedOrchestrationPlan.getLeafs().stream()
                .filter(node -> node.getState() == ACKNOWLEDGED)
                .filter(node -> {
                    boolean shouldNodeStartWithPlan = node.getOrchestrationNodeSchedule().getOrderItemStartDate() == null
                            || !node.getOrchestrationNodeSchedule().getOrderItemStartDate().isAfter(Instant.now());
                    return shouldNodeStartWithPlan;
                })
                .forEach(node -> orchestrationPlanService.updateOrchestrationPlanNodeState(node, OrchestrationPlanNodeState.IN_PROGRESS));

        log.debug("Finished executing orchestration plan {}", orchestrationPlan);
    }

    @Override
    public void executeOrchestrationPlanNode(OrchestrationPlanNode eventNode) {
        log.info(NODE_EXECUTION_STARTED_LOG, eventNode.getId());

        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByOrchestrationPlanNodes_id(eventNode.getId())
                .orElseThrow(() -> new CoodNonRecoverableAndNonRetryableException(
                        new OrchestrationPlanNodeNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, eventNode.getId())
                ));

        OrchestrationPlanNodeState state = eventNode.getState();
        List<OrchestrationPlanNode> relatedNodes = findRelatedNodes(orchestrationPlan, eventNode);
        switch (state) {
            case HELD -> {
                eventNode.getOrchestrationNodeSchedule().setEstimatedOrderItemDeliveryLeadTime(OrchestrationPlanServiceImpl.UNDEFINED_LONG_VALUE);

                orchestrationPlanService.updateNodeDataInOrchestrationPlan(eventNode);
            }
            case IN_PROGRESS -> {
                deliverSelectedNodesService.startDeliverSelectedNode(orchestrationPlan, eventNode);
            }
            case COMPLETED -> {
                //set order item completion date for completed node
                eventNode.getOrchestrationNodeSchedule().setActualOrderItemCompletionDate(Instant.now());
                orchestrationPlanService.updateNodeDataInOrchestrationPlan(eventNode);

                //set order item start date for nodes should be delivered after completed node
                getNodesThatShouldBeDeliveredAfterCompletedNode(eventNode, orchestrationPlan).forEach(node -> {
                    node.getOrchestrationNodeSchedule().setOrderItemStartDate(Instant.now());
                    node.setState(OrchestrationPlanNodeState.IN_PROGRESS);
                    orchestrationPlanService.updateNodeDataInOrchestrationPlan(node);
                });
            }
            case FAILED, ABORTED, REJECTED -> abortRelatedNodes(relatedNodes);
            default -> log.info(NO_ACTION_FOR_NODE_WITH_ID_IN_STATE, eventNode.getId(), state);
        }
        log.info(NODE_EXECUTION_FINISHED_LOG, eventNode.getId());
    }

    private void abortRelatedNodes(List<OrchestrationPlanNode> nodes) {
        nodes.forEach(node -> {
            try {
                node.setState(OrchestrationPlanNodeState.ABORTED);
                updateNodeAndProductStateHandler.update(node);
            } catch (Exception e) {
                log.error("Failed to abort node {}", node.getId(), e);
            }
        });
    }

    private List<OrchestrationPlanNode> findRelatedNodes(OrchestrationPlan orchestrationPlan, OrchestrationPlanNode node) {
        return orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(n -> isRelatedNode(node, n))
                .collect(Collectors.toList());
    }

    private boolean isRelatedNode(OrchestrationPlanNode currentNode, OrchestrationPlanNode relatedNode) {
        if (relatedNode.getRelatedOrchestrationPlanNode() != null) {
            return relatedNode.getRelatedOrchestrationPlanNode().stream()
                    .anyMatch(
                            rn -> rn.getRelatedNodeId().equals(currentNode.getId()) && rn.getRelationshipType().equals(RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER));
        }
        return false;
    }

    private Set<OrchestrationPlanNode> getNodesThatShouldBeDeliveredAfterCompletedNode(OrchestrationPlanNode completedNode, OrchestrationPlan orchestrationPlan) {
        List<OrchestrationPlanNode> relatedNodes = findRelatedNodes(orchestrationPlan, completedNode);

        return relatedNodes.stream()
                .filter(node -> deliverSelectedNodesService.shouldStartDeliveryForNode(node, orchestrationPlan))
                .collect(Collectors.toSet());
    }
}
