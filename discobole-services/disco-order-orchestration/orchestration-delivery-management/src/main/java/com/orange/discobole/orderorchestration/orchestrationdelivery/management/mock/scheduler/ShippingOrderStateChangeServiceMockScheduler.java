// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.scheduler;


import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryFactoryRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.util.ShippingOrderDtoBuilderUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.ReactiveDeliveryOrderRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class ShippingOrderStateChangeServiceMockScheduler {

    private static final int CHUNK_SIZE = 100;

    @Setter
    @Getter
    @Value("${mocks.shippingOrderStateChangeScheduler.enable:true}")
    private boolean isEnabled;

    private final ReactiveDeliveryOrderRepository reactiveDeliveryOrderRepository;

    private final EventPublisher eventPublisher;

    private final NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    private List<DeliveryOrder> filterDeliveryOrdersThatShouldBeMocked(List<DeliveryOrder> deliveryOrders) {
        List<String> allNodeIds = deliveryOrders.stream()
                .flatMap(deliveryOrder -> deliveryOrder.getOrderItemRef().stream())
                .map(OrderItemRef::getOrchestrationNodeId)
                .toList();

        Map<String, Instant> instantMap = nodeDeliveryTimeRepository.findByNodeIdIn(allNodeIds).stream()
                .collect(Collectors.toMap(
                        NodeDeliveryTimeRepository.NodeDeliveryTime::nodeId,
                        NodeDeliveryTimeRepository.NodeDeliveryTime::deliveryTime));

        return deliveryOrders.stream()
                .filter(deliveryOrder ->
                        deliveryOrder.getDeliveryFactoryRef() != null &&
                        DeliveryFactoryRef.DeliveryFactoryEnum.SHIPPING_ORDER_MANAGEMENT
                                .equals(deliveryOrder.getDeliveryFactoryRef().getDeliveryFactoryType()) &&
                        !CollectionUtils.isEmpty(deliveryOrder.getOrderItemRef()) &&
                        deliveryOrder.getOrderItemRef().stream()
                                .allMatch(orderItemRef -> {
                                    Instant t = instantMap.get(orderItemRef.getOrchestrationNodeId());
                                    return Objects.isNull(t) || t.isBefore(Instant.now());
                                }))
                .toList();
    }

    // use fixed delay to make sure that a job only starts after the previous one finishes
    // to avoid multiple jobs trying to deliver the same order.
    @Scheduled(fixedDelayString = "${mocks.shippingOrderStateChangeScheduler.jobDelayDuration}")
    public void mockShippingOrderStateChange() {
        if (!isEnabled) {
            log.debug("job is disabled");
            return;
        }

        log.debug("Starting ShippingOrderStateChangeServiceMockScheduler job");

        Flux<DeliveryOrder> deliveryOrdersFlux = reactiveDeliveryOrderRepository.findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull();

        deliveryOrdersFlux
                .buffer(CHUNK_SIZE)
                .flatMap(chunk -> {
                    try {
                        List<DeliveryOrder> mockableOrders = filterDeliveryOrdersThatShouldBeMocked(chunk);

                        List<ShippingOrder> shippingOrderList = mockableOrders.stream()
                                .map(ShippingOrderDtoBuilderUtil::buildShippingOrderFromDeliveryOrderItem)
                                .toList();

                        if (!CollectionUtils.isEmpty(shippingOrderList)) {
                            eventPublisher.publishEvents(CDCEvent.SHIPPING_ORDER_STATE_CHANGE_EVENT, shippingOrderList);
                        }
                    } catch (Exception e) {
                        log.error("Error processing chunk: {}", e.getMessage(), e);
                    }
                    return Mono.empty();
                })
                .doOnError(throwable -> {
                    log.error("Error in reactive pipeline: {}", throwable.getMessage(), throwable);
                    throw new CoodNonRecoverableAndNonRetryableException(
                            new CoodTechnicalException(ExceptionCode.PLANS_CHUNK_STATE_CHANGE_EXCEPTION));
                })
                .subscribe();

        log.debug("ShippingOrderStateChangeServiceMockScheduler job finished");
    }
}
