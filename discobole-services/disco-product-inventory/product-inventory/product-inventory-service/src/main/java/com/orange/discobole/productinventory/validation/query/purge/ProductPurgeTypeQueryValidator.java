// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.query.purge;

import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.validation.pageable.impl.ProductFieldFetcher;
import com.orange.discobole.productinventory.validation.query.QueryValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.orange.discobole.productinventory.constant.Constant.STATUS;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.ALL_STATUS_VALUES_SHOULD_BE_IN;

@Slf4j
public class ProductPurgeTypeQueryValidator extends PurgeTypeQueryValidator implements QueryValidator {
    public static final List<ProductStatusType> PRODUCT_STATUS_CAN_BE_DELETED = List.of(ProductStatusType.TERMINATED, ProductStatusType.ABORTED, ProductStatusType.CANCELLED);
    private static final ProductFieldFetcher PRODUCT_FIELD_FETCHER = new ProductFieldFetcher();

    @Override
    public void validate(String query) {
        validate(query, STATUS, ALL_STATUS_VALUES_SHOULD_BE_IN, PRODUCT_STATUS_CAN_BE_DELETED, PRODUCT_FIELD_FETCHER);
    }

}
