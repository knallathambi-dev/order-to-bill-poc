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
 * The StockItemExpurgedEvent type do stock item expurge event.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class StockItemExpurgedEvent implements StockItemEvent{

    private final StockItem stockItem;
    @TargetAggregateIdentifier
    private final String aggregrateId;

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private StockItemExpurgedEvent() {
        stockItem = null;
        aggregrateId= null;
    }

    /**
     * Instantiates a new StockItem expurged event.
     *
     * @param stockItem
     */
    public StockItemExpurgedEvent(String aggregrateId,StockItem stockItem) {
        this.stockItem = stockItem;
        this.aggregrateId = aggregrateId;
    }

    /**
     * Gets stock item
     *
     * @return the stock item
     */
    public StockItem getStockItem() {
        return stockItem;
    }

    public String getAggregrateId() {
		return aggregrateId;
	}

	/**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "StockItemExpurgedEvent{" +
                "stockItem=" + stockItem +
                '}';
    }

}
