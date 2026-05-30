// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.util;


import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ShippingOrderItemStatus;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ProductOrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ShippingOrderDtoBuilderUtil {
    private ShippingOrderDtoBuilderUtil() {
    }

    public static ShippingOrder buildShippingOrderFromDeliveryOrderItem(DeliveryOrder deliveryOrder) {
        if (deliveryOrder == null || CollectionUtils.isEmpty(deliveryOrder.getOrderItemRef())) {
            throw new IllegalArgumentException("deliveryOrder must not be null and must have at least one order item");
        }

        return ShippingOrder.builder()
                .id(deliveryOrder.getFactoryOrderId())
                .shippingOrderItem(ShippingOrderDtoBuilderUtil.shippingOrderItems(deliveryOrder))
                .build();
    }

    public static List<ShippingOrderItem> shippingOrderItems(DeliveryOrder deliveryOrder) {
        return deliveryOrder.getOrderItemRef().stream()
                .map(ref -> ShippingOrderItem.builder()
                        .id(ref.getFactoryOrderItemId())
                        .productOrderItem(ProductOrderItemRef.builder()
                                .id(ref.getProductOrderItemId())
                                .build())
                        .status(ref.getFactoryOrderItemId() != null && ref.getFactoryOrderItemId().contains("held")
                                ? ShippingOrderItemStatus.HELD.getValue()
                                : ShippingOrderItemStatus.COMPLETED.getValue())
                        .build())
                .toList();
    }
}
