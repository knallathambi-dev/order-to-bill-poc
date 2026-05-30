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

import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;

/**
 * The StockItemStatusVerifyEvent type capture the current and old life cycle status of stock item event.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class StockItemStatusVerifyEvent implements StockItemEvent{

    private final String stockItemId;

    private final StockItemLifeCycleEnum oldLifecycleStatus;

    private final StockItemLifeCycleEnum lifecycleStatus;
    @TargetAggregateIdentifier
    private String aggregratedId;
    

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private StockItemStatusVerifyEvent() {
        stockItemId = null;
        oldLifecycleStatus = null;
        lifecycleStatus = null;
    }

    /**
     * Instantiates a new status verified event.
     *
     * @param stockItemId           the stock item id
     * @param oldLifecycleStatus    the old lifecycle status
     * @param lifecycleStatus       the lifecycle status
     */
    public StockItemStatusVerifyEvent(String aggregratedId,String stockItemId, StockItemLifeCycleEnum oldLifecycleStatus, StockItemLifeCycleEnum lifecycleStatus) {
        this.stockItemId = stockItemId;
        this.oldLifecycleStatus = oldLifecycleStatus;
        this.lifecycleStatus = lifecycleStatus;
        this.aggregratedId = aggregratedId;
    }

    public String getStockItemId() {
        return stockItemId;
    }

    public StockItemLifeCycleEnum getOldLifecycleStatus() {
        return oldLifecycleStatus;
    }

    public StockItemLifeCycleEnum getLifecycleStatus() {
        return lifecycleStatus;
    }

    public String getAggregratedId() {
		return aggregratedId;
	}

	@Override
    public String toString() {
        return "StockItemStatusVerifyEvent{" +
                "stockItemId='" + stockItemId + '\'' +
                ", oldLifecycleStatus=" + oldLifecycleStatus +
                ", newLifecycleStatus=" + lifecycleStatus +
                '}';
    }
}
