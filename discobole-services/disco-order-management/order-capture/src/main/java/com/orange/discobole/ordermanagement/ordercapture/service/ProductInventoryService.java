// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.productinventory.dto.v1.Product;

import java.util.List;
import java.util.function.Predicate;

public interface ProductInventoryService {
    Product createProducts(Product product);

    List<Product> updateProducts(String jsonPatch);

    void cancelProducts(List<String> productsIds);

    List<String> getProductsIdsByProductOrder(ProductOrder productOrder, Predicate<Product> filter);

    void confirmProducts(List<String> productsIds);

    Product getProductById(String productId);

    List<Product> getProductByIds(List<String> productIds);

    List<Product> getProductsByProductOrderId(String productOrderId);

    List<Product> getProductsByRelationship(String productId);

    void terminateProducts(List<Product> terminatedProducts, List<Product> products, String productOrderId);

    void abortProducts(List<String> productsIds);

}