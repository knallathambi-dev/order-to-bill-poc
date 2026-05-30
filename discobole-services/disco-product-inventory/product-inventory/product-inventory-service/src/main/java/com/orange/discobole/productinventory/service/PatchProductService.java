// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.orange.discobole.productinventory.dto.v1.ProductPatch;
import com.orange.discobole.productinventory.model.ProductEntity;

import java.util.List;
import java.util.Map;

public interface PatchProductService {
    Map<String, ArrayNode> groupProductOperations(List<ProductPatch> productPatches);

    void checkPatchProduct(Map<String, ArrayNode> productPatchNodes, List<ProductEntity> products);

    List<ProductEntity> applyPatchToProductsWithValidation(Map<String, ArrayNode> productPatchNodes, List<ProductEntity> products);

    List<ProductEntity> abortProductsByOrderId(String productOrderId);
}