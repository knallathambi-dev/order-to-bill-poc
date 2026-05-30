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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * The Class StockItemNotificationSentEvent.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemNotificationSentEventTest extends ProductSpecificationApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(StockItemNotificationSentEventTest.class);

    private StockItemNotificationSentEvent stockItemNotificationSentEvent;

    @BeforeEach
    void setUp() {
        StockItem stockItem = new StockItem();
        stockItem.setId("1");
        stockItem.setState("active");
        stockItemNotificationSentEvent = new StockItemNotificationSentEvent("1",stockItem);
    }

    @Test
    void newStockItemArrivedEventTest() {
        Assertions.assertEquals(stockItemNotificationSentEvent.getStockItem().getId(), "1");
        LOGGER.info("StockItemNotificationSentEvent: {}", stockItemNotificationSentEvent);
    }
}