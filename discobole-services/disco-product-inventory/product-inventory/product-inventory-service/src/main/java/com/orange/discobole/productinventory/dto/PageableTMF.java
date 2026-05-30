// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;

import com.orange.discobole.productinventory.exception.ProductInventoryException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Getter
public class PageableTMF {
    private final Integer offset;
    private final Integer limit;
    private final Integer totalCount;
    private final List<String> sort;
    private final String fields;
    private final MultiValueMap<String, Object> filter;
    @Setter
    private Integer resultCount;

    public PageableTMF(Integer offset, Integer limit, Integer totalCount, List<String> sort, String fields, MultiValueMap<String, Object> filter) {
        if (Objects.nonNull(offset) && Objects.nonNull(totalCount) && offset > totalCount) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), ERROR_IN_OFFSET);
        }
        this.offset = offset;
        this.totalCount = totalCount;
        if (Objects.nonNull(sort) && (sort.isEmpty())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "sort" + NOT_BE_EMPTY);
        }
        this.sort = sort;
        if (Objects.nonNull(fields) && (StringUtils.isEmpty(fields))) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "fields" + NOT_BE_EMPTY);
        }
        if (Objects.isNull(limit)) {
            limit = 100;
        }
        this.limit = limit;
        this.fields = fields;
        this.filter = filter;
    }

    public void validatePaginationParameters() throws ProductInventoryException {
        if (offsetAndLimitAreNegative(offset, limit)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), OFFSET_AND_LIMIT_SHOULD_NOT_BE_NEGATIVE);
        }
        if (offsetOnlyIsNegative(offset, limit)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), OFFSET_SHOULD_NOT_BE_NEGATIVE);
        }
        if (limitOnlyIsNegative(offset, limit)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), LIMIT_SHOULD_NOT_BE_NEGATIVE);
        }

        if (limit > 1000) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), LIMIT_SHOULD_NOT_EXCEED);
        }
    }

    private static boolean limitOnlyIsNegative(Integer offset, Integer limit) {
        return (offset == null || offset >= 0) && (limit != null && limit < 0);
    }

    private static boolean offsetOnlyIsNegative(Integer offset, Integer limit) {
        return (offset != null && offset < 0) && (limit == null || limit > 0);
    }

    private static boolean offsetAndLimitAreNegative(Integer offset, Integer limit) {
        return (offset != null && offset < 0) && (limit != null && limit < 0);
    }

}
