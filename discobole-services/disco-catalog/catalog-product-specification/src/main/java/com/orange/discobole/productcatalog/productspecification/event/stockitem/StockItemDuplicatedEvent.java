// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;

/**
 * The StockItemDuplicatedEvent used to log duplicated event.
 *
 * @author Varshika Choudhary
 * @version 1.0
 */
public class StockItemDuplicatedEvent implements StockItemEvent{

    private final StockItem stockItem;

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private StockItemDuplicatedEvent() {
        stockItem = null;
    }

    /**
     * Instantiates a new stock item duplicated event.
     *
     * @param stockItem
     */

    public StockItemDuplicatedEvent(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    @Override
    public String toString() {
        return "StockItemDuplicatedEvent{" +
                "stockItem=" + stockItem +
                '}';
    }
}
