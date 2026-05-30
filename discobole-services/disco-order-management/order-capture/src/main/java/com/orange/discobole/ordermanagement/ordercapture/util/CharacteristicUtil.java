// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.processflow.dto.generated.*;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;


@Slf4j
public class CharacteristicUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    private CharacteristicUtil() {
    }

    public static <T> T getCharacteristicValue(List<Characteristic> characteristicList, List<CharacteristicSpecification> characteristicSpecificationList, Class<T> className) {
        Characteristic characteristic = getCharacteristic(characteristicList, className.getSimpleName(), characteristicSpecificationList);
        if (Objects.isNull(characteristic) || Objects.isNull(characteristic.getValue())) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_CHARACTERISTICS_INPUT);
        }
        T result = MiscUtil.convertInstanceOfObject(characteristic.getValue(), className);
        if (!ConstraintValidator.isValid(result)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_CHARACTERISTICS_INPUT);
        }
        return result;
    }

    public static <T> T getCharacteristicValue(List<Characteristic> characteristicList, List<CharacteristicSpecification> characteristicSpecificationList, Class<T> className, String name) {
        Characteristic characteristic = getCharacteristicStartWith(characteristicList, name, characteristicSpecificationList);
        if (Objects.isNull(characteristic) || Objects.isNull(characteristic.getValue())) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_CHARACTERISTICS_INPUT);
        }
        T result = MiscUtil.convertInstanceOfObject(characteristic.getValue(), className);
        if (!ConstraintValidator.isValid(result)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_CHARACTERISTICS_INPUT);
        }
        return result;
    }

    public static Characteristic getCharacteristic(final List<Characteristic> characteristics, final String className,
                                                   List<CharacteristicSpecification> characteristicSpecificationList) {
        List<Characteristic> characteristicList = new ArrayList<>();
        if (Objects.isNull(className)) {
            log.error(ExceptionMessage.NAME_CANNOT_BE_NULL);
            throw new IllegalArgumentException(ExceptionMessage.NAME_CANNOT_BE_NULL);
        }
        CharacteristicSpecification characteristicSpec = characteristicSpecificationList.stream()
                .filter(specification -> className.equalsIgnoreCase(specification.getName())).findAny().orElse(null);
        if (Objects.nonNull(characteristicSpec)) {
            int minCardinality = characteristicSpec.getMinCardinality();
            int maxCardinality = characteristicSpec.getMaxCardinality();
            if (Objects.isNull(characteristics) && minCardinality > 0) {
                throw new InvalidParameterException(format(ExceptionMessage.CHARACTERISTIC_CANNOT_BE_NULL_OR_EMPTY, className));
            } else if (Objects.nonNull(characteristics)) {
                characteristicList = characteristics.stream().filter(c -> className.equalsIgnoreCase(c.getName())).toList();
                checkCardinality(characteristicList, minCardinality, maxCardinality, className);
            }
        }
        return characteristicList.stream().filter(characteristic -> className.equalsIgnoreCase(characteristic.getName())).findAny().orElse(null);
    }

    public static Characteristic getCharacteristicStartWith(final List<Characteristic> characteristics, final String className,
                                                            List<CharacteristicSpecification> characteristicSpecificationList) {
        List<Characteristic> characteristicList = new ArrayList<>();
        if (Objects.isNull(className)) {
            log.error(ExceptionMessage.NAME_CANNOT_BE_NULL);
            throw new IllegalArgumentException(ExceptionMessage.NAME_CANNOT_BE_NULL);
        }
        CharacteristicSpecification characteristicSpec = characteristicSpecificationList.stream()
                .filter(specification -> className.startsWith(specification.getName())).findAny().orElse(null);
        if (Objects.nonNull(characteristicSpec)) {
            int minCardinality = characteristicSpec.getMinCardinality();
            int maxCardinality = characteristicSpec.getMaxCardinality();
            if (Objects.isNull(characteristics) && minCardinality > 0) {
                throw new InvalidParameterException(className + ExceptionMessage.CHARACTERISTIC_CANNOT_BE_NULL_OR_EMPTY);
            } else if (Objects.nonNull(characteristics)) {
                characteristicList = characteristics.stream().filter(c -> className.startsWith(c.getName())).toList();
                checkCardinality(characteristicList, minCardinality, maxCardinality, className);
            }
        }
        return characteristicList.stream().filter(characteristic -> className.startsWith(characteristic.getName())).findAny().orElse(null);
    }

    private static void checkCardinality(final List<Characteristic> characteristic, int minCardinality, int maxCardinality, String name) {
        if (Objects.nonNull(characteristic)) {
            if (characteristic.isEmpty() && minCardinality > 0) {
                throw new InvalidParameterException(name + ExceptionMessage.CHARACTERISTIC_CANNOT_BE_NULL_OR_EMPTY);
            } else if (characteristic.size() < minCardinality || characteristic.size() > maxCardinality) {
                throw new InvalidParameterException(format(ExceptionMessage.MIN_MAX_CARDINALITY, minCardinality, maxCardinality, name));
            }
        } else if (minCardinality == 1) {
            throw new InvalidParameterException(ExceptionMessage.ONE_CHARACTERISTIC_IS_MANDATORY);
        }
    }

    public static String createCharacteristicSpecificationId(String taskDefinitionId, String className) {
        return format("%s-%s", taskDefinitionId, className);
    }

    public static CharacteristicSpecification createCharacteristicSpecification(String id, String name,
                                                                                Integer minCardinality,
                                                                                Integer maxCardinality,
                                                                                List<CharacteristicValueSpecification> value,
                                                                                List<CharacteristicSpecificationRelationship> characteristicSpecificationRelationship,
                                                                                String valueType) {
        return new CharacteristicSpecification()
                .id(id)
                .name(name)
                .minCardinality(minCardinality)
                .maxCardinality(maxCardinality)
                .characteristicValueSpecification(value)
                .characteristicSpecificationRelationship(characteristicSpecificationRelationship)
                .valueType(valueType);
    }

    public static List<CharacteristicValueSpecification> getCharacteristicSpecificationValue(String fileName) throws IOException {
        String filePath = format("/schemas/%s.json", fileName);
        String file = FileUtil.read(filePath);
        Object value = mapper.readValue(file, Object.class);
        return Collections.singletonList(new ObjectCharacteristicValueSpecification()
                .value(value)
                .type(ObjectCharacteristicValueSpecification.class.getSimpleName()));
    }
}