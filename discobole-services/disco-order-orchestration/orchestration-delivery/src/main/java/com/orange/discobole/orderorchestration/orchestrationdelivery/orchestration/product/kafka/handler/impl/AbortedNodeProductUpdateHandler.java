// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.ProductUpdateHandler;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType.ADD;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType.MIGRATE;

@Slf4j
@Component
public class AbortedNodeProductUpdateHandler extends ProductUpdateHandler {

    public AbortedNodeProductUpdateHandler(ProductManagementService productManagementService) {
        super(productManagementService);
    }

    @Override
    public boolean isEligibleForUpdate(OrchestrationPlanNode node) {
        return OrchestrationPlanNodeState.ABORTED.equals(node.getState());
    }

    @Override
    protected void updateCfsProduct(Product product, OrchestrationPlanNode node) {
        RelatedProductOrderItem orderItem = node.getActualRelatedOrderItem();
        ProductOrderItemActionType actionType = ProductOrderItemActionType.fromValue(orderItem.getAction());
        if (List.of(ADD, MIGRATE).contains(actionType)) {
            productManagementService.updateCFSOperationalStatus(product, ProductOperationalStatusType.ABORTED);
        }
    }

    @Override
    protected void updateTangibleProduct(Product product, OrchestrationPlanNode node) {
        RelatedProductOrderItem orderItem = node.getActualRelatedOrderItem();
        if (ADD.getValue().equals(orderItem.getAction())) {
            productManagementService.updateTangibleOperationalStatus(product, ProductOperationalStatusType.ABORTED);
        }
    }
}
