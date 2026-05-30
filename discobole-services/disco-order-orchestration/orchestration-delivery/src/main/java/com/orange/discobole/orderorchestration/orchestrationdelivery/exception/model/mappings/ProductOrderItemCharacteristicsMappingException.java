// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.mappings;

import com.orange.discobole.orderorchestration.exception.model.CoodMappingException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;

public class ProductOrderItemCharacteristicsMappingException extends CoodMappingException {


    public ProductOrderItemCharacteristicsMappingException(ExceptionCode exceptionCode, Class<?> entityName, Class<?> targetEntityName, Object... parameters) {
        super(exceptionCode, entityName, targetEntityName, parameters);
    }

    @Override
    public String getMessage() {
        return "PRODUCT_ORDER_ITEM_CHARACTERISTIC_MAPPING_EXCEPTION | code: {%s} | message: {%s} | reason: {%s} | entityName: {%s} | targetEntityName: {%s}"
                .formatted(coodError.code(), coodError.message(), coodError.reason(), entityName, targetEntityName);
    }
}
