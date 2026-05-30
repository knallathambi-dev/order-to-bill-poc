// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.event;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.StockItemReplicatedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemReplicatedEventTest extends CatalogApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(StockItemReplicatedEventTest.class);

    private StockItemReplicatedEvent stockItemReplicatedEvent;

    @BeforeEach
    void setUp() {
        StockItem stockItem = new StockItem();
        stockItem.setId("1");
        stockItem.setState("active");
        stockItemReplicatedEvent = new StockItemReplicatedEvent("1",stockItem);
    }

    @Test
    void DuplicatedStockItemEventTest() {
        Assertions.assertAll("DuplicateStockItemData",
                () -> Assertions.assertEquals("1", stockItemReplicatedEvent.getStockItem().getId()),
                () -> Assertions.assertEquals("active",
                        stockItemReplicatedEvent.getStockItem().getState()));
        LOGGER.debug("StockItemReplicatedEvent: {}", stockItemReplicatedEvent);
    }
}
