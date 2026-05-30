// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.constant;

public final class EntityFields {
    public static final class Product {
        public static final String COLLECTION_NAME = "products";

        public static final String _ID = "_id";
        public static final String IS_ROOT_PRODUCT = "isRootProduct";
        public static final String PRODUCT_RELATIONSHIP = "productRelationship";
        public static final String PRODUCT_RELATIONSHIP_DOT_RELATIONSHIP_TYPE = PRODUCT_RELATIONSHIP + ".relationshipType";

        private Product() {}
    }

    private EntityFields() {}
}
