// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;



import java.time.OffsetDateTime;

import com.orange.discobole.processflow.event.Event;

/**
 * The StockItemEarlyTimeRejectedEvent handle event if current stockItem timeOccurred is before the existing stockItem timeOccurred.
 *
 * @author Varshika Choudhary
 * @version 1.0
 */
public class StockItemEarlyTimeRejectedEvent implements Event {

    private final String stockItemId;
    private final OffsetDateTime newTimeOccurred;
    private final OffsetDateTime oldTimeOccurred;


    public StockItemEarlyTimeRejectedEvent(String stockItemId, OffsetDateTime newTimeOccurred, OffsetDateTime oldTimeOccurred) {
        this.stockItemId = stockItemId;
        this.newTimeOccurred = newTimeOccurred;
        this.oldTimeOccurred = oldTimeOccurred;
    }

    public String getStockItemId() {
        return stockItemId;
    }

    public OffsetDateTime getNewTimeOccurred() {
        return newTimeOccurred;
    }

    public OffsetDateTime getOldTimeOccurred() {
        return oldTimeOccurred;
    }

    @Override
    public String toString() {
        return "StockItemEarlyTimeRejectedEvent{" +
                "stockItemId='" + stockItemId + '\'' +
                ", newTimeOccurred=" + newTimeOccurred +
                ", oldTimeOccurred=" + oldTimeOccurred +
                '}';
    }
}
