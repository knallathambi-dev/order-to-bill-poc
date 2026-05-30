// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mock.util;


import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.OrderItemRef;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.*;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper.CharacteristicMapper;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.*;

public class SOMDtoBuilderUtil {

    public static final String HELD = "held";

    public static final String STRING_VALUE_TYPE = "string";

    private static final CharacteristicMapper CHARACTERISTIC_MAPPER = Mappers.getMapper(CharacteristicMapper.class);

    private SOMDtoBuilderUtil() {
    }

    public static ServiceOrder.ServiceOrderBuilder getServiceOrderBuilder() {
        return ServiceOrder.builder()
                .id("4126c7sd0z567z501372l7784")
                .orderDate(Instant.now())
                .state(ServiceOrder.State.COMPLETED)
                .relatedParty(List.of(getRelatedPartyBuilder().build()))
                .note(List.of(getNoteBuilder().build()))
                .type("ServiceOrder");
    }

    public static ServiceOrderRelatedParty.ServiceOrderRelatedPartyBuilder getRelatedPartyBuilder() {
        return ServiceOrderRelatedParty.builder()
                .id("231-mf4")
                .name("Abir")
                .role("customer")
                .referredType("customer");

    }

    public static Note.NoteBuilder getNoteBuilder() {
        return Note.builder()
                .id("1243")
                .text(STRING_VALUE_TYPE)
                .date("2023-06-13T09:11:59.120Z")
                .author(STRING_VALUE_TYPE);
    }

    public static ServiceOrderItem.ServiceOrderItemBuilder getServiceOrderItemBuilder(DeliveryOrder deliveryOrder) {
        ServiceOrderItem.ServiceOrderItemBuilder serviceOrderItemBuilder = ServiceOrderItem.builder()
                .id("1243")
                .action(getActionEnum(deliveryOrder.getOrderItemRef().get(0)))
                .state(ServiceOrderItem.State.COMPLETED);
        if (deliveryOrder.getFactoryOrderId().contains(HELD)) {
            serviceOrderItemBuilder
                    .state(ServiceOrderItem.State.HELD)
                    .errorMessage(List.of(getServiceOrderItemErrorMessage().build()));
        }
        return serviceOrderItemBuilder;
    }

    public static ServiceOrderItem.Action getActionEnum(OrderItemRef orderItemRef) {
        String action = orderItemRef.getAction();

        if (ServiceOrderItem.Action.MIGRATE.toString().equals(action)) {
            return ServiceOrderItem.Action.MODIFY;
        }

        return Optional.ofNullable(action)
                .map(ServiceOrderItem.Action::fromValue)
                .orElse(null);
    }

    public static ServiceOrderItemErrorMessage.ServiceOrderItemErrorMessageBuilder getServiceOrderItemErrorMessage() {
        return ServiceOrderItemErrorMessage.builder()
                .message("Unable to fulfill the OrderItem, resource temporarily unavailable")
                .code("RESOURCE_UNAVAILABLE")
                .reason("RESOURCE_INVENTORY_BUSY");
    }

    public static Service.ServiceBuilder getServiceBuilder(OrderItemRef orderItemRef) {
        Set<Characteristic> serviceCharacteristics = CHARACTERISTIC_MAPPER.from(new HashSet<>(orderItemRef.getOrderItemCharacteristics()));
        return Service.builder()
                .id("3153")
                .href("http://serverlocation:port/serviceInventory/3153")
                .description(orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().get(0).getName())
                .endDate(Instant.now())
                .name(orderItemRef.getProductSpecificationRef().getServiceSpecificationRef().get(0).getName())
                .serviceType("CFS")
                .startDate(Instant.now())
                .note(List.of(getNoteBuilder().build()))
                .serviceCharacteristic(new ArrayList<>(serviceCharacteristics))
                .serviceRelationship(List.of(getServiceRelationshipBuilder().build()))
                .serviceSpecification(getServiceSpecificationBuilder().build())
                .state(Service.ServiceStateType.ACTIVE); // COMPLETED missing in the enum
    }

    public static StringCharacteristic getServiceCharacteristicRingBuilder() {
        return StringCharacteristic.builder()
                .id("006")
                .name("Tone")
                .value("ToneVIP")
                .valueType(STRING_VALUE_TYPE)
                .build();
    }

    public static StringCharacteristic getServiceCharacteristicSmsOptionBuilder() {
        return StringCharacteristic.builder()
                .id("003")
                .name("SMS_Number")
                .value("500-cood")
                .valueType(STRING_VALUE_TYPE)
                .build();
    }

    public static StringCharacteristic getServiceCharacteristicTimeBundleBuilder() {
        return StringCharacteristic.builder()
                .id("003")
                .name("volume")
                .value("10h-cood")
                .valueType(STRING_VALUE_TYPE)
                .build();
    }

    public static StringCharacteristic getServiceCharacteristicMobileLineBuilder() {
        return StringCharacteristic.builder()
                .id("001")
                .name("MSISDN")
                .value("4152797439-cood")
                .valueType(STRING_VALUE_TYPE)
                .build();
    }

    public static StringCharacteristic getServiceCharacteristicConnectivityBuilder() {
        return StringCharacteristic.builder()
                .id("002")
                .name("ICCID")
                .value("891004234814455936-cood")
                .valueType(STRING_VALUE_TYPE)
                .build();
    }

    public static ServiceRelationship.ServiceRelationshipBuilder getServiceRelationshipBuilder() {
        return ServiceRelationship.builder()
                .id("4321")
                .serviceRelationshipType("requires");
    }

    public static ServiceSpecification.ServiceSpecificationBuilder getServiceSpecificationBuilder() {
        return ServiceSpecification.builder()
                .id("5e18402d-c964-4d52-b362-22ef79c27c01")
                .name("Connectivity")
                .type("ServiceSpecificationRef");
    }
}