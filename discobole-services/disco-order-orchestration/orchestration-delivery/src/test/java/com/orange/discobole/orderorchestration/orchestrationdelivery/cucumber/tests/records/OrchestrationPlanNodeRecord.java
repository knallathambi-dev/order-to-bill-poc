// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records;

public record OrchestrationPlanNodeRecord(
        String id,
        String action,
        String state,
        String orderId,
        String previousState,
        String realizingServiceId,
        String relatedNodes,
        String realizingServiceHref,
        String productId,
        String productType,
        String relationshipType,
        String orderItemId,
        String orderItemAction,
        String relationType,
        String orderItemStartDate,
        String actualOrderItemStartDate,
        String actualOrderItemCompletionDate,
        String expectedOrderItemCompletionDate,
        String estimatedOrderItemDeliveryLeadTime,
        String shippingOrderId,
        String shippingOrderItem,
        String specificationId,
        String somRef,
        Long estimatedLeadTimeDelivery,
        String relatedSupplyChainOrderId,
        String relatedSupplyChainOrderItemId,
        String relatedServiceOrderId,
        String relatedServiceOrderItemId
        ) {
}
