// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.RelatedProductOrderItem;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public final class ProductUtil {

    private ProductUtil() {
    }

    public static boolean atLeastOneOrderItemExistInProduct(Product product, List<String> orderItemIds) {
        if (Objects.isNull(product)) {
            throw new IllegalArgumentException("Product should not be null");
        }
        if (!CollectionUtils.isEmpty(product.getProductOrderItem())) {
            for (RelatedProductOrderItem relatedPOI : product.getProductOrderItem()) {
                if (Objects.nonNull(orderItemIds) && orderItemIds.contains(relatedPOI.getOrderItemId())) {
                    return true;
                }
            }
        }
        return false;
    }
}