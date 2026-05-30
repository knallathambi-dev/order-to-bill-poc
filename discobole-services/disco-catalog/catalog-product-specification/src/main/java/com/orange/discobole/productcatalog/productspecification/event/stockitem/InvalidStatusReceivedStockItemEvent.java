// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;

/**
 * The Class InvalidStatusReceivedStockItemEvent.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class InvalidStatusReceivedStockItemEvent implements StockItemEvent {

    private final String stockItemId;

    private final StockItemLifeCycleEnum oldLifecycleStatus;

    private final StockItemLifeCycleEnum newLifecycleStatus;

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private InvalidStatusReceivedStockItemEvent() {
        stockItemId = null;
        oldLifecycleStatus = null;
        newLifecycleStatus = null;
    }

    /**
     * Instantiates a new invalid status received event.
     *
     * @param stockItemId           the stock item id
     * @param oldLifecycleStatus    the old lifecycle status
     * @param newLifecycleStatus    the new lifecycle status
     */
    public InvalidStatusReceivedStockItemEvent(String stockItemId, StockItemLifeCycleEnum oldLifecycleStatus, StockItemLifeCycleEnum newLifecycleStatus) {
        this.stockItemId = stockItemId;
        this.oldLifecycleStatus = oldLifecycleStatus;
        this.newLifecycleStatus = newLifecycleStatus;
    }

    public String getStockItemId() {
        return stockItemId;
    }

    public StockItemLifeCycleEnum getOldLifecycleStatus() {
        return oldLifecycleStatus;
    }

    public StockItemLifeCycleEnum getNewLifecycleStatus() {
        return newLifecycleStatus;
    }

    @Override
    public String toString() {
        return "InvalidStatusReceivedStockItemEvent{" +
                "stockItemId='" + stockItemId + '\'' +
                ", oldLifecycleStatus=" + oldLifecycleStatus +
                ", newLifecycleStatus=" + newLifecycleStatus +
                '}';
    }
}
