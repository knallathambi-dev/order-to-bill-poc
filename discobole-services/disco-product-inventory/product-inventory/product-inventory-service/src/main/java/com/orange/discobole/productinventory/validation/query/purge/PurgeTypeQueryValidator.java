// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.query.purge;

import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.PURGE_QUERY_CANNOT_BE_EMPTY;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_QUERY_SHOULD_INCLUDE_AT_LEAST_ON_OF_THESE_STATUS_VALUES;
import static com.orange.discobole.productinventory.util.QueryUtils.parseQueryToMultiValueMap;
import static com.orange.discobole.productinventory.validation.EnumValidator.areAllValuesValid;

@Slf4j
public abstract class PurgeTypeQueryValidator {

    public <E extends Enum<E>> void validate(String query, String statusField, String validStatusErrorMessage, List<E> statusesThatCanBeDeleted, FieldsFetcher validator) {
        if (query == null || query.isEmpty()) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), PURGE_QUERY_CANNOT_BE_EMPTY);
        }
        MultiValueMap<String, Object> queryMap = parseQueryToMultiValueMap(query, validator);
        if (queryMap.isEmpty()) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), PURGE_QUERY_CANNOT_BE_EMPTY);
        }
        List<String> statuses = Optional.ofNullable(queryMap.get(statusField))
                .orElse(List.of())
                .stream()
                .map(Object::toString)
                .filter(status -> status != null && !status.isBlank()) // Filter out null and blank strings
                .toList();
        if (statuses.isEmpty()) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(
                    THE_QUERY_SHOULD_INCLUDE_AT_LEAST_ON_OF_THESE_STATUS_VALUES, statusField, statusesThatCanBeDeleted));
        }
        areAllValuesValid(statuses, statusesThatCanBeDeleted, validStatusErrorMessage);
    }


}
