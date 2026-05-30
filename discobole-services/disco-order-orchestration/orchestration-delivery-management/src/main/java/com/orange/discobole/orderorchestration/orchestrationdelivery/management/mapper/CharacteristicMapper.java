// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper;


import com.orange.discobole.orderorchestration.exception.model.CoodTechnicalException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.servicecharacteristic.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CharacteristicMapper {
    @Mapping(target = "atType", source = "atType")
    Characteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    BooleanCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    BooleanArrayCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    FloatArrayCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    FloatCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    IntegerArrayCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    IntegerCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    ObjectArrayCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    ObjectCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    StringArrayCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    StringCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    AddressCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.AddressCharacteristic characteristic);

    @Mapping(target = "value.validTo", source = "value.validTo", qualifiedByName = "mapOffsetDateTimeToInstant")
    @Mapping(target = "value.validFrom", source = "value.validFrom", qualifiedByName = "mapOffsetDateTimeToInstant")
    @Mapping(target = "atType", source = "atType")
    ValidityCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    DateCharacteristic from(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic from(Characteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic from(BooleanCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic from(BooleanArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatArrayCharacteristic from(FloatArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatCharacteristic from(FloatCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerArrayCharacteristic from(IntegerArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerCharacteristic from(IntegerCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectArrayCharacteristic from(ObjectArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectCharacteristic from(ObjectCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringArrayCharacteristic from(StringArrayCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic from(StringCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.AddressCharacteristic from(AddressCharacteristic characteristic);

    @Mapping(target = "value.validTo", source = "value.validTo", qualifiedByName = "mapInstantToOffsetDateTime")
    @Mapping(target = "value.validFrom", source = "value.validFrom", qualifiedByName = "mapInstantToOffsetDateTime")
    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityCharacteristic from(ValidityCharacteristic characteristic);

    @Mapping(target = "atType", source = "atType")
    com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic from(DateCharacteristic characteristic);

    default Set<Characteristic> from(Set<? extends com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic> characteristics) {
        if (characteristics == null || characteristics.isEmpty()) {
            return new HashSet<>();
        }

        return characteristics.stream()
                .map(this::getCharacteristic)
                .collect(Collectors.toSet());
    }


    default com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic from(List<? extends Characteristic> characteristics) {
        if (characteristics == null || characteristics.isEmpty()) {
            return null;
        }

        return characteristics.stream()
                .map(this::getCharacteristic)
                .filter(Objects::nonNull) // filter out nulls
                .findFirst() // get the first non-null result
                .orElse(null);
    }

    private Characteristic getCharacteristic(com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic characteristic) {
        if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanCharacteristic booleanCharacteristic) {
            return from(booleanCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.BooleanArrayCharacteristic booleanArrayCharacteristic) {
            return from(booleanArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatArrayCharacteristic floatArrayCharacteristic) {
            return from(floatArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.FloatCharacteristic floatCharacteristic) {
            return from(floatCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerArrayCharacteristic integerArrayCharacteristic) {
            return from(integerArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.IntegerCharacteristic integerCharacteristic) {
            return from(integerCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectArrayCharacteristic objectArrayCharacteristic) {
            return from(objectArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ObjectCharacteristic objectCharacteristic) {
            return from(objectCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringArrayCharacteristic stringArrayCharacteristic) {
            return from(stringArrayCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic stringCharacteristic) {
            return from(stringCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.ValidityCharacteristic validityCharacteristic) {
            return from(validityCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.AddressCharacteristic addressCharacteristic) {
            return from(addressCharacteristic);
        } else if (characteristic instanceof com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.DateCharacteristic dateCharacteristic) {
            return from(dateCharacteristic);
        } else {
            throw new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "This characteristic type is not supported");
        }
    }

    private com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic getCharacteristic(Characteristic characteristic) {
        if (characteristic instanceof StringCharacteristic stringCharacteristic) {
            return from(stringCharacteristic);
        } else if (characteristic instanceof BooleanCharacteristic booleanCharacteristic) {
            return from(booleanCharacteristic);
        } else if (characteristic instanceof BooleanArrayCharacteristic booleanArrayCharacteristic) {
            return from(booleanArrayCharacteristic);
        } else if (characteristic instanceof FloatArrayCharacteristic floatArrayCharacteristic) {
            return from(floatArrayCharacteristic);
        } else if (characteristic instanceof FloatCharacteristic floatCharacteristic) {
            return from(floatCharacteristic);
        } else if (characteristic instanceof IntegerArrayCharacteristic integerArrayCharacteristic) {
            return from(integerArrayCharacteristic);
        } else if (characteristic instanceof IntegerCharacteristic integerCharacteristic) {
            return from(integerCharacteristic);
        } else if (characteristic instanceof StringArrayCharacteristic stringArrayCharacteristic) {
            return from(stringArrayCharacteristic);
        } else if (characteristic instanceof ValidityCharacteristic validityCharacteristic) {
            return from(validityCharacteristic);
        } else if (characteristic instanceof ObjectCharacteristic objectCharacteristic) {
            return from(objectCharacteristic);
        } else if (characteristic instanceof ObjectArrayCharacteristic objectArrayCharacteristic) {
            return from(objectArrayCharacteristic);
        } else if (characteristic instanceof AddressCharacteristic addressCharacteristic) {
            return from(addressCharacteristic);
        } else if (characteristic instanceof DateCharacteristic dateCharacteristic) {
            return from(dateCharacteristic);
        } else {
            throw new CoodTechnicalException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "This characteristic type is not supported");
        }
    }


    @Named("mapOffsetDateTimeToInstant")
    default Instant fromOffsetDateTime(OffsetDateTime offsetDateTime) {
        return Objects.nonNull(offsetDateTime) ? offsetDateTime.toInstant() : null;
    }

    @Named("mapInstantToOffsetDateTime")
    default OffsetDateTime fromInstant(Instant instant) {
        return Objects.nonNull(instant) ? instant.atOffset(ZoneOffset.UTC) : null;
    }
}
