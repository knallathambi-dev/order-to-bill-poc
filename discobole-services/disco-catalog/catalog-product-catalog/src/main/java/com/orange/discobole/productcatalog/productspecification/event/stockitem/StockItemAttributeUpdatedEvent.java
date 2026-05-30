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
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;

/**
 * The StockItemAttributeUpdatedEvent event contain modified stock item event.
 *
 * @author Varshika Choudhary
 * @version 1.0
 */
public class StockItemAttributeUpdatedEvent implements Event {

    private final StockItem stockItem;
    private final String aggregrateId;

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private StockItemAttributeUpdatedEvent() {
        stockItem = null;
        aggregrateId = null;
    }

    public StockItemAttributeUpdatedEvent(String aggregrateId,StockItem stockItem) {
        this.stockItem = stockItem;
        this.aggregrateId = aggregrateId;
    }

    public StockItem getStockItem() {
        return stockItem;
    }
    public String getAggregrateId() {
  		return aggregrateId;
  	}

    @Override
    public String toString() {
        return "StockItemAttributeUpdatedEvent{" +
                "stockItem=" + stockItem +
                '}';
    }
}