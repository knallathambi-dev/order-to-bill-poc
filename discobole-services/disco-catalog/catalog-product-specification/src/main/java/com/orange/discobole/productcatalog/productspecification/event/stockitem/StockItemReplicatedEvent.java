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
 * The StockItemReplicatedEvent type duplicate event.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class StockItemReplicatedEvent implements StockItemEvent{

    private final StockItem stockItem;
    @TargetAggregateIdentifier
    private final String aggregateId;

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private StockItemReplicatedEvent() {
        stockItem = null;
        aggregateId = null;
    }

    /**
     * Instantiates a new StockItem duplicated event.
     *
     * @param stockItem the stock item
     */
    public StockItemReplicatedEvent(String aggregateId,StockItem stockItem) {
        this.stockItem = stockItem;
        this.aggregateId = aggregateId;
    }

    /**
     * Gets stock item
     *
     * @return the stock item
     */
    public StockItem getStockItem() {
        return stockItem;
    }

    public String getaggregateId() {
		return aggregateId;
	}



	/**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "StockItemReplicatedEvent{" +
                "stockItem=" + stockItem +
                '}';
    }
}
