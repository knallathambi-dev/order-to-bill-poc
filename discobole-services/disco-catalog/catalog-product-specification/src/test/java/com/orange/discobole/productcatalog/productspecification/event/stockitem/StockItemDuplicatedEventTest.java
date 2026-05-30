// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;

import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



/**
 * The StockItemDuplicatedEvent used to log duplicated event.
 *
 * @author Varshika Choudhary
 * @version 1.0
 */
class StockItemDuplicatedEventTest extends ProductSpecificationApplicationTests {

    @Test
    void getStockItemTest() {
        StockItem stockItem = new StockItem();
        StockItemDuplicatedEvent stockItemDuplicatedEvent = new StockItemDuplicatedEvent(stockItem);
        StockItem stockItem1 = stockItemDuplicatedEvent.getStockItem();
        Assertions.assertNotNull(stockItem1);
        Assertions.assertEquals(stockItem.hashCode(),stockItem1.hashCode());
        Assertions.assertEquals(stockItem,stockItem1);
    }
}