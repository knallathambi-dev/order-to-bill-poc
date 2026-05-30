// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.creator;


import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.catalog.service.ServiceSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.OrderItemActionType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProductOrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;


@Slf4j
@Component
public class ServiceOrderCreator {

    private final CharacteristicMapper characteristicMapper;

    private final Map<ItemActionType, ServiceOrderItem.Action> productOrderItemActionTypeEnumMapping =
            new EnumMap<>(ItemActionType.class);

    public ServiceOrderCreator(CharacteristicMapper characteristicMapper) {
        this.characteristicMapper = characteristicMapper;
        productOrderItemActionTypeEnumMapping.put(ItemActionType.ADD, ServiceOrderItem.Action.ADD);
        productOrderItemActionTypeEnumMapping.put(ItemActionType.MODIFY, ServiceOrderItem.Action.MODIFY);
        productOrderItemActionTypeEnumMapping.put(ItemActionType.MIGRATE, ServiceOrderItem.Action.MODIFY);
        productOrderItemActionTypeEnumMapping.put(ItemActionType.DELETE, ServiceOrderItem.Action.DELETE);
        productOrderItemActionTypeEnumMapping.put(ItemActionType.NOCHANGE, ServiceOrderItem.Action.NO_CHANGE);
    }

    /**
     * Prepare service order request data
     *
     * @param orderItemRef
     * @param requestedDeliveryDate
     * @param productCharacteristics
     * @param serviceSpecification
     * @return
     */
    public ServiceOrder prepareServiceOrderRequest(OrderItemRef orderItemRef, Instant requestedDeliveryDate, List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> productCharacteristics, ServiceSpecification serviceSpecification) {
        log.info("Preparing service order characteristics for node id: {}", orderItemRef.getOrchestrationNodeId());
        List<Characteristic> serviceCharacteristicList = prepareServiceCharacteristics(productCharacteristics, serviceSpecification);
        log.debug("Preparing service order characteristics for node id: {}, serviceCharacteristicList: {}", orderItemRef.getOrchestrationNodeId(), serviceCharacteristicList);

        log.info("Preparing service Order item for node id: {}", orderItemRef);
        ServiceOrderItem serviceOrderItem = prepareServiceOrderItem(orderItemRef, serviceCharacteristicList);
        log.debug("Preparing service Order item for node id: {}, serviceOrderItem: {}", orderItemRef.getOrchestrationNodeId(), serviceOrderItem);

        log.info("Preparing service Order for node id: {}", orderItemRef.getOrchestrationNodeId());
        ServiceOrder serviceOrder = prepareServiceOrder(requestedDeliveryDate, serviceOrderItem);
        log.debug("Preparing service Order for node id: {}, serviceOrder: {}", orderItemRef.getOrchestrationNodeId(), serviceOrder);
        return serviceOrder;
    }

    private List<Characteristic> prepareServiceCharacteristics(List<com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> productCharacteristics, ServiceSpecification serviceSpecification) {
        List<Characteristic> serviceCharacteristicList = new ArrayList<>();

        productCharacteristics.forEach(productCharacteristic ->
            serviceSpecification.getSpecCharacteristic().stream()
                    .filter(serviceCatalogSpecCharacteristic -> productCharacteristic.getName().equalsIgnoreCase(serviceCatalogSpecCharacteristic.getName()))
                    .findFirst()
                    .ifPresent(serviceCatalogSpecCharacteristic -> {
                        serviceCharacteristicList.add(characteristicMapper.from(Set.of(productCharacteristic)).stream().toList().get(0));
                        log.info("Service Characteristic is prepared");
                    })
        );

        return serviceCharacteristicList;
    }

    private ServiceOrderItem prepareServiceOrderItem(OrderItemRef orderItemRef, List<Characteristic> serviceCharacteristicList) {
        return ServiceOrderItem.builder()
                .action(getEnumMapping(ItemActionType.fromValue(orderItemRef.getAction())))
                .service(prepareService(orderItemRef, serviceCharacteristicList))
                .serviceRelationship(List.of())
                .build();
    }

    private Service prepareService(OrderItemRef orderItemRef, List<Characteristic> serviceCharacteristicList) {
        return Service.builder()
                .serviceSpecification(com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceSpecification.builder()
                        .id(orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().get(0).getId())
                        .build())
                .serviceType("CFS")
                .serviceCharacteristic(serviceCharacteristicList)
                .build();
    }

    private ServiceOrder prepareServiceOrder(Instant requestedDeliveryDate, ServiceOrderItem serviceOrderItem) {
        return ServiceOrder.builder()
                .requestedCompletionDate(requestedDeliveryDate)
                .serviceOrderItem(List.of(serviceOrderItem))
                .build();
    }

    public List<ServiceRelationship> createServiceOrderItemRelationship(OrchestrationPlanNode orchestrationPlanNode) {
        List<ServiceRelationship> serviceRelationships = new ArrayList<>();
        orchestrationPlanNode.getRelatedProduct().forEach(relatedProduct -> {
            if (shouldCreateServiceOrderItemRelationship(relatedProduct, orchestrationPlanNode.getActualRelatedOrderItem())) {
                ServiceRelationship serviceRelationship = ServiceRelationship.builder()
                        .serviceRelationshipType(relatedProduct.getRelationshipType().getValue()).build();
                if (Objects.nonNull(relatedProduct.getRealisingService()) && !relatedProduct.getRealisingService().isEmpty()) {
                    ServiceRef service = ServiceRef.builder()
                            //todo PO i think we should send the list of getRealisingService as new ServiceRelationship
                            .href(relatedProduct.getRealisingService().get(0).getHref())
                            .id(relatedProduct.getRealisingService().get(0).getId()).build();
                    serviceRelationship.setService(service);
                }
                serviceRelationships.add(serviceRelationship);
            }
        });
        return serviceRelationships;
    }

    private boolean shouldCreateServiceOrderItemRelationship(RelatedProduct productOrder, RelatedProductOrderItem productOrderItem) {
        return Objects.nonNull(productOrder.getRelationshipType())
                //todo you should add requires condition besides relieson
                && productOrder.getRelationshipType().equals(RelatedProductRelationType.RELIES_ON)
                && Objects.nonNull(productOrderItem.getAction())
                //todo po will check if this will be removed
                && productOrderItem.getAction().equals(OrderItemActionType.ADD.value());
    }

    public ServiceOrderItem.Action getEnumMapping(ItemActionType type) {
        return productOrderItemActionTypeEnumMapping.get(type);
    }
}
