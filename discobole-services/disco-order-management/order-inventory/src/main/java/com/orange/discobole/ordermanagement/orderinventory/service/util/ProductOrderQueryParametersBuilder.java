// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service.util;

import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import com.orange.discobole.ordermanagement.orderinventory.exception.model.BusinessException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

import static com.orange.discobole.ordermanagement.orderinventory.constant.Constant.DATE_TYPE_REGEX;
import static com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static java.lang.String.format;

@SuppressFBWarnings(value = "EI_EXPOSE_REP")
public class ProductOrderQueryParametersBuilder {

    private static final Pattern ISO_DATE_PATTERN = Pattern.compile(DATE_TYPE_REGEX);
    private static final String START_OF_DAY = "T00:00:00Z";
    private static final String END_OF_DAY = "T23:59:59Z";

    private final MultiValueMap<String, Object> params;

    public ProductOrderQueryParametersBuilder() {
        this.params = new LinkedMultiValueMap<>();
    }

    public ProductOrderQueryParametersBuilder addIfNotBlank(String key, String value) {
        if (StringUtils.hasText(value)) {
            params.add(key, value);
        }
        return this;
    }

    public ProductOrderQueryParametersBuilder addListIfNotEmpty(String key, List<?> values) {
        if (values != null && !values.isEmpty()) {
            params.addAll(key, values);
        }
        return this;
    }

    public ProductOrderQueryParametersBuilder addIfPositive(String key, Integer value) {
        if (value != null && value > 0) {
            params.add(key, value);
        }
        return this;
    }

    public ProductOrderQueryParametersBuilder addDateRange(
            String baseKey,
            String exact,
            String gt,
            String gte,
            String lt,
            String lte) {

        if (StringUtils.hasText(exact)) {
            addDateParam(baseKey, exact, DateRangeType.EXACT);
        }
        if (StringUtils.hasText(gt)) {
            addDateParam(baseKey + ".gt", gt, DateRangeType.SINGLE);
        }
        if (StringUtils.hasText(gte)) {
            addDateParam(baseKey + ".gte", gte, DateRangeType.SINGLE);
        }
        if (StringUtils.hasText(lt)) {
            addDateParam(baseKey + ".lt", lt, DateRangeType.SINGLE);
        }
        if (StringUtils.hasText(lte)) {
            addDateParam(baseKey + ".lte", lte, DateRangeType.SINGLE);
        }
        return this;
    }

    private void addDateParam(String key, String dateValue, DateRangeType rangeType) {
        try {
            if (isIsoDateFormat(dateValue)) {
                addIsoDateParam(key, dateValue, rangeType);
            } else {
                addInstantParam(key, dateValue);
            }
        } catch (DateTimeParseException e) {
            throw new ProductOrderInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_QUERY_STRING_PARAMETER.getCode(),
                    INVALID_QUERY_STRING_PARAMETER.getStatus(),
                    format(BusinessException.INVALID_DATE_FORMAT, key, dateValue)
            );
        }
    }

    private boolean isIsoDateFormat(String value) {
        return ISO_DATE_PATTERN.matcher(value).matches();
    }

    private void addIsoDateParam(String key, String dateValue, DateRangeType rangeType) {
        if (rangeType == DateRangeType.EXACT) {
            String baseKey = extractBaseKey(key);
            params.add(baseKey + ".gte", dateValue + START_OF_DAY);
            params.add(baseKey + ".lte", dateValue + END_OF_DAY);
        } else {
            params.add(key, dateValue + START_OF_DAY);
        }
    }

    private void addInstantParam(String key, String instantValue) {
        Instant instant = Instant.parse(instantValue);
        params.add(key, instant);
    }

    private String extractBaseKey(String key) {
        return key.replaceAll("\\.(gte?|lte?)$", "");
    }

    public MultiValueMap<String, Object> build() {
        return new LinkedMultiValueMap<>(params);
    }

    private enum DateRangeType {
        SINGLE,
        EXACT
    }
}