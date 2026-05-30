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


import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ProductOrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShipmentRefOrValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderCreate;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderItem;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.exception.model.notfound.ProductCharacteristicsNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.RelatedPartyMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedParty;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class ShippingOrderCreator {

    private static final String REQUESTED_DELIVERY_DATE = "requested delivery date";

    private final RelatedPartyMapper relatedPartyMapper;

    private final CharacteristicMapper characteristicMapper;

    public ShippingOrderCreate prepareShippingOrderRequest(List<OrderItemRef> orderItemRefs, List<RelatedParty> relatedParties, String productOrderId) {
        log.info("Preparing shipping order request");

        List<ShippingOrderItem> shippingOrderItems = orderItemRefs.stream().map(orderItemRef -> {
            var requestedDeliveryDateCharacteristic = orderItemRef.getOrderItemCharacteristics()
                    .stream().filter(characteristic -> REQUESTED_DELIVERY_DATE.equalsIgnoreCase(characteristic.getName()))
                    .findFirst()
                    .orElseThrow(() -> CoodRecoverableAndNonRetryableException.of(
                            new ProductCharacteristicsNotFoundException(ExceptionCode.PRODUCT_CHARACTERISTIC_NOT_FOUND, REQUESTED_DELIVERY_DATE, orderItemRef.getProductOrderItemId())));

            return ShippingOrderItem.builder()
                    .action(ShippingOrderItem.ShippingOrderItemActionType.fromValue(orderItemRef.getAction()))
                    .productOrderItem(ProductOrderItemRef.builder()
                            .id(orderItemRef.getProductOrderItemId())
                            .productOrderId(productOrderId)
                            .build())
                    .quantity(orderItemRef.getQuantity().toString())
                    .shipment(ShipmentRefOrValue.builder()
                            .requestedDeliveryDate(((DateCharacteristic) requestedDeliveryDateCharacteristic).getValue().toInstant())
                            .build())
                    .build();
        }).toList();

        List<Characteristic> shippingOrderCharacteristics = characteristicMapper
                .from(new HashSet<>(orderItemRefs.get(0).getOrderItemCharacteristics()))
                .stream()
                .toList();

        return ShippingOrderCreate.builder()
                .shippingOrderItem(shippingOrderItems)
                .relatedParty(relatedPartyMapper.map(relatedParties))
                .shippingOrderCharacteristic(shippingOrderCharacteristics)
                .build();
    }
}
