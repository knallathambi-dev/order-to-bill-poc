// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.mapper;

import com.orange.discobole.ordermanagement.orderinventory.domain.*;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CharacteristicMapper {

    /**
     * Maps a {@link CharacteristicEntity} to its DTO equivalent.
     *
     * @param entity the source entity to be mapped
     * @return the mapped {@link Characteristic}, or {@code null} if the input is {@code null}
     */
    default Characteristic mapToDto(CharacteristicEntity entity) {
        if (entity instanceof BooleanCharacteristicEntity booleanEntity) {
            return mapToBooleanCharacteristic(booleanEntity);
        } else if (entity instanceof BooleanArrayCharacteristicEntity booleanArrayEntity) {
            return mapToBooleanArrayCharacteristic(booleanArrayEntity);
        } else if (entity instanceof StringCharacteristicEntity stringEntity) {
            return mapToStringCharacteristic(stringEntity);
        } else if (entity instanceof StringArrayCharacteristicEntity stringArrayEntity) {
            return mapToStringArrayCharacteristic(stringArrayEntity);
        } else if (entity instanceof IntegerCharacteristicEntity integerEntity) {
            return mapToIntegerCharacteristic(integerEntity);
        } else if (entity instanceof IntegerArrayCharacteristicEntity integerArrayEntity) {
            return mapToIntegerArrayCharacteristic(integerArrayEntity);
        } else if (entity instanceof ObjectCharacteristicEntity objectEntity) {
            return mapToObjectCharacteristic(objectEntity);
        } else if (entity instanceof ObjectArrayCharacteristicEntity objectArrayEntity) {
            return mapToObjectArrayCharacteristic(objectArrayEntity);
        } else if (entity instanceof FloatCharacteristicEntity floatEntity) {
            return mapToFloatCharacteristic(floatEntity);
        } else if (entity instanceof FloatArrayCharacteristicEntity floatArrayEntity) {
            return mapToFloatArrayCharacteristic(floatArrayEntity);
        } else if (entity instanceof ValidityCharacteristicEntity validityEntity) {
            return mapToValidityCharacteristic(validityEntity);
        } else if (entity instanceof AddressCharacteristicEntity addressCharacteristicEntity) {
            return mapToAddressCharacteristic(addressCharacteristicEntity);
        } else if (entity instanceof DateCharacteristicEntity dateCharacteristicEntity) {
            return mapToDateCharacteristic(dateCharacteristicEntity);
        }
        return null;
    }

    /**
     * Maps a {@link Characteristic} DTO to its entity equivalent.
     *
     * @param characteristic the source DTO to be mapped
     * @return the mapped {@link CharacteristicEntity}, or {@code null} if the input is {@code null}
     */
    default CharacteristicEntity mapToEntity(Characteristic characteristic) {
        if (characteristic instanceof BooleanCharacteristic booleanDto) {
            return mapToBooleanCharacteristicEntity(booleanDto);
        } else if (characteristic instanceof BooleanArrayCharacteristic booleanArrayDto) {
            return mapToBooleanArrayCharacteristicEntity(booleanArrayDto);
        } else if (characteristic instanceof StringCharacteristic stringDto) {
            return mapToStringCharacteristicEntity(stringDto);
        } else if (characteristic instanceof StringArrayCharacteristic stringArrayDto) {
            return mapToStringArrayCharacteristicEntity(stringArrayDto);
        } else if (characteristic instanceof IntegerCharacteristic integerDto) {
            return mapToIntegerCharacteristicEntity(integerDto);
        } else if (characteristic instanceof IntegerArrayCharacteristic integerArrayDto) {
            return mapToIntegerArrayCharacteristicEntity(integerArrayDto);
        } else if (characteristic instanceof ObjectCharacteristic objectDto) {
            return mapToObjectCharacteristicEntity(objectDto);
        } else if (characteristic instanceof ObjectArrayCharacteristic objectArrayDto) {
            return mapToObjectArrayCharacteristicEntity(objectArrayDto);
        } else if (characteristic instanceof FloatCharacteristic floatDto) {
            return mapToFloatCharacteristicEntity(floatDto);
        } else if (characteristic instanceof FloatArrayCharacteristic floatArrayDto) {
            return mapToFloatArrayCharacteristicEntity(floatArrayDto);
        } else if (characteristic instanceof ValidityCharacteristic validityDto) {
            return mapToValidityCharacteristicEntity(validityDto);
        } else if (characteristic instanceof AddressCharacteristic addressCharacteristic) {
            return mapToAddressCharacteristicEntity(addressCharacteristic);
        } else if (characteristic instanceof DateCharacteristic dateCharacteristic) {
            return mapToDateCharacteristicEntity(dateCharacteristic);
        }
        return null;
    }

    // Mapping methods for single characteristics

    BooleanCharacteristic mapToBooleanCharacteristic(BooleanCharacteristicEntity entity);

    StringCharacteristic mapToStringCharacteristic(StringCharacteristicEntity entity);

    IntegerCharacteristic mapToIntegerCharacteristic(IntegerCharacteristicEntity entity);

    ObjectCharacteristic mapToObjectCharacteristic(ObjectCharacteristicEntity entity);

    FloatCharacteristic mapToFloatCharacteristic(FloatCharacteristicEntity entity);

    ValidityCharacteristic mapToValidityCharacteristic(ValidityCharacteristicEntity entity);

    AddressCharacteristic mapToAddressCharacteristic(AddressCharacteristicEntity entity);

    DateCharacteristic mapToDateCharacteristic(DateCharacteristicEntity entity);

    // Mapping methods for array characteristics

    BooleanArrayCharacteristic mapToBooleanArrayCharacteristic(BooleanArrayCharacteristicEntity entity);

    StringArrayCharacteristic mapToStringArrayCharacteristic(StringArrayCharacteristicEntity entity);

    IntegerArrayCharacteristic mapToIntegerArrayCharacteristic(IntegerArrayCharacteristicEntity entity);

    ObjectArrayCharacteristic mapToObjectArrayCharacteristic(ObjectArrayCharacteristicEntity entity);

    FloatArrayCharacteristic mapToFloatArrayCharacteristic(FloatArrayCharacteristicEntity entity);

    // Reverse mapping methods for single characteristics

    BooleanCharacteristicEntity mapToBooleanCharacteristicEntity(BooleanCharacteristic dto);

    StringCharacteristicEntity mapToStringCharacteristicEntity(StringCharacteristic dto);

    IntegerCharacteristicEntity mapToIntegerCharacteristicEntity(IntegerCharacteristic dto);

    ObjectCharacteristicEntity mapToObjectCharacteristicEntity(ObjectCharacteristic dto);

    FloatCharacteristicEntity mapToFloatCharacteristicEntity(FloatCharacteristic dto);

    ValidityCharacteristicEntity mapToValidityCharacteristicEntity(ValidityCharacteristic dto);

    AddressCharacteristicEntity mapToAddressCharacteristicEntity(AddressCharacteristic dto);

    DateCharacteristicEntity mapToDateCharacteristicEntity(DateCharacteristic dto);


    // Reverse mapping methods for array characteristics

    BooleanArrayCharacteristicEntity mapToBooleanArrayCharacteristicEntity(BooleanArrayCharacteristic dto);

    StringArrayCharacteristicEntity mapToStringArrayCharacteristicEntity(StringArrayCharacteristic dto);

    IntegerArrayCharacteristicEntity mapToIntegerArrayCharacteristicEntity(IntegerArrayCharacteristic dto);

    ObjectArrayCharacteristicEntity mapToObjectArrayCharacteristicEntity(ObjectArrayCharacteristic dto);

    FloatArrayCharacteristicEntity mapToFloatArrayCharacteristicEntity(FloatArrayCharacteristic dto);
}