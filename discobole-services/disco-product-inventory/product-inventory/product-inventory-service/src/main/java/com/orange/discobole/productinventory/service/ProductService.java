// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.model.ProductEntity;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Set;


public interface ProductService {

    Product createProduct(Product product);

    ProductEntity getProductEntity(String id, String fields);

    List<ProductEntity> getProducts(PageableTMF attributes);

    int getTotalCount(MultiValueMap<String, Object> filter);

    Product updateProduct(String id, Product product);

    List<ProductEntity> updateProducts(List<ProductEntity> patchedProducts);

    List<ProductEntity> getListOfProductEntityBy(Set<String> ids);

    List<ProductEntity> getListOfProductEntityByIds(Set<String> ids);

    void deleteProduct(String id);
}
