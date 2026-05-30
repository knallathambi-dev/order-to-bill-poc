// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.product.kafka.handler.ProductUpdateHandler;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import org.springframework.stereotype.Component;

@Component
public class CompletedNodeProductUpdateHandler extends ProductUpdateHandler {

    public CompletedNodeProductUpdateHandler(ProductManagementService productManagementService) {
        super(productManagementService);
    }

    @Override
    public boolean isEligibleForUpdate(OrchestrationPlanNode node) {
        return OrchestrationPlanNodeState.COMPLETED.equals(node.getState());
    }

    @Override
    protected void updateCfsProduct(Product product, OrchestrationPlanNode node) {
        RelatedProductOrderItem orderItem = node.getActualRelatedOrderItem();
        switch (ProductOrderItemActionType.fromValue(orderItem.getAction())) {
            case ADD, MODIFY, MIGRATE ->
                    productManagementService.updateActualCPIBProductCharacteristicBasedOnServiceOrderItemCompletionStatue(product, node);
            case DELETE ->
                    productManagementService.updateCFSOperationalStatus(product, ProductOperationalStatusType.TERMINATED);
            default ->
                    throw CoodNonRecoverableAndNonRetryableException.of(new ProductOrderValidationException(ExceptionCode.INVALID_PRODUCT_ACTION, orderItem.getAction(), product.getId()));
        }
    }

    @Override
    protected void updateTangibleProduct(Product product, OrchestrationPlanNode node) {
        RelatedProductOrderItem orderItem = node.getActualRelatedOrderItem();

        if (!ProductOrderItemActionType.ADD.getValue().equals(orderItem.getAction())) {
            throw CoodNonRecoverableAndNonRetryableException.of(new ProductOrderValidationException(ExceptionCode.INVALID_PRODUCT_ACTION, orderItem.getAction(), product.getId()));
        }

        productManagementService.updateTangibleOperationalStatus(product, ProductOperationalStatusType.SOLD);
    }
}
