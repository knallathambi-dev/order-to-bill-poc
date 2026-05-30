// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.notfounds.OrchestrationPlanNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service.DeliveryOrderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.execution.kafka.handler.DeliveryOrderEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.repository.OrchestrationPlanRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryTangibleOrderItemsEventHandlerImpl implements DeliveryOrderEventHandler {

    private final OrchestrationPlanRepository orchestrationPlanRepository;

    private final EventPublisher eventPublisher;

    private final CharacteristicMapper characteristicMapper;

    private final DeliveryOrderService deliveryOrderService;

    @Override
    @Transactional
    public void handleEvent(OrchestrationPlanNodeStateChangeEvent message) {

        OrchestrationPlanNode orchestrationPlanNode = message.getEvent().getOrchestrationPlanNode();
        if (orchestrationPlanNode.isDeliveryBatched()) {
            return;
        }


        OrchestrationPlan orchestrationPlan = orchestrationPlanRepository
                .findOrchestrationPlanByOrchestrationPlanNodes_id(orchestrationPlanNode.getId())
                .orElseThrow(() -> new CoodNonRecoverableAndNonRetryableException(
                        new OrchestrationPlanNotFoundException(ExceptionCode.ORCHESTRATION_PLAN_NODE_ID_NOT_FOUND, orchestrationPlanNode.getId()))
                );

        if (orchestrationPlanNode.isTangibleOrchestrationPlanNode()) {
            processTangibleNodeAndRelatedNodes(orchestrationPlanNode, orchestrationPlan);
        } else if (orchestrationPlanNode.isCFSOrchestrationPlanNode() && HELD.equals(orchestrationPlanNode.getState())) {
            Map<String, List<OrchestrationPlanNode>> nodesTree = new HashMap<>();
            orchestrationPlan.getOrchestrationPlanNodes()
                    .forEach(node -> Optional.ofNullable(node.getRelatedOrchestrationPlanNode()).orElseGet(List::of)
                            .forEach(relatedNode -> {
                                        nodesTree.computeIfAbsent(relatedNode.getRelatedNodeId(), k -> new ArrayList<>())
                                                .add(node);
                                    }
                            ));

            Queue<OrchestrationPlanNode> queue = new ArrayDeque<>();
            Set<OrchestrationPlanNode> tangibleNodes = new HashSet<>();
            queue.add(orchestrationPlanNode);
            while (!queue.isEmpty()) {
                OrchestrationPlanNode node = queue.poll();
                if (node.isTangibleOrchestrationPlanNode() && !node.isDeliveryStarted()) {
                    tangibleNodes.add(node);
                }
                queue.addAll(nodesTree.getOrDefault(node.getId(), List.of()));
            }
            tangibleNodes.forEach(node -> processTangibleNodeAndRelatedNodes(node, orchestrationPlan));
        }
    }

    /* ===================== MAIN PROCESS ===================== */

    private void processTangibleNodeAndRelatedNodes(OrchestrationPlanNode sourceNode, OrchestrationPlan orchestrationPlan) {

        RelatedProduct shipmentProduct = sourceNode.getRelatedProduct().stream()
                .filter(rp -> RelatedProductType.SHIPMENT_PRODUCT.equals(rp.getType()))
                .findFirst()
                .orElseThrow();

        Map<String, Object> shipmentCharacteristics = extractCharacteristics(shipmentProduct);

        List<OrchestrationPlanNode> matchingNodes = orchestrationPlan.getOrchestrationPlanNodes().stream()
                .filter(node -> hasMatchingShipmentProduct(node, shipmentCharacteristics))
                .toList();

        List<OrchestrationPlanNode> ackNodes = matchingNodes.stream()
                .filter(node -> List.of(ACKNOWLEDGED, IN_PROGRESS).contains(node.getState()))
                .filter(node -> !orchestrationPlan.hasPredecessorInTerminalStates(node))
                .toList();

        if (ackNodes.isEmpty()) {
            List<OrchestrationPlanNode> inDeliveryNodes = matchingNodes.stream()
                    .filter(node -> IN_DELIVERY.equals(node.getState()) && !node.isDeliveryBatched())
                    .toList();

            List<OrderItemRef> orderItemRefs = inDeliveryNodes.stream().map(deliveryOrderService::buildOrderItemRef).toList();

            if (!inDeliveryNodes.isEmpty()) {
                DeliveryFactoryRef deliveryFactoryRef = DeliveryFactoryRef.builder()
                        .deliveryFactoryType(DeliveryFactoryRef.DeliveryFactoryEnum.SHIPPING_ORDER_MANAGEMENT)
                        .build();

                deliveryOrderService.publishStartDeliveryEvent(orchestrationPlan, orderItemRefs, deliveryFactoryRef);
                inDeliveryNodes.forEach(node -> node.setDeliveryBatched(true));
                orchestrationPlanRepository.save(orchestrationPlan);
            }
        }
    }

    /* ===================== CHARACTERISTICS ===================== */

    private Map<String, Object> extractCharacteristics(RelatedProduct product) {
        try {
            return product.getProductCharacteristic().stream()
                    .filter(characteristic -> {
                        log.info("characteristic name {}", characteristic.getName());
                        return characteristic.getName() != null;
                    })
                    .collect(Collectors.toMap(
                            characteristic -> characteristic.getName().toLowerCase(),
                            this::extractValue,
                            (a, b) -> a
                    ));
        }  catch (Exception e) {
            log.error("error in product characteristics {} with id {}", product.getProductCharacteristic(), product.getProductOrderItemId());
            throw new CoodDBException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, e, "Error while extracting product characteristics");
        }
    }

    private Object extractValue(Characteristic characteristic) {
        if (characteristic instanceof StringCharacteristic sc) {
            return sc.getValue();
        } else if (characteristic instanceof DateCharacteristic dc) {
            return dc.getValue();
        } else {
            log.info(characteristic.getName());
            return "";
        }
    }

    /* ===================== SHIPMENT MATCHING ===================== */

    private boolean hasMatchingShipmentProduct(OrchestrationPlanNode node, Map<String, Object> expectedCharacteristics) {
        return node.getRelatedProduct().stream()
                .filter(rp -> RelatedProductType.SHIPMENT_PRODUCT.equals(rp.getType()))
                .anyMatch(rp ->
                        extractCharacteristics(rp).equals(expectedCharacteristics)
                );
    }

    @Override
    public void handleDeadLetter(OrchestrationPlanNodeStateChangeEvent payload, FalloutCharacteristicWrapper characteristicWrapper) {
    }
}
