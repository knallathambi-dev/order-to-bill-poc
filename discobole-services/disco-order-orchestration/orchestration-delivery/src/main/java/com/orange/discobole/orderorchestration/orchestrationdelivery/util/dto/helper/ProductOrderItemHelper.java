// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util.dto.helper;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductSpecificationRef;

import java.util.Objects;
import java.util.Optional;

public class ProductOrderItemHelper {

    private ProductOrderItemHelper() {
    }

    public static Optional<String> getProductSpecificationId(ProductOrderItem orderItem) {
        if (Objects.nonNull(orderItem.getProduct())) {
            ProductRefOrValue product = orderItem.getProduct();
            // Check if product is an instance of Product
            if (product instanceof Product) {
                return Optional.ofNullable(((Product) product).getProductSpecification())
                        .map(ProductSpecificationRef::getId);
            }
        }
        return Optional.empty();
    }
}
