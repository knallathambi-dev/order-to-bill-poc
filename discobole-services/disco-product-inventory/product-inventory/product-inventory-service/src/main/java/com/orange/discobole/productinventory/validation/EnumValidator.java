// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation;

import com.orange.discobole.productinventory.exception.ProductInventoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.INVALID_FILTER_VALUE_FOR_ENUM;

@Slf4j
public class EnumValidator {
    private EnumValidator() {
    }

    /**
     * Verifies that all values in the given list are valid values for the specified enum type.
     *
     * @param values            A list of strings to validate.
     * @param statusToBeChecked The class object of the enum type.
     * @param <E>               The type of the enum.
     */
    public static <E extends Enum<E>> void areAllValuesValid(List<String> values, List<E> statusToBeChecked, String error) {
        if (isStatusNotNullOrEmpty(values)) {
            boolean allValid = statusToBeChecked.stream().map(Enum::toString).collect(Collectors.toSet()).containsAll(values);
            if (!allValid) {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(error,
                        statusToBeChecked));
            }
        }
    }

    @SuppressWarnings("REC_CATCH_EXCEPTION")
    public static <E extends Enum<E>> boolean valueInEnum(Class<?> enumClass, String value) { //NOSONAR
        try {
            Method method = enumClass.getMethod("getValue");

            return Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(e -> {
                        try {
                            String enumValue = (String) method.invoke(e);
                            return enumValue.equalsIgnoreCase(value);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            log.error("Error invoking getValue() on enum {}", e, ex);
                            return false;
                        }
                    });
        } catch (NoSuchMethodException ex) {
            log.error("Enum class {} does not have a getValue() method", enumClass.getSimpleName(), ex);
            return false;
        }
    }

    public static void validateEnumFieldValue(Field field, String filterValue) {
        if (field != null && field.getType().isEnum() && !valueInEnum(field.getType(), filterValue)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(INVALID_FILTER_VALUE_FOR_ENUM, filterValue, field.getType().getSimpleName()));
        }
    }

    private static boolean isStatusNotNullOrEmpty(List<String> statusList) {
        return Objects.nonNull(statusList) && !statusList.isEmpty();
    }

}
