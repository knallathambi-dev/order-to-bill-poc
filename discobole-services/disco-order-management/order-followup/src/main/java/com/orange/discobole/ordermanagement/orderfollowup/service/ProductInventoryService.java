// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderStateType;
import com.orange.discobole.productinventory.dto.v1.Product;

import java.util.List;

public interface ProductInventoryService {

    List<Product> getProductsByProductOrderById(String productOrderId);

    Product getProductById(String productId);

    void updateProductsHierarchy(String productOrderId, String deliveredProductOrderItemId, ProductOrderStateType productOrderState);

    List<Product> updateProducts(String jsonPatch);
}