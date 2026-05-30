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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ProductUtilTest {

    public static final String PRODUCT_ORDER_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ITEM_ID_2 = RandomStringUtils.randomAlphabetic(10);
    public static final String PRODUCT_ORDER_ITEM_ID_3 = RandomStringUtils.randomAlphabetic(10);
    public static final String ID = "id";

    @Test
    void testAtLeastOneOrderItemExistInProduct() {
        Product product = Product.builder()
                .id(ID)
                .productOrderItem(createRelatedProductOrderItemDTOs())
                .build();
        boolean result = ProductUtil.atLeastOneOrderItemExistInProduct(product, List.of(PRODUCT_ORDER_ITEM_ID_1));
        Assertions.assertTrue(result);
    }

    @Test
    void testNullAndEmptyOrderItemListAtLeastOneOrderItemExistInProduct() {
        Product product = Product.builder()
                .id(ID)
                .productOrderItem(createRelatedProductOrderItemDTOs())
                .build();
        boolean resultNull = ProductUtil.atLeastOneOrderItemExistInProduct(product, null);
        boolean resultEmpty = ProductUtil.atLeastOneOrderItemExistInProduct(product, List.of());
        Assertions.assertFalse(resultNull);
        Assertions.assertFalse(resultEmpty);
    }

    @Test
    void testNullProductAtLeastOneOrderItemExistInProduct() {
        List<String> ids = List.of(PRODUCT_ORDER_ITEM_ID_1);
        Assert.assertThrows(IllegalArgumentException.class, () -> ProductUtil.atLeastOneOrderItemExistInProduct(null, ids));
    }

    private List<RelatedProductOrderItem> createRelatedProductOrderItemDTOs() {
        RelatedProductOrderItem productOrderItem1 = RelatedProductOrderItem.builder()
                .orderItemId(PRODUCT_ORDER_ITEM_ID_1)
                .build();
        RelatedProductOrderItem productOrderItem2 = RelatedProductOrderItem.builder()
                .orderItemId(PRODUCT_ORDER_ITEM_ID_2)
                .build();
        RelatedProductOrderItem productOrderItem3 = RelatedProductOrderItem.builder()
                .orderItemId(PRODUCT_ORDER_ITEM_ID_3)
                .build();
        return
                List.of(productOrderItem1, productOrderItem2, productOrderItem3);
    }
}