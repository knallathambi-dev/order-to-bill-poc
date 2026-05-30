// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service;

import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.productinventory.dto.v1.Product;

public interface MaintainDeliveryNodeRelatedProduct {
    void maintainOrchestrationPlanNodeRelatedProduct(OrchestrationPlanNode opn, Product productDTO);
}
