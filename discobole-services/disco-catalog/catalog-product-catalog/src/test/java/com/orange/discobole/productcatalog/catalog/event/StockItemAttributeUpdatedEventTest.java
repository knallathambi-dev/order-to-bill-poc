// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.event;

import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemAttributeUpdatedEvent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemAttributeUpdatedEventTest {
    private StockItem stockItem;

    @BeforeEach
    void setUp() {
        stockItem = new StockItem();
        stockItem.setId("1");
        stockItem.setState("active");
        stockItem.setLastUpdate(OffsetDateTime.now());
    }

    @Test
    void serviceSpecAttributeValueModifiedTest() {
        StockItemAttributeUpdatedEvent attributeUpdatedEvent = new StockItemAttributeUpdatedEvent(
        		"1", stockItem);
        Assertions.assertEquals(stockItem, attributeUpdatedEvent.getStockItem());
    }
}
