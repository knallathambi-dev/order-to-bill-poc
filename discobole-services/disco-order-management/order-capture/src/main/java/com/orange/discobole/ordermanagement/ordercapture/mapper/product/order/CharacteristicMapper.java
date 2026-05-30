// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.mapper.product.order;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper interface for converting various types of `Characteristic` objects
 * from order management to the corresponding DTOs in product inventory.
 */
@Mapper(componentModel = "spring")
public interface CharacteristicMapper {

    /**
     * Generic mapping for various characteristic types to their corresponding DTOs.
     *
     * @param characteristic the characteristic to map
     * @return the mapped DTO
     */
    default com.orange.discobole.productinventory.dto.v1.Characteristic mapCharacteristicToDto(Characteristic characteristic) {
        if (characteristic instanceof BooleanCharacteristic booleanCharacteristic) {
            return mapBooleanCharacteristicToDto(booleanCharacteristic);
        } else if (characteristic instanceof BooleanArrayCharacteristic booleanArrayCharacteristic) {
            return mapBooleanArrayCharacteristicToDto(booleanArrayCharacteristic);
        } else if (characteristic instanceof StringCharacteristic stringCharacteristic) {
            return mapStringCharacteristicToDto(stringCharacteristic);
        } else if (characteristic instanceof StringArrayCharacteristic stringArrayCharacteristic) {
            return mapStringArrayCharacteristicToDto(stringArrayCharacteristic);
        } else if (characteristic instanceof IntegerCharacteristic integerCharacteristic) {
            return mapIntegerCharacteristicToDto(integerCharacteristic);
        } else if (characteristic instanceof IntegerArrayCharacteristic integerArrayCharacteristic) {
            return mapIntegerArrayCharacteristicToDto(integerArrayCharacteristic);
        } else if (characteristic instanceof ObjectCharacteristic objectCharacteristic) {
            return mapObjectCharacteristicToDto(objectCharacteristic);
        } else if (characteristic instanceof ObjectArrayCharacteristic objectArrayCharacteristic) {
            return mapObjectArrayCharacteristicToDto(objectArrayCharacteristic);
        } else if (characteristic instanceof ValidityCharacteristic validityCharacteristic) {
            return mapValidityCharacteristicToDto(validityCharacteristic);
        } else if (characteristic instanceof AddressCharacteristic addressCharacteristic) {
            return mapAddressCharacteristicToDto(addressCharacteristic);
        } else if (characteristic instanceof DateCharacteristic dateCharacteristic) {
            return mapDateCharacteristicToDto(dateCharacteristic);
        }
        return null;
    }

    // Single-value mappings
    com.orange.discobole.productinventory.dto.v1.BooleanCharacteristic mapBooleanCharacteristicToDto(BooleanCharacteristic booleanCharacteristic);

    com.orange.discobole.productinventory.dto.v1.StringCharacteristic mapStringCharacteristicToDto(StringCharacteristic stringCharacteristic);

    com.orange.discobole.productinventory.dto.v1.IntegerCharacteristic mapIntegerCharacteristicToDto(IntegerCharacteristic integerCharacteristic);

    com.orange.discobole.productinventory.dto.v1.ObjectCharacteristic mapObjectCharacteristicToDto(ObjectCharacteristic objectCharacteristic);

    @Mapping(source = "value.validTo", target = "value.validTo", qualifiedByName = "toOffsetDateTime")
    @Mapping(source = "value.validFrom", target = "value.validFrom", qualifiedByName = "toOffsetDateTime")
    com.orange.discobole.productinventory.dto.v1.ValidityCharacteristic mapValidityCharacteristicToDto(ValidityCharacteristic validityCharacteristic);

    // Array mappings
    com.orange.discobole.productinventory.dto.v1.BooleanArrayCharacteristic mapBooleanArrayCharacteristicToDto(BooleanArrayCharacteristic booleanArrayCharacteristic);

    com.orange.discobole.productinventory.dto.v1.StringArrayCharacteristic mapStringArrayCharacteristicToDto(StringArrayCharacteristic stringArrayCharacteristic);

    com.orange.discobole.productinventory.dto.v1.IntegerArrayCharacteristic mapIntegerArrayCharacteristicToDto(IntegerArrayCharacteristic integerArrayCharacteristic);

    com.orange.discobole.productinventory.dto.v1.ObjectArrayCharacteristic mapObjectArrayCharacteristicToDto(ObjectArrayCharacteristic objectArrayCharacteristic);

    com.orange.discobole.productinventory.dto.v1.AddressCharacteristic mapAddressCharacteristicToDto(AddressCharacteristic addressCharacteristic);

    @Mapping(source = "value", target = "value", qualifiedByName = "toOffsetDateTime")
    com.orange.discobole.productinventory.dto.v1.DateCharacteristic mapDateCharacteristicToDto(DateCharacteristic dateCharacteristic);

    /**
     * Custom mapper for converting `Instant` to `OffsetDateTime`.
     */
    @Named("toOffsetDateTime")
    default OffsetDateTime toOffsetDateTime(Instant instant) {
        return (instant == null) ? null : instant.atOffset(ZoneOffset.UTC);
    }
}