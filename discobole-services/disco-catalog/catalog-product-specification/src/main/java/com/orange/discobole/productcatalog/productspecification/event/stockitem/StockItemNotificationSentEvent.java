// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.stockitem;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;

/**
 * The Class StockItemNotificationSentEvent.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class StockItemNotificationSentEvent implements StockItemEvent{

    private final StockItem stockItem;
    @TargetAggregateIdentifier
    private final String aggregrateId;

    private StockItemNotificationSentEvent() {
        this.stockItem = null;
        this.aggregrateId = null;
    }

    /**
     * Instantiates a new stock item notification sent event.
	 *
     * @param stockItem the stock item
	 */
    public StockItemNotificationSentEvent(String aggregrateId,StockItem stockItem) {
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
        return "StockItemNotificationSentEvent{" +
                "stockItem=" + stockItem +
                '}';
    }
}
