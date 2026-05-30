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



/**
 * The StockItemStatusVerifyEvent type capture the current and old life cycle status of stock item event.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
class StockItemStatusVerifyEventTest extends ProductSpecificationApplicationTests {

    private static final Logger LOGGER = LogManager.getLogger(StockItemStatusVerifyEventTest.class);

    private StockItemStatusVerifyEvent stockItemStatusVerifyEvent;

    @BeforeEach
    void setUp() {
        stockItemStatusVerifyEvent = new StockItemStatusVerifyEvent("1","1", StockItemLifeCycleEnum.LAUNCHED, StockItemLifeCycleEnum.UNAVAILABLE);
    }

    @Test
    void activeStatusVerifiedEventTest() {
        Assertions.assertAll("StockItemStatusVerify", () -> Assertions.assertEquals(stockItemStatusVerifyEvent.getStockItemId(), "1"),
                () -> Assertions.assertEquals(stockItemStatusVerifyEvent.getLifecycleStatus(), StockItemLifeCycleEnum.UNAVAILABLE),
                () -> Assertions.assertEquals(stockItemStatusVerifyEvent.getOldLifecycleStatus(),
                        StockItemLifeCycleEnum.LAUNCHED));
        LOGGER.info("StatusVerifiedEvent: {}", stockItemStatusVerifyEvent);
    }
}