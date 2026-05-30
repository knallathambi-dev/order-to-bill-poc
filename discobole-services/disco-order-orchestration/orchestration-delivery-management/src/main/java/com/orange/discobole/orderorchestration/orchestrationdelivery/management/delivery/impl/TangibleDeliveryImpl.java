// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.delivery.impl;

import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderCreate;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderStateChangeEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ShippingOrderManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator.ShippingOrderCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.DeliveryStatusMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.RandomUtil;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TangibleDeliveryImpl {

    private final EventPublisher eventPublisher;

    private final ShippingOrderCreator shippingOrderCreator;

    private final ShippingOrderManagementService shippingOrderManagementService;

    private final DeliveryOrderRepository deliveryOrderRepository;

    private final ErrorMessageMapper errorMessageMapper;

    private final NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    private final DeliveryStatusMapper deliveryStatusMapper;

    @Value("${mocks.shippingOrderStateChangeScheduler.minDelay:PT0S}")
    private Duration minDelay;

    @Value("${mocks.shippingOrderStateChangeScheduler.maxDelay:PT0S}")
    private Duration maxDelay;

    @Value("${mocks.shippingOrderStateChangeScheduler.enableDelay:false}")
    private Boolean enableDelay;

    @Value("${config.delivery.timeout:PT10S}")
    private Duration deliveryTimeout;

    public void deliver(DeliveryOrderEvent deliveryOrderEvent) {
        DeliveryOrder deliveryOrder = deliveryOrderEvent.getEvent().getDeliveryOrder();

        // added to check idempotency in case the same event was sent multiple times
        if (deliveryOrderRepository.findById(deliveryOrder.getId()).isPresent()) {
            log.info("[DeliveryOrderId={}] Already exists, skipping duplicate", deliveryOrder.getId());
            return;
        }

        deliveryOrder.setDeliveryOrderCreationDate(Instant.now());
        deliveryOrderRepository.save(deliveryOrder);
        log.info("[DeliveryOrderId={}] Persisted", deliveryOrder.getId());

        executeDeliveryMono(deliveryOrder)
                .doOnError(error -> {
                    log.error("[DeliveryOrderId={}] Execute delivery failed, sending to DLT {}",
                            deliveryOrder.getId(), error.getMessage());

                    CoodError coodError = errorMessageMapper.map(error);

                    Map<String, Object> headers = new HashMap<>();
                    headers.put(KafkaHeaders.RECEIVED_TOPIC, KafkaTopic.DELIVERY_ORDER_TOPIC);
                    headers.put(Headers.CONSUMER_ERROR, coodError);

                    eventPublisher.publishEvent(CDCEvent.DELIVERY_ORDER_DLT_EVENT, deliveryOrderEvent, headers);
                })
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }

    private Mono<Void> executeDeliveryMono(DeliveryOrder deliveryOrder) {
        List<OrderItemRef> orderItemRefs = deliveryOrder.getOrderItemRef();

        ShippingOrderCreate shippingOrderCreate = shippingOrderCreator.prepareShippingOrderRequest(
                orderItemRefs, deliveryOrder.getRelatedParty(), deliveryOrder.getProductOrderId());

        return Mono.defer(() ->
                        Mono.fromCallable(() -> shippingOrderManagementService.createShippingOrder(shippingOrderCreate))
                                .subscribeOn(Schedulers.boundedElastic())
                                .timeout(deliveryTimeout)
                )
                .flatMap(shippingOrder ->
                        Mono.fromRunnable(() -> {
                                    log.info("[DeliveryOrderId={}] ShippingOrder created — ShippingOrderId={}, ItemIds={}",
                                            deliveryOrder.getId(),
                                            shippingOrder.getId(),
                                            shippingOrder.getShippingOrderItem().stream().map(ShippingOrderItem::getId).toList());

                                    deliveryOrder.setFactoryOrderId(shippingOrder.getId());

                                    shippingOrder.getShippingOrderItem().forEach(shippingOrderItem ->
                                            orderItemRefs.stream()
                                                    .filter(ref -> ref.getProductOrderItemId()
                                                            .equals(shippingOrderItem.getProductOrderItem().getId()))
                                                    .findFirst()
                                                    .ifPresent(ref -> ref.setFactoryOrderItemId(shippingOrderItem.getId()))
                                    );

                                    deliveryOrderRepository.save(deliveryOrder);
                                    log.info("[DeliveryOrderId={}] Updated with factoryOrderId={}",
                                            deliveryOrder.getId(), shippingOrder.getId());

                                    // this section is used in the scheduler mocking the shipping response
                                    // so we can know when each delivery order should be responded to form the tangible factory
                                    {
                                        List<NodeDeliveryTimeRepository.NodeDeliveryTime> deliveryTimes = orderItemRefs.stream()
                                                .map(ref -> {
                                                    NodeDeliveryTimeRepository.NodeDeliveryTime nodeDeliveryTime =
                                                            NodeDeliveryTimeRepository.NodeDeliveryTime.builder()
                                                                    .nodeId(ref.getOrchestrationNodeId())
                                                                    .deliveryTime(Instant.now().plusMillis(
                                                                            Boolean.TRUE.equals(enableDelay)
                                                                                    ? RandomUtil.randomBetween(minDelay, maxDelay).toMillis()
                                                                                    : Duration.ZERO.toMillis()))
                                                                    .build();
                                                    log.info("Create node delivery time record for node id: {} with deliveryTime: {}",
                                                            nodeDeliveryTime.nodeId(), nodeDeliveryTime.deliveryTime());
                                                    return nodeDeliveryTime;
                                                })
                                                .toList();
                                        nodeDeliveryTimeRepository.saveAll(deliveryTimes);
                                    }
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .then();
    }

    public void executePostProcessDelivery(ShippingOrderStateChangeEvent event) {
        ShippingOrder shippingOrder = event.getEvent().getShippingOrder();

        Optional<DeliveryOrder> deliveryOrderOpt = deliveryOrderRepository.findByFactoryOrderId(shippingOrder.getId());
        if (deliveryOrderOpt.isEmpty()) {
            log.warn("No delivery order found for shipping order ID: {}", shippingOrder.getId());
            throw new CoodRecoverableAndNonRetryableException(new CoodTechnicalException("No delivery order found for factoryOrderId: " + shippingOrder.getId()));
        }
        DeliveryOrder deliveryOrder = deliveryOrderOpt.get();
        List<OrderItemRef> orderItemRefs = deliveryOrder.getOrderItemRef();
        log.info("Handle deliveryPostProcessing from eventClass: [{}] and node ids: [{}]",
                event.getClass().getSimpleName(),
                orderItemRefs.stream().map(OrderItemRef::getOrchestrationNodeId).toList());

        List<DeliveryOrderItemStatusPayloadEvent> statusEvents = shippingOrder.getShippingOrderItem().stream()
                .flatMap(shippingOrderItem -> orderItemRefs.stream()
                        .filter(deliveryOrderItem -> deliveryOrderItem.getProductOrderItemId()
                                .equals(shippingOrderItem.getProductOrderItem().getId()))
                        .map(ref -> Map.entry(ref, shippingOrderItem)))
                .map(entry -> {
                    OrderItemRef ref = entry.getKey();
                    ShippingOrderItem shippingOrderResponseItem = entry.getValue();

                    ref.setDeliveryStatusMapping(deliveryStatusMapper.mapShippingItemStatus(shippingOrderResponseItem.getStatus()));

                    return DeliveryOrderItemStatusPayloadEvent.builder()
                            .factoryOrderId(shippingOrder.getId())
                            .orderItemRef(ref)
                            .sourcePayload(event)
                            .build();
                })
                .toList();

        deliveryOrderRepository.save(deliveryOrder);
        eventPublisher.publishEvents(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT, statusEvents);
    }

}
