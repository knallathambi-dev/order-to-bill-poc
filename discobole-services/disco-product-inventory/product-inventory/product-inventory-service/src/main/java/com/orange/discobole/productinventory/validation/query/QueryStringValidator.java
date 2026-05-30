// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.query;

import com.orange.discobole.productinventory.exception.ProductInventoryException;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.THE_PROVIDED_QUERY_STRING_IS_INVALID;

public class QueryStringValidator implements QueryValidator {

    /**
     * Validates a query string (key=value&key=value&...) to ensure it's in a valid URL query format.
     *
     * @param query The query string to validate.
     */
    @Override
    public void validate(String query) {
        if (query == null || query.isBlank()) {
            return; // propagate empty query validation to specific validators
        }
        try {
            UriComponentsBuilder.fromPath("").query(query).build(true);
        } catch (Exception e) {
            throw new ProductInventoryException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_INPUT.getCode(),
                    INVALID_INPUT.getStatus(),
                    THE_PROVIDED_QUERY_STRING_IS_INVALID
            );
        }
    }
}