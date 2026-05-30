// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.SelectProductSpecCharacteristicCommand}
 * is triggered and denotes that Stock item state has not been verified that
 * means its not in active or launched state.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

public class StockItemStateVerificationFailedEvent implements ProductSpecEvent {
	
    private final String stockItemId;
    private final StockItemLifeCycleEnum stockItemLifeCycleStatus;

    /**
     * Constructs an object of StockItemStateVerificationFailedEvent
     *
     * @param stockItemId              stock item id
     * @param stockItemLifeCycleStatus stock item lifecycle status
     */

    public StockItemStateVerificationFailedEvent(String stockItemId, StockItemLifeCycleEnum stockItemLifeCycleStatus) {
        super();
        this.stockItemId = stockItemId;
        this.stockItemLifeCycleStatus = stockItemLifeCycleStatus;
    }

    public StockItemStateVerificationFailedEvent() {
        super();
        this.stockItemId = null;
        this.stockItemLifeCycleStatus = null;
    }

    @Override
    public String toString() {
        return "StockItemStateVerificationFailedEvent [stockItemId=" + stockItemId + ", stockItemLifeCycleStatus="
                + stockItemLifeCycleStatus + "]";
    }

    public String getStockItemId() {
        return stockItemId;
    }

    public StockItemLifeCycleEnum getStockItemLifeCycleStatus() {
        return stockItemLifeCycleStatus;
    }

}

