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

public record ShippingOrderRecord(String id, String shippingOrderId, String shippingOrderItem, String shippingMode, String requestedDeliveryDate, String shippingAddress, String action) {
}
