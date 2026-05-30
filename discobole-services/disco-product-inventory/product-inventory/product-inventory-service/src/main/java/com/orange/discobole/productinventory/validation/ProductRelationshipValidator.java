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
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Slf4j
public class ProductRelationshipValidator {


    private ProductRelationshipValidator() {
    }


    public static void isProductOnRelationshipWithSameProduct(String productId, String id) {
        if (productId.equals(id)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), PRODUCT_CANNOT_BE_ON_RELATIONSHIP_WITH_THE_SAME_PRODUCT);
        }
    }

    public static void isSameProductDuplicatedRelationship(Set<String> productIdSet, String id) {
        if (!productIdSet.add(id)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), SAME_PRODUCT_WITH_DIFFERENT_RELATIONSHIP_TYPES_IS_NOT_ALLOWED);
        }
    }

    public static void isProductWithRelationshipExists(List<ProductRelationshipEntity> productRelationships, String id) {
        if (Objects.nonNull(productRelationships) && productRelationships.stream().map(p -> p.getProduct().getId()).anyMatch(prodId -> prodId.toString().equals(id))) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), String.format(THIS_PRODUCT_IS_ALREADY_ON_RELATIONSHIP_WITH_THE_PRODUCT, id));
        }
    }


}
