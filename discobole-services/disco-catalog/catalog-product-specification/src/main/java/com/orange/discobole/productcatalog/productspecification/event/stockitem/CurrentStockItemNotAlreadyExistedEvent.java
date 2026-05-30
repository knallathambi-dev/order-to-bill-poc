// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;


import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;

/**
 * The CurrentStockItemNotAlreadyExistedEvent consist event, if stock item is not already present in the system.
 *
 * @author Varshika Choudhary
 * @version 1.0
 */
public class CurrentStockItemNotAlreadyExistedEvent implements Event {

    private final StockItem stockItem;

    public CurrentStockItemNotAlreadyExistedEvent(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    public CurrentStockItemNotAlreadyExistedEvent() {
        this.stockItem = null;
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    @Override
    public String toString() {
        return "CurrentStockItemNotAlreadyExistedEvent{" +
                "stockItem=" + stockItem +
                '}';
    }
}
