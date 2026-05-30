// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.impl;

import com.orange.discobole.productinventory.dto.v1.MeasuredValue;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.validation.ProductEntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.orange.discobole.productinventory.constant.Constant.PRICE_TYPE_RECURRING;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Component
@Slf4j
@Order(3)
public class GenericProductValidator implements ProductEntityValidator {
    @Override
    public void validate(ProductEntity product) {
        if ((Objects.isNull(product.getProductSpecification()) && (Objects.isNull(product.getProductOffering())))) {
            log.error("Null product offering requires non-null product specification");
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), NULL_PRODUCT_OFFERING_NULL_PRODUCT_SPECIFICATION_CANNOT_ACCEPTED);
        }
        if (Objects.nonNull(product.getProductPrice())) {
            product.getProductPrice().forEach(productPriceEntity -> {
                if (PRICE_TYPE_RECURRING.equalsIgnoreCase(productPriceEntity.getPriceType())) {
                    checkIsNullOrEmpty(productPriceEntity.getRecurringChargePeriod());
                }
            });
        }
    }

    private void checkIsNullOrEmpty(MeasuredValue recurringChargePeriod) {
        if (Objects.isNull(recurringChargePeriod)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), WHEN_PRICETYPE_IS_RECURRING_RECURRINGCHARGEPERIOD_FIELD_SHOULD_NOT_BE_NULL);
        }

        Float amount = recurringChargePeriod.getAmount();
        String units = recurringChargePeriod.getUnits();

        if (Objects.isNull(amount) && (Objects.isNull(units) || units.trim().isEmpty())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), WHEN_PRICETYPE_IS_RECURRING_RECURRINGCHARGEPERIOD_FIELD_SHOULD_NOT_BE_EMPTY);

        }
    }
}