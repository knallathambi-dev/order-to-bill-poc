// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.*;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNodeNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.DeliveryStatusException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.ProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.RelatedProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.DataPersistenceKafkaSessionService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliverSelectedNodesService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliveryOrderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.MaintainDeliveryNodeRelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.VerifyNodeService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.UpdateNodeAndProductStateHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.service.OrchestrationPlanService;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import com.orange.discobole.productinventory.dto.v1.Product;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.COOD_TECHNICAL_EXCEPTION;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.util.ExceptionUtils.throwingFunctionWrapper;

@Slf4j
@Component
@SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
@RequiredArgsConstructor
public class DeliverSelectedNodesServiceImpl implements DeliverSelectedNodesService {

    public static final String STATUS_FIELD = "status";

    private final OrchestrationPlanService orchestrationPlanService;

    private final ProductManagementService productManagementService;

    private final MaintainDeliveryNodeRelatedProduct maintainDeliveryNodeRelatedProduct;

    private final VerifyNodeService verifyNodeService;

    private final DataPersistenceKafkaSessionService dataPersistenceKafkaSessionService;

    private final UpdateNodeAndProductStateHandler updateNodeAndProductStateHandler;

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    private final DeliveryOrderService deliveryOrderService;

    private final ObjectMapper objectMapper;

    /**
     * Deliver orchestration plan nodes
     *
     * @param orchestrationPlan
     * @param eventNode
     */
    @Override
    public void startDeliverSelectedNode(OrchestrationPlan orchestrationPlan, OrchestrationPlanNode eventNode) throws CoodTechnicalException, CoodDBException {
        log.info("Start delivery for orchestrationPlanId : {}.", orchestrationPlan.getId());
        log.debug("Start delivery for orchestrationPlan : {} and node {}", orchestrationPlan, eventNode);

        // set node actual order start date
        eventNode.getOrchestrationNodeSchedule().setActualOrderItemStartDate(Instant.now());

        Product installedProduct = getOrderItemProduct(eventNode.getActualOrderItemId(), orchestrationPlan.getRelatedProductOrder().getId());
        maintainDeliveryNodeRelatedProduct.maintainOrchestrationPlanNodeRelatedProduct(eventNode, installedProduct);
        dataPersistenceKafkaSessionService.persistAll();

        verifyNodeService.verifyOrchestrationPlanNodeDelivery(eventNode, installedProduct);

        if (verifyNodeService.isCommercialMigrationNode(eventNode)) {
            updateNodeState(eventNode, COMPLETED);
        } else {
            updateNodeState(eventNode, IN_DELIVERY);
            if (eventNode.isCFSOrchestrationPlanNode()) {
                List<OrderItemRef> orderItemRefs = List.of(deliveryOrderService.buildOrderItemRef(eventNode));

                DeliveryFactoryRef deliveryFactoryRef = DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT)
                        .href(eventNode.getRelatedServiceOrder().getSomRef())
                        .build();

                deliveryOrderService.publishStartDeliveryEvent(orchestrationPlan, orderItemRefs, deliveryFactoryRef);
            }
        }

        log.info("finished delivery for orchestrationPlanId : {}.", orchestrationPlan.getId());
        log.debug("finished delivery for orchestrationPlan : {} and node {}", orchestrationPlan, eventNode);
    }

    private void updateNodeState(OrchestrationPlanNode node, OrchestrationPlanNodeState state) {
        node.setState(state);
        updateNodeAndProductStateHandler.update(node);
    }

    public Product getOrderItemProduct(String orderItemId, String orderId) {
        log.info("DeliverSelectedNodesServiceImpl | getOrderItemToProductDtoMap | orderId : {} orderItemId : {}.", orderId, orderItemId);
        return productManagementService.getProductsByOrderIdAndItemIds(List.of(orderItemId), orderId)
                .stream().findFirst().orElseThrow(() -> new CoodRecoverableAndNonRetryableException(new ProductNotFoundException(ExceptionCode.INSTALLED_PRODUCT_NOT_FOUND_EXCEPTION, orderId, orderItemId, null)));
    }

    @Override
    public boolean shouldStartDeliveryForNode(OrchestrationPlanNode node, OrchestrationPlan orchestrationPlan) {
        //story 28
        log.info("DeliverSelectedNodesServiceImpl | shouldStartDeliveryForNode | Executing the validating of whether start delivering the node {id: {}}", node.getId());
        log.debug("DeliverSelectedNodesServiceImpl | shouldStartDeliveryForNode | Executing the validating of whether start delivering the node {}", node);
        List<OrchestrationPlanNode> prerequisiteNodes;
        List<OrchestrationPlanNodeState> orchestrationPlanNodeInProgressStates = Arrays.asList(INITIALIZED, IN_PROGRESS, ACKNOWLEDGED, IN_DELIVERY);
        List<RelatedOrchestrationPlanNode> relatedOrchestrationPlanNodes = node.getRelatedOrchestrationPlanNode();
        //TODO execute by single query please
        //todo consider if we need to check relation type if we added new relation
        prerequisiteNodes = relatedOrchestrationPlanNodes.stream().map(relatedNode -> orchestrationPlan.getOrchestrationPlanNodeById(relatedNode.getRelatedNodeId()).get())
                .collect(Collectors.toList());

        boolean isAllPrerequisiteNodesCompleted = prerequisiteNodes.stream()
                .allMatch(obj -> obj.getState().equals(COMPLETED));

        boolean isAnyPrerequisiteNodesInProgress = prerequisiteNodes.stream()
                .anyMatch(obj -> orchestrationPlanNodeInProgressStates.contains(obj.getState()));

        //TODO block method name, setNodeInProgressIfAllRelatedNodesCompleted
        if (isAllPrerequisiteNodesCompleted) {
            log.info("DeliverSelectedNodesServiceImpl | shouldStartDeliveryForNode | orchestration plan node published: " + node);
            //decide if we will start delivery then (true)
            log.info("DeliverSelectedNodesServiceImpl | shouldStartDeliveryForNode | Executing the validating of whether start delivering the node {id: {}} result {}", node.getId(), true);
            return true;
        } else if (isAnyPrerequisiteNodesInProgress) { // same as business condition in flow: some of prerequiset are not executed
            log.info("DeliverSelectedNodesServiceImpl | shouldStartDeliveryForNode | Some prerequisites are not executed {node id: {}}", node.getId());
        }
        log.info("DeliverSelectedNodesServiceImpl | shouldStartDeliveryForNode | Executing the validating of whether start delivering the node {id: {}} result {}", node.getId(), false);
        return false;
    }

    @Override
    public void postDeliverNodeActions(DeliveryOrderItemStatusPayloadEvent deliveryStatusPayloadEvent, Map<String, String> headers) {
        String orchestrationNodeId = deliveryStatusPayloadEvent.getOrderItemRef().getOrchestrationNodeId();
        OrchestrationPlanNode node = orchestrationPlanRepository.findOrchestrationPlanNodeById(orchestrationNodeId).orElseThrow(() -> new CoodNonRecoverableAndNonRetryableException(
                new OrchestrationPlanNodeNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, orchestrationNodeId)));

        log.info("Start delivery node id {}, isCFS: {}, isTangible: {}", node.getId(), node.isCFSOrchestrationPlanNode(), node.isTangibleOrchestrationPlanNode());
        log.debug("deliverNode | Start delivery node CFS: {}, Tangible: {}", node, node.isTangibleOrchestrationPlanNode());

        if (DeliveryStatusMapping.NodeStatusEnum.HELD.equals(deliveryStatusPayloadEvent.getOrderItemRef().getDeliveryStatusMapping().getNodeStatus())) {
            createFalloutProcess(node, headers);
        }

        node.setState(OrchestrationPlanNodeState.fromValue(deliveryStatusPayloadEvent.getOrderItemRef().getDeliveryStatusMapping().getNodeStatus().toString()));

        if (node.isTangibleOrchestrationPlanNode()) {
            node.setRelatedSupplyChainOrder(RelatedSupplyChainOrder.builder()
                    .id(deliveryStatusPayloadEvent.getFactoryOrderId())
                    .orderItemId(deliveryStatusPayloadEvent.getOrderItemRef().getFactoryOrderItemId())
                    .build());
        } else {
            RelatedProduct relatedProduct = node.getRelatedProduct().stream().filter(relatedProduct1 -> relatedProduct1.getRelationshipType() == RelatedProductRelationType.DELIVERS).findFirst()
                    .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(new RelatedProductNotFoundException(ExceptionCode.RELATED_PRODUCT_WITH_RELATION_TYPE_NOT_FOUND, RelatedProductRelationType.DELIVERS)));

            RealizingResourceRef realizingResourceRef = deliveryStatusPayloadEvent.getOrderItemRef().getRealizingResourceRef();

            if (realizingResourceRef == null) {
                throw new CoodRecoverableAndNonRetryableException(new CoodNotFoundException(COOD_TECHNICAL_EXCEPTION, "There is no realizingResourceRef in orderItemRef for node with id " + orchestrationNodeId));
            }

            relatedProduct.setRealisingService(List.of(RealisingService.builder()
                    .id(realizingResourceRef.getId())
                    .href(realizingResourceRef.getHref())
                    .build()));

            if (node.getRelatedServiceOrder() == null) {
                throw new CoodRecoverableAndNonRetryableException(new CoodNotFoundException(COOD_TECHNICAL_EXCEPTION, "There is no related service order in node with id " + orchestrationNodeId));
            }

            node.getRelatedServiceOrder().setId(deliveryStatusPayloadEvent.getFactoryOrderId());
            node.getRelatedServiceOrder().setOrderItemId(deliveryStatusPayloadEvent.getOrderItemRef().getFactoryOrderItemId());
        }

        updateNodeAndProductStateHandler.update(node);

        log.info("finished delivery node id {}", node.getId());
    }

    private void createFalloutProcess(OrchestrationPlanNode orchestrationPlanNode, Map<String, String> headers) {
        log.info("Orchestration plan node id {} has state HELD", orchestrationPlanNode.getId());
        String errorHeader = headers.get(Headers.CONSUMER_ERROR);
        
        CoodError coodError;
        if (errorHeader == null || errorHeader.trim().isEmpty()) {
            // Create a default error when the node is held but no specific error is provided
            String defaultErrorMessage = "Orchestration plan node with id[%s] held".formatted(orchestrationPlanNode.getId());
            log.info("No CONSUMER_ERROR header found, creating default error: {}", defaultErrorMessage);
            coodError = new CoodError(defaultErrorMessage, defaultErrorMessage, defaultErrorMessage, java.time.Instant.parse("2000-01-01T00:00:00Z"));
        } else {
            coodError = throwingFunctionWrapper(o -> objectMapper.readValue((String) o, CoodError.class))
                    .apply(errorHeader);
        }

        throw new CoodRecoverableAndNonRetryableException(DeliveryStatusException.of(coodError));
    }

    @Override
    public void updateNodeWithError(OrchestrationPlanNode orchestrationPlanNode) {
        orchestrationPlanService.updateOrchestrationPlanNodeStateWithErrorMessage(orchestrationPlanNode, OrchestrationPlanNodeState.HELD);
    }
}
