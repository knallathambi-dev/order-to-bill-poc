// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.impl;

import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.validation.ProductDatesChecker;
import com.orange.discobole.productinventory.validation.ProductEntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.orange.discobole.productinventory.validation.ProductCharacteristicsValidator.checkOnlyOneOccurrenceValidityCharacteristic;
import static com.orange.discobole.productinventory.validation.ProductCharacteristicsValidator.validateDateTimeCharacteristic;
import static com.orange.discobole.productinventory.validation.ProductOrderAttributesValidator.validateProductOrderItem;
import static com.orange.discobole.productinventory.validation.status.StatusCheckerContext.getStatusChecker;

@Component
@Slf4j
@Order(4)
public class ProductAttributesValidator implements ProductEntityValidator {


    @Override
    public void validate(ProductEntity product) {
        validateProductOrderItem(product.getProductOrderItem());
        checkOnlyOneOccurrenceValidityCharacteristic(product);
        validateDateTimeCharacteristic(product);
        ProductDatesChecker.validateProductStartDate(product.getStartDate());
        getStatusChecker(product.getAtType()).checkProductStatusBeforeCreated(product.getStatus(), product.getOperationalStatus());
    }

}
