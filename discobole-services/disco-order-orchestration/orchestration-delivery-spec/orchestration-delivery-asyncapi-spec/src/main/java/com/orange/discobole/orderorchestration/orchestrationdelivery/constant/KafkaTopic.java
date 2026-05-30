// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KafkaTopic {
    public static final String PRODUCT_ORDER_STATE_CHANGE_TOPIC = "disco.order-management.productOrderStateChange-event";
    public static final String PRODUCT_ORDER_STATE_CHANGE_DLT_TOPIC = "disco.order-management.productOrderStateChange-event-dlt";
    public static final String PRODUCT_ORDER_STATE_CHANGE_TMR_TOPIC = "disco.order-management.productOrderStateChange-event-tmr";
    public static final String ORCHESTRATION_PLAN_NODE_STATE_CHANGE_TOPIC = "disco.order-orchestration.orchestrationPlanNodeStateChange-event";
    public static final String ORCHESTRATION_PLAN_NODE_STATE_CHANGE_DLT_TOPIC = "disco.order-orchestration.orchestrationPlanNodeStateChange-event-dlt";
    public static final String ORCHESTRATION_PLAN_NODE_STATE_CHANGE_TMR_TOPIC = "disco.order-orchestration.orchestrationPlanNodeStateChange-event-tmr";
    public static final String ORCHESTRATION_PLAN_STATE_CHANGE_TOPIC = "disco.order-orchestration.orchestrationPlanStateChange-event";
    public static final String ORCHESTRATION_PLAN_STATE_CHANGE_DLT_TOPIC = "disco.order-orchestration.orchestrationPlanStateChange-event-dlt";
    public static final String DELIVERY_ORDER_ITEM_STATUS_TOPIC = "disco.delivery-management.deliveryOrderItemStatus-event";
    public static final String DELIVERY_ORDER_ITEM_STATUS_DLT_TOPIC = "disco.delivery-management.deliveryOrderItemStatus-event-dlt";
    public static final String FALLOUT_INCIDENT_STATE_CHANGE_TOPIC = "disco.order-orchestration-fallout.falloutIncidentStateChange-event";
    public static final String DELIVERY_ORDER_TOPIC = "disco.delivery-management.deliveryOrder-event";
    public static final String DELIVERY_ORDER_DLT_TOPIC = "disco.delivery-management.deliveryOrder-event-dlt";
    public static final String SERVICE_ORDER_STATE_CHANGE_TOPIC = "disco.service-order-management.serviceOrderStateChange-event";
    public static final String SHIPPING_ORDER_STATE_CHANGE_TOPIC = "disco.shipping-order-management.shippingOrderStateChange-event";
}
