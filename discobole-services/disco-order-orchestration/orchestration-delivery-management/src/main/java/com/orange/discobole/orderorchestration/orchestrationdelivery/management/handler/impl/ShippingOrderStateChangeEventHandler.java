// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.impl;


import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.TangibleDeliveryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShippingOrderStateChangeEventHandler {

    private final TangibleDeliveryImpl tangibleDelivery;

    public void handleEvent(ShippingOrderStateChangeEvent event) {
        log.info("trigger the shipping order event ... handler");
        tangibleDelivery.executePostProcessDelivery(event);
    }

    public void handleDltEvent(ShippingOrderStateChangeEvent event, CoodError error, String topicName) {
        log.error("Error has occurred: {} in [ShippingOrderStateChangeEventHandler] DLT event for shipping order with id={} received on topic={} — DLT handling not yet implemented",
                event.getEvent().getShippingOrder().getId(),
                error,
                topicName
        );
    }
}
