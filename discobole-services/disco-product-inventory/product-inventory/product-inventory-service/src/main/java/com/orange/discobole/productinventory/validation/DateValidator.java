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
import com.orange.discobole.productinventory.util.QueryUtils;
import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.FIELD_S_IS_NOT_A_VALID_DATE_OR_DATETIME_FIELD;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.INVALID_DATE_FORMAT_EXPECTED_ISO_8601_FORMAT;

@Slf4j
public class DateValidator {
    private DateValidator() {
    }

    public static void validateDateFilter(String fieldName, String filterValue, FieldsFetcher fieldFetcher) {
        String dateString = filterValue.trim();
        if (!QueryUtils.isValidDate(dateString)) {
            throw new ProductInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_INPUT.getCode(),
                    INVALID_INPUT.getStatus(),
                    INVALID_DATE_FORMAT_EXPECTED_ISO_8601_FORMAT
            );
        }

        // Extract the actual field name (remove suffix like .gte or .lte)
        String actualFieldName = fieldName.replaceAll("\\.(gte|lte)$", "");
        Field field = fieldFetcher.fetch(actualFieldName);

        if (field == null ||
                (!OffsetDateTime.class.isAssignableFrom(field.getType())
                        && !LocalDate.class.isAssignableFrom(field.getType()))) {
            throw new ProductInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_INPUT.getCode(),
                    INVALID_INPUT.getStatus(),
                    String.format(FIELD_S_IS_NOT_A_VALID_DATE_OR_DATETIME_FIELD, actualFieldName)
            );
        }

    }

}
