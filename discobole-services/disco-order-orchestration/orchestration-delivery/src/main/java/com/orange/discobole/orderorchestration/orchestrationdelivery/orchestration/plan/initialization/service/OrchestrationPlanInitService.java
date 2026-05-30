// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.exception.model.CoodMappingException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlan;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OrchestrationPlanInitService {
    OrchestrationPlan createOrchestrationPlan(ProductOrder productOrder) throws CoodMappingException, CoodDBException;

    List<ProductOrderItem> filterOrderItems(ProductOrder productOrder);

    List<OrchestrationPlanNode> buildDefaultCFSOrchestrationNodes(List<ProductOrderItem> productOrderItems, String productOrderId) throws CoodMappingException, CoodDBException;

    List<OrchestrationPlanNode> buildTangibleOrchestrationNodes(List<ProductOrderItem> productOrderItems, String productOrderId) throws CoodMappingException, CoodDBException;

    void initRelatedProductWithDeliversType(String orderId, Set<OrchestrationPlanNode> orchestrationPlanNodes, List<ProductOrderItem> orderItemDTOS, Map<String, ProductSpecification> productSpecification);

    OrchestrationPlan getOrchestrationPlanByRelatedProductOrderId(String relatedProductOrderId);

    List<OrchestrationPlanNode> buildMigrationOrchestrationNodes(List<ProductOrderItem> productOrderItems, String productOrderId);

    List<ProductOrderItem> fetchShippingAndTangibleOrderItems(List<ProductOrderItem> productOrderItems);

    Set<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> getProductOrderItemCharacteristics(ProductOrderItem productOrderItem, String orderId);
}


