// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.InvalidStatusReceivedStockItemEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Class InvalidStatusReceivedStockItemEvent.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
class InvalidStatusReceivedStockItemEventTest {

    private static final Logger LOGGER = LogManager.getLogger(InvalidStatusReceivedStockItemEventTest.class);

    private InvalidStatusReceivedStockItemEvent invalidStatusReceivedStockItemEvent;

    @BeforeEach
    void setUp() {
        String stockItemId = "1";
        invalidStatusReceivedStockItemEvent = new InvalidStatusReceivedStockItemEvent(stockItemId, StockItemLifeCycleEnum.ACTIVE,
                StockItemLifeCycleEnum.LAUNCHED);
    }

    @Test
    void invalidStatusReceivedEventTest() {
        Assertions.assertAll("InvalidStatusReceivedEvent",
                () -> Assertions.assertEquals("1",invalidStatusReceivedStockItemEvent.getStockItemId()),
                () -> Assertions.assertEquals(invalidStatusReceivedStockItemEvent.getOldLifecycleStatus(),
                        StockItemLifeCycleEnum.ACTIVE),
                () -> Assertions.assertEquals(invalidStatusReceivedStockItemEvent.getNewLifecycleStatus(),
                        StockItemLifeCycleEnum.LAUNCHED));
        LOGGER.info("Invalid Status Received StockItem Event: {}", invalidStatusReceivedStockItemEvent);
    }
}