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
import com.orange.discobole.productinventory.model.RelatedProductOrderItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.EMPTY_ORDER_ITEM_ID_DETECTED;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.EMPTY_PRODUCT_ORDER_ID_DETECTED;

@Slf4j
public class ProductOrderAttributesValidator {
    private ProductOrderAttributesValidator() {
    }

    public static void validateProductOrderItem(List<RelatedProductOrderItemEntity> productOrderItems) {
        if (productOrderItems == null || productOrderItems.isEmpty()) {
            return;
        }
        productOrderItems.forEach(productOrderItem -> {
            if (productOrderItem.getProductOrderId().isEmpty()) {
                log.error("Empty product order ID detected");
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), EMPTY_PRODUCT_ORDER_ID_DETECTED);
            }
            if (productOrderItem.getOrderItemId().isEmpty()) {
                log.error("Empty order item ID detected");
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), EMPTY_ORDER_ITEM_ID_DETECTED);
            }
        });
    }
}
