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
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.CfsDeliveryImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceOrderEventHandler {

    private final CfsDeliveryImpl cfsDelivery;

    public ServiceOrderEventHandler(CfsDeliveryImpl cfsDelivery) {
        this.cfsDelivery = cfsDelivery;
    }

    public final void handleEvent(ServiceOrderEvent event) {
        log.info("trigger the service order event ... handler");
        cfsDelivery.executePostProcessDelivery(event);
    }

    public final void handleDltEvent(ServiceOrderEvent event, CoodError error, String topicName) {
        log.error("Error has occurred: {} in [ServiceOrderStateChangeEventHandler] DLT event for service order with id={} received on topic={} — DLT handling not yet implemented",
                error,
                event.getEvent().getServiceOrder().getId(),
                topicName
        );
    }
}
