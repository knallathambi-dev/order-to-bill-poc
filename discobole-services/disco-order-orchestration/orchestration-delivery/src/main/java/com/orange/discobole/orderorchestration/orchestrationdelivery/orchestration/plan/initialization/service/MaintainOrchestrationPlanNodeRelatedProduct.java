// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.initialization.service;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationRelationship;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;

import java.util.Set;

public interface MaintainOrchestrationPlanNodeRelatedProduct {
    void maintainRelatedProductSpec(Set<OrchestrationPlanNode> orchestrationPlanNodes, ProductOrderItem productOrderItemId,
                                    ProductSpecificationRelationship productSpecificationRelationship);
}
