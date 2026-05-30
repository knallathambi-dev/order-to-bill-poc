// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.model.validations.OrchestrationPlanNodeValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.productinventory.dto.v1.Product;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class ProductUpdateHandler {

    protected final ProductManagementService productManagementService;

    public final void update(OrchestrationPlanNode node) {
        if (!isEligibleForUpdate(node)) {
            throw CoodNonRecoverableAndNonRetryableException
                    .of(new OrchestrationPlanNodeValidationException(ExceptionCode.INVALID_ORCHESTRATION_PLAN_NODE_STATE, node.getId()));
        }

        Product product = productManagementService.getProductsByOrderIdAndItemIds(List.of(node.getActualRelatedOrderItem().getId()), node.getRelatedProductOrder().getId()).get(0);
        if (node.isCFSOrchestrationPlanNode()) {
            updateCfsProduct(product, node);
        } else if (node.isTangibleOrchestrationPlanNode()) {
            updateTangibleProduct(product, node);
        }
    }

    public abstract boolean isEligibleForUpdate(OrchestrationPlanNode node);

    protected abstract void updateCfsProduct(Product product, OrchestrationPlanNode node);

    protected abstract void updateTangibleProduct(Product product, OrchestrationPlanNode node);

}
