// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation;

import com.orange.discobole.productinventory.enumerate.CharacteristicsValueType;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.CharacteristicEntity;
import com.orange.discobole.productinventory.model.ProductEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.orange.discobole.productinventory.constant.Constant.*;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

public class ProductCharacteristicsValidator {
    private ProductCharacteristicsValidator() {
    }

    public static void checkOnlyOneOccurrenceValidityCharacteristic(ProductEntity product) {
        if (Objects.nonNull(product.getProductCharacteristic()) && !product.getProductCharacteristic().isEmpty()) {
            List<CharacteristicEntity> validityCharacteristic = product.getProductCharacteristic().stream().filter(characteristic -> characteristic.getAtType().equals(VALIDITY_CHARACTERISTIC)).toList();
            if (validityCharacteristic.size() >= 2) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), ONLY_ONE_OCCURRENCE_IS_ACCEPTED_FOR_VALIDITY_CHARACTERISTIC);
            }
            if (!validityCharacteristic.isEmpty()) {
                validateCharacteristicFields(validityCharacteristic.get(0));
            }
        }
    }
    private static void validateCharacteristicFields(CharacteristicEntity characteristic) {
        Object value = characteristic.getValue();
        if (value instanceof Map) {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            validateMutualExclusivity(valueMap);
            validateValidToIfPresent(valueMap);
        }
    }
    private static void validateMutualExclusivity(Map<String, Object> valueMap) {
        boolean hasDurationAndUnit = valueMap.containsKey("value") && valueMap.containsKey(UNITE_OF_MEASURE);
        boolean hasValidTo = valueMap.containsKey(VALID_TO);
        if ((hasDurationAndUnit && hasValidTo) || (!hasDurationAndUnit && !hasValidTo)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), EXCLUSION_FOR_VALUE_UNITE_OF_MEASURE_VALID_TO);
        }
    }
    private static void validateValidToIfPresent(Map<String, Object> valueMap) {
        if (valueMap.containsKey(VALID_TO)) {
            String validToStr = valueMap.get(VALID_TO).toString();
            validateValidToDate(validToStr);
        }
    }
    private static void validateValidToDate(String validToStr) {
        try {
            LocalDateTime validToDate = LocalDateTime.parse(validToStr, DateTimeFormatter.ISO_DATE_TIME);
            if (validToDate.isBefore(LocalDateTime.now())) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), VALID_TO_DATE_ERROR_MESSAGE);
            }
        } catch (Exception e) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), INVALID_FORMAT_DATE);
        }
    }

    public static void validateDateTimeCharacteristic(ProductEntity product) {
        List<CharacteristicEntity> characteristics = product.getProductCharacteristic();
        if (Objects.isNull(characteristics) || characteristics.isEmpty()) {
            return;
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        for (CharacteristicEntity characteristic : characteristics) {
            if (OBJECT_CHARACTERISTIC.equals(characteristic.getAtType()) &&
                    CharacteristicsValueType.DATE_TIME.getValue().equals(characteristic.getValueType())) {
                try {
                    dateTimeFormatter.parse(String.valueOf(characteristic.getValue()));
                } catch (DateTimeParseException e) {
                    throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), INVALID_FORMAT_FOR_DATE_TIME_PRODUCT_CHARACTERISTIC);
                }
            }
        }
    }

}