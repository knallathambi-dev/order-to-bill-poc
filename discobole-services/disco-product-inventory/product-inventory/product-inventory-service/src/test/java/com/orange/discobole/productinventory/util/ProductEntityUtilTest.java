// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.model.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.orange.discobole.productinventory.constant.Constant.PRODUCT_SPECIFICATION_REF;
import static com.orange.discobole.productinventory.enumerate.ProductOfferingTypeEnum.*;

class ProductEntityUtilTest {

    public static final String ID_PARENT = ObjectId.get().toString();


    static final ProductEntity CHILD_BUNDLED = ProductEntity.builder().id("id").productOffering(ProductOfferingRefEntity.builder().atType(BUNDLE_PRODUCT_OFFERING.getValue()).build()).productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.HASPARENT.getValue()).product(new ProductRefEntity(ID_PARENT)).build())).build();
    static final ProductEntity CHILD_SP = ProductEntity.builder().id("id").productSpecification(ProductSpecificationRefEntity.builder().atType(PRODUCT_SPECIFICATION_REF).build()).productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.HASPARENT.getValue()).product(new ProductRefEntity(ID_PARENT)).build())).build();
    static final ProductEntity CHILD_ATOMIC = ProductEntity.builder().id("id").productOffering(ProductOfferingRefEntity.builder().atType(ATOMIC_PRODUCT_OFFERING.getValue()).build()).productRelationship(List.of(ProductRelationshipEntity.builder().relationshipType(ProductRelationshipType.HASPARENT.getValue()).product(new ProductRefEntity(ID_PARENT)).build())).build();
    static final ProductEntity ROOT_PRODUCT = ProductEntity.builder().id(ID_PARENT).productOffering(ProductOfferingRefEntity.builder().atType(CONTRACT.getValue()).build()).build();
    static final ProductEntity PARENT_BUNDLED = ProductEntity.builder().id(ID_PARENT).productOffering(ProductOfferingRefEntity.builder().atType(BUNDLE_PRODUCT_OFFERING.getValue()).build()).build();
    static final ProductEntity PARENT_ATOMIC = ProductEntity.builder().id(ID_PARENT).productOffering(ProductOfferingRefEntity.builder().atType(ATOMIC_PRODUCT_OFFERING.getValue()).build()).build();

    @Test
    void testIsBundlesRelationShip() {
        boolean resultContractBundled = ProductEntityUtil.isBundlesRelationShip(CHILD_BUNDLED, ROOT_PRODUCT);
        boolean resultAtomic = ProductEntityUtil.isBundlesRelationShip(CHILD_ATOMIC, PARENT_BUNDLED);
        boolean resultBundled = ProductEntityUtil.isBundlesRelationShip(CHILD_BUNDLED, CHILD_BUNDLED);

        Assertions.assertTrue(resultContractBundled);
        Assertions.assertTrue(resultAtomic);
        Assertions.assertTrue(resultBundled);
    }

    @Test
    void testIsSellsRelationShip() {
        boolean resultAtomic = ProductEntityUtil.isSellsRelationShip(CHILD_ATOMIC, ROOT_PRODUCT);
        boolean resultSP = ProductEntityUtil.isSellsRelationShip(CHILD_SP, PARENT_ATOMIC);

        Assertions.assertFalse(resultAtomic);
        Assertions.assertTrue(resultSP);
    }
}
