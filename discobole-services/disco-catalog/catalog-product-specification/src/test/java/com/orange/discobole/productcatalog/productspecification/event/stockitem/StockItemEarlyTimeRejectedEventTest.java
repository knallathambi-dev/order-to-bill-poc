// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import java.time.LocalDateTime;
import java.time.OffsetDateTime;



/**
 * The StockItemEarlyTimeRejectedEvent handle event if current stockItem timeOccurred is before the existing stockItem timeOccurred.
 *
 * @author Varshika Choudhary
 * @version 1.0
 */
class StockItemEarlyTimeRejectedEventTest {

    private String stockItemId;
    private OffsetDateTime newTimeOccurred;
    private OffsetDateTime oldTimeOccurred;

    @BeforeEach
    void setUp() {
        stockItemId = "1";
        LocalDateTime dateTime = LocalDateTime.now();
        newTimeOccurred = OffsetDateTime.now();
        oldTimeOccurred = OffsetDateTime.now().plusHours(1L);
    }

    @Test
    void stockItemEarlyTimeRejectedTest() {
        StockItemEarlyTimeRejectedEvent earlyTimeRejectedEvent = new StockItemEarlyTimeRejectedEvent(stockItemId, newTimeOccurred, oldTimeOccurred);
        Assertions.assertEquals(stockItemId, earlyTimeRejectedEvent.getStockItemId());
        Assertions.assertEquals(newTimeOccurred, earlyTimeRejectedEvent.getNewTimeOccurred());
        Assertions.assertEquals(oldTimeOccurred, earlyTimeRejectedEvent.getOldTimeOccurred());
    }
}