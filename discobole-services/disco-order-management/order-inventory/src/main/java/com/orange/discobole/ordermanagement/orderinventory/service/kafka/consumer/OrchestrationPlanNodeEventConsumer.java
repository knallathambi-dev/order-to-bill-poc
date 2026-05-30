// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.kafka.consumer;

import com.orange.discobole.ordermanagement.orderinventory.domain.ProductOrderItemStateType;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrchestrationPlanNodeStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrderItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.orange.discobole.ordermanagement.commons.enumeration.EventType.ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT;

public class OrchestrationPlanNodeEventConsumer implements Consumer<Message<OrchestrationPlanNodeStateChangeEvent>> {
    private final Logger log = LoggerFactory.getLogger(OrchestrationPlanNodeEventConsumer.class);
    private final ProductOrderService productOrderService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrchestrationPlanNodeEventConsumer(ProductOrderService productOrderService) {
        this.productOrderService = productOrderService;
    }

    @Override
    public void accept(Message<OrchestrationPlanNodeStateChangeEvent> message) {
        try {
            log.debug("Consuming orchestration node state change event message: {}", message);

            OrchestrationPlanNodeStateChangeEvent payload = message.getPayload();
            if (!isOrchestrationNodeStateChangeEvent(payload)) {
                return;
            }

            OrchestrationPlanNode orchestrationPlanNode = payload.getEvent().getOrchestrationPlanNode();
            if (isOrchestrationNodeNotValid(orchestrationPlanNode)) {
                return;
            }

            OrchestrationPlanNodeState state = orchestrationPlanNode.getState();
            if (!isValidOrchestrationState(state)) {
                return;
            }

            String productOrderId = orchestrationPlanNode.getRelatedProductOrder().getId();
            ProductOrderItemStateType stateType = mapOrchestrationPlanStateToItemState(state);
            var productOrderItemIds = getProductOrderItemIds(orchestrationPlanNode);
            for (String productOrderItemId : productOrderItemIds) {
                productOrderService.updateProductOrderHierarchy(productOrderId, productOrderItemId, stateType);
            }
        } catch (Exception e) {
            log.error("Invalid received message", e);
        }
    }

    private boolean isOrchestrationNodeStateChangeEvent(OrchestrationPlanNodeStateChangeEvent payload) {
        return payload != null && ORCHESTRATION_PLAN_NODE_STATE_CHANGE_EVENT.getValue().equals(payload.getEventType()) && payload.getEvent() != null;
    }

    private boolean isOrchestrationNodeNotValid(OrchestrationPlanNode orchestrationPlanNode) {
        return orchestrationPlanNode == null ||
                orchestrationPlanNode.getRelatedProductOrder() == null ||
                orchestrationPlanNode.getRelatedProductOrder().getId() == null ||
                orchestrationPlanNode.getRelatedProductOrderItem() == null ||
                isRelatedProductOrderItemIdsNotNull(orchestrationPlanNode.getRelatedProductOrderItem()) ||
                orchestrationPlanNode.getState() == null;
    }

    private boolean isValidOrchestrationState(OrchestrationPlanNodeState state) {
        return switch (state) {
            case IN_PROGRESS, HELD, COMPLETED, ABORTED, FAILED -> true;
            default -> false;
        };
    }

    private List<String> getProductOrderItemIds(OrchestrationPlanNode orchestrationPlanNode) {
        return orchestrationPlanNode.getRelatedProductOrderItem().stream()
                .map(RelatedProductOrderItem::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean isRelatedProductOrderItemIdsNotNull(List<RelatedProductOrderItem> relatedProductOrderItems) {
        return !relatedProductOrderItems.stream().filter(relatedProductOrderItem -> Objects.isNull(relatedProductOrderItem.getId())).toList().isEmpty();
    }

    private ProductOrderItemStateType mapOrchestrationPlanStateToItemState(OrchestrationPlanNodeState orchestrationPlanNodeState) {
        return switch (orchestrationPlanNodeState) {
            case ACKNOWLEDGED -> ProductOrderItemStateType.ACKNOWLEDGED;
            case IN_PROGRESS -> ProductOrderItemStateType.INPROGRESS;
            case HELD -> ProductOrderItemStateType.HELD;
            case REJECTED -> ProductOrderItemStateType.REJECTED;
            case COMPLETED -> ProductOrderItemStateType.COMPLETED;
            case FAILED, ABORTED -> ProductOrderItemStateType.FAILED;
            default ->
                    throw new IllegalArgumentException("Unhandled OrchestrationPlanState: " + orchestrationPlanNodeState);
        };
    }
}