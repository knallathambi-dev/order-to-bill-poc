// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ItemActionType;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductOrderUtilTest {
    private static final String PRODUCT_ORDER_ITEM_ID_1 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ORDER_ITEM_ID_2 = RandomStringUtils.randomAlphabetic(10);
    private static final String PRODUCT_ID = RandomStringUtils.randomAlphabetic(10);

    @Test
    @DisplayName("Given product order with ADD action, " +
            "when getOrderItemsIdBy is called, " +
            "then return matching item IDs")
    void shouldReturnOrderItemIdsForAddAction() {
        // Given
        ProductOrder productOrder = createProductOrderWithItems(ItemActionType.ADD);

        // When
        List<String> result = ProductOrderUtil.getOrderItemsIdBy(productOrder, ItemActionType.ADD);

        // Then
        assertEquals(List.of(PRODUCT_ORDER_ITEM_ID_1), result);
    }

    @Test
    @DisplayName("Given product order with DELETE action, " +
            "when getOrderItemsIdBy is called, " +
            "then return matching item IDs")
    void shouldReturnOrderItemIdsForDeleteAction() {
        // Given
        ProductOrder productOrder = createProductOrderWithItems(ItemActionType.DELETE);

        // When
        List<String> result = ProductOrderUtil.getOrderItemsIdBy(productOrder, ItemActionType.DELETE);

        // Then
        assertEquals(List.of(PRODUCT_ORDER_ITEM_ID_2), result);
    }

    @Test
    @DisplayName("Given product order with no matching action, " +
            "when getOrderItemsIdBy is called, " +
            "then return empty list")
    void shouldReturnEmptyListForNoMatchingAction() {
        // Given
        ProductOrder productOrder = createProductOrderWithItems(ItemActionType.MODIFY);

        // When
        List<String> result = ProductOrderUtil.getOrderItemsIdBy(productOrder, ItemActionType.ADD);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given null product order, " +
            "when getOrderItemsIdBy is called, " +
            "then throw IllegalArgumentException")
    void shouldThrowExceptionForNullProductOrder() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class, () ->
                ProductOrderUtil.getOrderItemsIdBy(null, ItemActionType.ADD)
        );
    }

    @Test
    @DisplayName("Given product order with multiple actions, " +
            "when getOrderItemsIdBy is called, " +
            "then return matching IDs for all actions")
    void shouldReturnOrderItemIdsForMultipleActions() {
        // Given
        ProductOrderItem addItem = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_1, ItemActionType.ADD);
        ProductOrderItem deleteItem = createProductOrderItem(PRODUCT_ORDER_ITEM_ID_2, ItemActionType.DELETE);

        ProductOrder productOrder = ProductOrder.builder()
                .id(PRODUCT_ID)
                .productOrderItem(List.of(addItem, deleteItem))
                .build();

        // When
        List<String> result = ProductOrderUtil.getOrderItemsIdBy(productOrder, ItemActionType.ADD, ItemActionType.DELETE);

        // Then
        assertEquals(List.of(PRODUCT_ORDER_ITEM_ID_1, PRODUCT_ORDER_ITEM_ID_2), result);
    }

    private ProductOrder createProductOrderWithItems(ItemActionType actionType) {
        ProductOrderItem item = createProductOrderItem(
                actionType == ItemActionType.ADD ? PRODUCT_ORDER_ITEM_ID_1 : PRODUCT_ORDER_ITEM_ID_2,
                actionType
        );
        return ProductOrder.builder()
                .id(PRODUCT_ID)
                .productOrderItem(List.of(item))
                .build();
    }

    private ProductOrderItem createProductOrderItem(String itemId, ItemActionType actionType) {
        return ProductOrderItem.builder()
                .id(itemId)
                .action(actionType)
                .isInstallable(Boolean.TRUE)
                .build();
    }
}