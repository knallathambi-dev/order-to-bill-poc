// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.query;

import com.orange.discobole.productinventory.dto.v1.JobSpecification;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.validation.pageable.impl.FieldFetcher;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.INVALID_FILTER_FORMAT;
import static com.orange.discobole.productinventory.validation.DateValidator.validateDateFilter;
import static com.orange.discobole.productinventory.validation.EnumValidator.validateEnumFieldValue;

public class JobSpecificationQueryValidator implements QueryValidator {
    private final FieldFetcher<JobSpecification> jobSpecificationFieldFetcher = new FieldFetcher<>(JobSpecification.class);

    @Override
    public void validate(String query) {
        // Implement product-specific query validation logic here
        String[] filters = query.split("&");

        for (String filter : filters) {
            validateFilter(filter);
        }
    }

    // Main method to validate a filter string
    public void validateFilter(String filter) {
        String[] filterParts = filter.split("=");
        if (filterParts.length != 2) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), INVALID_FILTER_FORMAT);
        }
        String fieldName = filterParts[0].trim();
        String filterValue = filterParts[1].trim();

        // Check for date comparison filters (gte, lte)
        if (fieldName.endsWith(".gte") || fieldName.endsWith(".lte")) {
            validateDateFilter(fieldName, filterValue, jobSpecificationFieldFetcher);
            return;
        }

        // Handle simple or nested field validation
        Field field = jobSpecificationFieldFetcher.fetch(fieldName);
        validateEnumFieldValue(field, filterValue);

    }


}