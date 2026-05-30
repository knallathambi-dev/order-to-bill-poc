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
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryFactoryRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrderItemStatusPayloadEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.CfsDeliveryImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl.TangibleDeliveryImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.handler.DeliveryOrderEventHandler;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryOrderEventHandlerImpl implements DeliveryOrderEventHandler {

    private final TangibleDeliveryImpl deliveryFactory;

    private final CfsDeliveryImpl cfsDelivery;

    private final EventPublisher eventPublisher;

    @Override
    public void handleEvent(DeliveryOrderEvent deliveryOrderEvent) {
        log.info("Start delivery for nodes");
        DeliveryFactoryRef.DeliveryFactoryEnum deliveryFactoryType = deliveryOrderEvent.getEvent()
                .getDeliveryOrder()
                .getDeliveryFactoryRef()
                .getDeliveryFactoryType();

        if (deliveryFactoryType == DeliveryFactoryRef.DeliveryFactoryEnum.SHIPPING_ORDER_MANAGEMENT) {
            deliveryFactory.deliver(deliveryOrderEvent);
        } else {
            cfsDelivery.deliver(deliveryOrderEvent);
        }
    }

    @Override
    public void handleDeadLetter(DeliveryOrderEvent deliveryOrderEvent, CoodError error, String traceParent, String topicName) {
        log.info("Delivery start DLT handler | Orchestration plan id: {} | exceptionMessage: {} | exceptionReason: {} | exceptionCode: {} | traceParent: {}",
                deliveryOrderEvent.getEvent().getDeliveryOrder().getOrchestrationPlanId(),
                error.message(),
                error.reason(),
                error.code(),
                traceParent
        );

        deliveryOrderEvent.getEvent()
                .getDeliveryOrder()
                .getOrderItemRef().forEach(orderItemRef -> {
                    orderItemRef.setDeliveryStatusMapping(
                            DeliveryStatusMapping.builder()
                                    .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.HELD)
                                    .deliveryStatus(null)
                                    .build());

                    eventPublisher.publishEvent(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT, DeliveryOrderItemStatusPayloadEvent.builder()
                                    .sourcePayload(deliveryOrderEvent)
                                    .orderItemRef(orderItemRef)
                                    .build(),
                            Map.of(
                                    Headers.CONSUMER_ERROR, error,
                                    Headers.SOURCE_TOPIC_NAME, topicName
                            ));
                });
    }
}
