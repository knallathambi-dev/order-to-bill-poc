// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.orchestration.plan.delivery.service;

import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.notfounds.RelatedProductNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.internal.CDCEvent;
import com.orange.discobole.orderorchestration.outbox.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryOrderService {

    private final EventPublisher eventPublisher;

    public OrderItemRef buildOrderItemRef(OrchestrationPlanNode node) {
        Optional<RelatedProduct> shipmentProduct = node.getRelatedProduct().stream()
                .filter(rp -> RelatedProductType.SHIPMENT_PRODUCT.equals(rp.getType()))
                .findFirst();

        RelatedProduct relatedProduct = node.getRelatedProduct().stream().filter(relatedProduct1 -> relatedProduct1.getRelationshipType() == RelatedProductRelationType.DELIVERS).findFirst()
                .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(new RelatedProductNotFoundException(ExceptionCode.RELATED_PRODUCT_WITH_RELATION_TYPE_NOT_FOUND, RelatedProductRelationType.DELIVERS)));

        List<Characteristic> relatedProductOrderItemCharacteristics = relatedProduct.getProductCharacteristic() != null ?
                new HashSet<>(relatedProduct.getProductCharacteristic()).stream().toList() : new ArrayList<>();

        List<Characteristic> shipmentChar = shipmentProduct.isPresent() && shipmentProduct.get().getProductCharacteristic() != null ?
                new HashSet<>(shipmentProduct.get().getProductCharacteristic()).stream().toList() : new ArrayList<>();

        List<Characteristic> allCharacteristics = shipmentProduct.isPresent() ?
                shipmentChar : relatedProductOrderItemCharacteristics;

        String relatedProductId = relatedProduct.getProductOrderItemId();

        RelatedProductOrderItem relatedProductOrderItemData = node.getRelatedProductOrderItem().stream().filter(relatedProductOrderItem -> Objects.equals(relatedProductOrderItem.getId(), relatedProductId)).findFirst()
                .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(new RelatedProductNotFoundException(ExceptionCode.RELATED_PRODUCT_NOT_FOUND, relatedProductId)));

        ProductSpecification productSpecification = relatedProduct.getProductSpecification();
        List<ServiceSpecification> serviceSpecifications = productSpecification.getServiceSpecification();
        List<ServiceSpecificationRef> serviceSpecificationRefs = new ArrayList<>();

        if (serviceSpecifications != null) {
            serviceSpecificationRefs = serviceSpecifications.stream().map(serviceSpecification ->
                    ServiceSpecificationRef.builder()
                            .id(serviceSpecification.getId())
                            .name(serviceSpecification.getName())
                            .href(serviceSpecification.getHref())
                            .build()
            ).toList();
        }

        return OrderItemRef.builder()
                .productOrderItemId(relatedProduct.getId())
                .orchestrationNodeId(node.getId())
                .action(relatedProductOrderItemData.getAction())
                .quantity(relatedProductOrderItemData.getQuantity())
                .orderItemCharacteristics(allCharacteristics)
                .productSpecificationRef(ProductSpecificationRef.builder()
                        .id(productSpecification.getId())
                        .name(productSpecification.getName())
                        .serviceSpecificationRef(serviceSpecificationRefs)
                        .build())
                .build();
    }

    public DeliveryErrorMessage convertToDeliveryErrorMessage(OrchestrationPlanErrorMessage errorMessage) {
        return DeliveryErrorMessage.builder()
                .code(errorMessage.getCode())
                .message(errorMessage.getMessage())
                .reason(errorMessage.getReason())
                .build();
    }

    public void publishStartDeliveryEvent(OrchestrationPlan plan, List<OrderItemRef> orderItemRefs, DeliveryFactoryRef deliveryFactoryRef) {
        eventPublisher.publishEvent(
                CDCEvent.DELIVERY_ORDER_EVENT,
                DeliveryOrderPayloadEvent.builder()
                        .deliveryOrder(DeliveryOrder.builder()
                                .id(UUID.randomUUID().toString())
                                .requestedDeliveryDate(plan.getRequestedDeliveryDate())
                                .productOrderId(plan.getRelatedProductOrder().getId())
                                .orchestrationPlanId(plan.getId())
                                .orderItemRef(orderItemRefs)
                                .startDate(plan.getOrchestrationPlanSchedule().getOrderStartDate())
                                .relatedParty(plan.getRelatedParty())
                                .issues(plan.getErrorMessage() != null ? plan.getErrorMessage().stream().map(this::convertToDeliveryErrorMessage).toList() : new ArrayList<>())
                                .deliveryFactoryRef(DeliveryFactoryRef.builder()
                                        .deliveryFactoryType(deliveryFactoryRef.getDeliveryFactoryType())
                                        .href(deliveryFactoryRef.getHref())
                                        .build())
                                .build())
                        .build()
        );
    }
}
