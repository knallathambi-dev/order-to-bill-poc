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
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator.ServiceOrderCreator;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.repository.NodeDeliveryTimeRepository;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository.ReactiveDeliveryOrderRepository;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import com.orange.discobole.orderorchestration.outbox.internal.EventEntity;
import com.orange.discobole.orderorchestration.outbox.internal.EventRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.util.SOMDtoBuilderUtil.*;


@Component
@Slf4j
@RequiredArgsConstructor
@SuppressFBWarnings("PREDICTABLE_RANDOM")
public class ServiceOrderStateChangeServiceMockScheduler {

    private static final int CHUNK_SIZE = 100;

    @Setter
    @Getter
    @Value("${mocks.serviceOrderStateChangeScheduler.enable:true}")
    private boolean isEnabled;

    private final ReactiveDeliveryOrderRepository reactiveDeliveryOrderRepository;

    private final EventPublisher eventPublisher;

    private final NodeDeliveryTimeRepository nodeDeliveryTimeRepository;

    private List<DeliveryOrder> filterDeliveryOrdersThatShouldBeMocked(List<DeliveryOrder> deliveryOrders) {
        List<String> nodeIds = deliveryOrders.stream().map(deliveryOrder -> deliveryOrder.getOrderItemRef().get(0).getOrchestrationNodeId()).toList();

        Map<String, Instant> instantMap = nodeDeliveryTimeRepository.findByNodeIdIn(nodeIds).stream()
                .collect(Collectors.toMap(NodeDeliveryTimeRepository.NodeDeliveryTime::nodeId, NodeDeliveryTimeRepository.NodeDeliveryTime::deliveryTime));

        return deliveryOrders.stream().filter(deliveryOrder ->
                deliveryOrder.getDeliveryFactoryRef().getDeliveryFactoryType() == DeliveryFactoryRef.DeliveryFactoryEnum.SERVICE_ORDER_MANAGEMENT &&
                        deliveryOrder.getOrderItemRef().get(0).getProductOrderItemId() != null &&
                        (Objects.isNull(instantMap.get(deliveryOrder.getId())) || instantMap.get(deliveryOrder.getId()).isBefore(Instant.now()))).toList();
    }

    // use fixed delay to make sure that a job only starts after the previous one finishes
    // to avoid multiple jobs trying to deliver the same node.
    @Scheduled(fixedDelayString = "${mocks.serviceOrderStateChangeScheduler.jobDelayDuration}")
    public void mockServiceOrderStateChange() {
        if (isEnabled) {
            Flux<DeliveryOrder> deliveryOrdersFlux
                    = reactiveDeliveryOrderRepository.findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull();
            deliveryOrdersFlux
                    .limitRate(CHUNK_SIZE)
                    .buffer(CHUNK_SIZE)
                    .doOnSubscribe(subscription -> log.debug("Starting ServiceOrderStateChangeServiceMockScheduler job"))
                    .doOnComplete(() -> log.debug("ServiceOrderStateChangeServiceMockScheduler job finished"))
                    .doOnError(throwable -> {
                        throw new CoodNonRecoverableAndNonRetryableException(new CoodTechnicalException(ExceptionCode.PLANS_CHUNK_STATE_CHANGE_EXCEPTION));
                    })
                    .subscribe(deliveryOrders -> {
                        List<DeliveryOrder> deliveryOrdersList = filterDeliveryOrdersThatShouldBeMocked(deliveryOrders);

                        List<ServiceOrder> serviceOrders = deliveryOrdersList.stream().map(this::getMockedServiceOrder).toList();
                        if (!CollectionUtils.isEmpty(serviceOrders)) {
                            eventPublisher.publishEvents(CDCEvent.SERVICE_ORDER_STATE_CHANGE_EVENT, serviceOrders);
                        }
                    });
        } else {
            log.debug("ServiceOrderStateChangeServiceMockScheduler job");
        }
    }

    public ServiceOrder getMockedServiceOrder(DeliveryOrder deliveryOrder) {
        String serviceOrderID = deliveryOrder.getFactoryOrderId();
        OrderItemRef orderItemRef = deliveryOrder.getOrderItemRef().get(0);

        String serviceOrderItemId = orderItemRef.getFactoryOrderItemId();
        String serviceSpecificationId = orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().get(0).getId();
        return getServiceOrderBuilder()
                .id(serviceOrderID)
                .state(ServiceOrder.State.COMPLETED)
                .serviceOrderItem(List.of(getServiceOrderItemBuilder(deliveryOrder)
                        .id(serviceOrderItemId)
                        .serviceRelationship(List.of())
                        .service(getServiceBuilder(orderItemRef)
                                .id(UUID.randomUUID().toString())
                                .serviceSpecification(getServiceSpecificationBuilder().id(serviceSpecificationId).build()).build())
                        .build()
                )).build();

    }
}
