// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.cucumber.tests.records;

public record OrchestrationPlanNodeRecord(String id, String state, String orderId, String previousState, String realizingServiceId, String realizingServiceHref, String productId, String productType, String relationshipType, String orderItemId,  String orderItemAction, String supplyChainOrderId, String supplyChainOrderItemId, String serviceOrderId, String serviceOrderItemId, String serviceSpecificationId, String requestedDeliveryDate, String action, String quantity, String shippingOrderId) {
}
