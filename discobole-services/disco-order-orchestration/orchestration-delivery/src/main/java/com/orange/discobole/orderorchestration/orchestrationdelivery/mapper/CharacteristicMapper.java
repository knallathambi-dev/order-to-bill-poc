// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.mapper;

import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface CharacteristicMapper {

    @SubclassMapping(target = BooleanArrayCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanArrayCharacteristic.class)
    @SubclassMapping(target = FloatArrayCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.FloatArrayCharacteristic.class)
    @SubclassMapping(target = FloatCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.FloatCharacteristic.class)
    @SubclassMapping(target = IntegerArrayCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerArrayCharacteristic.class)
    @SubclassMapping(target = IntegerCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerCharacteristic.class)
    @SubclassMapping(target = ObjectArrayCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.ObjectArrayCharacteristic.class)
    @SubclassMapping(target = BooleanCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanCharacteristic.class)
    @SubclassMapping(target = ObjectCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.ObjectCharacteristic.class)
    @SubclassMapping(target = StringArrayCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringArrayCharacteristic.class)
    @SubclassMapping(target = StringCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic.class)
    @SubclassMapping(target = AddressCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.AddressCharacteristic.class)
    @SubclassMapping(target = DateCharacteristic.class, source = com.orange.discobole.ordermanagement.orderinventory.dto.v1.DateCharacteristic.class)
    Characteristic from(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic characteristic);

    @Mapping(target = "value.validTo", source = "value.validTo", qualifiedByName = "mapInstantToOffsetDateTime")
    @Mapping(target = "value.validFrom", source = "value.validFrom", qualifiedByName = "mapInstantToOffsetDateTime")
    ValidityCharacteristic from(com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic characteristic);

    @Mapping(target = "value", source = "value", qualifiedByName = "mapInstantToOffsetDateTime")
    DateCharacteristic from(com.orange.discobole.ordermanagement.orderinventory.dto.v1.DateCharacteristic characteristic);

    default Set<Characteristic> from(List<? extends com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic> characteristics) {
        if (characteristics == null || characteristics.isEmpty()) {
            return new HashSet<>();
        }

        return characteristics.stream()
                .map(this::getCharacteristic)
                .collect(Collectors.toSet());
    }

    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.BooleanArrayCharacteristic.class, source = BooleanArrayCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.NumberArrayCharacteristic.class, source = FloatArrayCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.NumberCharacteristic.class, source = FloatCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.IntegerArrayCharacteristic.class, source = IntegerArrayCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.IntegerCharacteristic.class, source = IntegerCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.ObjectArrayCharacteristic.class, source = ObjectArrayCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.BooleanCharacteristic.class, source = BooleanCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.ObjectCharacteristic.class, source = ObjectCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.StringArrayCharacteristic.class, source = StringArrayCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.StringCharacteristic.class, source = StringCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.ValidityCharacteristic.class, source = ValidityCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.AddressCharacteristic.class, source = AddressCharacteristic.class)
    @SubclassMapping(target = com.orange.discobole.productinventory.dto.v1.DateCharacteristic.class, source = DateCharacteristic.class)
    com.orange.discobole.productinventory.dto.v1.Characteristic from(Characteristic characteristic);

    private Characteristic getCharacteristic(com.orange.discobole.ordermanagement.orderinventory.dto.v1.Characteristic characteristic) {
        if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanCharacteristic booleanCharacteristic) {
            return from(booleanCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.BooleanArrayCharacteristic booleanArrayCharacteristic) {
            return from(booleanArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.FloatArrayCharacteristic floatArrayCharacteristic) {
            return from(floatArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.FloatCharacteristic floatCharacteristic) {
            return from(floatCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerArrayCharacteristic integerArrayCharacteristic) {
            return from(integerArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.IntegerCharacteristic integerCharacteristic) {
            return from(integerCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.ObjectArrayCharacteristic objectArrayCharacteristic) {
            return from(objectArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.ObjectCharacteristic objectCharacteristic) {
            return from(objectCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringArrayCharacteristic stringArrayCharacteristic) {
            return from(stringArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.StringCharacteristic stringCharacteristic) {
            return from(stringCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic validityCharacteristic) {
            return from(validityCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.AddressCharacteristic addressCharacteristic) {
            return from(addressCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.ordermanagement.orderinventory.dto.v1.DateCharacteristic dateCharacteristic) {
            return from(dateCharacteristic);
        } else {
            return from(characteristic);
        }
    }

    @Named("mapOffsetDateTimeToInstant")
    default Instant fromOffsetDateTime(OffsetDateTime offsetDateTime) {
        return Objects.nonNull(offsetDateTime) ? offsetDateTime.toInstant() : null;
    }

    @Named("mapInstantToOffsetDateTime")
    default OffsetDateTime fromInstant(Instant instant) {
        return Objects.nonNull(instant) ? instant.atOffset(java.time.ZoneOffset.UTC) : null;
    }
}
