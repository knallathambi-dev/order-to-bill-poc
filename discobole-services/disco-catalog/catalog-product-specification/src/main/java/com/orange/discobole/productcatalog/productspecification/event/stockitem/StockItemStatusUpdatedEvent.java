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

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;

/**
 * The StockItemStatusUpdatedEvent type capture the updated life cycle status and stock item time.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class StockItemStatusUpdatedEvent implements StockItemEvent{

    private final String stockItemId;
    private final StockItemLifeCycleEnum status;
    private final OffsetDateTime stockItemTimeOccurred;
    @TargetAggregateIdentifier
    private final String aggregrateId;
    

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private StockItemStatusUpdatedEvent() {
        stockItemId = null;
        status = null;
        stockItemTimeOccurred = null;
        aggregrateId = null;
    }

    public StockItemStatusUpdatedEvent(String aggregrateId,String stockItemId, StockItemLifeCycleEnum status, OffsetDateTime stockItemTimeOccurred) {
        this.stockItemId = stockItemId;
        this.status = status;
        this.stockItemTimeOccurred = stockItemTimeOccurred;
        this.aggregrateId = aggregrateId;
    }

    public String getStockItemId() {
        return stockItemId;
    }

    public StockItemLifeCycleEnum getStatus() {
        return status;
    }

    public OffsetDateTime getStockItemTimeOccurred() {
        return stockItemTimeOccurred;
    }

    public String getAggregrateId() {
		return aggregrateId;
	}



	@Override
    public String toString() {
        return "StockItemStatusUpdatedEvent{" +
                "stockItemId='" + stockItemId + '\'' +
                ", status=" + status +
                ", stockItemTimeOccurred=" + stockItemTimeOccurred +
                '}';
    }
}
