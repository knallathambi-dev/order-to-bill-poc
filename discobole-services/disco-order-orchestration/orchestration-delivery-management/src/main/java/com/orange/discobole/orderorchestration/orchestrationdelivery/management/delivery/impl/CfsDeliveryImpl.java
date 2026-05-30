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
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.constant.KafkaTopic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItemErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceCatalogManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ServiceOrderManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator.ServiceOrderCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.exception.ServiceSpecificationValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.DeliveryStatusMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.ErrorMessageMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.RelatedPartyMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.DeliveryOrderRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.DiscoServiceUrl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.RandomUtil;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.consts.Headers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class CfsDeliveryImpl {

    private static final @NonNull Boolean ENABLE_SOM_CHARACTERISTICS = true;

    private final ServiceCatalogManagementService serviceCatalogManagementService;

    private final ServiceOrderManagementService serviceOrderManagementService;

    private final ErrorMessageMapper errorMessageMapper;

    private final ServiceOrderCreator serviceOrderCreator;

    private final DiscoServiceUrl discoServiceUrl;

    private final EventPublisher eventPublisher;

    private final DeliveryOrderRepository deliveryOrderRepository;

    private final CharacteristicMapper characteristicMapper;

    private final NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    private final RelatedPartyMapper relatedPartyMapper;

    private final DeliveryStatusMapper deliveryStatusMapper;

    @Value("${mocks.serviceOrderStateChangeScheduler.minDelay:PT0S}")
    private Duration minDelay;

    @Value("${mocks.serviceOrderStateChangeScheduler.maxDelay:PT0S}")
    private Duration maxDelay;

    @Value("${mocks.serviceOrderStateChangeScheduler.enableDelay:false}")
    private Boolean enableDelay;

    @Value("${config.serviceCatalogValidation.enabled:true}")
    private boolean isServiceCatalogValidationEnabled;

    @Value("${config.delivery.timeout:PT10S}")
    private Duration deliveryTimeout;

    @Transactional
    public void deliver(DeliveryOrderEvent deliveryOrderEvent) {
        DeliveryOrder deliveryOrder = deliveryOrderEvent.getEvent().getDeliveryOrder();

        deliveryOrder.setDeliveryOrderCreationDate(Instant.now());
        deliveryOrderRepository.save(deliveryOrder);

        executeDeliveryMono(deliveryOrder)
                .doOnError(error -> {
                    log.error("[DeliveryOrderId={}] Execute delivery failed, sending to DLT {}",
                            deliveryOrder.getId(), error.getMessage());

                    CoodError coodError = errorMessageMapper.map(error);

                    Map<String, Object> headers = new HashMap<>();
                    headers.put(KafkaHeaders.RECEIVED_TOPIC, KafkaTopic.DELIVERY_ORDER_TOPIC);
                    headers.put(Headers.CONSUMER_ERROR, coodError);

                    eventPublisher.publishEvent(
                            CDCEvent.DELIVERY_ORDER_DLT_EVENT,
                            deliveryOrderEvent.getEvent(),
                            headers
                    );
                })
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }

    public Mono<Void> executeDeliveryMono(DeliveryOrder deliveryOrder) {
        // Currently, for cfs we only support one service per delivery order
        List<OrderItemRef> orderItemRefs = deliveryOrder.getOrderItemRef();
        Instant requestedDeliveryDate = deliveryOrder.getRequestedDeliveryDate();

        if (orderItemRefs == null || orderItemRefs.isEmpty()) {
            log.error("No order item ref found in delivery order with id: {}", deliveryOrder.getId());
            throw new CoodRecoverableAndNonRetryableException(new CoodTechnicalException("No order item ref found in delivery order with id: " + deliveryOrder.getId()));
        }

        OrderItemRef orderItemRef = orderItemRefs.get(0);

        log.info("Start delivery for order item ref with product order item id: [{}] and node id: [{}]",
                orderItemRef.getProductOrderItemId(), orderItemRef.getOrchestrationNodeId());

        return Mono.defer(() ->
                        Mono.fromCallable(() -> {
                                    // since we only process one service order item, we expect to have exactly one service specification
                                    List<ServiceSpecification> serviceSpecifications =
                                            Optional.ofNullable(getServiceSpecificationFrom(orderItemRef)).orElse(List.of());

                                    ServiceSpecification serviceSpecification = serviceSpecifications.stream()
                                            .findFirst()
                                            .orElseThrow(() -> new CoodRecoverableAndNonRetryableException(
                                                    new CoodTechnicalException("No service specification found in order item ref with id: " + orderItemRef.getProductOrderItemId()))
                                            );

                                    log.info("Validating service specification for order item ref with product order item id: {}, ServiceSpecification: {}, isServiceCatalogValidationEnabled: {}",
                                            orderItemRef.getProductOrderItemId(), serviceSpecification, isServiceCatalogValidationEnabled);

                                    if (isServiceCatalogValidationEnabled) {
                                        log.info("Service catalog validation is enabled");
                                        // service catalog characteristic names validation
                                        // we only verify the names case in-sensitive everything else is ignored
                                        Set<String> serviceCharacteristicNames = Objects.nonNull(serviceSpecification.getSpecCharacteristic())
                                                ? serviceSpecification.getSpecCharacteristic().stream().map(spec -> spec.getName().toLowerCase())
                                                .collect(Collectors.toSet())
                                                : Set.of();

                                        List<String> productCharacteristicNames = orderItemRef.getOrderItemCharacteristics().stream()
                                                .map(pc -> pc.getName().toLowerCase())
                                                .toList();

                                        if (!serviceCharacteristicNames.containsAll(productCharacteristicNames)) {
                                            throw CoodRecoverableAndNonRetryableException.of(
                                                    new ServiceSpecificationValidationException(
                                                            ExceptionCode.SERVICE_SPECIFICATION_CHARACTERISTICS_NOT_FOUND,
                                                            productCharacteristicNames
                                                    )
                                            );
                                        }
                                    }

                                    ServiceOrder serviceOrderRequest =
                                            serviceOrderCreator.prepareServiceOrderRequest(
                                                    orderItemRef,
                                                    requestedDeliveryDate,
                                                    orderItemRef.getOrderItemCharacteristics(),
                                                    serviceSpecification
                                            );

                                    serviceOrderRequest.setRelatedParty(relatedPartyMapper.mapToServiceOrderRelatedParty(deliveryOrder.getRelatedParty()));

                                    return serviceOrderRequest;
                                })
                                .timeout(deliveryTimeout)
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .flatMap(serviceOrderRequest ->
                        serviceOrderManagementService.createServiceOrderInSOM(
                                serviceOrderRequest,
                                getServiceOrderingUrl(deliveryOrder)
                        )
                )
                .flatMap(serviceOrderResponse ->
                        Mono.fromRunnable(() -> {
                            log.info("[DeliveryOrderId={}] ServiceOrder created successfully — ServiceOrderId={}, ItemId={}",
                                    deliveryOrder.getId(),
                                    serviceOrderResponse.getId(),
                                    serviceOrderResponse.getServiceOrderItem().get(0).getId());

                            deliveryOrder.setFactoryOrderId(serviceOrderResponse.getId());
                            orderItemRef.setFactoryOrderItemId(serviceOrderResponse.getServiceOrderItem().get(0).getId());

                            deliveryOrderRepository.save(deliveryOrder);

                            log.info("Updated deliver order item with [NodeId={}] by service order details ",
                                    orderItemRef.getOrchestrationNodeId());

                            // this section is used in the scheduler mocking the service factory response
                            // so we can know when each delivery order should be responded to form the service factory
                            {
                                NodeDeliveryTimeRepository.NodeDeliveryTime deliveryTime = NodeDeliveryTimeRepository.NodeDeliveryTime.builder()
                                        .nodeId(orderItemRef.getOrchestrationNodeId())
                                        .deliveryTime(Instant.now()
                                                .plusMillis(Boolean.TRUE.equals(enableDelay) ? RandomUtil.randomBetween(minDelay, maxDelay).toMillis() : Duration.ZERO.toMillis()))
                                        .build();
                                nodeDeliveryTimeRepository.save(deliveryTime);

                                log.info("Create node delivery time record for node id: {} with Delivery time: {}",
                                        deliveryTime.nodeId(),
                                        deliveryTime.deliveryTime());
                            }
                        })
                )
                .then();
    }

    public String getServiceOrderingUrl(DeliveryOrder deliveryOrder) {
        return Optional.ofNullable(deliveryOrder.getDeliveryFactoryRef())
                .map(DeliveryFactoryRef::getHref)
                .orElseGet(discoServiceUrl::getServiceOrderingUrl);
    }

    private List<ServiceSpecification> getServiceSpecificationFrom(OrderItemRef orderItemRef) {
        List<String> serviceSpecificationsIds = orderItemRef.getProductSpecificationRef().getServiceSpecificationRef()
                .stream()
                .map(ServiceSpecificationRef::getId)
                .toList();
        log.info("Get service specification for order item ref with product order item id: {}, serviceSpecificationIds: {}", orderItemRef.getProductOrderItemId(), serviceSpecificationsIds);
        return serviceCatalogManagementService.getServiceSpecificationByIds(serviceSpecificationsIds, getServiceCatalogManagementUrl(orderItemRef));
    }

    public String getServiceCatalogManagementUrl(OrderItemRef orderItemRef) {
        boolean serviceCatalogManagementURL = orderItemRef.getProductOrderItemId() != null
                && Objects.nonNull(orderItemRef.getProductSpecificationRef())
                && !orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().isEmpty()
                && Objects.nonNull(orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().get(0).getHref());

        return serviceCatalogManagementURL ?
                orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().get(0).getHref() :
                discoServiceUrl.getServiceCatalogManagementUrl();
    }

    public void executePostProcessDelivery(ServiceOrderEvent event) {
        ServiceOrder serviceOrder = event.getEvent().getServiceOrder();

        Optional<DeliveryOrder> deliveryOrderOpt = deliveryOrderRepository.findByFactoryOrderId(serviceOrder.getId());

        if (deliveryOrderOpt.isEmpty()) {
            log.warn("No delivery order found for service order ID: {}", serviceOrder.getId());
            throw new CoodRecoverableAndNonRetryableException(new CoodTechnicalException("No delivery order found for factoryOrderId: " + serviceOrder.getId()));
        }

        DeliveryOrder deliveryOrder = deliveryOrderOpt.get();
        List<OrderItemRef> orderItemRefs = deliveryOrder.getOrderItemRef();

        if (orderItemRefs == null || orderItemRefs.isEmpty()) {
            log.error("No order item ref found in delivery order with id: {}", deliveryOrder.getId());
            throw new CoodRecoverableAndNonRetryableException(new CoodTechnicalException("No order item ref found in delivery order with id: " + deliveryOrder.getId()));
        }

        OrderItemRef orderItemRef = orderItemRefs.get(0);

        log.info("Handle deliveryPostProcessing for eventClass: {} and node id: {}", event.getClass().getSimpleName(), orderItemRef.getOrchestrationNodeId());

        List<ServiceOrderItem> serviceOrderItems = serviceOrder.getServiceOrderItem();

        if (serviceOrderItems == null || serviceOrderItems.isEmpty()) {
            log.error("No service order item found in service order with id: {}", serviceOrder.getId());
            throw new CoodRecoverableAndNonRetryableException(new CoodTechnicalException("No service order item found in service order with id: " + serviceOrder.getId()));
        }

        ServiceOrderItem serviceOrderItem = serviceOrderItems.get(0);

        orderItemRef.setDeliveryStatusMapping(deliveryStatusMapper.mapServiceOrderItemStateType(serviceOrderItem.getState()));

        List<Characteristic> serviceCharacteristic = serviceOrderItem.getService().getServiceCharacteristic();
        OrderItemRef updatedOrderItemRef = updateOrderItemRefProductCharacteristics(serviceCharacteristic, orderItemRef);

        updatedOrderItemRef.setRealizingResourceRef(RealizingResourceRef.builder()
                .id(serviceOrderItem.getService().getId())
                .href(serviceOrderItem.getService().getHref())
                .build());

        CoodError coodError = getErrorMessageIfExists(serviceOrderItem.getErrorMessage());

        Map<String, Object> header = new HashMap<>();
        header.put(Headers.SOURCE_TOPIC_NAME, KafkaTopic.SERVICE_ORDER_STATE_CHANGE_TOPIC);

        if (Objects.nonNull(coodError)) {
            header.put(Headers.CONSUMER_ERROR, coodError);
        }

        DeliveryOrderItemStatusPayloadEvent deliveryOrderItemStatusPayloadEvent = DeliveryOrderItemStatusPayloadEvent
                .builder()
                .factoryOrderId(deliveryOrder.getFactoryOrderId())
                .orderItemRef(orderItemRef)
                .sourcePayload(event)
                .build();

        deliveryOrderRepository.save(deliveryOrder);
        eventPublisher.publishEvent(CDCEvent.DELIVERY_ORDER_ITEM_STATUS_EVENT, deliveryOrderItemStatusPayloadEvent, header);
    }

    private OrderItemRef updateOrderItemRefProductCharacteristics(List<Characteristic> serviceCharacteristics, OrderItemRef orderItemRef) {

        if (!ENABLE_SOM_CHARACTERISTICS) {
            return orderItemRef;
        }
        log.info("Checking product characteristics in service order item for node id: {}", orderItemRef.getOrchestrationNodeId());
        boolean serviceCharacteristicNotNull = Objects.nonNull(serviceCharacteristics)
                && !serviceCharacteristics.isEmpty();

        if (!serviceCharacteristicNotNull) {
            return orderItemRef;
        }

        if (!CollectionUtils.isEmpty(orderItemRef.getOrderItemCharacteristics())) {
            List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> characteristics = orderItemRef.getOrderItemCharacteristics();
            serviceCharacteristics.forEach(serviceCharacteristic ->
                    characteristics.stream().filter(characteristic -> characteristic.getName().equalsIgnoreCase(serviceCharacteristic.getName()))
                            .findFirst()
                            .ifPresent(characteristic -> {
                                characteristics.remove(characteristic);
                                characteristics.add(characteristicMapper.from(List.of(serviceCharacteristic)));
                            })
            );
        }

        return orderItemRef;
    }

    private CoodError getErrorMessageIfExists(List<ServiceOrderItemErrorMessage> errorMessage) {
        return !CollectionUtils.isEmpty(errorMessage) ? errorMessageMapper.map(errorMessage.get(0)) : null;
    }
}
