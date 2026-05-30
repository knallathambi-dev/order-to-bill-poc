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
import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;



/**
 * The StockItemStatusUpdatedEvent type capture the updated life cycle status and stock item time.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemStatusUpdatedEventTest extends ProductSpecificationApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(StockItemStatusUpdatedEventTest.class);

    private StockItemStatusUpdatedEvent stockItemStatusUpdatedEvent;
    private OffsetDateTime dateTime;

    @BeforeEach
    void setUp() {
        dateTime = OffsetDateTime.now();
        stockItemStatusUpdatedEvent = new StockItemStatusUpdatedEvent("1","1", StockItemLifeCycleEnum.LAUNCHED, dateTime);
    }

    @Test
    void stockItemLaunchedEventTest() {
        Assertions.assertAll("StockItemLaunchedEvent",
                () -> Assertions.assertEquals(stockItemStatusUpdatedEvent.getStockItemId(), "1"),
                () -> Assertions.assertEquals(stockItemStatusUpdatedEvent.getStatus(),
                        StockItemLifeCycleEnum.LAUNCHED),
                () -> Assertions.assertEquals(stockItemStatusUpdatedEvent.getStockItemTimeOccurred(),
                        dateTime));
        LOGGER.info("StockItemLaunchedEventTest: {}", stockItemStatusUpdatedEvent);
    }
}